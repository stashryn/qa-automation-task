FROM debian:bookworm-slim

# Debian's chromium package works on amd64 and arm64, and chromium-driver always matches its version
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-17-jdk-headless maven chromium chromium-driver fonts-liberation ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# see src/test/resources/config.properties; --no-sandbox is needed because Chrome runs as root here
ENV UI_HEADLESS=true \
    BROWSER_BINARY=/usr/bin/chromium \
    CHROMEDRIVER_PATH=/usr/bin/chromedriver \
    CHROME_ARGS=--no-sandbox,--disable-dev-shm-usage

WORKDIR /app

# dependencies first, so this layer is cached until the pom changes
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp test-compile

# extra arguments from `docker run` are passed to Maven, e.g. -Dgroups=api
ENTRYPOINT ["mvn", "-B", "-ntp", "test"]
