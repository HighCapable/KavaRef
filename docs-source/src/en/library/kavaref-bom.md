# kavaref-bom

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-bom?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-bom%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

This is the BOM dependency for unified version management of `KavaRef` related modules.

## Configure Dependency

You can add this module to your project using the following method.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
kavaref-bom = "<version>"

[libraries]
kavaref-bom = { module = "com.highcapable.kavaref:kavaref-bom", version.ref = "kavaref-bom" }
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core" }
kavaref-android = { module = "com.highcapable.kavaref:kavaref-android" }
kavaref-jvm = { module = "com.highcapable.kavaref:kavaref-jvm" }
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(platform(libs.kavaref.bom))

implementation(libs.kavaref.core)

// Choose the appropriate base module according to your project type (choose one).
implementation(libs.kavaref.android)
implementation(libs.kavaref.jvm)

implementation(libs.kavaref.extension)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(platform("com.highcapable.kavaref:kavaref-bom:<version>"))

implementation("com.highcapable.kavaref:kavaref-core")

// Choose the appropriate base module according to your project type (choose one).
implementation("com.highcapable.kavaref:kavaref-android")
implementation("com.highcapable.kavaref:kavaref-jvm")

implementation("com.highcapable.kavaref:kavaref-extension")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

`kavaref-bom` does not contain actual code itself. It only serves as the BOM of `KavaRef` related modules for unified dependency version management.

It currently manages the versions of the following modules:

- [kavaref-core](./kavaref-core.md)
- [kavaref-android](./kavaref-android.md)
- [kavaref-jvm](./kavaref-jvm.md)
- [kavaref-extension](./kavaref-extension.md)