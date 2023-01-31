import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Misc
    api("org.jetbrains:annotations:23.0.0")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("io.strikt:strikt-core:0.31.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    constraints {
        // KRUD
        implementation("dev.krud:shapeshift:${Versions.SHAPESHIFT}")
        implementation("dev.krud:spring-boot-starter-shapeshift:${Versions.SHAPESHIFT}")
        implementation("dev.krud:spring-componentmap:${Versions.SPRING_COMPONENTMAP}")

        // Others
        implementation("com.google.code.gson:gson:${Versions.GSON}")
        implementation("com.github.tomas-langer:chalk:${Versions.CHALK}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<BootRun>("bootRun") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

kotlin {
    jvmToolchain(17)
}