plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.kavaref.bom.version

dependencies {
    // Android Only
    compileOnly(projects.kavarefAndroidStub)

    implementation(projects.kavarefExtension)
    implementation(libs.slf4j.api)
}