# Lint Rules

> To help developers discover potential problems at compile time and keep API usage consistent, `KavaRef` provides a built-in set of Android Lint rules.

## Rule List

The following are all Lint rules currently in effect (Some rules are only effective for Kotlin language).

### kavaref-android

<div class="lint-rules-table">

| Issue ID                                                                                                                                                           | Category      | Severity  | Priority | Brief Description                            |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------- | --------- | -------- | -------------------------------------------- |
| [UnsupportedExecutableCondition](repo://tree/main/kavaref-android-lint/src/main/java/com/highcapable/kavaref/android/lint/detector/ExecutableConditionDetector.kt) | `CORRECTNESS` | `ERROR`   | `10`     | Unsupported executable condition on Android. |
| [ReplaceWithKavaRefExtension](repo://tree/main/kavaref-android-lint/src/main/java/com/highcapable/kavaref/android/lint/detector/ExtensionUsageDetector.kt)         | `USABILITY`   | `WARNING` | `5`      | Use KavaRef's extension instead.             |

</div>

## How to Disable or Adjust Rules

If you want to disable a specific rule or change its severity, you can create a `lint.xml` file in the project root.

> The following example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <issue id="ReplaceWithKavaRefExtension" severity="ignore" />
    <issue id="UnsupportedExecutableCondition" severity="error" />
</lint>
```

Common `severity` values are:

- `ignore`
- `warning`
- `error`
- `fatal`

You can also control them directly in Gradle.

> The following example

```kotlin
android {
    lint {
        disable += "ReplaceWithKavaRefExtension"
        error += "UnsupportedExecutableCondition"
    }
}
```

If you only want to check part of the rules, you can also use `checkOnly`.

> The following example

```kotlin
android {
    lint {
        checkOnly += setOf(
            "ReplaceWithKavaRefExtension",
            "UnsupportedExecutableCondition"
        )
    }
}
```

## Feedback

Currently, most Lint detectors are completed by AI Agent, and there may still be issues, such as complex UAST scenarios that have not been tested.
If you find that a Lint rule is misreporting or the Quick Fix cannot correctly fix the problem during use, you can provide an Issue ID to directly feedback to us on [GitHub Issues](repo://issues).

If you think some rules are not reasonable and overly restrictive on the code, or if you have some new rule suggestions,
please also feel free to provide feedback to us, and we will evaluate and adjust them.