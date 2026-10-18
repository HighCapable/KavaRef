---
layout: home
title: 首页
hero:
  name: KavaRef
  tagline: 一个使用 Kotlin 实现的现代化 Java 反射
  image:
    src: /images/logo.svg
    alt: KavaRef
  actions:
    - text: 立即开始
      link: /zh-cn/guide/home
      theme: brand
    - text: 更新日志
      link: /zh-cn/about/changelog
      theme: alt
features:
  - icon: 🪶
    title: 轻量优雅
    details: 拥有一套强大、优雅、人性化、完全使用 Kotlin DSL 打造的 API，可以帮你快速实现字节码的过滤以及反射功能。
  - icon: 🔄
    title: 全面兼容
    details: 使用原生 Java API 实现反射功能，可在任何 Kotlin on JVM 的项目上使用，在 Android 上使用也丝毫不成问题。
  - icon: ⚡
    title: 快速上手
    details: 简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用。
---

### 随时随地，开始反射。

```java
public class World {

    private void sayHello(String content) {
        System.out.println("Hello " + content + "!");
    }
}
```

```kotlin
val myWorld = World()

World::class.resolve().firstMethod {
    name = "sayHello"
    parameters(String::class)
}.of(myWorld).invoke("KavaRef")
```