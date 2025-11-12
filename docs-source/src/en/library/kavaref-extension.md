# kavaref-extension

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-extension?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-extension%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

This is an extended dependency for KavaRef-related features.

## Configure Dependency

You can add this module to your project using the following method.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
kavaref-extension = "<version>"

[libraries]
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension", version.ref = "kavaref-extension" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.kavaref.extension)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.kavaref:kavaref-extension:<version>")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

You can view the KDoc [click here](kdoc://kavaref-extension).

### Class Extensions

KavaRef provides some extensions that are more convenient when dealing with `Class` objects.

KavaRef also adds `KClass` extensions to the `Class` extensions,
which is used to call `KClass.java`, making it more concise than using `Some::class.java` directly.

#### Create Class Object

For example, we need to create a `Class` object using the string class name.

> The following example

```kotlin
val myClass = "com.example.MyClass".toClass()
// You can use a method with OrNull suffix to return null
// when the Class is not found instead of throwing an exception.
val myClassOrNull = "com.example.MyClass".toClassOrNull()
```

These methods use `ClassLoaderProvider` to get the default `ClassLoader`,
you can set the default `ClassLoader` to affect global functionality.

> The following example

```kotlin
ClassLoaderProvider.classLoader = MyCustomClassLoader()
```

You can also manually pass a `ClassLoader` parameter to the `toClass` method to specify which `ClassLoader` to use.

#### Class Object Reference

Referring to a Java Class in Kotlin requires writing a very long statement,
such as `MyClass::class.java`, which you can simplify in the following way.

> The following example

```kotlin
val myClass = classOf<MyClass>()
```

You can use the `isSubclassOf` method to determine whether a `Class` is another `Class` subclass.

> The following example

```kotlin
val isSubclass = MyClass::class isSubclassOf MySuperClass::class
// Of course, it also has a corresponding antonym of judgment.
val isNotSubclass = MyClass::class isNotSubclassOf MySuperClass::class
```

You can also use the `hasSuperclass` and `hasInterfaces` methods to determine whether a `Class` has a superclass or an interface.

::: danger

The `Class` passed in by the `classOf` method will perform unboxing of Java wrapper classes by default,
whether you pass in something like `kotlin.Boolean` or `java.lang.Boolean` (see [Java Wrapper Classes Extensions](#java-wrapper-classes-extensions) below),
If you need to avoid the incoming `Class` being unboxed into primitive types, you need to explicitly set the `primitiveType = false` parameter.

:::

#### Create New Instance

KavaRef provides a way for `Class` to easily create a new instance.
You don't need to consider the type of constructing parameters,
you just need to pass in the corresponding parameters to create a new instance immediately.

> The following example

```kotlin
val myClass = MyClass::class.createInstance("Hello", 123)
// You can also use a method with the OrNull suffix to return null
// when creation fails instead of throwing an exception.
val myClassOrNull = MyClass::class.createInstanceOrNull("Hello", 123)
// The createInstance method only filters public constructors by default.
// If you need to call non-public constructors, please set isPublic = false.
val myClassWithPrivateConstructor = MyClass::class.createInstance("Private!", isPublic = false)
// If you want to specify the type to create an instance to use another type,
// you can use the following method.
val mySuperClass = MyClass::class.createInstanceAsType<MySuperClass>("Hello", 123)
// Similarly, you can use a method with the OrNull suffix to return null when
// creation fails instead of throwing an exception.
val mySuperClassOrNull = MyClass::class.createInstanceAsTypeOrNull<MySuperClass>("Hello", 123)
```

::: tip

After the `createInstance` method is successfully matched once, it will cache the results to prevent performance losses
caused by duplicated reflections. It is thread-safe and you can use it in any standard scenario with confidence.

:::

::: danger

When you pass in a parameter with `null`, KavaRef tries to use it as part of the matchable condition (vague condition), and the accuracy may decrease.

The `createInstance` method does not allow all parameters to be `null` (the conditions are completely vague),
and an exception will be thrown directly because this situation cannot be determined which instance to create.

:::

#### Class Modifier

KavaRef also extends `Modifier`, you can directly use `Class.isPublic` and other methods to judge a `Class` modifier.

#### VariousClass

KavaRef provides the `VariousClass` class to load the `Class` object with an indeterminate full class name and return the first match successfully.

This feature is usually used for class names in Android apps that are obfuscated by R8.

> The following example

```kotlin
// Assume that in version A, this class is com.example.a,
// In version B, this class is com.example.b.
val myClass = VariousClass("com.example.a", "com.example.b").load()
// You can also use a method with the suffix OrNull to return null
// instead of throwing an exception if Class is not found.
val myClassOrNull = VariousClass("com.example.a", "com.example.b").loadOrNull()
```

#### Lazy Loading Class Object

KavaRef provides the `LazyClass` class to lazy loading the `Class` object.

You can load `Class` when needed, instead of loading it immediately when created,
which can solve some `Class` that need to be loaded when run or run to specific conditions.

> The following example

```kotlin
// Define a Class that cannot be loaded for null and hosts it to myClass.
val myClass by lazyClass("com.example.MyClass")
// Define a Class that can be loaded for null delay and host it to myClassOrNull.
val myClassOrNull by lazyClassOrNull("com.example.MyClass")
// It can also support incoming VariousClass.
val otherClassOrNull by lazyClassOrNull(VariousClass("com.example.a", "com.example.b"))
// Called and loaded when needed.
myClass.resolve()
myClassOrNull?.resolve()
otherClassOrNull?.resolve()
```

#### ClassLoader Extensions

KavaRef also provides some practical extension methods for `ClassLoader`.

> The following example

```kotlin
// Assume that's your ClassLoader.
val classLoader: ClassLoader
// Load a Class and return null if the load fails.
val myClassOrNull = classLoader.loadClassOrNull("com.example.MyClass")
// Determine whether this Class exists in the current ClassLoader.
val isClassExists = classLoader.hasClass("com.example.MyClass")
```

### Array Class Extensions

In Java, the `Class` object of an array is a special `Class` object, and usually we create it as follows.

For example, create a `Class` object of `java.lang.String[]`.

> The following example

```kotlin
val arrayClass = java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass
```

This is very long to write and is not convenient to maintain, so KavaRef provides a way to simplify this process.

Now, the `Class` object that creates `java.lang.String[]` can be written like this.

> The following example

```kotlin
val arrayClass = ArrayClass(String::class)
```

### Member Extensions

KavaRef provides some extension methods to simplify operations on `Member`.

You can set its accessibility using the `makeAccessible` method on any `Member` object.

It will take effect if `Member` is the `AccessibleObject` type.

> The following example

```kotlin
// Suppose this is your current Member object.
val method: Method
// Make method is accessible.
method.makeAccessible()
```

Similarly, KavaRef also extends `Modifier`, and you can directly use `Member.isPublic` and other methods to judge a `Member` modifier.

### Type Extensions

When manipulating types or generic types in Java, you usually need to use the `Type` interface and its subinterface to handle it.

KavaRef provides some extension methods to simplify operations on `Type`.

For example, you can convert a `Type` that meets the requirements to a `Class` object.

> The following example

```kotlin
val type: Type
val clazz = type.toClass()
// You can also use a method with the suffix named OrNull to
// return null when the conversion fails instead of throwing an exception.
val clazzOrNull = type.toClassOrNull()
```

You can also convert `Type` that meets the requirements to `ParameterizedType` object.

> The following example

```kotlin
val type: Type
val parameterizedType = type.asParameterizedType()
// You can also use a method with the suffix named OrNull to
// return null when the conversion fails instead of throwing an exception.
val parameterizedTypeOrNull = type.asParameterizedTypeOrNull()
```

You can also use the following method to get the generic parameter array in the superclass,
which is often used in some superclass and subclass encapsulation operations.

> The following example

```kotlin
val myClass: Class<*>
// Get the generic parameter array of myClass superclass.
// If the acquisition fails or cannot be retrieved, the empty array will be returned.
val arguments = myClass.genericSuperclassTypeArguments()
```

### Java Wrapper Classes Extensions

In Kotlin, you can directly use `Boolean::class`, `Byte::class`, etc. to obtain Java's original types `boolean` and `byte` instead of their wrapper classes.

If you need to get Java wrapper classes, you need to use the complete `java.lang.Boolean::class`, `java.lang.Byte::class`, etc.
or use `Boolean::class.javaObjectType`, `Byte::class.javaObjectType`.

So, KavaRef provides some type alias to handle Java wrapper classes. Now you only need to prefix `J` to these types, such as `JBoolean::class`.
It is equivalent to `java.lang.Boolean::class`, and some types need to be filled in the full name, such as `JInteger::class`.