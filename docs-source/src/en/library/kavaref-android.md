# kavaref-android

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-android?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-android%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

This is the core base module for KavaRef on the Android platform.

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you first refer to [kavaref-bom](./kavaref-bom.md) to use BOM for unified version management.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
kavaref-android = "<version>"

[libraries]
kavaref-android = { module = "com.highcapable.kavaref:kavaref-android", version.ref = "kavaref-android" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.kavaref.android)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.kavaref:kavaref-android:<version>")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

The core base module provides essential support for using KavaRef on the Android platform.
It does not expose any public API for direct use.
Please refer to [kavaref-core](./kavaref-core.md) to utilize these functionalities.