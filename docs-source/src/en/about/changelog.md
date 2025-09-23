# Changelog

> The version update history of `KavaRef` is recorded here.

::: danger

We will only maintain the latest API version. If you are using an outdated API version, you voluntarily renounce any possibility of maintenance.

:::

::: warning

To avoid translation time consumption, Changelog will use **Google Translation** from **Chinese** to **English**, please refer to the original text for actual reference.

Time zone of version release date: **UTC+8**

:::

## kavaref-core

### 1.0.2 | 2025.09.23 &ensp;<Badge type="tip" text="latest" vertical="middle" />

- Remove the `org.slf4j:slf4j-simple` dependency to fix conflict issues in SpringBoot projects
- Remove the deprecated `T.resolve()` method to avoid its scope contamination. If you still haven't migrated, please follow the documentation instructions to migrate to `T.asResolver()`

### 1.0.1 | 2025.07.06 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- `T.resolve()` has been deprecated because it has namespace pollution problems. It is now recommended to migrate to `T.asResolver()`
- Removed the residual `block` method in `KavaRef`. If this method is used, you can manually implement it with `apply`

### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven

## kavaref-extension

### 1.0.1 | 2025.07.06 &ensp;<Badge type="tip" text="latest" vertical="middle" />

- Fixed an issue where the return type of `loadOrNull` is `Class<*>?` instead of `Class<Any>?` in `VariousClass`

### 1.0.0 | 2025.06.25 &ensp;<Badge type="warning" text="stale" vertical="middle" />

- The first version is submitted to Maven