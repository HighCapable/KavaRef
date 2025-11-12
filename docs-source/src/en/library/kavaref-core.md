# kavaref-core

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-core?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-core%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

This is the core dependency of KavaRef, and you need to introduce this module to use the basic features of KavaRef.

## Configure Dependency

You can add this module to your project using the following method.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
kavaref-core = "<version>"

[libraries]
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core", version.ref = "kavaref-core" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.kavaref.core)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.kavaref:kavaref-core:<version>")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

You can view the KDoc [click here](kdoc://kavaref-core).

### Basic Usage

KavaRef adopts a chained call design, which creates extension methods for available Java reflection APIs (such as `Class`).
You only need to call `resolve()` to these contents to enter the world of KavaRef.

The relationship diagram is as follows.

``` :no-line-numbers
KavaRef
└── KClass/Class.resolve()
    ├── method()
    ├── constructor()
    └── field()
```

Next, we will give multiple examples of Java `Class`, which will be explained based on them in the future.

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

Suppose we want to get the `doTask` method of `Test` and execute it. In KavaRef, you can do it in the following way.

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Reflect it by instantiating Class.
// In KavaRef you don't need to convert it to `java.lang.Class`,
// it will automatically call KClass.java.
Test::class
    // Create KavaRef reflections.
    .resolve()
    // Create method condition.
    .method {
        // Set method name.
        name = "doTask"
        // Set method parameter type.
        parameters(String::class)
    }
    // After the condition is executed, the matching List<MethodResolver>
    // instance will be returned.
    // Here we get the first filtering result.
    .first()
    // Set an instance of Test on MethodResolver.
    .of(test)
    // Call the method and pass in parameters.
    .invoke("task_name")
```

In the above writing method, we use `Test::class.resolve()` to get the KavaRef reflection instance of the current `Class`.
Then create a method filtering condition `MethodCondition` using `method { ... }`, which sets the method name and parameter type, and returns the `List<MethodResolver>` instance after execution.
Then we use `first()` to get the first matched `MethodResolver` instance,
then use `of(test)` to set the current instance of `Class`, and finally use `invoke("task_name")` to execute the method and pass in the parameters.

In this, MethodCondition is inherited from MemberCondition, which allows you to conditionally filter the Method, containing a conditional image of the Java-core reflection API.
You can view the corresponding comments to understand the native usage of each API.

Similarly, MethodResolver is inherited from `MemberResolver`, which allows you to make reflection calls to `Method` in the filtered result.

Since the reflection requirement here is to obtain an available method result, the call chain of `method { ... }.first()` may be more cumbersome,
so there is the following simplified solution.

> The following example

```kotlin
Test::class
    .resolve()
    // Use firstMethod directly to get the first matching
    // MethodResolver instance.
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }
    .of(test)
    .invoke("task_name")
```

Since we can now get an instance of `Test`, there is also a simplified way to write this:
you can use this instance to create KavaRef reflections directly.

> The following example

```kotlin
// Here, the test instance test will be passed to
// KavaRef and get test::class.java.
test.asResolver()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    } // Since you set up an instance, of(test) is no longer needed here.
    .invoke("task_name")
```

Next, we need to get the `isTaskRunning` variable, which can be written in the following form.

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Call and execute with KavaRef.
val isTaskRunning = test.asResolver()
    .firstField {
        name = "isTaskRunning"
        type = Boolean::class
    }.get<Boolean>()
```

The constructor in `Test` is private, and now we can create an instance of it using the following method.

> The following example

```kotlin
val test = Test::class.resolve()
    .firstConstructor {
        // For the zero parameter constructor,
        // the following condition can be used to filter.
        // It is equivalent to parameterCount = 0.
        emptyParameters()
    }.create() // Create a new Test instance.
```

You can also use `createAsType<T>()` to specify its superclass type `BaseTest` for the actual object `Test`.

> The following example

```kotlin
val test = Test::class.resolve()
    .firstConstructor {
        emptyParameters()
    }.createAsType<BaseTest>() // Create a new BaseTest instance.
```

::: tip

In addition to methods such as `firstMethod`, you can also use methods such as `lastMethod` to get the last matching `MethodResolver` instance, which is equivalent to `method { ... }.last()`.

After you get the `MemberResolver` instance, you can use `self` to get the `Member` original instance of the current `MemberResolver` to do some of your own operations.

In `MemberResolver` inherited from `InstanceAwareResolver` (for example `MethodResolver` and `FieldResolver`), you can use `of(instance)`
to set the current instance. If the reflection is a static member, you do not need to set the instance.

:::

::: warning

The `Any.resolve()` function has been deprecated in version `1.0.1` because it pollutes the namespace (for example `File.resolve("/path/to/file")`), and now use `Any.asResolver()` instead.

:::

::: danger

In `MemberResolver` inherited from `InstanceAwareResolver`, the type of `of(instance)` requires the same type as the currently reflected `Class` instance generic type.
Unless the `Class` generic type is not specified, or the `Class` generic type is set to `Any`.

If `of(instance)` shows a `Required: Nothing?` error (this is usually due to `Class` created via `Class.forName(...)` or `ClassLoader.loadClass(...)`),
then your `Class` is `Class<*>` (in Java it is `Class<?>`).
At this time, if you do not want to specify the type, please set or convert it to `Class<Any>`, as follows.

> The following example

```kotlin
val myClass = Class.forName("com.xxx.MyClass") as Class<Any>
// Suppose this is an instance of this Class.
val myClassInstance: Any
myClass.resolve()
    .firstMethod {
        // ...
    }.of(myClassInstance).invoke(...)
```

You can also use [Create Class Object](kavaref-extension.md#create-class-object) provided in [kavaref-extension](kavaref-extension.md) to solve this problem.

:::

### Vague Conditions

You will notice that there is a `release` method in `Test`, but its method parameters are very long and some types may not be directly obtainable.

At this point, you can use the `parameters(...)` condition with `VagueType` to fill in the method parameter types you don't want to specify.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Call and execute with KavaRef.
Test::class.resolve()
    .firstMethod {
        name = "release"
        // Use VagueType to fill in the types you don't want to fill in,
        // and ensure that other types can match.
        parameters(String::class, VagueType, Boolean::class)
    } // Get this method.
```

::: warning

`VagueType` can only be used when there are filter conditions with multiple parameters,
it cannot be used in filter conditions with only a single parameter, such as `type`.

You can create it using `VagueType`, `VagueType::class` or `VagueType::class.java`, all of which are correctly recognized as fuzzy filtering conditions.

:::

### Freedom Conditions

In `MemberCondition`, `name`, `type`, `parameterCount` and other conditions can all use the Kotlin lambda feature to create free filtering conditions.

Suppose we want to get the `doTask` method in `Test`, we can use the following implementation.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Call and execute with KavaRef.
Test::class.resolve()
    .firstMethod {
        // Use lambda to set the method name.
        name {
            // Set the name not case sensitive.
            it.equals("dotask", ignoreCase = true)
        }
        // Set parameter type.
        parameters(String::class)
    }.of(test).invoke("task_name")
```

### Generic Conditions

KavaRef supports adding generic filtering conditions, which you can use the relevant functions provided by `TypeMatcher`.

Suppose we need to filter the `print` method in `Box<String>`.

> The following example

```kotlin
// Suppose this is an example of this Class.
val box: Box<String>
// Call and execute with KavaRef.
box.asResolver()
    .firstMethod {
        name = "print"
        // Set generic parameter conditions.
        genericParametes(
            // Filter generic name "T".
            typeVar("T"),
            // Create TypeMatcher through Class.
            String::class.toTypeMatcher()
        )
    }.invoke("item", "str")
```

### Filter in Superclass

You will notice that `Test` inherits from `BaseTest`, and now we want to get the `doBaseTask` method of `BaseTest`.

Without knowing the superclass name, we only need to add `superclass()` to the filter condition to achieve this function.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Call and execute with KavaRef.
Test::class.resolve()
    .firstMethod {
        name = "doBaseTask"
        parameters(String::class)
        // Just add this condition.
        superclass()
    }.of(test).invoke("task_name")
```

At this time, we can get this method in the superclass.

::: tip

`superclass()` once set it,it will automatically loop backwards whether there is this method in all inherited
superclasses until the target has no superclass (the inheritance relationship is `java.lang.Object`).

:::

::: danger

The current filtering method can only filter to the current `Class` method unless the `superclass()` condition is specified,
which is the default behavior of the Java reflection API.
KavaRef will call `Class.getDeclaredMethods()` to get the current `Class` method instead of `Class.getMethods()`.

:::

### Other Conditions

KavaRef provides some filtering conditions to assist in the use of the Java reflection API.

Suppose we want to get the contents of the static variable `TAG` in `Test`.

In order to reflect that the filtering conditions include static descriptors, we can implement them using the following methods.

> The following example

```kotlin
val tag = Test::class.resolve()
    .firstField {
        name = "TAG"
        type = String::class
        // Create descriptor filtering.
        modifiers(Modifiers.STATIC)
        // Or.
        modifiers {
            it.contains(Modifiers.STATIC)
        }
    }.get<String>() // Get field content.
```

You can also use string types to pass in full class names in conditions such as `type`, `parameters`, etc.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Call and execute with KavaRef.
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        // Pass the full class name using string type.
        parameters("java.lang.String")
    }.of(test).invoke("task_name")
```

### Exception Handling

By default, KavaRef throws an exception when a member is not found during a reflection call.

> The following example

```kotlin
Test::class.resolve()
    .method {
        name = "doNonExistentMethod"
    } // NoSuchMethodException will be thrown here.
```

If you do not want an exception to be thrown, you can set the optional condition `optional()`.

> The following example

```kotlin
Test::class.resolve()
    // Set optional conditions.
    .optional()
    .method {
        name = "doNonExistentMethod"
        
    } // Return empty List<MethodResolver>.
```

KavaRef prints the complete exception content for debugging, and when using `optional()`, the exception is printed as a WARN level log.

> The following example

``` :no-line-numbers
No method found matching the condition for current class.
+------------------------------------------------+
| class com.demo                                 |
+------------+-----------------------------------+
| name       | doNonExistentMethod               |
| parameters | [class java.lang.String, boolean] |
+------------+-----------------------------------+
```

If you don't want KavaRef to throw or print anything, you can use `optional(silent = true)` to silently handle it,
but we **do not recommend this**, which will mask the problem unless it is necessary.

::: danger

If you set `optional()`, please do not use `firstMethod`, `firstConstructor` and other methods to get a single result.
Because they throw an exception with empty list when there is no result, you can use the method with the suffix `OrNull` to get a single result.

But one thing you need to pay attention to here is that if you do not set `optional()`,
then methods such as `firstMethodOrNull` will still throw exceptions when there is no result**, which is the expected behavior because `method { ... }`
This is the "build" operation of the filter. Exceptions are handled here.
Methods such as `firstMethodOrNull` are just an encapsulation.
It is an exception handling of Kotlin's own standard library whether the result `List` is empty. It does not participate in exception handling of KavaRef filters.

So you must do it like the following.

> The following example

```kotlin
Test::class.resolve()
    // Set optional conditions.
    .optional()
    .firstMethodOrNull {
        name = "doNonExistentMethod"
    } // Return MethodResolver or null.
```

:::

### Log Management

KavaRef provides its own log management function, you can set the log level through `KavaRef.logLevel`.

You can set `KavaRef.logLevel = KavaRefRuntime.LogLevel.DEBUG` to enable DEBUG level logs so that KavaRef
prints more detailed step-by-step filtering condition logs to the console during the filtering process.

If you want to turn off all log printing of KavaRef, you can set `KavaRef.logLevel = KavaRefRuntime.LogLevel.OFF`.

::: tip

On the JVM platform, KavaRef uses SLF4J as the implementation of log printing, and you can control log printing by configuring the implementation of SLF4J.

By default, you only need to introduce `org.slf4j:slf4j-simple` as a dependency to complete the log provider implementation, and in projects such as SpringBoot, you do not need additional configuration.

On the Android platform, KavaRef uses `android.util.Log` as an implementation of log printing, and you don't need additional configuration.

:::

If you have more advanced requirements, you can implement `KavaRefRuntime.Logger` to customize your own log printing method.

> The following example

```kotlin
class MyLogger : KavaRefRuntime.Logger {

    // Here you can specify the tag for log printing.
    override val tag = "MyLogger"

    override fun debug(msg: Any?, throwable: Throwable?) {
        // Implement your log printing logic here.
    }

    override fun info(msg: Any?, throwable: Throwable?) {
        // Implement your log printing logic here.
    }

    override fun warn(msg: Any?, throwable: Throwable?) {
        // Implement your log printing logic here.
    }

    override fun error(msg: Any?, throwable: Throwable?) {
        // Implement your log printing logic here.
    }
}
```

Then, set it to KavaRef.

> The following example

```kotlin
KavaRef.setLogger(MyLogger())
```

### Advanced Usage

The above content explains all the usage methods in standard scenarios.
If you have a more fine-grained usage scenario, you can manually create related components of KavaRef.

If you don't like the Kotlin lambda writing, you can create chained calls manually.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Call and execute with KavaRef.
Test::class.resolve()
    .method() // Conditions begin.
    .name("doTask")
    .parameters(String::class)
    .build() // Conditions ends (executes)
    .first()
    .of(test) // Setting up instance.
    .invoke("task_name")
```

You can also manually create any filtering conditions to achieve multiplexing it in any reflection.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Create MethodCondition manually.
val condition = MethodCondition<Test>()
condition.name = "doTask"
condition.parameters(String::class)
// Apply condition to reflection object.
Test::class.resolve()
    .firstMethod(condition)
    .of(test) // Setting up instance.
    .invoke("task_name")
```

Alternatively, you can also manually and completely implement the entire reflection process.

> The following example

```kotlin
// Suppose this is an example of this Class.
val test: Test
// Create MethodCondition manually.
val condition = MethodCondition<Test>()
condition.name = "doTask"
condition.parameters(String::class)
// Create MemberCondition.Configuration manually.
val configuration = Test::class.java.createConfiguration(
    memberInstance = test, // Setting up instance.
    processorResolver = null, // Use the default resolver, refer to the "Custom Resolver" below.
    superclass = false, // Whether to filter in superclass.
    optional = MemberCondition.Configuration.Optional.NO // Configure optional conditions.
)
// Create and start filtering.
val resolvers = condition.build(configuration)
// Get the first result.
val resolver = resolvers.first()
// Execute the method.
resolver.invoke("task_name")
```

If you have more advanced requirements for business layer logic, you can also use `mergeWith` to merge multiple filter conditions.

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Create MethodCondition manually.
// Create the first condition.
val condition1 = MethodCondition<Test>()
condition1.name = "doTask"
// Create a second condition.
val condition2 = MethodCondition<Test>()
condition2.parameters(String::class)
// Merge condition2 into condition1.
// At this time, the condition of condition1 will contain the condition that condition2 is not null.
// The duplicated conditions in condition1 will be overwritten by the condition2 condition.
condition1.mergeWith(condition2)
// You can also use the infix syntax.
condition1 mergeWith condition2
// Call and execute with KavaRef.
Test::class.resolve()
    .firstMethod(condition1)
    .of(test)
    .invoke("task_name")
```

::: danger

Reused use of `build(...)` will no longer be allowed for creation when `MemberCondition.Configuration` is set.
At this point you need to use `copy()` to copy and create a new `MemberCondition`.

Similarly, `InstanceAwareResolver` is not allowed to duplicately set up new instances after setting the instance via `MemberCondition.Configuration.memberInstance` or `of(instance)`.
At this time, you also need to use `copy()` to copy and create a new `InstanceAwareResolver`.

:::

### Custom Resolver

KavaRef uses the default `Member` resolver for filtering.
If you want to implement your own resolver, you can customize the global and the resolver used for each reflection process.

You can inherit from `MemberProccessor.Resolver` to implement your own resolver.

> The following example

```kotlin
class MyMemberProcessorResolver : MemberProcessor.Resolver() {

    override fun <T : Any> getDeclaredConstructors(declaringClass: Class<T>): List<Constructor<T>> {
        // Intercept and implement your constructor filtering logic here.
        return super.getDeclaredConstructors(declaringClass)
    }

    override fun <T : Any> getDeclaredMethods(declaringClass: Class<T>): List<Method> {
        // Intercept and implement your method filtering logic here.
        return super.getDeclaredMethods(declaringClass)
    }

    override fun <T : Any> getDeclaredFields(declaringClass: Class<T>): List<Field> {
        // Intercept and implement your field filtering logic here.
        return super.getDeclaredFields(declaringClass)
    }
}
```

You can then set it to the global configuration.

> The following example

```kotlin
MemberProcessor.globalResolver = MyMemberProcessorResolver()
```

Alternatively, during each reflection, you can set up a custom resolver using `MemberCondition.Configuration` or use a chain call to set up a resolver.

> The following example

```kotlin
// Create resolver.
val myResolver = MyMemberProcessorResolver()
// Suppose this is an instance of this Class.
val test: Test
// Call and execute using KavaRef.
Test::class.resolve()
    // Set custom resolver.
    .processor(myResolver)
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test).invoke("task_name")
```

::: tip

You can find some publicly maintained custom solvers in [here](../config/processor-resolvers.md) and define them in your project to use.

:::

### About Cache

Due to the diversity of filtering conditions, KavaRef does not directly provide caching function,
and the implementation method of caching will also vary depending on the implementation method of each developer.

We recommend manually implementing caches of `MemberResolver` created with filter results for improved performance
and refer to [Create Manually](#create-manually) to split filter conditions to optimize code reuse.

::: danger

If you use `val myResolver by lazy { ... }` to implement the cache, for example, do so below.

> The following example

```kotlin
val myResolver by lazy {
    Test::class.resolve()
        .firstMethod {
            name = "doTask"
            parameters(String::class)
        }
}
```

You may do this when calling.

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Call and execute using KavaRef.
myResolver.of(test).invoke("task_name")
```

Please note that since `MemberResolver` is cached, the same instance is called every time you reference it,
and the instance object of `MemberResolver` is not allowed to be set duplicately (see the "Pay Attention" below [Create Manually](#create-manually)).

So calling this directly will throw an exception, you need to change it to the following form.

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Call and execute using KavaRef.
myResolver.copy().of(test).invoke("task_name")
```

This allows you to copy a new `MemberResolver` instance every time you call without repeating the reflection process and throwing an exception.

:::

### Java Usage

KavaRef is not recommended to be used directly in Java because its API design is based on Kotlin's features and syntax sugar.

If you need to use KavaRef in Java, you can do it in the following ways.

> The following example

```java
public class Main {

    public static void main(String[] args) {
        // Suppose this is an example of this Class.
        Test test;
        // Call and execute with KavaRef.
        KavaRef.resolveClass(Test.class)
            .method()
            .name("doTask")
            .parameters(String.class)
            .build()
            .get(0)
            .of(test)
            .invoke("task_name");
        // Or create KavaRef reflections using an instance.
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