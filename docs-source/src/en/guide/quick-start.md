# Quick Start

> Integrate `KavaRef` into your project.

## Project Requirements

The project needs to be created using `IntelliJ IDEA` or `Android Studio` and should be a Java or Android
project with integrated Kotlin environment dependencies.

- IntelliJ IDEA (It is recommended to get the latest version from [here](https://www.jetbrains.com/idea))

- Android Studio (It is recommended to get the latest version from [here](https://developer.android.com/studio))

- Kotlin 1.9.0+, Gradle 8+, Java 17+

### Configure Repositories

The dependencies of `KavaRef` are published in **Maven Central** and our public repository,
you can use the following method to configure repositories.

We recommend using Kotlin DSL as the Gradle build script language.

Configure repositories in your project's `build.gradle.kts`.

```kotlin
repositories {
    google()
    mavenCentral()
    // (Optional) You can add this URL to use our public repository
    // When Sonatype-OSS fails and cannot publish dependencies, this repository is added as a backup
    // For details, please visit: https://github.com/HighCapable/maven-repository
    maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
}
```

### Configure Java Version

Modify the Java version of Kotlin in your project's `build.gradle.kts` to 17 or above.

> Java Project

```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}
```

> Android Project

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
```

## Features Overview

The project is divided into multiple modules. You can choose the module you wish to include as a dependency in your project, but be sure to include the **kavaref-core**
module and a platform-related base module (**kavaref-android** or **kavaref-jvm**).

Click the corresponding module below to view detailed feature descriptions.

::: tip Version Notes

Starting from `1.0.3`, `KavaRef` started using unified versioning for releases.
In most cases, you only need to pay attention to the same major version.
You can also refer to the [kavaref-bom](../library/kavaref-bom.md) below to use BOM for unified dependency version management.

For details, please see the [changelog](../about/changelog.md).

:::

- [kavaref-bom](../library/kavaref-bom.md)
- [kavaref-core](../library/kavaref-core.md)
- [kavaref-android](../library/kavaref-android.md)
- [kavaref-jvm](../library/kavaref-jvm.md)
- [kavaref-extension](../library/kavaref-extension.md)

## Demo

You can find some samples [here](repo://tree/main/samples) to view the corresponding demo projects to better understand how these functions work and quickly
select the functions you need.