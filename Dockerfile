# ----- Base Java - Check Dependencies ----
#checkov:skip=CKV_DOCKER_2: No healthcheck is needed
FROM azul/zulu-openjdk-alpine:11.0.16 AS base

RUN wget https://get.sdkman.io
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"

WORKDIR /app
ARG MAVEN_VERSION=3.6.3

# Install Maven
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -xf apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    mv apache-maven-${MAVEN_VERSION}/ apache-maven/

ENV PATH=/app/apache-maven/bin:${PATH}

#
# ----Build App with Dependencies ----
FROM base AS dependencies
COPY . /app

RUN mvn clean package -DskipTests --no-transfer-progress

#
# ---- Release App ----
FROM  azul/zulu-openjdk-alpine:11.0.16-jre AS release
WORKDIR /app

# Create the ngsa user so we can run the app as non-root under ngsa
RUN addgroup -g 4120 ngsa && \
    adduser -u 4120 -G ngsa -h /home/ngsa -D ngsa

USER ngsa

COPY --from=dependencies /app/target/ngsa.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
