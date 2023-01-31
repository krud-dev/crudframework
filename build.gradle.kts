plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.7.20"
}

if (hasProperty("release")) {
    val releaseVersion = extra["crudframework.version"].toString()
    subprojects {
        apply(plugin = "java-library")
        apply(plugin = "signing")
        apply(plugin = "maven-publish")
        apply(plugin = "org.jetbrains.dokka")
        group = "dev.krud"
        version = releaseVersion
        val isSnapshot = version.toString().endsWith("-SNAPSHOT")
        val repoUri = if (isSnapshot) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
        }

        if (!isSnapshot) {
            java {
                withJavadocJar()
                withSourcesJar()
            }
        }

        // Hack described in https://github.com/spring-gradle-plugins/dependency-management-plugin/issues/257#issuecomment-895790557
        tasks.withType<GenerateMavenPom>().all {
            doLast {
                val file = File("$buildDir/publications/maven/pom-default.xml")
                var text = file.readText()
                val regex = "(?s)(<dependencyManagement>.+?<dependencies>)(.+?)(</dependencies>.+?</dependencyManagement>)".toRegex()
                val matcher = regex.find(text)
                if (matcher != null) {
                    text = regex.replaceFirst(text, "")
                    val firstDeps = matcher.groups[2]!!.value
                    text = regex.replaceFirst(text, "$1$2$firstDeps$3")
                }
                file.writeText(text)
            }
        }

        publishing {
            publications.create<MavenPublication>("maven") {
                from(components["java"])
                this.version = releaseVersion
                repositories {
                    mavenLocal()
                    maven {
                        name = "OSSRH"
                        url = uri(repoUri)
                        credentials {
                            username = System.getenv("OSSRH_USERNAME") ?: extra["ossrh.username"]?.toString()
                            password = System.getenv("OSSRH_PASSWORD") ?: extra["ossrh.password"]?.toString()
                        }
                    }
                }
                pom {
                    name.set(this@subprojects.name)
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

        if (!isSnapshot) {
            val javadocTask = tasks.named<Javadoc>("javadoc").get()

            tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
                javadocTask.dependsOn(this)
                outputDirectory.set(javadocTask.destinationDir)
            }

            signing {
                sign(publishing.publications["maven"])
            }
        }
    }
}