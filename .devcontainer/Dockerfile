# this image is built and updated weekly
# https://github.com/cse-labs/codespaces-images

#checkov:skip=CKV_DOCKER_2: No healthcheck is needed 
#checkov:skip=CKV_DOCKER_3: No user is needed 
#checkov:skip=CKV_DOCKER_5: Update is used in same line as upgrade 
#checkov:skip=CKV_DOCKER_7: Alright to use "latest" tag

FROM ghcr.io/cse-labs/k3d:latest

# use scripts from: https://github.com/microsoft/vscode-dev-containers/tree/main/script-library
RUN /bin/bash /scripts/java-debian.sh
RUN /bin/bash /scripts/maven-debian.sh

RUN sdk install java 11.0.16.1-zulu && sdk flush archives && sdk flush tem
RUN sdk default java 11.0.16.1-zulu

# some images require specific values
ARG USERNAME=vscode
