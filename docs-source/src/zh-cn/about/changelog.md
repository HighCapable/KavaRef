# 更新日志

> 这里记录了 `KavaRef` 的版本更新历史。

::: danger

我们只会对最新的 API 版本进行维护，若你正在使用过时的 API 版本则代表你自愿放弃一切维护的可能性。

:::

## kavaref-core

### 1.0.2 | 2025.09.23 &ensp;<Badge type="tip" text="最新" vertical="middle" />

- 移除 `org.slf4j:slf4j-simple` 依赖以修复在 SpringBoot 项目中引起的冲突问题
- 移除已被弃用的 `T.resolve()`  方法避免其污染作用域，如果仍未迁移，请按照文档指引迁移到 `T.asResolver()`

### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- `T.resolve()` 已被弃用，因为其存在命名空间污染问题，现在推荐迁移到 `T.asResolver()`
- 移除了 `KavaRef` 中存在的残留 `block` 方法，如果有用到此类方法，你可以手动使用 `apply` 来实现

### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 首个版本提交至 Maven

## kavaref-extension

### 1.0.2 | 2025.12.13 &ensp;<Badge type="tip" text="最新" vertical="middle" />

- 新增 `TypeRef` 功能，可用于在运行时保留泛型信息

### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 修复 `VariousClass` 中 `loadOrNull` 返回类型是 `Class<*>?` 而不是 `Class<Any>?` 的问题

### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="过旧" vertical="middle" />

- 首个版本提交至 Maven