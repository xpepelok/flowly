# ===== build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# кладём обёртку и конфиги (кэшируется лучше)
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle/ ./
RUN chmod +x ./gradlew

COPY . .

RUN ./gradlew --no-daemon --no-build-cache :protos:build

RUN ./gradlew --no-daemon --no-build-cache :grpc-server-java:clean :grpc-server-java:bootJar -x test

RUN set -eux; \
    JAR_DIR="grpc-server-java/build/libs"; \
    ls -l "$JAR_DIR" || true; \
    JAR="$(ls -1 ${JAR_DIR}/*-boot.jar 2>/dev/null || ls -1 ${JAR_DIR}/*.jar | head -n 1)"; \
    echo "Picked JAR: $JAR"; \
    mkdir -p /dist; \
    cp "$JAR" /dist/server.jar

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=build /dist/server.jar /opt/app/server.jar

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

EXPOSE 9090 9091
ENTRYPOINT ["java","-jar","/opt/app/server.jar"]
