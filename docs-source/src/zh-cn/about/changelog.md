# 更新日志

> 这里记录了 `KavaRef` 的版本更新历史。

::: danger

我们只会对最新的 API 版本进行维护，若你正在使用过时的 API 版本则代表你自愿放弃一切维护的可能性。

:::

### 1.1.0 | 2026.06.06 &ensp;<Badge type="tip" text="最新" vertical="middle" />

#### kavaref-core

- 新增平台底座接入机制，核心 API 保持不变，使用时需按平台引入 [kavaref-android](../library/kavaref-android.md) 或 [kavaref-jvm](../library/kavaref-jvm.md)
- 新增 `MethodCondition.genericReturnType(...)` 与 `MethodCondition.defaultValue(...)` 条件
- 调整 `ExecutableCondition` 内部实现，避免低版本 Android 直接依赖 `Executable`
- 将成员访问、可访问性、注解与泛型等平台差异能力下沉到平台底座

#### kavaref-android

- 新增 Android 平台底座模块，支持 Min SDK 21 环境下使用 `KavaRef`
- 新增 Android Lint 规则
- 新增 R8/ProGuard 配置与 Android 平台日志实现

#### kavaref-jvm

- 新增 JVM 平台底座模块，提供完整 JVM 反射能力支持
- 新增 JVM 平台日志实现，并保持原有 JVM 使用体验

#### kavaref-extension

- 优化 `createInstance()` 的构造方法参数匹配逻辑，兼容低版本 Android
- 优化 `Type.toClass()` 的类型解析实现

### 1.0.3 | 2026.05.28 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

#### kavaref-core

- 统一 `MethodResolver`、`FieldResolver`、`ConstructorResolver` 的成员可访问性处理逻辑
- 优化 `MemberProcessor` 的注解处理逻辑，并改进条件匹配失败时的提示信息
- 修复可选模式下字符串类型解析失败时可能误匹配 `Any` 的问题

#### kavaref-extension

- 调整 `makeAccessible()` 的行为，现在会返回 `Boolean` 表示是否成功，并支持使用 `trySetAccessible()` 设置可访问性
- 优化 `createInstance()` 的构造方法缓存逻辑，并补充构造方法可访问性校验

## 历史版本

### kavaref-core

#### 1.0.2 | 2025.09.23 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 移除 `org.slf4j:slf4j-simple` 依赖以修复在 SpringBoot 项目中引起的冲突问题
- 移除已被弃用的 `T.resolve()`  方法避免其污染作用域，如果仍未迁移，请按照文档指引迁移到 `T.asResolver()`

#### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- `T.resolve()` 已被弃用，因为其存在命名空间污染问题，现在推荐迁移到 `T.asResolver()`
- 移除了 `KavaRef` 中存在的残留 `block` 方法，如果有用到此类方法，你可以手动使用 `apply` 来实现

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 首个版本提交至 Maven

### kavaref-extension

#### 1.0.2 | 2025.12.13 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 新增 `TypeRef` 功能，可用于在运行时保留泛型信息

#### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 修复 `VariousClass` 中 `loadOrNull` 返回类型是 `Class<*>?` 而不是 `Class<Any>?` 的问题

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 首个版本提交至 Maven