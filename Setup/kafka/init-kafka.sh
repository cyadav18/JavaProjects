#!/bin/bash

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 15

# Define topics
TOPICS=("task-created" "task-updated" "task-deleted")

# Create topics
for topic in "${TOPICS[@]}"; do
  echo " Creating topic: $topic"
  kafka-topics --bootstrap-server kafka:9092 \
    --create --if-not-exists \
    --topic "$topic" \
    --partitions 1 \
    --replication-factor 1
done
