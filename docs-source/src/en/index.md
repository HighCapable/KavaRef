---
layout: home
title: Home
hero:
  name: KavaRef
  tagline: A modernizing Java reflection with Kotlin
  image:
    src: /images/logo.svg
    alt: KavaRef
  actions:
    - text: Get Started
      link: /en/guide/home
      theme: brand
    - text: Changelog
      link: /en/about/changelog
      theme: alt
features:
  - icon: 🪶
    title: Light and Elegant
    details: A powerful, elegant, beautiful API built with Kotlin lambda can help you quickly implement bytecode filtering and reflection functions.
  - icon: 🔄
    title: Fully Compatible
    details: Using native Java APIs to implement reflection functionality, it can be used on any Kotlin on JVM project, and it is no problem on Android.
  - icon: ⚡
    title: Quick to Start
    details: Simple and easy to use right now! No complex configuration or extensive development experience required. Just integrate the dependencies and enjoy!
---

### Start reflecting anytime, anywhere.

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