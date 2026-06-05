# Changelog

> The version update history of `KavaRef` is recorded here.

::: danger

We will only maintain the latest API version. If you are using an outdated API version, you voluntarily renounce any possibility of maintenance.

:::

### 1.1.0 | 2026.06.06 &ensp;<Badge type="tip" text="latest" vertical="middle" />

#### kavaref-core

- Added platform base module integration while keeping the core API unchanged. Add [kavaref-android](../library/kavaref-android.md) or [kavaref-jvm](../library/kavaref-jvm.md) according to the target platform
- Added `MethodCondition.genericReturnType(...)` and `MethodCondition.defaultValue(...)` conditions
- Adjusted the internal implementation of `ExecutableCondition` to avoid directly depending on `Executable` on lower Android versions
- Moved platform-specific member access, accessibility, annotation, and generic handling to platform base modules

#### kavaref-android

- Added Android platform base module with support for using `KavaRef` in environments with Min SDK 21
- Added Android Lint rules
- Added R8/ProGuard configuration and Android platform logging implementation

#### kavaref-jvm

- Added JVM platform base module with full JVM reflection support
- Added JVM platform logging implementation while keeping the original JVM usage experience

#### kavaref-extension

- Optimized constructor argument matching in `createInstance()` for lower Android version compatibility
- Optimized the type resolution implementation of `Type.toClass()`

### 1.0.3 | 2026.05.28 &ensp;<Badge type="warning" text="stale" vertical="middle" />

#### kavaref-core

- Unified the member accessibility handling of `MethodResolver`, `FieldResolver`, and `ConstructorResolver`
- Optimized annotation handling in `MemberProcessor` and improved condition mismatch messages
- Fixed an issue where failed string type resolution in optional mode could incorrectly match `Any`

#### kavaref-extension

- Adjusted the behavior of `makeAccessible()`, which now returns `Boolean` to indicate whether succeeded, and supports using `trySetAccessible()` to set accessibility
- Optimized the constructor cache logic of `createInstance()` and added constructor accessibility checks

## Historical Versions

### kavaref-core

#### 1.0.2 | 2025.09.23 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Remove the `org.slf4j:slf4j-simple` dependency to fix conflict issues in SpringBoot projects
- Remove the deprecated `T.resolve()` method to avoid its scope contamination. If you still haven't migrated, please follow the documentation instructions to migrate to `T.asResolver()`

#### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- `T.resolve()` has been deprecated because it has namespace pollution problems. It is now recommended to migrate to `T.asResolver()`
- Removed the residual `block` method in `KavaRef`. If this method is used, you can manually implement it with `apply`

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven

### kavaref-extension

#### 1.0.2 | 2025.12.13 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Added `TypeRef` feature, which can be used to preserve generic information at runtime

#### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Fixed an issue where the return type of `loadOrNull` is `Class<*>?` instead of `Class<Any>?` in `VariousClass`

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven