# kavaref-core

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-core?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-core%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

这是 KavaRef 的核心依赖，你需要引入此模块才能使用 KavaRef 的基本功能。

## 配置依赖

你可以使用以下方式将此模块添加到你的项目中。

### SweetDependency (推荐)

在你的项目 `SweetDependency` 配置文件中添加依赖。

```yaml
libraries:
  com.highcapable.kavaref:
    kavaref-core:
      version: +
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(com.highcapable.kavaref.kavaref.core)
```

### Version Catalog

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
kavaref-core = "<version>"

[libraries]
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core", version.ref = "kavaref-core" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.kavaref.core)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.kavaref:kavaref-core:<version>")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

你可以 [点击这里](kdoc://kavaref-core) 查看 KDoc。

### 基本用法

KavaRef 采用链式调用的设计方案，它对可用的 Java 反射 API (例如 `Class`) 创建了扩展方法，你只需要对这些内容调用 `resolve()`，即可进入 KavaRef 的世界。

关系图如下。

``` :no-line-numbers
KavaRef
└── KClass/Class.resolve()
    ├── method()
    ├── constructor()
    └── field()
```

接下来，我们将给出多个示例的 Java `Class`，后续都将基于它们进行基本的反射方案讲解。

```java :no-line-numbers
package com.demo;

public class BaseTest {

    public BaseTest() {
        // ...
    }

    private void doBaseTask(String taskName) {
        // ...
    }
}
```

```java :no-line-numbers
package com.demo;

public class Test extends BaseTest {

    private Test() {
        // ...
    }

    private static TAG = "Test";

    private boolean isTaskRunning = false;

    private void doTask(String taskName) {
        // ...
    }

    private void release(String taskName, Function<boolean, String> task, boolean isFinish) {
        // ...
    }

    private void stop() {
        // ...
    }

    private String getName() {
        // ...
    }
}
```

```java
public class Box<T> {

    public void print(T item, String str) {
        // ...
    }
}
```

假设，我们想要得到 `Test` 的 `doTask` 方法并执行，在 KavaRef 中，你可以通过以下方式来实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 通过实例化 Class 的方式对其进行反射
// 在 KavaRef 中，你无需将其转换为 `java.lang.Class`，
// 它会自动调用 KClass.java
Test::class
    // 创建 KavaRef 反射
    .resolve()
    // 创建 Method (方法) 条件
    .method {
        // 设置方法名
        name = "doTask"
        // 设置方法参数类型
        parameters(String::class)
    }
    // 条件执行后会返回匹配到的 List<MethodResolver> 实例
    // 这里我们获取到过滤结果的第一个
    .first()
    // 在 MethodResolver 上设置 Test 的实例
    .of(test)
    // 调用方法并传入参数
    .invoke("task_name")
```

在以上写法中，我们通过 `Test::class.resolve()` 来获取当前 `Class` 的 KavaRef 反射实例，
然后通过 `method { ... }` 来创建一个方法过滤条件 `MethodCondition`，在其中设置方法名和参数类型，执行后返回 `List<MethodResolver>` 实例，
接着我们通过 `first()` 来获取第一个匹配到的 `MethodResolver` 实例，
然后通过 `of(test)` 来设置当前 `Class` 的实例，最后通过 `invoke("task_name")` 来执行方法并传入参数。

在这其中，`MethodCondition` 继承自 `MemberCondition`，它允许你对 `Method` 进行条件筛选，其中包含了 Java 核心的反射 API 的条件镜像，你可以查看对应的注释来了解每个 API 的原生用法。

同样地，`MethodResolver` 继承自 `MemberResolver`，它允许你对过滤结果中的 `Method` 进行反射调用。

由于这里的反射需求是得到一个可用的方法结果，所以 `method { ... }.first()` 的调用链可能来起来会比较繁琐，这个时候就有以下简化方案。

> 示例如下

```kotlin
Test::class
    .resolve()
    // 直接使用 firstMethod 来获取第一个匹配到的 MethodResolver 实例
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }
    .of(test)
    .invoke("task_name")
```

由于我们现在可以拿到 `Test` 的实例，那么还有一种简化写法，你可以直接使用这个实例创建 KavaRef 反射。

> 示例如下

```kotlin
// 在这里，Test 的实例 test 会被传给 KavaRef 并获取 test::class.java
test.asResolver()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    } // 由于你设置了实例，所以这里不再需要 of(test)
    .invoke("task_name")
```

接下来，我们需要得到 `isTaskRunning` 变量，可以写作以下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
val isTaskRunning = test.asResolver()
    .firstField {
        name = "isTaskRunning"
        type = Boolean::class
    }.get<Boolean>()
```

`Test` 中的构造方法是私有化的，现在，我们可以使用以下方式来创建它的实例。

> 示例如下

```kotlin
val test = Test::class.resolve()
    .firstConstructor {
        // 对于零参构造方法，可以使用以下条件过滤
        // 它等价于 parameterCount = 0
        emptyParameters()
    }.create() // 创建一个新的 Test 实例
```

你也可以使用 `createAsType<T>()` 为实际对象 `Test` 指定其超类类型 `BaseTest`。

> 示例如下

```kotlin
val test = Test::class.resolve()
    .firstConstructor {
        emptyParameters()
    }.createAsType<BaseTest>() // 创建一个新的 BaseTest 实例
```

::: tip

除了 `firstMethod` 等方法外，你也可以使用 `lastMethod` 等方法来获取最后一个匹配到的 `MethodResolver` 实例，它等价于 `method { ... }.last()`。

在得到 `MemberResolver` 实例后，你可以使用 `self` 来获取当前 `MemberResolver` 的 `Member` 原始实例来对其进行一些你自己的操作。

在继承于 `InstanceAwareResolver` 的 `MemberResolver` 中 (例如 `MethodResolver` 和 `FieldResolver`)，你都可以使用 `of(instance)`
来设置当前实例，如果反射得到的是静态 (static) 成员，你无需设置实例。

:::

::: warning

`Any.resolve()` 方法已在 `1.0.1` 版本被弃用，因为它会污染命名空间 (例如 `File.resolve("/path/to/file")`)，现在请使用 `Any.asResolver()` 来代替。

:::

::: danger

在继承于 `InstanceAwareResolver` 的 `MemberResolver` 中，`of(instance)` 的类型要求与当前反射的 `Class` 实例泛型类型相同，
除非不指定 `Class` 泛型类型，或将 `Class` 泛型类型设置为 `Any`。

如果 `of(instance)` 出现 `Required: Nothing?` 错误 (这通常由于 `Class` 通过 `Class.forName(...)` 或 `ClassLoader.loadClass(...)` 创建)，
则是你的 `Class` 为 `Class<*>` (Java 中是 `Class<?>`)，此时如果你不想指定类型，请设置或转换为 `Class<Any>`，就像下面这样。

> 示例如下

```kotlin
val myClass = Class.forName("com.xxx.MyClass") as Class<Any>
// 假设这就是这个 Class 的实例
val myClassInstance: Any
myClass.resolve()
    .firstMethod {
        // ...
    }.of(myClassInstance).invoke(...)
```

你也可以使用 [kavaref-extension](kavaref-extension.md) 中提供的 [创建 Class 对象](kavaref-extension.md#创建-class-对象) 来解决这个问题。

:::

### 模糊条件

你会注意到 `Test` 中有一个 `release` 方法，但是它的方法参数很长，而且部分类型可能无法直接得到。

此时，你可以借助 `parameters(...)` 条件使用 `VagueType` 来填充你不想填写的方法参数类型。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .firstMethod {
        name = "release"
        // 使用 VagueType 来填充不想填写的类型，同时保证其它类型能够匹配
        parameters(String::class, VagueType, Boolean::class)
    } // 得到这个方法
```

::: warning

`VagueType` 只能在有多个参数的过滤条件时使用，它不可以在只能设置单个参数的过滤条件中使用，例如 `type`。

你可以使用 `VagueType`、`VagueType::class` 或 `VagueType::class.java` 来创建，它们都能被正确识别为模糊过滤条件。

:::

### 自由条件

在 `MemberCondition` 中，`name`、`type`、`parameterCount` 等条件都可以使用 Kotlin lambda 特性创建自由过滤条件。

假设我们要得到 `Test` 中的 `doTask` 方法，可以使用以下实现。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .firstMethod {
        // 使用 lambda 来设置方法名
        name {
            // 设置名称不区分大小写
            it.equals("dotask", ignoreCase = true)
        }
        // 设置参数类型
        parameters(String::class)
    }.of(test).invoke("task_name")
```

### 泛型条件

KavaRef 支持添加泛型过滤条件，你可以使用 `TypeMatcher` 提供的相关功能来实现。

假设我们需要过滤 `Box<String>` 中的 `print` 方法。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val box: Box<String>
// 使用 KavaRef 调用并执行
box.asResolver()
    .firstMethod {
        name = "print"
        // 设置泛型参数条件
        genericParametes(
            // 过滤泛型名称 "T"
            typeVar("T"),
            // 通过 Class 创建 TypeMatcher
            String::class.toTypeMatcher()
        )
    }.invoke("item", "str")
```

### 在超类过滤

你会注意到 `Test` 继承于 `BaseTest`，现在我们想得到 `BaseTest` 的 `doBaseTask` 方法。

在不知道超类名称的情况下，我们只需要在过滤条件中加入 `superclass()` 即可实现这个功能。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .firstMethod {
        name = "doBaseTask"
        parameters(String::class)
        // 只需要添加这个条件
        superclass()
    }.of(test).invoke("task_name")
```

这个时候我们就可以在超类中获取到这个方法了。

::: tip

`superclass()` 一旦设置就会自动循环向后过滤全部继承的超类中是否有这个方法，直到过滤到目标没有超类 (继承关系为 `java.lang.Object`) 为止。

:::

::: danger

当前过滤的方法除非指定 `superclass()` 条件，否则只能过滤到当前 `Class` 的方法，这是 Java 反射 API 的默认行为，
KavaRef 会调用 `Class.getDeclaredMethods()` 来获取当前 `Class` 的方法而不是 `Class.getMethods()`。

:::

### 更多条件

KavaRef 提供了一些过滤条件来辅助 Java 反射 API 的使用。

假设我们要得到 `Test` 中的静态变量 `TAG` 的内容。

为了体现过滤的条件包含静态描述符 (static)，我们可以使用以下方式来实现。

> 示例如下

```kotlin
val tag = Test::class.resolve()
    .firstField {
        name = "TAG"
        type = String::class
        // 创建描述符过滤
        modifiers(Modifiers.STATIC)
        // 或者
        modifiers {
            it.contains(Modifiers.STATIC)
        }
    }.get<String>() // 获取字段内容
```

你还可以在 `type`、`parameters` 等条件中使用字符串类型传入完整类名。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        // 使用字符串类型传入完整类名
        parameters("java.lang.String")
    }.of(test).invoke("task_name")
```

### 异常处理

在默认情况下，KavaRef 会在反射调用过程中找不到成员时抛出异常。

> 示例如下

```kotlin
Test::class.resolve()
    .method {
        name = "doNonExistentMethod"
    } // 这里会抛出 NoSuchMethodException
```

如果你不希望抛出异常，可以设置可选条件 `optional()`。

> 示例如下

```kotlin
Test::class.resolve()
    // 设置可选条件
    .optional()
    .method {
        name = "doNonExistentMethod"
    } // 返回空的 List<MethodResolver>
```

KavaRef 会打印完整的异常内容以供调试，在使用 `optional()` 时，异常会以 WARN 级别的日志打印。

> 示例如下

``` :no-line-numbers
No method found matching the condition for current class.
+------------------------------------------------+
| class com.demo                                 |
+------------+-----------------------------------+
| name       | doNonExistentMethod               |
| parameters | [class java.lang.String, boolean] |
+------------+-----------------------------------+
```

如果你不希望 KavaRef 抛出或打印任何内容，你可以使用 `optional(silent = true)` 静默化处理，但是我们**不建议这样做**，这会掩盖问题，除非有必要这么做。

::: danger

如果你设置了 `optional()`，那么请不要使用 `firstMethod`、`firstConstructor` 等方法来获取单个结果，
因为它们会在没有结果时抛出列表为空的异常，你可以使用后缀为 `OrNull` 的方法来获取单个结果。

但是这里需要注意一个事情，**如果你没有设置 `optional()`，那么 `firstMethodOrNull` 等方法依然会在没有结果时抛出异常**，这是预期行为，因为 `method { ... }`
才是过滤器的 “构建” 操作，异常在此处理，`firstMethodOrNull` 等方法只是一个封装，是对结果 `List` 是否为空的 Kotlin 自身标准库异常处理，不参与 KavaRef 过滤器的异常处理。

所以你一定要像下面这样做。

> 示例如下

```kotlin
Test::class.resolve()
    // 设置可选条件
    .optional()
    .firstMethodOrNull {
        name = "doNonExistentMethod"
    } // 返回 MethodResolver 或 null
```

:::

### 日志管理

KavaRef 提供了其自身的日志管理功能，你可以通过 `KavaRef.logLevel` 来设置日志级别。

你可以设置 `KavaRef.logLevel = KavaRefRuntime.LogLevel.DEBUG` 来启用 DEBUG 级别的日志使得 KavaRef 在过滤过程向控制台打印更为详细的分步过滤条件日志。

如果你想关闭 KavaRef 的全部日志打印，你可以设置 `KavaRef.logLevel = KavaRefRuntime.LogLevel.OFF`。

::: tip

在 JVM 平台，KavaRef 使用 SLF4J 作为日志打印的实现，你可以通过配置 SLF4J 的实现来控制日志的打印。

默认情况下，你只需要引入 `org.slf4j:slf4j-simple` 作为依赖即可完成日志提供者实现，在 SpringBoot 等项目中，你无需额外配置。

在 Android 平台，KavaRef 使用 `android.util.Log` 作为日志打印的实现，你无需额外配置。

:::

如果你有更高级的需求，你可以实现 `KavaRefRuntime.Logger` 来自定义自己的日志打印方式。

> 示例如下

```kotlin
class MyLogger : KavaRefRuntime.Logger {

    // 在这里可以指定日志打印的标签
    override val tag = "MyLogger"

    override fun debug(msg: Any?, throwable: Throwable?) {
        // 在这里实现你的日志打印逻辑
    }

    override fun info(msg: Any?, throwable: Throwable?) {
        // 在这里实现你的日志打印逻辑
    }

    override fun warn(msg: Any?, throwable: Throwable?) {
        // 在这里实现你的日志打印逻辑
    }

    override fun error(msg: Any?, throwable: Throwable?) {
        // 在这里实现你的日志打印逻辑
    }
}
```

然后，将其设置到 KavaRef 上即可。

> 示例如下

```kotlin
KavaRef.setLogger(MyLogger())
```

### 进阶用法

上述内容讲解的均为标准场景下的使用方法，如果你有更加细粒度的使用场景，你可以手动创建 KavaRef 的相关组件。

如果你不喜欢 Kotlin lambda 的写法，你可以手动创建链式调用。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .method() // 条件开始
    .name("doTask")
    .parameters(String::class)
    .build() // 条件结束 (执行)
    .first()
    .of(test) // 设置实例
    .invoke("task_name")
```

你还可以手动创建任何过滤条件以实现在任何反射中复用它。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 手动创建 MethodCondition
val condition = MethodCondition<Test>()
condition.name = "doTask"
condition.parameters(String::class)
// 应用条件到反射对象
Test::class.resolve()
    .firstMethod(condition)
    .of(test) // 设置实例
    .invoke("task_name")
```

或者，你还可以手动完整地实现整个反射过程。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 手动创建 MethodCondition
val condition = MethodCondition<Test>()
condition.name = "doTask"
condition.parameters(String::class)
// 手动创建 MemberCondition.Configuration
val configuration = Test::class.java.createConfiguration(
    memberInstance = test, // 设置实例
    processorResolver = null, // 使用默认的解析器，可参考下方的 "自定义解析器"
    superclass = false, // 是否在超类中过滤
    optional = MemberCondition.Configuration.Optional.NO // 配置可选条件
)
// 创建并开始过滤
val resolvers = condition.build(configuration)
// 获取第一个结果
val resolver = resolvers.first()
// 执行方法
resolver.invoke("task_name")
```

如果你对业务层逻辑有更高级的需求，你还可以使用 `mergeWith` 来合并多个过滤条件。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 手动创建 MethodCondition
// 创建第一个条件
val condition1 = MethodCondition<Test>()
condition1.name = "doTask"
// 创建第二个条件
val condition2 = MethodCondition<Test>()
condition2.parameters(String::class)
// 将 condition2 合并到 condition1 中
// 此时 condition1 的条件将包含 condition2 不为 null 的条件，
// condition1 中重复的条件将被 condition2 的条件覆盖
condition1.mergeWith(condition2)
// 你还可以使用 infix 语法
condition1 mergeWith condition2
// 使用 KavaRef 调用并执行
Test::class.resolve()
    .firstMethod(condition1)
    .of(test)
    .invoke("task_name")
```

::: danger

当 `MemberCondition` 已经设置 `MemberCondition.Configuration` 时将不再允许重复使用 `build(...)` 进行创建，
此时你需要使用 `copy()` 来复制并创建一份新的 `MemberCondition`。

同样地，`InstanceAwareResolver` 在通过 `MemberCondition.Configuration.memberInstance` 或 `of(instance)` 设置实例后也不允许重复设置新的实例，
此时你也需要使用 `copy()` 来复制并创建一份新的 `InstanceAwareResolver`。

:::

### 自定义解析器

KavaRef 使用默认的 `Member` 解析器进行过滤操作，如果你想实现自己的解析器，你可以自定义全局和每一个反射过程使用的解析器。

你可以继承于 `MemberProccessor.Resolver` 来实现自己的解析器。

> 示例如下

```kotlin
class MyMemberProcessorResolver : MemberProcessor.Resolver() {

    override fun <T : Any> getDeclaredConstructors(declaringClass: Class<T>): List<Constructor<T>> {
        // 在这里拦截并实现你的构造方法过滤逻辑
        return super.getDeclaredConstructors(declaringClass)
    }

    override fun <T : Any> getDeclaredMethods(declaringClass: Class<T>): List<Method> {
        // 在这里拦截并实现你的方法过滤逻辑
        return super.getDeclaredMethods(declaringClass)
    }

    override fun <T : Any> getDeclaredFields(declaringClass: Class<T>): List<Field> {
        // 在这里拦截并实现你的字段过滤逻辑
        return super.getDeclaredFields(declaringClass)
    }
}
```

然后你可以将其设置到全局配置中。

> 示例如下

```kotlin
MemberProcessor.globalResolver = MyMemberProcessorResolver()
```

或者，在每次反射过程中，你可以使用 `MemberCondition.Configuration` 来设置自定义解析器，或者使用链式调用设置解析器。

> 示例如下

```kotlin
// 创建解析器
val myResolver = MyMemberProcessorResolver()
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
Test::class.resolve()
    // 设置自定义解析器
    .processor(myResolver)
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test).invoke("task_name")
```

::: tip

你可以在 [这里](../config/processor-resolvers.md) 找到一些公开维护的自定义解析器，定义在你的项目中即可使用。

:::

### 关于缓存

由于过滤条件的多样性，KavaRef 不直接提供缓存功能，根据每个开发者的实现方式不同，缓存的实现方式也会有所不同。

我们建议手动对过滤结果创建的 `MemberResolver` 实现缓存以提高性能并参考 [手动创建](#手动创建) 拆分过滤条件以优化代码复用率。

::: danger

如果你使用了 `val myResolver by lazy { ... }` 来实现缓存，例如下方这样做。

> 示例如下

```kotlin
val myResolver by lazy {
    Test::class.resolve()
        .firstMethod {
            name = "doTask"
            parameters(String::class)
        }
}
```

你在调用时可能会这样做。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
myResolver.of(test).invoke("task_name")
```

请注意，由于 `MemberResolver` 已被缓存，在你每次引用它时调用的是同一个实例，而 `MemberResolver` 的实例对象不允许重复设置 (参考 [手动创建](#手动创建) 下方的 “特别注意”)，
所以直接这样调用会抛出异常，你需要改为以下形式。

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 使用 KavaRef 调用并执行
myResolver.copy().of(test).invoke("task_name")
```

这样一来，你就可以在每次调用时复制一个新的 `MemberResolver` 实例而不需要重复反射过程，也不会抛出异常。

:::

### Java 用法

KavaRef 不推荐直接在 Java 中使用，因为它的 API 设计是基于 Kotlin 的特性和语法糖。

如果你需要在 Java 中使用 KavaRef，你可以使用以下方式来实现。

> 示例如下

```java
public class Main {

    public static void main(String[] args) {
        // 假设这就是这个 Class 的实例
        Test test;
        // 使用 KavaRef 调用并执行
        KavaRef.resolveClass(Test.class)
            .method()
            .name("doTask")
            .parameters(String.class)
            .build()
            .get(0)
            .of(test)
            .invoke("task_name");
        // 或者，使用实例创建 KavaRef 反射
        KavaRef.resolveObject(test)
            .method()
            .name("doTask")
            .parameters(String.class)
            .build()
            .get(0)
            .invoke("task_name");
    }
}
```