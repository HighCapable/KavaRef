plugins {
    `java-platform`
    alias(libs.plugins.maven.publish)
}

group = gropify.project.groupName
version = gropify.project.kavaref.bom.version

dependencies {
    constraints {
        api(projects.kavarefCore)
        api(projects.kavarefExtension)
    }
}