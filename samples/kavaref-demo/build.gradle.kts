plugins {
    autowire(libs.plugins.kotlin.jvm)
}

group = property.project.samples.kavaref.demo.groupName
version = property.project.samples.kavaref.demo.version

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
    sourceSets.all { languageSettings { languageVersion = "2.0" } }
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }
}

dependencies {
    implementation(projects.kavarefCore)
    implementation(projects.kavarefExtension)

    // SLF4J Simple Logger
    implementation(org.slf4j.slf4j.simple)
}