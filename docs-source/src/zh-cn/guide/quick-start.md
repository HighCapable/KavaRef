# 快速开始

> 集成 `KavaRef` 到你的项目中。

## 项目要求

项目需要使用 `IntelliJ IDEA` 或 `Android Studio` 创建且类型为 Java 或 Android 项目并已集成 Kotlin 环境依赖。

- IntelliJ IDEA (建议从 [这里](https://www.jetbrains.com/idea) 获取最新版本)

- Android Studio (建议从 [这里](https://developer.android.com/studio) 获取最新版本)

- Kotlin 1.9.0+、Gradle 8+、Java 17+

### 配置存储库

`KavaRef` 的依赖发布在 **Maven Central** 和我们的公共存储库中，你可以使用如下方式配置存储库。

我们推荐使用 Kotlin DSL 作为 Gradle 构建脚本语言。

在你的项目 `build.gradle.kts` 中配置存储库。

```kotlin
repositories {
    google()
    mavenCentral()
    // (可选) 你可以添加此 URL 以使用我们的公共存储库
    // 当 Sonatype-OSS 发生故障无法发布依赖时，此存储库作为备选进行添加
    // 详情请前往：https://github.com/HighCapable/maven-repository
    // 中国大陆用户请将下方的 "raw.githubusercontent.com" 修改为 "raw.gitmirror.com"
    maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
}
```

### 配置 Java 版本

在你的项目 `build.gradle.kts` 中修改 Kotlin 的 Java 版本为 17 及以上。

> Java 项目

```kt
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}
```

> Android 项目

```kt
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

## 功能一览

整个项目分为多个模块，你可以选择你希望引入的模块作为依赖应用到你的项目中，但一定要包含 **kavaref-core** 模块。

你可以点击下方对应的模块前往查看详细的功能介绍。

- [kavaref-core](../library/kavaref-core.md)
- [kavaref-extension](../library/kavaref-extension.md)

## Demo

你可以在 [这里](repo://tree/main/samples) 找到一些示例，查看对应的演示项目来更好地了解这些功能的运作方式，快速地挑选出你需要的功能。