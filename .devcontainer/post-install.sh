#!/bin/sh

# copy vscode files
mkdir -p .vscode && cp docs/vscode-template/* .vscode

# set auth type
export AUTH_TYPE=CLI

# update .bashrc
echo "" >> ~/.bashrc
#echo 'export PATH="$PATH:~/.dotnet/tools"' >> ~/.bashrc
echo "export AUTH_TYPE=CLI" >> ~/.bashrc

