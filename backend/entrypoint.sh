#!/bin/sh

# JWT Secret auto-generation and persistence
JWT_SECRET_FILE="/app/secrets/jwt.secret"

# Create secrets directory if it doesn't exist
mkdir -p /app/secrets

# Check if JWT secret already exists
if [ ! -f "$JWT_SECRET_FILE" ]; then
    echo "No JWT secret found. Generating new JWT secret..."
    # Generate random 64-character hex string
    JWT_SECRET=$(head -c 32 /dev/urandom | xxd -p -c 64)
    echo "$JWT_SECRET" > "$JWT_SECRET_FILE"
    chmod 600 "$JWT_SECRET_FILE"
    echo "JWT secret generated and saved to $JWT_SECRET_FILE"
else
    echo "JWT secret already exists. Loading from $JWT_SECRET_FILE"
    JWT_SECRET=$(cat "$JWT_SECRET_FILE")
fi

# Export JWT secret as environment variable
export JWT_SECRET="$JWT_SECRET"

# Print confirmation (without showing the actual secret)
echo "JWT secret loaded successfully"
echo "Starting application..."

# Execute the main application
exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar
