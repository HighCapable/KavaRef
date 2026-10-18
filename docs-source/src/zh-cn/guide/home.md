# 介绍

> `KavaRef` 是一个使用 Kotlin 实现的现代化 Java 反射 API。

## 背景

这是一个使用 Kotlin 实现的现代化 Java 反射 API，旨在提供更简洁、更易用的 API，同时保留 Java 反射的强大功能。

项目图标由 [MaiTungTM](https://github.com/Lagrio) 设计，名称取自 **K**otlinJ**avaRef**lection，意为使用 Kotlin 实现的 Java 反射。

它最早诞生于 [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)，后期被解耦合为 [YukiReflection](https://github.com/HighCapable/YukiReflection) 项目。

如你所见，现在 KavaRef 是借助 YukiReflection 的设计思想完全重构的一套全新 API，它们没有从属关系，并将取代 YukiReflection 成为一个全新的反射解决方案。

如果你正在使用 YukiReflection 或与之相关的 YukiHookAPI 项目，你可以参考 [这里](../config/migration) 来迁移反射 API 的写法到 KavaRef。

## 用途

KavaRef 采用 Kotlin DSL 语法与 Java Builder 风格构建。

它能取代 [Java 原生的反射 API](https://pdai.tech/md/java/basic/java-basic-x-reflection.html)，使用更加人性化的语言实现一套更加完善的反射方案。

## 技能要求

你必须已熟练掌握 Java 原生的反射 API，了解 Java 的类加载机制、字节码结构以及它们在 Kotlin 中的用法 (如果你正在使用 Kotlin)。

## 语言要求

推荐使用 Kotlin，API 代码构成同样支持 Java，但是在纯 Java 项目中 KavaRef 有可能无法发挥其全部功能和语法糖优势。

文档全部的 Demo 示例代码都将首先使用 Kotlin 进行描述，如果你完全不会使用 Kotlin 那你将有可能无法更全面地体验和使用 KavaRef 的功能。

## 功能贡献

本项目的维护离不开各位开发者的支持和贡献，目前这个项目处于初期阶段，可能依然存在一些问题或者缺少你需要的功能，
如果可能，欢迎提交 PR 为此项目贡献你认为需要的功能或前往 [GitHub Issues](repo://issues) 向我们提出建议。