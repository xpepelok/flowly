plugins {
    `java-library`
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "dev.xpepelok.flowly"
version = "1.0-SNAPSHOT"

dependencies {
    api(libs.springBootStarter)
    api(libs.springBootStarterWeb)
    api(libs.springBootStarterValidation)
    api(libs.springBootActuator)
    api(libs.grpcNettyShaded)
    api(libs.telegramApi)
    api(libs.slf4j)
    api(libs.logback)
    api(libs.apachePoi)
    api(files("../grpc-client-java/build/libs/grpc-client-java-1.0-SNAPSHOT-all.jar"))
//    api(projects.grpcClientJava)
}

tasks {
    compileJava {
        sourceCompatibility = "21"
        targetCompatibility = "21"

        with(options) {
            encoding = "UTF-8"
            compilerArgs.add("-Xlint:none")
        }
    }

    val resourcesReplaces: Map<String, String> = mapOf(
        "version" to version.toString()
    )

    processResources {
        filter {
            var result = it
            resourcesReplaces.forEach { (key, value) ->
                result = result.replace("\${$key}", value)
            }
            result
        }
    }

    shadowJar {
        from(sourceSets.main.get().output)

        manifest {
            attributes(
                "Main-Class" to "dev.xpepelok.flowly.FlowlyApplication",
                "Encoding" to "UTF-8"
            )
        }

        includeEmptyDirs = false
        mergeServiceFiles()
    }
}