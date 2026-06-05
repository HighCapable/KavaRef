plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.groupName
version = gropify.project.kavaref.bom.version

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
            kotlin.srcDir("../kavaref-core/src/share/kotlin")
        }
    }
}