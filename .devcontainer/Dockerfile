FROM mcr.microsoft.com/vscode/devcontainers/base

ENV DEBIAN_FRONTEND=noninteractive

ARG USERNAME="vscode"

RUN export DEBIAN_FRONTEND=noninteractive && \
    curl https://packages.microsoft.com/config/debian/10/packages-microsoft-prod.deb --output /tmp/packages-microsoft-prod.deb && \
    dpkg -i /tmp/packages-microsoft-prod.deb && \
    apt-get update && \
    apt-get -y install --no-install-recommends apt-utils dialog && \
    apt-get -y install --no-install-recommends apt-transport-https ca-certificates software-properties-common libssl-dev libffi-dev \
        dotnet-sdk-3.1 build-essential gnupg-agent dnsutils httpie bash-completion curl wget git unzip jq lsb-release procps gnupg2 && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9 && \
    curl -O https://cdn.azul.com/zulu/bin/zulu-repo_1.0.0-2_all.deb && \
    apt-get install ./zulu-repo_1.0.0-2_all.deb && \
    apt-get update && \
    apt-get -y install zulu11-jdk && \
    apt-get -y upgrade

COPY .devcontainer/library-scripts /tmp/library-scripts/

# [Option] Install Maven
ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION="3.6.3"
RUN if [ "${INSTALL_MAVEN}" = "true" ]; then bash /tmp/library-scripts/maven-debian.sh; fi
ENV PATH=/apache-maven/bin:${PATH}

# [Option] Install Azure CLI
ARG  INSTALL_AZURE_CLI="true"
RUN  if [ "$INSTALL_AZURE_CLI" = "true" ]; then bash /tmp/library-scripts/azcli-debian.sh; fi

# [Option] Install Docker CLI
# To use the Docker CLI inside a container, use the bind mount option to add the docker socket to the container.
# Example: "docker run -v '/var/run/docker.sock:/var/run/docker.sock' ..."
ARG  INSTALL_DOCKER="true"
RUN  if [ "${INSTALL_DOCKER}" = "true" ]; then bash /tmp/library-scripts/docker-debian.sh "${USERNAME}"; fi

ENV PATH="~/.dotnet/tools:${PATH}"
USER $USERNAME
