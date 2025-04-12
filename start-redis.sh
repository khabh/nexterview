#!/bin/bash

CONTAINER_NAME=dev-redis

EXISTING=$(docker ps -a -q -f name="^/${CONTAINER_NAME}$")

if [ -n "$EXISTING" ]; then
  echo "🛑 Redis container already exists. Stopping and removing..."
  docker stop $CONTAINER_NAME > /dev/null 2>&1 || true
  docker rm $CONTAINER_NAME
fi

echo "🚀 Starting Redis container..."
docker run -d --name $CONTAINER_NAME -p 6379:6379 redis:7.2
