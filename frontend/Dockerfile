# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files and install dependencies (cached layer)
COPY package*.json ./
RUN npm ci --legacy-peer-deps

# Copy source code and build for production
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Runtime
FROM nginx:1.27-alpine

# Copy built application to nginx
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Copy custom nginx configuration for SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget --spider --quiet http://localhost || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
