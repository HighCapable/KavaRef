# kavaref-android

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-android?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-android%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

这是 KavaRef 在 Android 平台的核心底座模块。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [kavaref-bom](./kavaref-bom.md) 使用 BOM 统一管理版本。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
kavaref-android = "<version>"

[libraries]
kavaref-android = { module = "com.highcapable.kavaref:kavaref-android", version.ref = "kavaref-android" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.kavaref.android)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.kavaref:kavaref-android:<version>")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

底座提供了在 Android 平台上使用 KavaRef 的核心功能支持，其中没有公开的调用 API，请参考 [kavaref-core](./kavaref-core.md) 来使用这些功能。