#!/bin/bash

# Create the network if it doesn't exist
docker network create hackathon-network || true

# Connect the existing postgres container to the network
docker network connect hackathon-network postgres || true

echo "PostgreSQL container connected to hackathon-network"
echo "You can now deploy your backend - it will be able to reach postgres via container name" 