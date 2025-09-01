plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    implementation(libs.springBootStarter)
    implementation(libs.grpcNettyShaded)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobuf)
    implementation(projects.stubJava)
}

tasks {
    compileJava {
        sourceCompatibility = "21"
        targetCompatibility = "21"

        with(options) {
            encoding = "UTF-8"
        }
    }
}