enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.8"
}

sweetDependency {
    isEnableVerboseMode = false
}

sweetProperty {
    global {
        sourcesCode {
            includeKeys(
                "^project\\..*\$".toRegex(),
                "^gradle\\..*\$".toRegex()
            )
            isEnableRestrictedAccess = true
        }
    }

    rootProject {
        all {
            isEnable = false
        }
    }
}

rootProject.name = "KavaRef"

include(":kavaref-core", ":kavaref-extension", "kavaref-android-stub")
include(":samples:kavaref-demo")