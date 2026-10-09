# Changelog

> The version update history of `KavaRef` is recorded here.

::: danger

We will only maintain the latest API version. If you are using an outdated API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

Time zone of version release date: **UTC+8**

:::

### 1.1.1 | 2026.10.09 &ensp;<Badge type="latest" text="latest" vertical="middle" />

#### kavaref-core

- <u>**⚠️ Breaking Change:**</u> Changed `Configuration.superclass` to the `Superclass` enum
- <u>**⚠️ Breaking Change:**</u> Changed position-sensitive condition properties such as `exceptionTypes` and `genericParameters` to `List`
- <u>**⚠️ Breaking Change:**</u> Changed `annotatedParameterTypes` and `annotatedExceptionTypes` to take annotation sets by position
- Added `superclass(interfaces = true)` to filter members in interfaces
- Added conditions such as `emptyExceptionTypes()` to match empty results
- Added `withHandler()` to customize the invocation and read/write behavior of `MethodResolver`, `ConstructorResolver` and `FieldResolver`
- Changed the type parameters of `MemberResolver` and `InstanceAwareResolver` to include the handler type `H`
- Aligned annotation related conditions and `WildcardTypeMatcher` with Java reflection
- Changed `asResolver()` to always treat the receiver as an instance, use `resolve()` on `KClass` and `Class`
- Changed `createAsType<T>()` to a non-reified function, type parameters of classes can now be passed
- Unified the handling of exceptions thrown by freedom conditions and listed them in the exception message
- Fixed `superclass()` affecting subsequent filters in the same scope and several incorrect condition matches
- Fixed `copy()` and `mergeWith` losing the modifiers condition
- Improved the resolution performance of string type conditions

#### kavaref-android

- Added Lint rule `EmptyConditionArguments`
- Added replacement suggestions for extensions such as `isNotSubclassOf`, `toClassOrNull` and `genericSuperclassTypeArguments` to `ReplaceWithKavaRefExtension`
- Fixed false positives of `ReplaceWithKavaRefExtension` on class literals and `modifiers` of types other than `Member` and `Class`
- Fixed quick fixes of `UnsupportedExecutableCondition` and `ReplaceWithKavaRefExtension` generating invalid code in contexts such as chained calls

#### kavaref-jvm

- Removed the SLF4J dependency to avoid modifying the global logging configuration, logs are now printed to the console by default

#### kavaref-extension

- Changed `createInstance()` to select constructors following Java overload resolution
- Changed `createInstanceAsType<T>()` to a non-reified function, type parameters of classes can now be passed
- Added nullable type and generic array support to `TypeRef`, with built-in R8/ProGuard rules
- Made `LazyClass` thread-safe and cache failed loads
- Improved `makeAccessible()` to skip members that are already accessible
- Fixed `toClass()` initializing classes and `VariousClass` failing to load classes when no `ClassLoader` is passed
- Fixed the `createInstance()` cache preventing `ClassLoader` from being collected
- Fixed modifier extensions such as `isTransient` and `isVolatile` misjudging varargs and bridge methods

### 1.1.0 | 2026.06.06 &ensp;<Badge type="warning" text="stale" vertical="middle" />

#### kavaref-core

- Added platform base module integration while keeping the core API unchanged. Add [kavaref-android](../library/kavaref-android.md) or [kavaref-jvm](../library/kavaref-jvm.md) according to the target platform
- Added `MethodCondition.genericReturnType(...)` and `MethodCondition.defaultValue(...)` conditions
- Adjusted the internal implementation of `ExecutableCondition` to avoid directly depending on `Executable` on lower Android versions
- Moved platform-specific member access, accessibility, annotation, and generic handling to platform base modules

#### kavaref-android

- Added Android platform base module with support for using KavaRef in environments with Min SDK 21
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
- Removed the residual `block` method in KavaRef. If this method is used, you can manually implement it with `apply`

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven

### kavaref-extension

#### 1.0.2 | 2025.12.13 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Added `TypeRef` feature, which can be used to preserve generic information at runtime

#### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- Fixed an issue where the return type of `loadOrNull` is `Class<*>?` instead of `Class<Any>?` in `VariousClass`

#### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven