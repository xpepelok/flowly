import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf

plugins {
    alias(libs.plugins.grpc)
}

dependencies {
    implementation(libs.grpcProtobuf)
    implementation(libs.grpcStub)
    implementation(libs.protobuf)
    compileOnly(libs.annotationApi)
    protobuf(projects.protos)
}

sourceSets {
    main {
        proto {
            srcDir("build/generated/source/proto/main/grpc")
            srcDir("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}

tasks {
    jar {
        exclude("**/*.proto")
        includeEmptyDirs = false
    }

    copy {
        processResources {
            configurations {
                dependsOn(generateProto)
            }
        }
    }
}