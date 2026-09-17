plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "fptu.exe202.signify"
version = "1.0.0"
description = "Consistent API responses and exception handling for Spring Boot MVC"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

}

repositories {
    mavenCentral()
}

val springBootVersion =
    providers.gradleProperty("springBootVersion").getOrElse("3.5.11")

dependencies {
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

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    coordinates(
        "io.github.gwuy.apiresponse",
        "api-response",
        "1.0.0"
    )


    pom {
        name.set("Common Api Response")
        description.set(
            "Standardized API responses and validation error handling for Spring Boot."
        )
        inceptionYear.set("2026")
        url.set("https://github.com/EXE101-Signify/Api-Response")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("GWuy")
                name.set("Bùi Gia Huy")
                url.set("https://github.com/GWuy")
            }
        }

        scm {
            url.set("https://github.com/EXE101-Signify/Api-Response")
            connection.set(
                "scm:git:git://github.com/EXE101-Signify/Api-Response.git"
            )
            developerConnection.set(
                "scm:git:ssh://git@github.com/EXE101-Signify/Api-Response.git"
            )
        }
    }
}

signing {
    useGpgCmd()
}