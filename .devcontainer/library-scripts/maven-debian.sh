#!/usr/bin/env bash
#-------------------------------------------------------------------------------------------------------------
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License. See https://go.microsoft.com/fwlink/?linkid=2090316 for license information.
#-------------------------------------------------------------------------------------------------------------
#
# Docs: https://github.com/microsoft/vscode-dev-containers/blob/main/script-library/docs/maven.md
# Maintainer: The VS Code and Codespaces Teams
#
# Syntax: ./maven-debian.sh [install maven flag][maven version]

set -e

wget http://www-us.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz 
tar -xf apache-maven-${MAVEN_VERSION}-bin.tar.gz 
mv apache-maven-${MAVEN_VERSION}/ apache-maven/

ENV PATH=/apache-maven/bin:${PATH}

