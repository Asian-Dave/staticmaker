# FF14 Static Maker - React Frontend

A modern React web application for managing Final Fantasy 14 raiding groups. This replaces the original JavaFX desktop application with a containerized web-based solution.

## Features

- **Authentication**: User registration and login with JWT
- **Raid Group Management**: Create and manage raid groups
- **Player Management**: Add/remove players with FFLogs validation
- **Region Statistics**: View statistics across regions
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Real-time Validation**: Instant feedback on form inputs

## Technology Stack

- **React 18** - UI library
- **React Router 6** - Routing
- **Axios** - HTTP client
- **CSS3** - Styling
- **JWT** - Authentication

## Getting Started

### Prerequisites

- Node.js 16+ and npm

### Installation

```bash
cd frontend
npm install
```

### Development

```bash
npm start
```

The app will open at http://localhost:3000

### Building for Production

```bash
npm run build
```

## Project Structure

```
frontend/
├── public/
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/
│   │   ├── Header.jsx
│   │   ├── LoginForm.jsx
│   │   ├── RegisterForm.jsx
│   │   ├── RaidGroupList.jsx
│   │   ├── PlayerForm.jsx
│   │   └── RegionStats.jsx
│   ├── pages/
│   │   ├── HomePage.jsx
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   └── RaidGroupPage.jsx
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── playerService.js
│   │   └── raidGroupService.js
│   ├── hooks/
│   │   └── useAuth.js
│   ├── utils/
│   │   └── constants.js
│   ├── App.js
│   ├── App.css
│   └── index.js
├── package.json
└── Dockerfile
```

## API Integration

The frontend connects to the Spring Boot backend API at http://localhost:8080

### Authentication

All authenticated requests include the JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

## Environment Variables

Create `.env.local` for local development:

```env
REACT_APP_API_URL=http://localhost:8080
```

## Docker

Run the frontend in a container:

```bash
docker build -t staticmaker-frontend .
docker run -p 3000:80 staticmaker-frontend
```

## Available Scripts

- `npm start` - Run development server
- `npm test` - Run tests
- `npm run build` - Build for production
- `npm run eject` - Eject from Create React App (one-way operation)
