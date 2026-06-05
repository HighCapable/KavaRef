enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.highcapable.gropify") version "1.0.1"
}

gropify {
    global {
        android {
            includeKeys(
                "^project\\..*$".toRegex(),
                "^gradle\\..*$".toRegex()
            )
            isRestrictedAccessEnabled = true
        }
        jvm {
            includeKeys(
                "^project\\..*$".toRegex(),
                "^gradle\\..*$".toRegex()
            )
            isRestrictedAccessEnabled = true
        }
    }

    rootProject {
        common {
            isEnabled = false
        }
    }

    projects(":samples") {
        common {
            isEnabled = false
        }
    }
    projects(
        ":samples:demo-android",
        ":samples:demo-jvm"
    ) {
        android {
            isEnabled = false
        }
        jvm { 
            isEnabled = false
        }
    }

    projects(":kavaref-bom") {
        jvm {
            isEnabled = false
        }
    }

    projects(
        ":kavaref-core",
        ":kavaref-runtime-stub",
        ":kavaref-android",
        ":kavaref-android-lint",
        ":kavaref-jvm",
    ) {
        android {
            className = rootProject.name
        }
        jvm { 
            className = rootProject.name
        }
    }
}

rootProject.name = "KavaRef"

include(":samples:demo-android", ":samples:demo-jvm")
include(":kavaref-bom")
include(":kavaref-core", ":kavaref-extension", ":kavaref-runtime-stub", ":kavaref-android", ":kavaref-jvm")
include(":kavaref-android-lint")