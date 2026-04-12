plugins {
    kotlin("jvm") version "2.3.20"
    groovy
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
    testImplementation("org.apache.groovy:groovy:5.0.5")
    testImplementation("org.spockframework:spock-core:2.4-groovy-5.0")
}

tasks.test {
    useJUnitPlatform()
}
