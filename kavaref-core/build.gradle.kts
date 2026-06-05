plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.kavaref.bom.version

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
            kotlin.srcDir("src/share/kotlin")
        }
    }
}

dependencies {
    compileOnly(projects.kavarefRuntimeStub)
    implementation(projects.kavarefExtension)
}