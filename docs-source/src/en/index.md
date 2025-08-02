---
home: true
title: Home
heroImage: /images/logo.svg
actions:
  - text: Get Started
    link: /en/guide/home
    type: primary
  - text: Changelog
    link: /en/about/changelog
    type: secondary
features:
  - title: Light and Elegant
    details: A powerful, elegant, beautiful API built with Kotlin lambda can help you quickly implement bytecode filtering and reflection functions.
  - title: Fully Compatible
    details: Using native Java APIs to implement reflection functionality, it can be used on any Kotlin on JVM project, and it is no problem on Android.
  - title: Quick to Start
    details: Simple and easy to use right now! No complex configuration or extensive development experience required. Just integrate the dependencies and enjoy!
footer: Apache-2.0 License | Copyright (C) 2019 HighCapable
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