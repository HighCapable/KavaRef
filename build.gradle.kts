import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.maven.publish) apply false
}

val dokkaPluginId = libs.plugins.kotlin.dokka.get().pluginId

allprojects {
    plugins.withId("java") {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withSourcesJar()
        }
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.addAll(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions"
            )
        }
    }
}

libraryProjects {
    afterEvaluate {
        configure<PublishingExtension> {
            repositories {
                val repositoryDir = gradle.gradleUserHomeDir
                    .resolve("highcapable-maven-repository")
                    .resolve("repository")

                maven {
                    name = "HighCapableMavenReleases"
                    url = repositoryDir.resolve("releases").toURI()
                }
                maven {
                    name = "HighCapableMavenSnapShots"
                    url = repositoryDir.resolve("snapshots").toURI()
                }
            }
        }
    }

    plugins.withId(dokkaPluginId) {
        configure<DokkaExtension> {
            dokkaPublications.named("html") {
                outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
            }
            pluginsConfiguration.withType<DokkaHtmlPluginParameters>().configureEach {
                footerMessage.set("KavaRef | Apache-2.0 License | Copyright (C) 2019 HighCapable")
            }
        }

        tasks.register("publishKDoc") {
            group = "documentation"
            dependsOn("dokkaGeneratePublicationHtml")

            doLast {
                val docsDir = rootProject.projectDir
                    .resolve("docs-source")
                    .resolve("dist")
                    .resolve("KDoc")
                    .resolve(project.name)

                if (docsDir.exists()) docsDir.deleteRecursively() else docsDir.mkdirs()
                layout.buildDirectory.dir("dokka/html").get().asFile.copyRecursively(docsDir)
            }
        }
    }
}

fun libraryProjects(action: Action<in Project>) {
    val libraries = listOf(
        Libraries.KAVAREF_CORE,
        Libraries.KAVAREF_EXTENSION
    )
    allprojects { if (libraries.contains(name)) action.execute(this) }
}

object Libraries {
    const val KAVAREF_CORE = "kavaref-core"
    const val KAVAREF_EXTENSION = "kavaref-extension"
}