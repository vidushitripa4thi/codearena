# ==============================================================================
# DeployForge — Frontend Multi-Stage Dockerfile (React 18 + Vite + Nginx)
# ==============================================================================
# Multi-stage build strategy:
# Stage 1 (Builder): Uses Node.js 20 Alpine to resolve dependencies, compile
#                   TypeScript, and build static production bundles via Vite.
# Stage 2 (Runner):  Uses lightweight Nginx Alpine to serve static web assets
#                   and reverse-proxy /api traffic to the backend.
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build production bundle
# ------------------------------------------------------------------------------
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependency manifests
COPY package.json package-lock.json ./

# Install exact dependencies reproducibly using clean install
RUN npm ci

# Copy frontend source and configuration files
COPY . .

# Compile TypeScript and build production assets with Vite (outputs to /app/dist)
RUN npm run build

# ------------------------------------------------------------------------------
# Stage 2: Production Nginx web server & reverse proxy
# ------------------------------------------------------------------------------
FROM nginx:1.27-alpine AS runner

# Metadata labels
LABEL maintainer="DeployForge Team <devops@codearena.com>"
LABEL project="DeployForge - DevOps Automation for CodeArena"
LABEL component="frontend-ui"

# Remove default Nginx HTML landing page
RUN rm -rf /usr/share/nginx/html/*

# Copy compiled SPA bundles from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration with reverse proxy routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose standard HTTP port
EXPOSE 80

# Healthcheck probe against Nginx root via loopback IP
HEALTHCHECK --interval=15s --timeout=5s --start-period=5s --retries=3 \
    CMD wget -q -O - http://127.0.0.1/ > /dev/null || exit 1

# Run Nginx in foreground mode
CMD ["nginx", "-g", "daemon off;"]
