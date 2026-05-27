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

    projects(":kavaref-bom") {
        jvm {
            isEnabled = false
        }
    }
}

rootProject.name = "KavaRef"

include(":samples:kavaref-demo")
include(":kavaref-bom")
include(":kavaref-core", ":kavaref-extension", "kavaref-android-stub")