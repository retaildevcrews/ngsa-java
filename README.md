# NGSA Java App

NGSA Java App is inteneded for platform testing and monitoring in one or many Kubernetes clusters and/or cloud deployments.

## Prerequisites

- Bash shell (tested on Visual Studio Codespaces, Mac, Ubuntu, Windows with WSL2)
  - Will not work in Cloud Shell or WSL1
- Azure CLI ([download](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest))
- Docker CLI ([download](https://docs.docker.com/install/))
- Java 11+ ([download](https://www.azul.com/downloads/?package=jdk))
- Maven ([download](https://maven.apache.org/download.cgi))
- Cosmos DB setup (follow the steps in the [imdb readme](https://github.com/retaildevcrews/imdb.git) )
- Visual Studio Code (optional) ([download](https://code.visualstudio.com/download))

## Ngsa-java Usage

> NOTE: Command-line arguments take precedence over `application.properties` values and env vars.

```text

Usage:
        mvn clean spring-boot:run -Dspring-boot.run.arguments=[options]
        
        export [env var]=<value>
        mvn clean spring-boot:run

Options: 
        --help                                               Show help and usage information
        --version                                            Shows version information
        --dry-run                                            Validates configuration
        --log-level=<trace|info|warn|error|fatal>            Log Level [default: Error]
        --burst-header=<true|false>                          Enable bursting metrics [default: false]
        --burst-service                                      Service name for bursting metrics (string) [default: ngsa-java]
        --burst-target                                       Target level for bursting metrics (int) [default: 60]
        --burst-max                                          Max level for bursting metrics (int) [default: 80]
        --prometheus=<true|false>                            Enable prometheus metrics [default: false]
        --zone                                               Zone for log [default: dev]
        --region                                             Region for log [default: dev]
        --secrets-volume                                     Secrets Volume Path from project root directory [default: secrets]   
              
Env vars:
        BURST_HEADER=<true|false>
        BURST_SERVICE                                        
        BURST_TARGET                                         
        BURST_MAX                                            
        PROMETHEUS=<true|false>
        ZONE
        REGION
        SECRETS_VOLUME                              
```

## Run the Application

### Using Visual Studio Codespaces

> Visual Studio Codespaces is the easiest way to evaluate ngsa-java.

1. Set up Codespaces from the GitHub repo

2. Create CosmosKey file within the secrets folder

3. Input credentials in CosmosUrl and CosmosKey files within secrets folder

4. Run the application

```bash

# run the application
mvn clean spring-boot:run

```

### Using bash shell

> This will work from a terminal in Visual Studio Codespaces as well

1. Clone the repo

> git clone <https://github.com/retaildevcrews/ngsa-java.git>

2. Create CosmosKey file within secrets folder

3. Input credentials in CosmosUrl and CosmosKey files within secrets folder

4. Run the application

```bash

# run the application
mvn clean spring-boot:run

```

wait for `Netty started on port(s): 8080`

### Testing the application

Open a new bash shell

```bash

# test the application

# test using httpie (installed automatically in Codespaces)
http localhost:8080/version

# test using curl
curl localhost:8080/version

# test using lr
docker run --rm --network=host ghcr.io/retaildevcrews/ngsa-lr:beta -s http://localhost:8080 --max-errors 1 -f baseline.json

```

Stop ngsa by typing Ctrl-C or the stop button if run via F5

#### Run unit tests

```bash

# run unit tests
mvn test -Dmaven.test.skip=false

```

## Deploying With Local Cluster

Ensure to [create secrets](#using-bash-shell) before running the following commands.

```bash
# delete cluster if exists, create cluster, and build/deploy application
make all

# deploy latest changes locally if cluster already exists
make deploy-ngsa-java

# check if cluster and application is deployed
make check
```

### Engineering Docs

- Team Working [Agreement](.github/WorkingAgreement.md)
- Team [Engineering Practices](.github/EngineeringPractices.md)
- CSE Engineering Fundamentals [Playbook](https://github.com/Microsoft/code-with-engineering-playbook)

## How to file issues and get help

This project uses GitHub Issues to track bugs and feature requests. Please search the existing issues before filing new issues to avoid duplicates. For new issues, file your bug or feature request as a new issue.

For help and questions about using this project, please open a GitHub issue.

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us the rights to use your contribution. For details, visit <https://cla.opensource.microsoft.com>

When you submit a pull request, a CLA bot will automatically determine whether you need to provide a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services.

Authorized use of Microsoft trademarks or logos is subject to and must follow [Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).

Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.

Any use of third-party trademarks or logos are subject to those third-party's policies.
