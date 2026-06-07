#!/bin/sh

# JWT Secret auto-generation and persistence
JWT_SECRET_FILE="/app/secrets/jwt.secret"

# Create secrets directory if it doesn't exist
mkdir -p /app/secrets

# Prefer JWT from environment (CI/tests), then persisted file, then auto-generate
if [ -n "$JWT_SECRET" ]; then
    echo "Using JWT secret from environment variable"
elif [ -f "$JWT_SECRET_FILE" ]; then
    echo "JWT secret already exists. Loading from $JWT_SECRET_FILE"
    JWT_SECRET=$(cat "$JWT_SECRET_FILE")
else
    echo "No JWT secret found. Generating new JWT secret..."
    JWT_SECRET=$(head -c 32 /dev/urandom | base64 | tr -d '\n')
    echo "$JWT_SECRET" > "$JWT_SECRET_FILE"
    chmod 600 "$JWT_SECRET_FILE"
    echo "JWT secret generated and saved to $JWT_SECRET_FILE"
fi

# Export JWT secret as environment variable
export JWT_SECRET="$JWT_SECRET"

# Print confirmation (without showing the actual secret)
echo "JWT secret loaded successfully"
echo "Starting application..."

# Execute the main application
exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar
