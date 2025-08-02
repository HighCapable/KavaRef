# Introduction

> `KavaRef` is a modern Java reflection API implemented using Kotlin.

## Background

This is a modern Java reflection API implemented using Kotlin, designed to provide a cleaner and easier-to-use API while retaining the power of Java
reflection.

The project icon is designed by [MaiTungTM](https://github.com/Lagrio) and is named from **K**otlinJ**avaRef**lection, meaning Java reflection
implemented using Kotlin.

It was first born in the [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI), and was later decoupled into
the [YukiReflection](https://github.com/HighCapable/YukiReflection) project.

As you can see, `KavaRef` is now a completely new set of APIs, completely refactored with the design ideas of `YukiReflection`,
which has no affiliation and will replace `YukiReflection` as a new reflection solution.

If you are using `YukiReflection` or the `YukiHookAPI` project related to it, you can refer to [here](../config/migration) to migrate the reflection API to `KavaRef`.

## Usage

`KavaRef` is built with Kotlin **lambda** syntax in a Java Builder style.

It can replace [Java's native Reflection API](https://www.oracle.com/technical-resources/articles/java/javareflection.html) and implement a more complete reflection solution with a more human-friendly language.

## Skill Requirements

You should be proficient with Java's native reflection APIs, understand Java's class loading mechanisms, bytecode structures, and how they are used in Kotlin (if you are using Kotlin).

## Language Requirement

It is recommended to use Kotlin. API composition also supports Java,
but in pure Java projects, `KavaRef` may not be able to demonstrate its full functionality and syntactic sugar advantages.

All the demo sample code in the documentation will be described using Kotlin first.
If you don't know how to use Kotlin at all, you may not be able to fully experience and use the functionality of `KavaRef`.

## Contribution

The maintenance of this project is inseparable from the support and contributions of all developers.

This project is currently in its early stages, and there may still be some problems or lack of functions you need.

If possible, feel free to submit a PR to contribute features you think are needed to this project or go to [GitHub Issues](repo://issues)
to make suggestions to us.