# this image is built and updated weekly
# https://github.com/cse-labs/codespaces-images

#checkov:skip=CKV_DOCKER_2: No healthcheck is needed 
#checkov:skip=CKV_DOCKER_3: No user is needed 
#checkov:skip=CKV_DOCKER_5: Update is used in same line as upgrade 
#checkov:skip=CKV_DOCKER_7: Alright to use "latest" tag

FROM ghcr.io/cse-labs/k3d:latest

RUN apk update
RUN apk upgrade
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apk -qq -y install curl wget unzip zip bash

RUN curl -s "https://get.sdkman.io" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"

# use scripts from: https://github.com/microsoft/vscode-dev-containers/tree/main/script-library
# RUN /bin/bash /scripts/java-debian.sh
# RUN /bin/bash /scripts/maven-debian.sh

#   COPY installJava.sh installJava.sh

#   RUN chmod +x installJava.sh

#   RUN ./installJava.sh

#   RUN install.sh java 11.0.16.1-zulu /usr/local/sdkman/

# some images require specific values
ARG USERNAME=vscode
