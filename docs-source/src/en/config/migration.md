# Migration to KavaRef

If you are used to using the reflection API in [YukiReflection](https://github.com/HighCapable/YukiReflection) or [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI), you can refer to the following to migrate to `KavaRef`.

::: warning

For `YukiHookAPI`, you need to continue using its Hook API, and `KavaRef` only includes Java reflection-related APIs.

:::

## Basic Functions

The design concept of `KavaRef` is similar to `YukiReflection`, but not exactly the same.
The following lists the differences between `YukiReflection` and `KavaRef` in basic reflection functions, which you can manually migrate based on.

For example, we have the following Java class.

> The following example

```java :no-line-numbers
public class MyClass {

    private void myMethod(String content) {
        System.out.println("Hello " + content + "!");
    }
}
```

Here is a comparison of `KavaRef` with `YukiReflection` using examples.

> The following example

<div style="display: flex; gap: 16px;">

<div style="flex: 1;">
<h4>KavaRef</h4>

```kotlin
// Assume that's your MyClass instance.
val myClass: MyClass
// Call and execute using KavaRef.
MyClass::class.resolve().firstMethod {
    name = "myMethod"
    parameters(String::class)
}.of(myClass).invoke("Hello, KavaRef!")
// Direct reference to instance.
myClass.asResolver().firstMethod {
    name = "myMethod"
    parameters(String::class)
}.invoke("Hello, KavaRef!")
```

</div>

<div style="flex: 1;">
<h4>YukiReflection</h4>

```kotlin
// Assume that's your MyClass instance.
val myClass: MyClass
// Call and execute using YukiReflection.
MyClass::class.java.method {
    name = "myMethod"
    param(StringClass)
}.get(myClass).call("Hello, YukiReflection!")
// Direct reference to instance.
myClass.current().method {
    name = "myMethod"
    param(StringClass)
}.call("Hello, YukiReflection!")
```

</div>
</div>

`KavaRef` starts reflection at any time; you need to use `resolve()` to create a reflection scope.
You no longer directly extend the related `method` and `constructor` methods to avoid polluting their scope.

`KavaRef` provides the `asResolver()` method to directly reference the reflection scope of the instance object,
avoiding pollution caused by the creation of uncontrollable instance objects by the `current()` method in `YukiReflection`.

`KavaRef` abandons the "Finder" design concept and uses the "Filter" design concept to obtain reflection results.
"Find" is no longer finding, but "filtering".

`KavaRef` canceled the design scheme defined in `YukiReflection` for determining whether the `Member` obtained in the resulting instance is multiple or single,
and directly returns the entire `List<MemberResolver>`.
The example you see above uses `firstMethod` to get the first matching `MethodResolver`.
If you need to get all matches, you can change to `method`.

The conditional method name in `MethodCondition` of `KavaRef` has been modified from abbreviations
such as `param` used previously in `YukiReflection` to `parameters` to better align with the naming conventions of the Java reflection API.

`KavaRef` no longer provides the `param(...).order()` function in conditions, because this function itself is unstable.
`KavaRef` now uses an iterator for filtering, and the bytecode will no longer be in order, nor should bytecode be filtered by order.
You can use `firstMethod`, `firstField`, or `lastMethod`, `lastField`, etc. to get the first or last matching result.

`KavaRef` renames the `get(instance)` method to `of(instance)` because `get(...)` may be confused with the `get(...)` usage of `Field` and lacks semantic clarity.
At the same time, `get(instance)` no longer gets the `MethodFinder.Result.Instance` instance from something like `MethodFinder.Result`,
but uses `of(instance)` to consistently operate and set the instance object to `MemberResolver`.

Methods such as `string()`, `int()`, etc. in `MethodFinder.Result.Instance` have been removed in `KavaRef`.
You can directly use `get<String>()`, `get<Int>()`, `invoke<String>(...)`, `invoke<Int>(...)`, etc. to get or call the corresponding type results.

::: danger

If you are looking for (filtering) `Field`, you need to note that there may be semantic conflicts between `KavaRef` and `YukiReflection` in the acquisition method of `Field`.
Please pay special attention when migrating this part.

For example, get the static field of `content` in `MyClass`, in `YukiReflection`, you would do this.

> The following example

```kotlin
MyClass::class.java
    .field { name = "content" } // Return FieldFinder.Result
    .get() // Cannot be omitted, return FieldFinder.Result.Instance
    .string() // value
```

In `KavaRef` you need to do this.

> The following example

```kotlin
MyClass::class.resolve()
    .firstField { name = "content" }  // Return FieldResolver<MyClass>
    .get<String>() // value
```

As mentioned above, `get(...)` is used to get the `FieldFinder.Result.Instance` object in Y`ukiReflection`, not the value.
To get the value and process it as a specified type, you need to call `string()` or `cast<String>()`, and in `KavaRef`,
you use `get<T>()` directly in `MemberResolver` to get the value of the specified type.
The usage of `get(...)` of `KavaRef` for `get(...)` to `of(...)`.

So the complete writing of the above example in `KavaRef` should be.

> The following example

```kotlin
// Since the call is a static instance, "of(null)" can be omitted.
MyClass::class.resolve()
    .firstField { name = "content" } // It's already a call chain object FieldResolver<MyClass>
    .of(null) // Can be omitted and return to the call chain object FieldResolver<MyClass>
    .get<String>() // value
```

:::

`KavaRef` no longer provides `call` methods for `Method`, and is now merged uniformly into `invoke` (with generic parameters).
At the same time, `KavaRef` defines the `newInstance` method of `Constructor` as `create` (with generic parameters).

You may have noticed that the condition `superClass()` is gone, it is still there,
in `KavaRef` it has been renamed to `superclass()`, docking with the standard Java reflection API.

At the same time, `KavaRef` extends `KClass`, and you no longer need to use `Some::class.java` to declare an instance of `Class` in most scenarios.

Another design idea of ​​`KavaRef` is type safety.
As long as you use `KClass<T>` and `Class<T>` that declare the generic type, it will be checked and converted to the
corresponding type when `of(instance)` and `create(...)`, and type checking will be completed during coding to avoid runtime errors.

> The following example

```kotlin
// Assume that's your MyClass instance.
val myClass: MyClass
// Using KavaRef to call and execute.
MyClass::class.resolve()
    .firstMethod {
        name = "myMethod"
        parameters(String::class)
    }
    // Only instances of type MyClass can be passed in.
    .of(myClass)
    .invoke("Hello, KavaRef!")
```

## Other Functions

`KavaRef` and `YukiReflection` are not much different in other functions and extended functions.
`KavaRef` separates these functions into a separate module.

The following functionality is provided in `YukiReflection` but is not implemented and no longer provided in `KavaRef`:

- Preset reflection type constant classes, such as `StringClass`, `IntType`, etc
  - You can use Kotlin class references such as `String::class`, `Int::class`, etc. to instead it.
    For primitive types and wrapper classes, `IntType` is equivalent to `Int::class`, and `IntClass` is equivalent to `JInteger::class`

- `DexClassFinder` function
  - Due to its design flaws and possible performance issues when used on Android platforms, it is no longer available for now

- `RemedyPlan` and `method { ... } .remedys { ... }` functions
  - Due to possible black box problems, maintenance is relatively difficult.
    If you need to use similar functions, please implement them manually and will no longer be provided

- `ClassLoader.listOfClasses()` function
  - Due to the complex and unstable implementation solutions of each platform, it will no longer be provided

- `ClassLoader.searchClass()` function
  - Due to performance issues and design time is limited to Android platform,
    it is relatively difficult to maintain filter conditions and is no longer provided

- `Class.hasExtends`, `Class.extends`, `Class.implements` functions
  - You can replace them with `A::class isSubclassOf B::class`

- `Class.toJavaPrimitiveType()` function
  - There is conceptual confusion in functional design and will no longer be provided

- `"com.some.clazz".hasClass(loader)` function
  - You can use `loader.hasClass("com.some.clazz")` to instead it

- `Class.hasField`, `Class.hasMethod`, `Class.hasConstructor` functions
  - Due to design defects, no longer provided

- `Class.hasModifiers(...)`, `Member.hasModifiers(...)` functions
  - You can replace them directly with extension methods such as `Class.isPublic`, `Member.isPublic`

- `Class.generic()`, `GenericClass` functions
  - If you just want to get generic parameters of the superclass, you can use `Class.genericSuperclassTypeArguments()`.
    Due to design defects, no longer provided

- `Any.current()`, `CurrentClass` functions
  - You can use `Any.asResolver()` to instead it

- `Class.buildOf(...)` function
  - You can use `Class.createInstance(...)` to instead it

- `Class.allMethods()`, `Class.allFields()`, `Class.allConstructors()` functions
  - Due to its pollution scope, no longer provided

- `YLog` log function
  - `KavaRef` no longer takes over the logs, you can use the corresponding platform implementation method and no longer provided

## Exception Handling

`KavaRef` is completely different from `YukiReflection` in exception handling.
The exception logic of `KavaRef` will remain transparent by default. <u>**It no longer actively intercepts exceptions and prints error logs or even provides `onNoSuchMethod` listener**</u>.
When no valid members are filtered, `KavaRef` will throw an exception directly unless you **explicitly declare the condition as optional (consistent with `YukiReflection` logic)**.

> The following example

```kotlin
// Assume that's your MyClass instance.
val myClass: MyClass
// Using KavaRef to call and execute.
MyClass::class.resolve()
    .optional() // Declare as optional, do not throw exceptions.
    // Use firstMethodOrNull instead of firstMethod,
    // because the NoSuchElementException of Kotlin itself will be thrown.
    .firstMethodOrNull {
        name = "doNonExistentMethod" // Assume that this method does not exist.
        parameters(String::class)
    }?.of(myClass)?.invoke("Hello, KavaRef!")
```

For more information, please refer to the [Exception Handling](../library/kavaref-core.md#exception-handling) section in [kavaref-core](../library/kavaref-core.md).

## New to KavaRef

If you haven't used `YukiReflection` or `YukiHookAPI`, it doesn't matter, you can refer to the following content to get started quickly.

::: tip What to Do Next

For more information, please continue reading [kavaref-core](../library/kavaref-core.md) and [kavaref-extension](../library/kavaref-extension.md).

Get started using `KavaRef` now!

:::