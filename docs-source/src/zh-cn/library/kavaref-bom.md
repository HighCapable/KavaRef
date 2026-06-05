# kavaref-bom

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-bom?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-bom%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

这是针对 `KavaRef` 相关模块统一版本管理的 BOM 依赖。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

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

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(platform(libs.kavaref.bom))

implementation(libs.kavaref.core)

// 根据你的项目类型选择合适的底座模块 (二选一)
implementation(libs.kavaref.android)
implementation(libs.kavaref.jvm)

implementation(libs.kavaref.extension)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(platform("com.highcapable.kavaref:kavaref-bom:<version>"))

implementation("com.highcapable.kavaref:kavaref-core")

// 根据你的项目类型选择合适的底座模块 (二选一)
implementation("com.highcapable.kavaref:kavaref-android")
implementation("com.highcapable.kavaref:kavaref-jvm")

implementation("com.highcapable.kavaref:kavaref-extension")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

`kavaref-bom` 本身不包含实际代码，它仅作为 `KavaRef` 相关模块的 BOM 用于统一管理依赖版本。

目前它会管理以下模块的版本：

- [kavaref-core](./kavaref-core.md)
- [kavaref-android](./kavaref-android.md)
- [kavaref-jvm](./kavaref-jvm.md)
- [kavaref-extension](./kavaref-extension.md)