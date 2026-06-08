# KavaRef

[![GitHub license](https://img.shields.io/github/license/HighCapable/KavaRef?color=blue&style=flat-square)](https://github.com/HighCapable/KavaRef/blob/main/LICENSE)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/KavaRef)
[![Telegram](https://img.shields.io/badge/discussion%20dev-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/HighCapable_Dev)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=Pnsc5RY6N2mBKFjOLPiYldbAbprAU3V7&jump_from=webapi&authKey=X5EsOVzLXt1dRunge8ryTxDRrh9/IiW1Pua75eDLh9RE3KXE+bwXIYF5cWri/9lf)

<img src="img-src/icon.svg" width = "100" height = "100" alt="LOGO"/>

A modernizing Java Reflection with Kotlin.

English | [简体中文](README-zh-CN.md)

| <img src="https://github.com/HighCapable/.github/blob/main/img-src/logo.jpg?raw=true" width = "30" height = "30" alt="LOGO"/> | [HighCapable](https://github.com/HighCapable) |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|

This project belongs to the organization above. **Click the link to follow us** and discover more awesome projects.

## What's this

This is a modern Java reflection API implemented using Kotlin, designed to provide a cleaner and easier-to-use API while retaining the power of Java
reflection.

The project icon is designed by [MaiTungTM](https://github.com/Lagrio) and is named from **K**otlinJ**avaRef**lection, meaning Java reflection
implemented using Kotlin.

It was firstborn in the [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI), and was later decoupled into
the [YukiReflection](https://github.com/HighCapable/YukiReflection) project.

As you can see, `KavaRef` is an entirely new API set refactored around the design principles of `YukiReflection`. It carries no affiliation and has
officially replaced `YukiReflection` as the new reflection solution.

## Features Overview

> Target Java code

```java
public class World {

    private void sayHello(String content) {
        System.out.println("Hello " + content + "!");
    }
}
```

> Calling with `KavaRef`

```kotlin
val myWorld = World()

World::class.resolve().firstMethod {
    name = "sayHello"
    parameters(String::class)
}.of(myWorld).invoke("KavaRef")
```

## Get Started

| <img src="img-src/icon.svg" width = "30" height = "30" alt="LOGO"/> | [KavaRef Documentation](https://highcapable.github.io/KavaRef/en) |
|---------------------------------------------------------------------|-------------------------------------------------------------------|

You can go to the documentation page for more detailed tutorials and content.

### What's next?

1. **Add dependencies**: Add `kavaref-core` and the corresponding platform modules to your project.
2. **Sync the project**: After a Gradle sync, you can start using `KavaRef`.

In the opened page, select the **Quick Start** section in the sidebar to continue reading.

## More Projects

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h2>Hey, wait a second! 👋</h2>
    <h3>If this project was helpful, why not stick around and check out more of my work below?</h3>
    <h3>Feel free to leave a follow or a star ⭐️ if they bring you value!</h3>
    <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README.md">→ Click here to discover more of my projects ←</a></h1>
</div>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/KavaRef&type=Date)

## Third-Party Open Source Usage Statement

- [Gson](https://github.com/google/gson)

## License

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

Copyright © 2019 HighCapable