#!/bin/bash

# Step 1: Remove old versions
sudo apt remove -y docker docker-engine docker.io containerd runc

# Step 2: Update system and install prerequisites
sudo apt update
sudo apt install -y ca-certificates curl gnupg lsb-release

# Step 3: Add Docker’s official GPG key
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Step 4: Set up the Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) \
  signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Step 5: Install Docker Engine and Compose plugin
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Step 6: Enable and start Docker
sudo systemctl enable docker
sudo systemctl start docker

# Step 7: Add current user to docker group
sudo usermod -aG docker $USER

echo "Docker and Docker Compose installed. Please log out and back in or run 'newgrp docker' to apply group changes."

exist