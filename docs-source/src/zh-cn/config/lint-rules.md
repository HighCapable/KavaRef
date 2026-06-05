# Lint 静态检查规范

> 为了帮助开发者在编译期及时发现潜在问题、规范 API 调用，`KavaRef` 内置了一套 Android Lint 规则。

## 规则列表

以下是目前生效的所有 Lint 规则列表 (部分规则仅对 Kotlin 语言生效)。

### kavaref-android

<div class="lint-rules-table">

| Issue ID                                                                                                                                                           | 类别          | 级别      | 优先级 | 简要描述                                     |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------- | --------- | ------ | -------------------------------------------- |
| [UnsupportedExecutableCondition](repo://tree/main/kavaref-android-lint/src/main/java/com/highcapable/kavaref/android/lint/detector/ExecutableConditionDetector.kt) | `CORRECTNESS` | `ERROR`   | `10`   | Unsupported executable condition on Android. |
| [ReplaceWithKavaRefExtension](repo://tree/main/kavaref-android-lint/src/main/java/com/highcapable/kavaref/android/lint/detector/ExtensionUsageDetector.kt)         | `USABILITY`   | `WARNING` | `5`    | Use KavaRef's extension instead.             |

</div>

## 如何关闭或调整规则

如果你想关闭某一条规则，或将某一条规则调整为其它级别，可以在项目根目录创建 `lint.xml`。

> 示例如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<lint>
    <issue id="ReplaceWithKavaRefExtension" severity="ignore" />
    <issue id="UnsupportedExecutableCondition" severity="error" />
</lint>
```

其中 `severity` 常用值如下：

- `ignore`
- `warning`
- `error`
- `fatal`

你也可以直接在 Gradle 中进行控制。

> 示例如下

```kotlin
android {
    lint {
        disable += "ReplaceWithKavaRefExtension"
        error += "UnsupportedExecutableCondition"
    }
}
```

当你只想检查部分规则时，也可以使用 `checkOnly`。

> 示例如下

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

## 问题反馈

目前大部分 Lint 检测器均由 AI Agent 代为完成，可能仍然存在问题，例如尚未测试过的复杂 UAST 语法树场景。如果你在使用过程中发现了 Lint 规则出现了误报或 Quick Fix 无法正确修复问题，可以通过提供 Issue ID 在 [GitHub Issues](repo://issues) 直接向我们反馈。

如果你认为一些规则不够合理出现过于约束代码的情况，或者你有一些新的规则建议，也欢迎向我们反馈，我们会进行评估和调整。