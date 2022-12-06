# this image is built and updated weekly
# https://github.com/cse-labs/codespaces-images

#checkov:skip=CKV_DOCKER_2: No healthcheck is needed 
#checkov:skip=CKV_DOCKER_3: No user is needed 
#checkov:skip=CKV_DOCKER_5: Update is used in same line as upgrade 
#checkov:skip=CKV_DOCKER_7: Alright to use "latest" tag

FROM ghcr.io/cse-labs/k3d:latest

# use scripts from: https://github.com/microsoft/vscode-dev-containers/tree/main/script-library
# RUN /bin/bash /scripts/java-debian.sh
# RUN /bin/bash /scripts/maven-debian.sh

COPY installJava.sh installJava.sh

RUN chmod +x installJava.sh

RUN ./installJava.sh

RUN install.sh 19-zulu /usr/local/sdkman/

# some images require specific values
ARG USERNAME=vscode


     local install_type=$1
    local requested_version=$2
    local prefix=$3
    local suffix="${4:-"\\s*"}"
    local full_version_check=${5:-".*-[a-z]+"}
    local set_as_default=${6:-"true"}