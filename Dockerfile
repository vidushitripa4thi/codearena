# ==============================================================================
# DeployForge — Backend Multi-Stage Dockerfile (Spring Boot 3 + Java 17)
# ==============================================================================
# Multi-stage build strategy:
# Stage 1 (Builder): Uses full JDK 17 + Maven to compile source and package JAR.
# Stage 2 (Runner):  Uses minimal Eclipse Temurin JRE 17 Alpine runtime to keep
#                   image size minimal, reduce attack surface, and run as non-root.
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build the Spring Boot application artifact
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy Maven Project Object Model (POM) descriptor
COPY pom.xml .

# Pre-fetch project dependencies to leverage Docker layer caching
RUN mvn dependency:resolve -B

# Copy application source code
COPY src ./src

# Compile and package executable fat JAR (automated tests are executed in CI pipeline)
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Minimal, secure runtime container with JDK 17 for code compilation
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-alpine AS runner

# Set metadata labels for container governance
LABEL maintainer="DeployForge Team <devops@codearena.com>"
LABEL project="DeployForge - DevOps Automation for CodeArena"
LABEL component="backend-api"

WORKDIR /app

# Create an unprivileged dedicated user and group for defense-in-depth
RUN addgroup -S deployforge -g 1001 && \
    adduser -S deployforge -u 1001 -G deployforge

# Copy compiled JAR from builder stage and assign non-root ownership
COPY --from=builder --chown=deployforge:deployforge /build/target/codearena-0.0.1-SNAPSHOT.jar /app/app.jar

# Switch to unprivileged user
USER deployforge:deployforge

# Expose backend REST API port
EXPOSE 8080

# Configure JVM memory and performance flags with environment override capability
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Container healthcheck: probes the public problems library endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
    CMD wget -q -O - http://127.0.0.1:8080/api/problems > /dev/null || exit 1

# Execute the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
