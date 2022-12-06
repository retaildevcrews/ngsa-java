#!/bin/sh
WGET https://github.com/devcontainers/features/blob/main/src/java/devcontainer-feature.json
WGET https://github.com/devcontainers/features/blob/main/src/java/install.sh
WGET https://github.com/devcontainers/features/blob/main/src/java/wrapper.sh

chmod +x install.sh
chmod +x wrapper.sh

