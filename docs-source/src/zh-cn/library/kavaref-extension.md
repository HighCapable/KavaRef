# kavaref-extension

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.kavaref/kavaref-extension?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fkavaref%2Fkavaref-extension%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

这是 KavaRef 相关功能的扩展依赖。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
kavaref-extension = "<version>"

[libraries]
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension", version.ref = "kavaref-extension" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.kavaref.extension)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.kavaref:kavaref-extension:<version>")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

你可以 [点击这里](kdoc://kavaref-extension) 查看 KDoc。

### Class 扩展

KavaRef 提供了一些扩展，在处理 `Class` 对象时会更加方便。

KavaRef 对 `Class` 的扩展同样添加了 `KClass` 扩展，作用是调用 `KClass.java`，写法上比直接使用 `Some::class.java` 更加简洁。

#### 创建 Class 对象

例如我们需要使用字符串类名创建一个 `Class` 对象。

> 示例如下

```kotlin
val myClass = "com.example.MyClass".toClass()
// 你可以使用带有 OrNull 后缀的方法在找不到 Class 时返回 null 而不是抛出异常
val myClassOrNull = "com.example.MyClass".toClassOrNull()
```

这些方法统一使用 `ClassLoaderProvider` 来获取默认的 `ClassLoader`，你可以设置默认的 `ClassLoader` 以影响全局功能。

> 示例如下

```kotlin
ClassLoaderProvider.classLoader = MyCustomClassLoader()
```

你也可以手动向 `toClass` 方法传入一个 `ClassLoader` 参数来指定使用哪个 `ClassLoader`。

#### Class 对象引用

在 Kotlin 中引用 Java Class 需要写很长的声明，例如 `MyClass::class.java`，此时你可以用以下方式来简化。

> 示例如下

```kotlin
val myClass = classOf<MyClass>()
```

你可以使用 `isSubclassOf` 方法来判断一个 `Class` 是否是另一个 `Class` 的子类。

> 示例如下

```kotlin
val isSubclass = MyClass::class isSubclassOf MySuperClass::class
// 当然，它也有一个对应的反义判断方法
val isNotSubclass = MyClass::class isNotSubclassOf MySuperClass::class
```

你还可以使用 `hasSuperclass` 和 `hasInterfaces` 方法来判断一个 `Class` 是否有超类或接口。

::: danger

`classOf` 方法传入的 `Class` 默认会进行 Java 包装类的拆箱操作，无论你传入的是类似 `kotlin.Boolean` 还是 `java.lang.Boolean` (参考下方的 [Java 包装类扩展](#java-包装类扩展))，
如果你需要避免传入的 `Class` 被拆箱变为原始类型，你需要明确设置 `primitiveType = false` 参数。

:::

#### 创建新的实例

KavaRef 为 `Class` 提供了一个方法来方便地创建一个新的实例，你不需要考虑构造参数的类型，你只需要传入对应的参数即可立即创建一个新的实例。

> 示例如下

```kotlin
val myClass = MyClass::class.createInstance("Hello", 123)
// 你也可以使用带有 OrNull 后缀的方法在创建失败时返回 null 而不是抛出异常
val myClassOrNull = MyClass::class.createInstanceOrNull("Hello", 123)
// createInstance 方法默认仅过滤公开的构造方法，如果你需要调用非公有构造方法，请设置 isPublic = false
val myClassWithPrivateConstructor = MyClass::class.createInstance("Private!", isPublic = false)
// 如果你想指定创建实例的类型使用另一个类型，可以使用以下方法
val mySuperClass = MyClas::class.createInstanceAsType<MySuperClass>("Hello", 123)
// 同样地，你也可以使用带有 OrNull 后缀的方法在创建失败时返回 null 而不是抛出异常
val mySuperClassOrNull = MyClass::class.createInstanceAsTypeOrNull<MySuperClass>("Hello", 123)
```

::: tip

`createInstance` 方法在成功匹配一次后，会将结果进行缓存防止重复反射造成的性能损耗，它是线程安全的，你可以放心在任何标准场景下使用。

:::

::: danger

当你传入带有 `null` 的参数时，KavaRef 会尝试将其作为可匹配到条件的一部分 (模糊条件)，准确性可能会下降。

`createInstance` 方法不允许所有参数均为 `null` 的情况 (条件完全模糊)，会直接抛出异常，因为这种情况无法确定要创建哪个实例。

:::

#### Class 修饰符

KavaRef 也对 `Modifier` 进行了扩展，你可以直接使用 `Class.isPublic` 等方法来判断一个 `Class` 的修饰符。

#### VariousClass

KavaRef 提供了 `VariousClass` 类来装载不确定完整类名的 `Class` 对象，并返回成功匹配到的第一个。

此功能通常可用于 Android 应用中那些被 R8 混淆后的类名。

> 示例如下

```kotlin
// 假设在 A 版本中，这个类为 com.example.a，
// 在 B 版本中，这个类为 com.example.b
val myClass = VariousClass("com.example.a", "com.example.b").load()
// 你也可以使用后缀名为 OrNull 的方法在找不到 Class 时返回 null 而不是抛出异常
val myClassOrNull = VariousClass("com.example.a", "com.example.b").loadOrNull()
```

#### 延迟装载 Class 对象

KavaRef 提供了 `LazyClass` 类来延迟装载 `Class` 对象。

你可以在需要时再装载 `Class`，而不是在创建时就立即装载，这可以解决一些需要运行时或运行到特定条件下才需要装载的 `Class`。

> 示例如下

```kotlin
// 定义一个不可为空延迟装载的 Class 并托管给 myClass
val myClass by lazyClass("com.example.MyClass")
// 定义一个可为空延迟装载的 Class 并托管给 myClassOrNull
val myClassOrNull by lazyClassOrNull("com.example.MyClass")
// 它亦可支持传入 VariousClass
val otherClassOrNull by lazyClassOrNull(VariousClass("com.example.a", "com.example.b"))
// 在需要时调用即装载
myClass.resolve()
myClassOrNull?.resolve()
otherClassOrNull?.resolve()
```

#### ClassLoader 扩展

KavaRef 还为 `ClassLoader` 提供了一些实用的扩展方法。

> 示例如下

```kotlin
// 假设这就是你的 ClassLoader
val classLoader: ClassLoader
// 装载一个 Class，在装载失败时返回 null
val myClassOrNull = classLoader.loadClassOrNull("com.example.MyClass")
// 判断这个 Class 是否存在于当前 ClassLoader 中
val isClassExists = classLoader.hasClass("com.example.MyClass")
```

### 数组 Class 扩展

在 Java 中，数组的 `Class` 对象是一个特殊的 `Class` 对象，通常，我们创建它的方式如下。

例如创建一个 `java.lang.String[]` 的 `Class` 对象。

> 示例如下

```kotlin
val arrayClass = java.lang.reflect.Array.newInstance(String::class.java, 0).javaClass
```

这样写起来非常长，而且不方便维护，所以 KavaRef 提供了一个方法来简化这个过程。

现在，创建 `java.lang.String[]` 的 `Class` 对象可以这样写。

> 示例如下

```kotlin
val arrayClass = ArrayClass(String::class)
```

### Member 扩展

KavaRef 提供了一些扩展方法来简化对 `Member` 的操作。

你可以在任何 `Member` 对象上使用 `makeAccessible` 方法来设置其可访问性。

如果 `Member` 是 `AccessibleObject` 类型即可生效。

> 示例如下

```kotlin
// 假设这个是你当前的 Member 对象
val method: Method
// 设置方法可访问
method.makeAccessible()
```

同样地，KavaRef 也对 `Modifier` 进行了扩展，你可以直接使用 `Member.isPublic` 等方法来判断一个 `Member` 的修饰符。

### Type 扩展

在 Java 中操作类型或泛型类型时，通常需要使用 `Type` 接口及其子接口来处理。

KavaRef 提供了一些扩展方法来简化对 `Type` 的操作。

例如，你可以将一个符合要求的 `Type` 转换为 `Class` 对象。

> 示例如下

```kotlin
val type: Type
val clazz = type.toClass()
// 你也可以使用后缀名为 OrNull 的方法在转换失败时返回 null 而不是抛出异常
val clazzOrNull = type.toClassOrNull()
```

你也可以将符合要求的 `Type` 转换为 `ParameterizedType` 对象。

> 示例如下

```kotlin
val type: Type
val parameterizedType = type.asParameterizedType()
// 你也可以使用后缀名为 OrNull 的方法在转换失败时返回 null 而不是抛出异常
val parameterizedTypeOrNull = type.asParameterizedTypeOrNull()
```

你还可以使用以下方式获取超类中的泛型参数数组，这在一些超类与子类的封装操作中会经常用到。

> 示例如下

```kotlin
val myClass: Class<*>
// 获取 myClass 的超类的泛型参数数组，获取失败或无法获取时将返回空数组
val arguments = myClass.genericSuperclassTypeArguments()
```

### 类型引用扩展

在 Java 中，方法的泛型会在编译后被类型擦除，在运行获取到的类型是 `java.lang.Object`。

KavaRef 提供了 `TypeRef` 类来包装你的目标泛型来确保你可以在运行时获取到正确的泛型类型，它的核心功能参考于 [Gson](https://github.com/google/gson) 的 `TypeToken`。

它的使用方法非常简单，你可以像下面这样使用它。

> 示例如下

```kotlin
val listStringType = typeRef<List<String>>()
// 获取存储的类型，将会是 List<? extends String>
val type = listStringType.type
// 获取其原始类型，将会是 List
val rawType = listStringType.rawType
```

在使用 Gson 等需要传入 `Type` 的场景中，你可以为此实现一个带有 `reified` 泛型的扩展方法。

> 示例如下

```kotlin
val gson = Gson()

inline fun <reified T : Any> T.toJson(): String = gson.toJson(this, typeRef<T>().type)

// 使用方法
val json = listOf("KavaRef", "is", "awesome").toJson()
```

### Java 包装类扩展

在 Kotlin 中直接使用 `Boolean::class`、`Byte::class` 等方式获取到的是 Java 的原始类型 `boolean`、`byte` 而不是它们的包装类。

如果你需要获取 Java 的包装类，你需要使用完整的 `java.lang.Boolean::class`、`java.lang.Byte::class` 等方式或使用 `Boolean::class.javaObjectType`、`Byte::class.javaObjectType`。

所以，KavaRef 提供了一些类型别名来处理 Java 的包装类，现在你只需要在这些类型加上 `J` 前缀即可，例如 `JBoolean::class`，
它等价于 `java.lang.Boolean::class`，部分类型需要填写全称，例如 `JInteger::class`。