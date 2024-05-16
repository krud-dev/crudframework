plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.7.20"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

if (hasProperty("release")) {
    val releaseVersion = System.getenv("RELEASE_VERSION")
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    if (!isSnapshot) {
      java {
        withJavadocJar()
        withSourcesJar()
      }
    }
  
    nexusPublishing {
      this@nexusPublishing.repositories {
        sonatype {
          username.set(properties["ossrhUsername"].toString())
          password.set(properties["ossrhPassword"].toString())
          nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
          snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
      }
    }
  
    publishing {
      publications.create<MavenPublication>("maven") {
        artifact(tasks.bootJar.get().archiveFile)
        from(components["java"])
        version = releaseVersion
        pom {
          name = project.name
          version = releaseVersion
          description.set("Spring-powered Crud Framework")
          url.set("https://github.com/krud-dev/crud-framework")
          licenses {
              license {
                  name.set("MIT License")
                  url.set("https://opensource.org/licenses/MIT")
              }
          }

          developers {
              developer {
                  name.set("KRUD")
                  email.set("admin@krud.dev")
                  organization.set("KRUD")
                  organizationUrl.set("https://www.krud.dev")
              }
          }

          scm {
              connection.set("scm:git:git://github.com/krud-dev/crud-framework.git")
              developerConnection.set("scm:git:ssh://git@github.com/krud-dev/crud-framework.git")
              url.set("https://github.com/krud-dev/crud-framework")
          }
        }
      }
    }
  
    val javadocTask = tasks.named<Javadoc>("javadoc").get()
  
    tasks.withType<DokkaTask> {
      javadocTask.dependsOn(this)
      outputDirectory.set(javadocTask.destinationDir)
    }
    if (!isSnapshot) {
      signing {
        val signingKeyBase64: String? by project
        val signingKey = signingKeyBase64?.let { decodeBase64(it) }
        val signingPassword: String? by project
        useInMemoryPgpKeys(
          signingKey,
          signingPassword
        )
        sign(publishing.publications["maven"])
      }
    }
  }
  
  fun decodeBase64(base64: String): String {
    return String(Base64.getDecoder().decode(base64))
  }