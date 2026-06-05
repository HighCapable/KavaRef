plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = gropify.project.groupName

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Lint-Registry-V2" to gropify.project.kavaref.android.lint.registry.v2.clazz
        )
    }
}

dependencies {
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)

    testImplementation(libs.android.lint)
    testImplementation(libs.android.lint.tests)
}