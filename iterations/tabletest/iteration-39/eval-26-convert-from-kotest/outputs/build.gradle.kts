plugins {
    kotlin("jvm") version "2.3.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
    testImplementation("org.tabletest:tabletest-junit:1.2.1")
}

tasks.test {
    useJUnitPlatform()
}
