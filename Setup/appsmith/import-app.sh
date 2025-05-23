#!/bin/bash

set -e

APPSMITH_URL="http://localhost"   # Or EC2 public IP if running externally
IMPORT_FILE="./appsmith/import/task_manager_appsmith.json"
USERNAME="admin@appsmith.com"
PASSWORD="password"  # Or use an environment variable
MAX_ATTEMPTS=30

echo " Waiting for Appsmith to be ready..."

# Wait for Appsmith to be ready
attempts=0
until curl -s "$APPSMITH_URL/api/v1/users/me" > /dev/null; do
  sleep 5
  attempts=$((attempts+1))
  echo "Waiting... attempt $attempts"
  if [ "$attempts" -ge "$MAX_ATTEMPTS" ]; then
    echo "Appsmith did not start in time."
    exit 1
  fi
done

echo "Appsmith is up. Attempting to log in..."

# Authenticate and get session cookie
AUTH_RESPONSE=$(curl -s -X POST "$APPSMITH_URL/api/v1/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

if echo "$AUTH_RESPONSE" | grep -q '"responseMeta":{"status":false'; then
  echo "Login failed. Response: $AUTH_RESPONSE"
  exit 1
fi

echo "Logged in. Importing the Appsmith app..."

# Import the app
IMPORT_RESPONSE=$(curl -s -X POST "$APPSMITH_URL/api/v1/app/import" \
  -b cookies.txt \
  -F "file=@$IMPORT_FILE")

echo "Import response:"
echo "$IMPORT_RESPONSE"

# Clean up
rm cookies.txt
