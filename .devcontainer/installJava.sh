#!/bin/sh

wget https://github.com/devcontainers/features/blob/main/src/java/devcontainer-feature.json
wget https://github.com/devcontainers/features/blob/main/src/java/install.sh
wget https://github.com/devcontainers/features/blob/main/src/java/wrapper.sh

chmod +x install.sh
chmod +x wrapper.sh
