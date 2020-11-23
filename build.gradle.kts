description = "petclinic_Kihoon Kim"
group = "org.springframework.samples"
version = "2.3.0.RC1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10" 
    id("org.springframework.boot") version "2.3.0.RC1"
    id("com.google.cloud.tools.jib") version "2.6.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.3.0.RC1")
    implementation("org.springframework.boot:spring-boot-starter-validation:2.3.0.RC1")
    implementation("org.springframework.boot:spring-boot-starter-cache:2.3.0.RC1")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:2.3.0.RC1")
    implementation("org.springframework.boot:spring-boot-starter-web:2.3.0.RC1")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.3.0.RC1")
    implementation("org.webjars:webjars-locator-core:0.45")
    implementation("org.webjars:jquery:2.2.4")
    implementation("org.webjars:jquery-ui:1.11.4")
    implementation("org.webjars:bootstrap:3.3.6")
    implementation("org.springframework.boot:spring-boot-devtools:2.3.0.RC1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("mysql:mysql-connector-java:8.0.20")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.0.RC1")
    testImplementation("org.testcontainers:mysql:1.14.1")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

jib {
    from {
        image = "openjdk:8-jre-alpine"
    }
    to {
        image = "spring-petclinic-data-jdbc:2.3.0.RC1"
    }
    container {
        ports = listOf("8080")
    }
}