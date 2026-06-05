plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.samples.demo.jvm.groupName
version = gropify.project.samples.demo.jvm.version

dependencies {
    implementation(projects.kavarefCore)
    implementation(projects.kavarefJvm)
    implementation(projects.kavarefExtension)

    // SLF4J Simple Logger
    implementation(libs.slf4j.simple)
}