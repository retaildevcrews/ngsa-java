# NGSA Java App

NGSA Java App is inteneded for platform testing and monitoring in one or many Kubernetes clusters and/or cloud deployments.

## Prerequisites

- Bash shell (tested on Visual Studio Codespaces, Mac, Ubuntu, Windows with WSL2)
  - Will not work in Cloud Shell or WSL1
- Azure CLI ([download](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest))
- Docker CLI ([download](https://docs.docker.com/install/))
- Java 11+ ([download](https://www.azul.com/downloads/?package=jdk))
- Maven ([download](https://maven.apache.org/download.cgi))
- Cosmos DB setup (follow the steps in the [imdb readme](https://github.com/cse-labs/imdb) )
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
        --prometheus=<true|false>                            Enable prometheus metrics [default: false]
        --zone                                               Zone for log [default: dev]
        --region                                             Region for log [default: dev]
        --secrets-volume                                     Secrets Volume Path from project root directory [default: secrets]
        --cosmos-auth-type=<ManagedIdentity|SecretKey>       CosmosDB Auth type [default: SecretKey]
Env vars:
        PROMETHEUS=<true|false>
        ZONE
        REGION
        SECRETS_VOLUME
        COSMOS_AUTH_TYPE=<ManagedIdentity|SecretKey>

```

## Run the Application

### Using Visual Studio Codespaces

> Visual Studio Codespaces is the easiest way to evaluate ngsa-java.

1. Set up Codespaces from the GitHub repo

2. Write CosmosDB URL in [secrets/CosmosUrl](./secrets/CosmosUrl)

3. Create `CosmosKey` file in [./secrets](./secrets) folder and write the primary key

   > [Optional]: You can also use Azure CLI to authenticate to CosmosDB. See [this section](#alternative-to-secret-key-cosmosdb-access-using-azure-cli)

4. Run the application
    - From terminal:

        ```bash
        # run the application
        mvn clean spring-boot:run
        ```

    - Codespace/VSCode IDE
      - Wait for the Java Projects to load
        - Goto File Browser (Ctrl+B)
        - Expand Java Projects at the bottom, and wait for `ngsa` project to be available
      - Press F5 or select `Run/Start Debugging`
    > wait for `Netty started on port(s): 8080`

### [Alternative to secret key] CosmosDB access using Azure CLI

We can use Azure CLI to authenticate ourselves to use CosmosDB without using Secret Key for Local Development.

Follow the steps below:

1. Add your AAD user to CosmosDB (in terminal):

    ```bash
    # Login to azure using specific tenant
    az login --tenant <TENANT-ID>
    # Or if you only have one tenant `az login`

    # Select proper subscription/tenant using 
    az account set -s '<SUBSCRIPTION-NAME>'
    
    # Get your own Principal ID (replace the email with yours)
    export PRINCIPAL=$(az ad signed-in-user show --query 'id' -o tsv)
    export COSMOS_RG=<Your-COSMOSDB-Resource-Group>
    export COSMOS_NAME=<Your-COSMOSDB-Name>
    export COSMOS_SCOPE=$(az cosmosdb show -g $COSMOS_RG -n $COSMOS_NAME --query id -o tsv)
    
    # Add yourself to CosmosDB SQL Access
    az cosmosdb sql role assignment create -g $COSMOS_RG --account-name $COSMOS_NAME --role-definition-id 00000000-0000-0000-0000-000000000002 --principal-id $PRINCIPAL --scope $COSMOS_SCOPE
    ```

    > If you're not using Codespace, you also need to add your network's Public IP address to CosmosDB in Azure Portal->CosmosDB->Networking->[FireWall]
    >
    > Google or use `dig +short myip.opendns.com @resolver1.opendns.com` or `curl ifconfig.me` to find your Public IP

2. Goto [src/main/resources/application.properties](./src/main/resources/application.properties) and use `cosmos-auth-type=ManagedIdentity`

3. In `pom.xml` file under `spring-boot-maven-plugin` plugin, set the <AZURE_TENANT_ID> xml value to your `<TENANT-ID>`.

    ```xml
    ...
    <environmentVariables>
      <AZURE_TENANT_ID>TENANT-ID</AZURE_TENANT_ID>
    </environmentVariables>
    ...
    ```

    > To find your tenant ID run, `az account show --query tenantId -o tsv`

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

## Deploying in Local Cluster

Ensure to [create CosmosUrl and CosmosKey (secret key) file](#using-visual-studio-codespaces) before running the following commands.

```bash
# delete cluster if exists, create cluster, and build/deploy application
make all

# deploy latest changes locally if cluster already exists
make deploy-ngsa-java

# check if cluster and application is deployed
make check
```

## Run Checkov scan

- Navigate to `Codespaces main menu` (top left icon with three horizontal lines)
- Click on `Terminal` menu item, then `Run Task`
- From tasks menu locate `Run Checkov Scan` and click on it
- Task terminal will show up executing substasks and indicating when scan completed
- Scan results file `checkov_scan_results` will be created at root level, and automatically will get open by VSCode
- Review the file and evaluate failed checks. For instance:

```bash
  kubernetes scan results:

  Passed checks: 860, Failed checks: 146, Skipped checks: 0
  ...
  ...

  dockerfile scan results:

  Passed checks: 22, Failed checks: 4, Skipped checks: 0

  ...
  ...

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
