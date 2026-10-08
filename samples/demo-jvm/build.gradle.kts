plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.samples.demo.jvm.groupName
version = gropify.project.samples.demo.jvm.version

dependencies {
    implementation(projects.kavarefCore)
    implementation(projects.kavarefJvm)
    implementation(projects.kavarefExtension)

    testImplementation(libs.junit)
}