# 迁移至 KavaRef

如果你已经习惯使用 [YukiReflection](https://github.com/HighCapable/YukiReflection) 或 [YukiHook](https://github.com/HighCapable/YukiHook) 中的反射 API，你可以参考以下内容来迁移至 KavaRef。

::: warning

针对 YukiHook，你需要继续使用其 Hook API，KavaRef 仅包含 Java 反射相关 API。

:::

## 基本功能

KavaRef 的设计理念与 YukiReflection 类似，但不完全相同，以下内容列举了 YukiReflection 与 KavaRef 在基本反射功能上的差异，你可以根据这些差异手动进行迁移。

例如我们有以下 Java 类。

> 示例如下

```java :no-line-numbers
public class MyClass {

    private static String content = "Hello, World!";

    private void myMethod(String content) {
        System.out.println("Hello " + content + "!");
    }
}
```

以下是 KavaRef 与 YukiReflection 的使用示例对比。

> 示例如下

<div style="display: flex; gap: 16px;">

<div style="flex: 1;">
<h4>KavaRef</h4>

```kotlin
// 假设这就是你的 MyClass 实例
val myClass: MyClass
// 使用 KavaRef 调用并执行
MyClass::class.resolve().firstMethod {
    name = "myMethod"
    parameters(String::class)
}.of(myClass).invoke("Hello, KavaRef!")
// 直接引用实例方式
myClass.asResolver().firstMethod {
    name = "myMethod"
    parameters(String::class)
}.invoke("Hello, KavaRef!")
```

</div>

<div style="flex: 1;">
<h4>YukiReflection</h4>

```kotlin
// 假设这就是你的 MyClass 实例
val myClass: MyClass
// 使用 YukiReflection 调用并执行
MyClass::class.java.method {
    name = "myMethod"
    param(StringClass)
}.get(myClass).call("Hello, YukiReflection!")
// 直接引用实例方式
myClass.current().method {
    name = "myMethod"
    param(StringClass)
}.call("Hello, YukiReflection!")
```

</div>
</div>

KavaRef 在任何时候开始反射都需要使用 `resolve()` 来创建反射作用域，不再对 `Class` 等实例直接进行扩展相关 `method`、`constructor` 方法以避免污染其作用域。

KavaRef 提供了 `asResolver()` 方法来直接引用实例对象的反射作用域，避免了 YukiReflection 中的 `current()` 方法创建不可控实例对象造成的污染。

KavaRef 抛弃了 "Finder" 的设计理念，使用 "Filter" (过滤器) 的设计理念来获取反射结果，“查找” 不再是查找，而是 “过滤”。

KavaRef 取消了 YukiReflection 在结果实例中定义获取的 `Member` 为多重还是单一的设计方案，直接返回整个 `List<MemberResolver>`，
你在上方看到的示例使用了 `firstMethod` 来获取第一个匹配的 `MethodResolver`，如果你需要获取所有匹配的结果，可以改为 `method`。

KavaRef 在 `MethodCondition` 中的条件方法名称已由 YukiReflection 之前的 `param` 等简写修改为 `parameters`，以更符合 Java 反射 API 的命名习惯。

KavaRef 不再提供条件中的 `param(...).order()` 功能，因为这个功能本身就不稳定，KavaRef 现在使用迭代器进行过滤操作，字节码将不再有顺序，且本不应该使用顺序筛选字节码，你可以使用 `firstMethod`、`firstField` 或 `lastMethod`、`lastField` 等方法来获取第一个或最后一个匹配的结果。

KavaRef 将 `get(instance)` 方法更名为 `of(instance)`，因为 `get(...)` 可能会与 `Field` 的 `get(...)` 用法产生混淆且语义不明确，
同时 `get(instance)` 也不再是从类似 `MethodFinder.Result` 来获取 `MethodFinder.Result.Instance` 实例，而是使用 `of(instance)` 来始终操作和设置实例对象到 `MemberResolver`。

类似 `MethodFinder.Result.Instance` 中的 `string()`、`int()` 等方法在 KavaRef 中已被移除，
你可以直接使用 `get<String>()`、`get<Int>()`、`invoke<String>(...)`、`invoke<Int>(...)` 等方式来获取或调用对应类型的结果。

::: danger

如果你正在查找 (过滤) `Field`，你需要注意 KavaRef 与 YukiReflection 在 `Field` 的获取方式上有可能会发生语义冲突，在迁移这部分的时候请特别注意。

例如获取 `MyClass` 中的 `content` 静态字段，在 YukiReflection 中，你会这样做。

> 示例如下

```kotlin
MyClass::class.java
    .field { name = "content" } // 返回 FieldFinder.Result
    .get() // 不可省略，返回 FieldFinder.Result.Instance
    .string() // 值
```

在 KavaRef 中，你需要这样做。

> 示例如下

```kotlin
MyClass::class.resolve()
    .firstField { name = "content" }  // 返回 FieldResolver<MyClass>
    .get<String>() // 值
```

正如上面所说，`get(...)` 在 YukiReflection 中是获取 `FieldFinder.Result.Instance` 对象，而不是值，要获取值并处理为指定类型，你需要调用 `string()` 或者 `cast<String>()`，而在 KavaRef 中是在 `MemberResolver` 中直接使用 `get<T>()` 来获取指定类型的值，KavaRef 对应 YukiReflection 的 `get(...)` 的用法是 `of(...)`。

所以上述示例在 KavaRef 中的完整写法应该为。

> 示例如下

```kotlin
// 由于调用的是静态实例，"of(null)" 可被省略
MyClass::class.resolve()
    .firstField { name = "content" } // 已是调用链对象 FieldResolver<MyClass>
    .of(null) // 可省略，返回调用链对象 FieldResolver<MyClass>
    .get<String>() // 值
```

:::

KavaRef 不再对 `Method` 提供 `call` 方法，现在统一合并为 `invoke` (带泛型参数)，同时 KavaRef 将 `Constructor` 的 `newInstance` 方法定义为 `create` (带泛型参数)。

你可能注意到条件 `superClass()` 消失了，它还在，在 KavaRef 中它已更名为 `superclass()`，对接标准的 Java 反射 API。

同时，KavaRef 对 `KClass` 进行了扩展，你不再需要在大部分场景中使用 `Some::class.java` 的方式来声明一个 `Class` 实例。

KavaRef 的另一个设计思想就是类型安全，只要是你在使用声明指定泛型类型的 `KClass<T>`、`Class<T>` 时，在 `of(instance)`、`create(...)` 时都会校验、转换为对应类型，在编码期间就完成类型检查，避免运行时错误。

> 示例如下

```kotlin
// 假设这就是你的 MyClass 实例
val myClass: MyClass
// 使用 KavaRef 调用并执行
MyClass::class.resolve()
    .firstMethod {
        name = "myMethod"
        parameters(String::class)
    }
    // 只能传入 MyClass 类型的实例
    .of(myClass)
    .invoke("Hello, KavaRef!")
```

## 其它功能

KavaRef 与 YukiReflection 在其它功能及扩展功能中的实现差异不大，KavaRef 将这些功能单独分离为了一个独立的模块。

以下功能在 YukiReflection 中提供，但在 KavaRef 中没有实现且不再提供：

- 预置反射类型常量类，如 `StringClass`、`IntType` 等
  - 你可以直接使用 `String::class`、`Int::class` 等 Kotlin 的类引用进行替代，对于原始类型与包装类，`IntType` 等价于 `Int::class`，`IntClass` 等价于 `JInteger::class`

- `DexClassFinder` 功能
  - 由于其设计缺陷，且在 Android 平台上使用时可能存在性能问题，目前不再提供

- `RemedyPlan` 和 `method { ... } .remedys { ... }` 功能
  - 由于此功能存在可能的黑盒问题，维护相对困难，如需使用类似功能，请手动实现，不再提供

- `ClassLoader.listOfClasses()` 功能
  - 由于各个平台实现方案复杂且不稳定，不再提供

- `ClassLoader.searchClass()` 功能
  - 由于性能问题，且设计时仅限于 Android 平台使用，过滤条件维护相对困难，不再提供

- `Class.hasExtends`、`Class.extends`、`Class.implements` 功能
  - 你可以使用 `A::class isSubclassOf B::class` 来取代它们

- `Class.toJavaPrimitiveType()` 功能
  - 功能设计上存在概念混淆问题，不再提供

- `"com.some.clazz".hasClass(loader)` 功能
  - 你可以使用 `loader.hasClass("com.some.clazz")` 来取代它

- `Class.hasField`、`Class.hasMethod`、`Class.hasConstructor` 功能
  - 由于设计缺陷，不再提供

- `Class.hasModifiers(...)`、`Member.hasModifiers(...)` 功能
  - 你可以直接使用 `Class.isPublic`、`Member.isPublic` 等扩展方法来取代它们。

- `Class.generic()`、`GenericClass` 功能
  - 如果只是希望获取超类的泛型参数，你可以使用 `Class.genericSuperclassTypeArguments()`，由于设计缺陷，不再提供

- `Any.current()`、`CurrentClass` 功能
  - 你可以使用 `Any.asResolver()` 来取代它

- `Class.buildOf(...)` 功能
  - 你可以使用 `Class.createInstance(...)` 来取代它

- `Class.allMethods()`、`Class.allFields()`、`Class.allConstructors()` 功能
  - 由于其污染作用域，不再提供

- `YLog` 日志功能
  - KavaRef 不再接管日志，你可以使用对应平台的实现方式，不再提供

## 异常处理

KavaRef 在异常处理方面与 YukiReflection 完全不同，KavaRef 的异常逻辑将保持默认透明，<u>**它不再主动拦截异常并打印错误日志甚至是提供 `onNoSuchMethod` 监听**</u>，当没有过滤到任何有效的成员时，KavaRef 会直接抛出异常，除非你**明确声明条件为可选 (与 YukiReflection 逻辑保持一致)**。

> 示例如下

```kotlin
// 假设这就是你的 MyClass 实例
val myClass: MyClass
// 使用 KavaRef 调用并执行
MyClass::class.resolve()
    .optional() // 声明为可选，不要抛出异常
    // 使用 firstMethodOrNull 替代 firstMethod，因为找不到会抛出 Kotlin 自身的 NoSuchElementException
    .firstMethodOrNull {
        name = "doNonExistentMethod" // 假设这个方法不存在
        parameters(String::class)
    }?.of(myClass)?.invoke("Hello, KavaRef!")
```

更多内容请参考 [kavaref-core](../library/kavaref-core.md) 中的 [异常处理](../library/kavaref-core.md#异常处理) 部分。

## 初次使用 KavaRef

如果你没用过 YukiReflection 或者 YukiHook，没关系，你可以参考以下内容来快速上手。

::: tip 接下来做什么

更多内容，请继续阅读 [kavaref-core](../library/kavaref-core.md) 和 [kavaref-extension](../library/kavaref-extension.md)。

立即开始使用 KavaRef 吧！

:::