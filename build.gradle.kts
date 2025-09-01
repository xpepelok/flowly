import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import io.freefair.gradle.plugins.lombok.LombokPlugin

plugins {
    java
    alias(libs.plugins.lombok)
    alias(libs.plugins.shadowJar)
}

allprojects {
    group = "dev.xpepelok.flowly"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    apply<JavaPlugin>()
    apply<LombokPlugin>()
    apply<ShadowPlugin>()

    tasks {
        compileJava {
            sourceCompatibility = "21"
            targetCompatibility = "21"

            with(options) {
                encoding = "UTF-8"
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
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}
