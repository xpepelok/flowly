FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle/ ./
COPY . .

RUN ./gradlew --no-daemon :protos:build
RUN ./gradlew --no-daemon :grpc-client-java:shadowJar
RUN ./gradlew --no-daemon :app:bootJar

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=build /workspace/app/build/libs/*.jar /opt/app/

EXPOSE 8080
CMD sh -c 'exec java -jar "$(ls /opt/app/*.jar | grep -v plain | head -n1)"'
