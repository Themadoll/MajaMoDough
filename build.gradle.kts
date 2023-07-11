plugins {
    id("java")
}

group = "org.welcome"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation ("org.javacord:javacord:3.8.0")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}