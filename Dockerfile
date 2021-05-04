# ----- Base Java - Check Dependencies ----
FROM maven:3.6.3-jdk-11 AS base
WORKDIR /app
ADD pom.xml /app

#
# ----Build App with Dependencies ----
FROM base AS dependencies
ADD . /app

RUN echo "Breakpoint" && ls && ls ./secrets && cat ./secrets/*
#RUN mvn clean package

#
# ---- Release App ----
FROM  openjdk:11.0-jre-slim AS release
WORKDIR /app

# Create the ngsa user so we can run the app as non-root under ngsa
RUN groupadd -g 4120 ngsa && \
    useradd -u 4120 -g ngsa -s /bin/sh ngsa
USER ngsa

COPY --from=dependencies /app/target/ngsa.jar app.jar
EXPOSE 4120
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
