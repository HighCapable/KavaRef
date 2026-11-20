---
name: kavaref-usage
description: Use KavaRef for Java reflection in Kotlin on Android and JVM. Find documentation for integrating KavaRef into a project, choosing a base module, finding and using members, loading classes, handling missing members, and migrating from YukiReflection or YukiHook (formerly YukiHookAPI). This skill covers using KavaRef as a dependency.
---

# Use KavaRef

Use this skill when using KavaRef for Java reflection. The [documentation website](https://highcapable.github.io/KavaRef/en) is available for
browsing.

Read the English Markdown sources in `docs-source/src/en/` on the `main` branch of `HighCapable/KavaRef`.
The links below serve the raw `.md` files directly; fetch the relevant chapters without converting HTML.
Resolve relative documentation links against the source file's directory on the same branch.

## Setup

- [Quick Start: requirements, dependencies, and choosing a base module](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/guide/quick-start.md)
- [BOM and dependency versions](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/library/kavaref-bom.md)
- [R8 and ProGuard](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/config/r8-proguard.md)

## Base Modules

- [Android](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/library/kavaref-android.md)
- [JVM](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/library/kavaref-jvm.md)

## Reflection

- [Core library and reflection DSL](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/library/kavaref-core.md)
- [Class, member, and type extensions](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/library/kavaref-extension.md)

When a lookup may find no match, call `optional()` before the lookup. Use `firstMethodOrNull`, `lastFieldOrNull`, or the corresponding `OrNull`
overload when selecting a single result. With the default processor, missing methods and constructors throw `NoSuchMethodException` and missing
fields throw `NoSuchFieldException` unless optional mode is enabled. In optional mode, list lookups return an empty list and `OrNull` lookups return
`null` when no member matches. `optional(silent = true)` also suppresses the warning for a missing member.

## Configuration

- [Third-party member resolvers](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/config/processor-resolvers.md)
- [Third-party member handlers](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/config/resolver-handlers.md)
- [Third-party loggers](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/config/runtime-loggers.md)

## Migration from YukiReflection or YukiHook

For projects moving from YukiReflection or YukiHook (formerly YukiHookAPI) to KavaRef,
read [Migration to KavaRef](https://raw.githubusercontent.com/HighCapable/KavaRef/main/docs-source/src/en/config/migration.md).
Keep using YukiHook's Hook API for existing hooks.