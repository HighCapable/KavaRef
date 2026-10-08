# 第三方 Member 处理器

> 这里收录了一些第三方的 Member 处理器，可供参考与使用。
>
> 使用方法请阅读 [自定义处理器](../library/kavaref-core.md#自定义处理器)。

## XposedBridge

[项目地址](https://github.com/rovo89/XposedBridge)

> The Java part of the Xposed framework

使用 `XposedBridge.invokeOriginalMethod` 跳过 Hook 调用原始方法。

```kotlin
class XposedOriginalMethodHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? =
        XposedBridge.invokeOriginalMethod(method, instance, args)
}
```

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 调用原始方法
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(XposedOriginalMethodHandler())
    .invoke("task_name")
```

## libxposed

[项目地址](https://github.com/libxposed/api)

> libxposed API

使用 `XposedInterface.getInvoker` 调用方法，可通过 `type` 指定调用的 Hook 链，通过 `special` 进行非虚调用。

```kotlin
class LibXposedMethodHandler(
    private val xposed: XposedInterface,
    private val type: XposedInterface.Invoker.Type = XposedInterface.Invoker.Type.ORIGIN,
    private val special: Boolean = false
) : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? {
        val invoker = xposed.getInvoker(method).setType(type)

        return if (special)
            invoker.invokeSpecial(
                requireNotNull(instance) {
                    "An instance is required for special invocation."
                }, *args
            )
        else invoker.invoke(instance, *args)
    }
}
```

> 示例如下

```kotlin
// 假设这就是你的 XposedModule
val module: XposedModule
// 假设这就是这个 Class 的实例
val test: Test
// 得到方法
val doTask = Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
// 调用原始方法
doTask.withHandler(LibXposedMethodHandler(module))
    .invoke("task_name")
// 非虚调用原始方法，相当于 super.doTask("task_name")
doTask.withHandler(LibXposedMethodHandler(module, special = true))
    .invoke("task_name")
```

## Pine

[项目地址](https://github.com/canyie/pine)

> Dynamic java method hook framework on ART. Allowing you to change almost all java methods' behavior dynamically

使用 `Pine.invokeOriginalMethod` 跳过 Hook 调用原始方法。

```kotlin
class PineOriginalMethodHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? =
        Pine.invokeOriginalMethod(method, instance, *args)
}
```

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 调用原始方法
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(PineOriginalMethodHandler())
    .invoke("task_name")
```

## MethodHandle

[文档地址](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html)

> Java 9 及以上版本提供的 `MethodHandles.Lookup`

使用 `MethodHandles.Lookup.unreflectSpecial` 对方法进行非虚调用，无需依赖 Hook 框架。

```kotlin
class MethodHandleSpecialHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? {
        requireNotNull(instance) {
            "An instance is required for special invocation."
        }

        val lookup = MethodHandles.privateLookupIn(instance.javaClass, MethodHandles.lookup())
        return lookup.unreflectSpecial(method, instance.javaClass)
            .bindTo(instance)
            .invokeWithArguments(*args)
    }
}
```

> 示例如下

```kotlin
// 假设这就是 Test 子类的实例，并且其重写了 doTask 方法
val test: Test
// 非虚调用 Test 中的方法，相当于 super.doTask("task_name")
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(MethodHandleSpecialHandler())
    .invoke("task_name")
```

## Unsafe

[文档地址](https://openjdk.org/jeps/471)

> `sun.misc.Unsafe` 提供的内存访问方法

使用 `sun.misc.Unsafe` 修改 `static final` 字段，通过 `Field.set` 修改此类字段时会抛出 `IllegalAccessException`。

```kotlin
class UnsafeFieldHandler : FieldResolver.Handler() {

    private val unsafe = Unsafe::class.resolve().firstField { name = "theUnsafe" }.get<Unsafe>()!!

    override fun set(field: Field, instance: Any?, value: Any?) {
        if (!field.isStatic || !field.isFinal) return super.set(field, instance, value)

        // Unsafe 不会初始化类，需要先初始化以免写入的值被类初始化覆盖
        field.declaringClass.let {
            it.name.toClass(it.classLoader, initialize = true)
        }

        val base = unsafe.staticFieldBase(field)
        val offset = unsafe.staticFieldOffset(field)
        when (field.type) {
            classOf<Boolean>() -> unsafe.putBooleanVolatile(base, offset, value as Boolean)
            classOf<Byte>() -> unsafe.putByteVolatile(base, offset, value as Byte)
            classOf<Char>() -> unsafe.putCharVolatile(base, offset, value as Char)
            classOf<Short>() -> unsafe.putShortVolatile(base, offset, value as Short)
            classOf<Int>() -> unsafe.putIntVolatile(base, offset, value as Int)
            classOf<Long>() -> unsafe.putLongVolatile(base, offset, value as Long)
            classOf<Float>() -> unsafe.putFloatVolatile(base, offset, value as Float)
            classOf<Double>() -> unsafe.putDoubleVolatile(base, offset, value as Double)
            else -> unsafe.putObjectVolatile(base, offset, value)
        }
    }
}
```

> 示例如下

```kotlin
// 修改 Test 中的 static final 字段
Test::class.resolve()
    .firstField { name = "TASK_NAME" }
    .withHandler(UnsafeFieldHandler())
    .set("new_task_name")
```

::: warning

编译期常量 (例如 `static final String TASK_NAME = "task_name"`) 会被编译器内联到使用处，修改后这些使用处的值不会发生改变。

`sun.misc.Unsafe` 的内存访问方法已在 JDK 23 被标记为待移除 ([JEP 471](https://openjdk.org/jeps/471))，并会在 JDK 24 及以上版本首次使用时输出警告 ([JEP 498](https://openjdk.org/jeps/498))。

:::

## Spring Framework

[项目地址](https://github.com/spring-projects/spring-framework)

> Spring Framework

使用 Spring 的 `ReflectionUtils` 调用方法与读写字段，方法抛出的运行时异常将被直接抛出，受检异常将被包装为 `UndeclaredThrowableException`，而不是 `InvocationTargetException`。

```kotlin
class SpringMethodHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? =
        ReflectionUtils.invokeMethod(method, instance, *args)
}

class SpringFieldHandler : FieldResolver.Handler() {

    override fun get(field: Field, instance: Any?): Any? =
        ReflectionUtils.getField(field, instance)

    override fun set(field: Field, instance: Any?, value: Any?) =
        ReflectionUtils.setField(field, instance, value)
}
```

> 示例如下

```kotlin
// 假设这就是这个 Class 的实例
val test: Test
// 调用方法，运行时异常将被直接抛出
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(SpringMethodHandler())
    .invoke("task_name")
```