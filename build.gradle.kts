plugins {
    `java-library`
    `maven-publish`
}

group = "fptu.exe202.signify"
version = "1.0.0"
description = "Consistent API responses and exception handling for Spring Boot MVC"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(17) }
    withSourcesJar()
    withJavadocJar()
}

repositories { mavenCentral() }

val springBootVersion = providers.gradleProperty("springBootVersion").getOrElse("3.5.11")

dependencies {
    // A regular platform supplies defaults without forcing versions on consumers.
    api(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    api("org.springframework:spring-web")
    api("com.fasterxml.jackson.core:jackson-annotations")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webmvc")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.slf4j:slf4j-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework.security:spring-security-core")

    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-web")
    testImplementation("org.springframework.security:spring-security-config")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
    options.compilerArgs.add("-parameters")
}
tasks.withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
tasks.test { useJUnitPlatform() }

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "api-response"
            pom {
                name.set("api-response")
                description.set(project.description)
            }
        }
    }
}
