plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

gradlePlugin {
    plugins {
        create("DtoGenerator") {
            id = "org.example.dto-generator"
            implementationClass =
                "org.example.DtoGenerator"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
