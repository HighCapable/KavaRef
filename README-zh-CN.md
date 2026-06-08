# KavaRef

[![GitHub license](https://img.shields.io/github/license/HighCapable/KavaRef?color=blue&style=flat-square)](https://github.com/HighCapable/KavaRef/blob/main/LICENSE)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/KavaRef)
[![Telegram](https://img.shields.io/badge/discussion%20dev-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/HighCapable_Dev)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=Pnsc5RY6N2mBKFjOLPiYldbAbprAU3V7&jump_from=webapi&authKey=X5EsOVzLXt1dRunge8ryTxDRrh9/IiW1Pua75eDLh9RE3KXE+bwXIYF5cWri/9lf)

<img src="img-src/icon.svg" width = "100" height = "100" alt="LOGO"/>

一个使用 Kotlin 实现的现代化 Java 反射。

[English](README.md) | 简体中文

| <img src="https://github.com/HighCapable/.github/blob/main/img-src/logo.jpg?raw=true" width = "30" height = "30" alt="LOGO"/> | [HighCapable](https://github.com/HighCapable) |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|

这个项目属于上述组织，**点击上方链接关注这个组织**，发现更多好项目。

## 这是什么

这是一个使用 Kotlin 实现的现代化 Java 反射 API，旨在提供更简洁、更易用的 API，同时保留 Java 反射的强大功能。

项目图标由 [MaiTungTM](https://github.com/Lagrio) 设计，名称取自 **K**otlinJ**avaRef**lection，意为使用 Kotlin 实现的 Java 反射。

它最早诞生于 [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)，后期被解耦合为 [YukiReflection](https://github.com/HighCapable/YukiReflection)
项目。

如你所见，现在 `KavaRef` 是借助 `YukiReflection` 的设计思想完全重构的一套全新 API，它们没有从属关系，并取代 `YukiReflection` 成为一个全新的反射解决方案。

## 功能一览

> 目标 Java 代码

```java
public class World {

    private void sayHello(String content) {
        System.out.println("Hello " + content + "!");
    }
}
```

> 使用 `KavaRef` 调用

```kotlin
val myWorld = World()

World::class.resolve().firstMethod {
    name = "sayHello"
    parameters(String::class)
}.of(myWorld).invoke("KavaRef")
```

## 开始使用

| <img src="img-src/icon.svg" width = "30" height = "30" alt="LOGO"/> | [KavaRef 文档](https://highcapable.github.io/KavaRef/zh-cn) |
|---------------------------------------------------------------------|-----------------------------------------------------------|

你可以前往文档页面查看更多详细教程和内容。

### 下一步做什么？

1. **引入依赖**: 将 `kavaref-core` 和对应平台的模块添加到你的项目中。
2. **同步项目**: 在 Gradle 同步后，你就可以开始使用 `KavaRef` 了。

在打开的页面中，选择侧边栏的 **快速开始** 章节以继续阅读。

## 更多项目

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h2>嘿，还请君留步！👋</h2>
    <h3>如果你觉得这个项目能给你提供帮助，不妨继续往下看看我的更多项目吧！</h3>
    <h3>如果这些项目能为你提供帮助，不妨为我点个关注或者 star ⭐️ 吧！</h3>
    <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README-zh-CN.md">→ 查看更多关于我的项目，请点击这里 ←</a></h1>
</div>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/KavaRef&type=Date)

## 第三方开源使用声明

- [Gson](https://github.com/google/gson)

## 许可证

- [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)

```
Apache License Version 2.0

Copyright (C) 2019 HighCapable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

版权所有 © 2019 HighCapable