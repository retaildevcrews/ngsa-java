# ----- Base Java - Check Dependencies ----
FROM maven:3.6.3-jdk-11 AS base
WORKDIR /app
ADD pom.xml /app

# Pull the app insights jar into the docker image as it is still in public preview
RUN  wget -O /app/applicationinsights-agent-3.0.0-PREVIEW.2.jar https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.0.0-PREVIEW.2/applicationinsights-agent-3.0.0-PREVIEW.2.jar

#
# ----Build App with Dependencies ----
FROM base AS dependencies
ADD . /app

# While creating the docker container as part of the CI/CD, the managed identity is not set
# and thus many tests will fail, so skipping during docker build. Instead they will run as part
# of the CI/CD.
RUN mvn clean package -DskipTests

#
# ---- Release App ----
FROM  openjdk:11.0-jre-slim AS release
WORKDIR /app

# Create the ngsa user so we can run the app as non-root under ngsa
RUN groupadd -g 4120 ngsa && \
    useradd -u 4120 -g ngsa -s /bin/sh ngsa
USER ngsa

COPY --from=dependencies /app/target/ngsa.jar app.jar
COPY --from=dependencies /app/applicationinsights-agent-3.0.0-PREVIEW.2.jar applicationinsights-agent-3.0.0-PREVIEW.2.jar
EXPOSE 4120
CMD ["java", "-javaagent:/app/applicationinsights-agent-3.0.0-PREVIEW.2.jar", "-jar", "/app/app.jar"]
