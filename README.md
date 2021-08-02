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

```text

Usage:
        mvn clean spring-boot:run -Dspring-boot.run.arguments=[options] 

Options: 
        --help                                                   Show help and usage information
        --burst-header                                           Enable burst metrics header in healthz and version requests
        --burst-service                                          Service name for bursting metrics (string) [default: ngsa-java]
        --burst-target                                           Target level for bursting metrics (int) [default: 60]
        --burst-max                                              Max level for bursting metrics (int) [default: 80]
        --dry-run                                                Validates configuration
        --log-level=<trace|info|warn|error|fatal>                Log Level [default: Error]
        --version                                                Shows version information       

```

## Run the Application

### Using Visual Studio Codespaces

> Visual Studio Codespaces is the easiest way to evaluate ngsa-java.

1. Set up Codespaces from the GitHub repo

2. Create CosmosKey file within the ngsa/secrets folder

3. Input credentials in CosmosUrl and CosmosKey files within ngsa/secrets folder 

4. Run the application

```bash

# run the application
mvn clean spring-boot:run

```

### Using bash shell

> This will work from a terminal in Visual Studio Codespaces as well

1. Clone the repo

> git clone https://github.com/retaildevcrews/ngsa-java.git

2. Create CosmosKey file within ngsa/secrets folder

3. Input credentials in CosmosUrl and CosmosKey files within ngsa/secrets folder

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

### Run Application Unit Tests

To run unit tests

```bash

mvn clean package -Dmaven.test.skip=false

```

## Contributing

This project welcomes contributions and suggestions. Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit [Microsoft Contributor License Agreement](https://cla.opensource.microsoft.com).

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
