# FFLogs API Setup Guide

## Overview

The application now uses the official FFLogs API v2 (GraphQL) to validate character existence instead of HTML scraping. This provides:

- ✅ **Reliable**: Uses official API instead of parsing HTML
- ✅ **Efficient**: GraphQL queries return only needed data
- ✅ **Secure**: OAuth 2.0 authentication
- ✅ **Maintainable**: Won't break when website changes
- ✅ **Rate Limited**: Proper API rate limiting

## What Changed

### Before (HTML Scraping)
```java
// Old method - REMOVED
String url = "https://www.fflogs.com/character/na/Gilgamesh/First%20Last";
// Parse HTML, look for "No character could be found"
```

**Problems**:
- Brittle (breaks if HTML changes)
- Inefficient (downloads entire page)
- Unofficial (could be blocked)
- No rate limiting

### After (GraphQL API)
```java
// New method - CURRENT
FFLogsApiClient.characterDoesNotExist("First", "Last", "Gilgamesh", RegionType.AMERICA)
// Uses official GraphQL API with OAuth 2.0
```

**Benefits**:
- Official API support
- OAuth 2.0 authentication
- GraphQL efficient queries
- Proper error handling
- Token caching
- Graceful degradation (fails open if API unavailable)

## Getting API Credentials

### Step 1: Create FFLogs Account

1. Go to https://www.fflogs.com
2. Register for an account (or log in)

### Step 2: Create API Client

1. Navigate to https://www.fflogs.com/api/clients
2. Click **"Create Client"**
3. Fill in the form:
   - **Name**: Your application name (e.g., "Static Maker Dev")
   - **Redirect URLs**: `http://localhost:8080` (not used for client credentials flow)
   - **Grant Types**: Select **"Client Credentials"**
4. Click **"Create"**

### Step 3: Copy Credentials

After creating the client, you'll see:
- **Client ID**: A long alphanumeric string
- **Client Secret**: A long secret string (shown only once!)

**IMPORTANT**: Save the Client Secret immediately - it won't be shown again!

### Step 4: Configure Application

Add your credentials to `.env` file:

```env
# FFLogs API Configuration
FFLOGS_CLIENT_ID=your_client_id_here
FFLOGS_CLIENT_SECRET=your_client_secret_here
```

**For Docker**: The `.env` file is automatically loaded by docker-compose.

**For local development**: Set environment variables:
```bash
export FFLOGS_CLIENT_ID=your_client_id_here
export FFLOGS_CLIENT_SECRET=your_client_secret_here
mvn spring-boot:run
```

## How It Works

### 1. OAuth 2.0 Token Exchange

When the application starts, it exchanges client credentials for an access token:

```
POST https://www.fflogs.com/oauth/token
Authorization: Basic base64(client_id:client_secret)
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials

Response:
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### 2. GraphQL Character Query

Using the access token, query for character:

```graphql
query {
  characterData {
    character(
      name: "First Last",
      serverSlug: "Gilgamesh",
      serverRegion: "na"
    ) {
      id
      name
    }
  }
}
```

**If character exists**: Returns character data
**If character doesn't exist**: Returns null

### 3. Token Caching

The application caches access tokens:
- Tokens are valid for ~1 hour
- Cached token is reused until 5 minutes before expiry
- New token is automatically requested when needed

## Configuration Options

### Required Configuration

```yaml
# application.yml
fflogs:
  client:
    id: ${FFLOGS_CLIENT_ID}      # Required
    secret: ${FFLOGS_CLIENT_SECRET}  # Required
```

### Optional: Disable Validation

If you don't want to use FFLogs validation (not recommended):

```env
# Leave these empty or unset
FFLOGS_CLIENT_ID=
FFLOGS_CLIENT_SECRET=
```

**Behavior**: Character validation will be skipped, and all characters will be allowed.

**Warning**: This is not recommended for production as it allows creation of non-existent characters.

## Testing

### Test API Connection

```bash
# Using curl
curl -u CLIENT_ID:CLIENT_SECRET \
  -d grant_type=client_credentials \
  https://www.fflogs.com/oauth/token
```

Should return:
```json
{
  "access_token": "eyJ...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### Test Character Validation

```bash
# Register and login to get JWT token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"youruser","password":"yourpass"}' \
  | jq -r '.token')

# Try to create a player (should validate via FFLogs API)
curl -X POST http://localhost:8080/api/players \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstname": "Real",
    "surname": "Character",
    "role": "Tank",
    "datacenter": "Aether",
    "server": "Gilgamesh",
    "staticName": "MyStatic"
  }'
```

## Error Handling

### Graceful Degradation

The application handles API failures gracefully:

```java
if (clientId == null || clientSecret == null) {
    log.warn("FFLogs API credentials not configured. Skipping validation.");
    return false; // Allow character creation
}
```

**Fail Open Strategy**:
- If API credentials are missing → Skip validation
- If API is unreachable → Skip validation
- If API returns error → Skip validation

This ensures the application continues working even if FFLogs API is unavailable.

### Common Issues

#### 1. Invalid Credentials
```
Error: Failed to obtain FFLogs API access token
```
**Solution**: Check your Client ID and Client Secret in `.env`

#### 2. Rate Limiting
```
Error: HTTP 429 Too Many Requests
```
**Solution**: FFLogs API has rate limits. Wait and retry.

#### 3. Character Not Found
```
Error: Character 'First Last' does not exist on FFLogs
```
**Solution**: This is expected - the character doesn't exist on FFLogs. Use a real character name.

#### 4. Network Error
```
Error: Connection timeout
```
**Solution**: Check internet connectivity. The application will skip validation and allow creation.

## Rate Limits

FFLogs API rate limits (as of 2024):
- **Client Credentials**: 400 requests per minute
- **Per IP**: Standard web rate limiting applies

The application implements:
- Token caching (reduces token requests)
- Single API call per character validation
- Error handling for rate limit responses

## Security Notes

### Protect Your Credentials

**NEVER**:
- Commit credentials to version control
- Share your Client Secret
- Use production credentials in development

**DO**:
- Use environment variables
- Keep `.env` in `.gitignore`
- Use different credentials for dev/staging/prod
- Rotate credentials if compromised

### Production Recommendations

1. **Separate Credentials**: Use different API clients for:
   - Development
   - Staging
   - Production

2. **Secret Management**: Use proper secret management:
   - Docker Secrets
   - Kubernetes Secrets
   - AWS Secrets Manager
   - Azure Key Vault

3. **Monitor Usage**: Check API usage at https://www.fflogs.com/api/clients

## Troubleshooting

### Check Configuration

```bash
# Verify environment variables are set
echo $FFLOGS_CLIENT_ID
echo $FFLOGS_CLIENT_SECRET

# Check Docker container environment
docker exec staticmaker_app env | grep FFLOGS
```

### Enable Debug Logging

```env
LOG_LEVEL=DEBUG
```

Look for log messages:
```
DEBUG: Obtained new FFLogs API access token (expires in 3600 seconds)
DEBUG: Character found: First Last on Gilgamesh (na)
```

### Test GraphQL Query Directly

Use a GraphQL client (Insomnia, Postman, or curl):

```bash
# Get token
TOKEN=$(curl -u CLIENT_ID:CLIENT_SECRET \
  -d grant_type=client_credentials \
  https://www.fflogs.com/oauth/token | jq -r '.access_token')

# Query character
curl -X POST https://www.fflogs.com/api/v2/client \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ characterData { character(name: \"First Last\", serverSlug: \"Gilgamesh\", serverRegion: \"na\") { id name } } }"
  }'
```

## API Documentation

- **Official Docs**: https://www.fflogs.com/api/docs
- **GraphQL Schema**: https://www.fflogs.com/v2-api-docs/ff/
- **API Clients**: https://www.fflogs.com/api/clients
- **Forums**: https://forums.combatlogforums.com/

## Migration Notes

### From Old Version

If upgrading from the old HTML scraping version:

1. **No code changes needed** - just configure API credentials
2. **Character validation is now more reliable**
3. **Failed validations are properly logged**
4. **Graceful degradation if API is unavailable**

### Benefits of Migration

| Aspect | Old (HTML Scraping) | New (API) |
|--------|---------------------|-----------|
| **Reliability** | Breaks if HTML changes | Stable API contract |
| **Performance** | Slow (full page download) | Fast (targeted query) |
| **Rate Limiting** | Risk of IP ban | Proper API limits |
| **Error Handling** | Generic timeout | Specific error codes |
| **Official Support** | None | Official API |
| **Maintainability** | Brittle string parsing | Typed GraphQL queries |

## Summary

✅ **Replace HTML scraping with official FFLogs API v2**
✅ **OAuth 2.0 client credentials flow**
✅ **GraphQL for efficient queries**
✅ **Token caching for performance**
✅ **Graceful degradation on errors**
✅ **Comprehensive error handling**
✅ **Production-ready configuration**

Get your API credentials at: https://www.fflogs.com/api/clients
