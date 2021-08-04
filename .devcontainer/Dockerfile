FROM ghcr.io/retaildevcrews/dind:latest

ENV DEBIAN_FRONTEND=noninteractive

ARG USERNAME="vscode"
ARG JAVA_VERSION="11"
ARG MAVEN_VERSION="3.6.3"
ARG ZULU_VERSION=1.0.0-2

RUN apt-get update && \
    apt-get -y install --no-install-recommends software-properties-common libssl-dev libffi-dev \
        build-essential gnupg-agent dnsutils httpie bash-completion procps gnupg2 && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9 && \
    curl -O https://cdn.azul.com/zulu/bin/zulu-repo_${ZULU_VERSION}_all.deb && \
    apt-get install ./zulu-repo_${ZULU_VERSION}_all.deb && \
    apt-get update && \
    apt-get -y install zulu${JAVA_VERSION}-jdk

COPY library-scripts /tmp/library-scripts/

RUN bash /tmp/library-scripts/maven-debian.sh

ENV PATH=/apache-maven/bin:${PATH}:~/.dotnet/tools

RUN apt-get update && apt-get upgrade -y

USER $USERNAME
