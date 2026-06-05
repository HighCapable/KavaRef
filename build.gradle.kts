import com.android.build.api.dsl.CommonExtension
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.maven.publish) apply false
}

val androidApplicationPluginId = libs.plugins.android.application.get().pluginId
val androidLibraryPluginId = libs.plugins.android.library.get().pluginId
val dokkaPluginId = libs.plugins.kotlin.dokka.get().pluginId

allprojects {
    fun Project.configureAndroidJvm() {
        configure<CommonExtension> {
            compileOptions.sourceCompatibility = JavaVersion.VERSION_17
            compileOptions.targetCompatibility = JavaVersion.VERSION_17
        }
    }

    fun KotlinJvmCompilerOptions.configureKotlinJvm() {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }

    plugins.withId(androidLibraryPluginId) {
        configureAndroidJvm()
    }

    plugins.withId(androidApplicationPluginId) {
        configureAndroidJvm()
    }

    plugins.withId("java") {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withSourcesJar()
        }
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            configureKotlinJvm()
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

        configure<MavenPublishBaseExtension> {
            if (name == Libraries.KAVAREF_ANDROID)
                configure(AndroidSingleVariantLibrary(JavadocJar.None(), SourcesJar.Sources()))
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

registerAggregatePublishTask(
    name = "publishBomToMavenLocal",
    description = "Publishes the BOM and all modules to the local Maven cache.",
    taskName = "publishToMavenLocal",
    projectNames = Libraries.entries
)

registerAggregatePublishTask(
    name = "publishBomToMavenCentral",
    description = "Publishes the BOM and all modules to the Maven Central repository.",
    taskName = "publishMavenPublicationToMavenCentralRepository",
    projectNames = Libraries.entries
)

registerAggregatePublishTask(
    name = "publishBomToHighCapableMavenReleases",
    description = "Publishes the BOM and all modules to the HighCapableMavenReleases repository.",
    taskName = "publishAllPublicationsToHighCapableMavenReleasesRepository",
    projectNames = Libraries.entries
)

registerAggregatePublishTask(
    name = "publishBomToHighCapableMavenSnapShots",
    description = "Publishes the BOM and all modules to the HighCapableMavenSnapShots repository.",
    taskName = "publishAllPublicationsToHighCapableMavenSnapShotsRepository",
    projectNames = Libraries.entries
)

fun registerAggregatePublishTask(name: String, description: String, taskName: String, projectNames: List<String>) {
    tasks.register(name) {
        this.group = "publishing"
        this.description = description
        dependsOn(projectNames.map { ":$it:$taskName" })
    }
}

fun libraryProjects(action: Action<in Project>) {
    val libraries = Libraries.entries
    allprojects { if (libraries.contains(name)) action.execute(this) }
}

object Libraries {
    const val KAVAREF_BOM = "kavaref-bom"
    const val KAVAREF_CORE = "kavaref-core"
    const val KAVAREF_ANDROID = "kavaref-android"
    const val KAVAREF_JVM = "kavaref-jvm"
    const val KAVAREF_EXTENSION = "kavaref-extension"

    val entries = listOf(
        KAVAREF_BOM,
        KAVAREF_CORE,
        KAVAREF_ANDROID,
        KAVAREF_JVM,
        KAVAREF_EXTENSION
    )
}