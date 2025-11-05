# syntax=docker/dockerfile:1
# Multi-stage Dockerfile for Spring Boot development

# Layer with cached dependencies
FROM maven:3.9-eclipse-temurin-17 AS dependencies
LABEL stage=dependencies
WORKDIR /opt/app
COPY .docker/dev/settings.xml .
COPY pom.xml .
RUN mvn -B dependency:go-offline -s settings.xml

# Development stage
FROM dependencies AS dev
LABEL stage=dev
WORKDIR /opt/app

# Copy source code
COPY src src

# Expose application and debug ports
EXPOSE 8080 5005

# Run the application with remote debugging enabled
ENTRYPOINT ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]

# Production build stage
FROM dependencies AS build
LABEL stage=build
WORKDIR /opt/app
COPY src src
RUN mvn -B clean package -DskipTests -s settings.xml

# Production stage
FROM eclipse-temurin:17-jre-alpine AS production
LABEL stage=production
WORKDIR /opt/app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from build stage
COPY --from=build /opt/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
