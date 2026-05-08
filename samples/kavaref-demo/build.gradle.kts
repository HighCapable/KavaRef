plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.samples.kavaref.demo.groupName
version = gropify.project.samples.kavaref.demo.version

dependencies {
    implementation(projects.kavarefCore)
    implementation(projects.kavarefExtension)

    // SLF4J Simple Logger
    implementation(libs.slf4j.simple)
}