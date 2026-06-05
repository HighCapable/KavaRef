plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.kavaref.bom.version

dependencies {
    implementation(projects.kavarefCore)
    implementation(libs.slf4j.api)
}