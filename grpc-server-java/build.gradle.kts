plugins {
    `java-library`
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "dev.xpepelok.flowly"
version = "1.0-SNAPSHOT"

dependencies {
    api(libs.springBootStarter)
    api(libs.springBootActuator)
    api(libs.grpcNettyShaded)
    api(libs.grpcStub)
    api(libs.grpcProtobuf)
    api(libs.logback)
    api(libs.mariaDB)
    api(libs.mysqlConnector)
    api(libs.hikari)
    api(projects.stubJava)

    compileOnly(libs.jetBrainsAnnotations)
}

tasks {
    compileJava {
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.encoding = "UTF-8"
    }

    withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        archiveBaseName.set("grpc-server")
        archiveVersion.set("")
        archiveClassifier.set("")
    }

    withType<Jar> {
        enabled = false
    }
}
