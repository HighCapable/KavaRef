# Third-party Member Handlers

> Here are some third-party Member handlers for reference and use.
>
> Please read [Custom Handler](../library/kavaref-core.md#custom-handler) for usage instructions.

## XposedBridge

[Project URL](https://github.com/rovo89/XposedBridge)

> The Java part of the Xposed framework

Use `XposedBridge.invokeOriginalMethod` to invoke the original method without hooks.

```kotlin
class XposedOriginalMethodHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? =
        XposedBridge.invokeOriginalMethod(method, instance, args)
}
```

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Invoke the original method.
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(XposedOriginalMethodHandler())
    .invoke("task_name")
```

## libxposed

[Project URL](https://github.com/libxposed/api)

> libxposed API

Use `XposedInterface.getInvoker` to invoke methods and constructors, you can specify the hook chain by `type`,
invoke methods non-virtually by `special`, and create a subclass instance initialized only by the current constructor by `subclass`.

```kotlin
class LibXposedMethodHandler(
    private val xposed: XposedInterface,
    private val type: XposedInterface.Invoker.Type = XposedInterface.Invoker.Type.ORIGIN,
    private val special: Boolean = false
) : MethodResolver.Handler() {

    // The invoker of libxposed bypasses access checks, no need to make the member accessible.
    override fun requireAccessible(member: Method) = Unit

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

class LibXposedConstructorHandler(
    private val xposed: XposedInterface,
    private val type: XposedInterface.Invoker.Type = XposedInterface.Invoker.Type.ORIGIN,
    private val subclass: Class<*>? = null
) : ConstructorResolver.Handler() {

    // The invoker of libxposed bypasses access checks, no need to make the member accessible.
    override fun requireAccessible(member: Constructor<*>) = Unit

    override fun <T> newInstance(constructor: Constructor<T>, args: Array<out Any?>): T {
        val invoker = xposed.getInvoker(constructor).setType(type)

        @Suppress("UNCHECKED_CAST")
        return if (subclass != null)
            invoker.newInstanceSpecial(subclass, *args) as T
        else invoker.newInstance(*args)
    }
}
```

> The following example

```kotlin
// Suppose this is your XposedModule.
val module: XposedModule
// Suppose this is an instance of this Class.
val test: Test
// Get the method.
val doTask = Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
// Invoke the original method.
doTask.withHandler(LibXposedMethodHandler(module))
    .invoke("task_name")
// Invoke the original method non-virtually, equivalent to super.doTask("task_name").
doTask.withHandler(LibXposedMethodHandler(module, special = true))
    .invoke("task_name")
// Get the constructor.
val constructor = Test::class.resolve()
    .firstConstructor { parameters(String::class) }
// Create an instance with the original constructor.
val newTest = constructor.withHandler(LibXposedConstructorHandler(module))
    .create("task_name")
// Create a SubTest instance, but only initialize it with the original constructor of Test.
val subTest = constructor.withHandler(LibXposedConstructorHandler(module, subclass = SubTest::class.java))
    .create("task_name")
```

::: warning

An instance created by `subclass` does not call the constructor of the subclass, and the fields in the subclass may remain uninitialized.

:::

## Pine

[Project URL](https://github.com/canyie/pine)

> Dynamic java method hook framework on ART. Allowing you to change almost all java methods' behavior dynamically

Use `Pine.invokeOriginalMethod` to invoke the original method without hooks.

```kotlin
class PineOriginalMethodHandler : MethodResolver.Handler() {

    override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? =
        Pine.invokeOriginalMethod(method, instance, *args)
}
```

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Invoke the original method.
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(PineOriginalMethodHandler())
    .invoke("task_name")
```

## MethodHandle

[Documentation URL](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html)

> `MethodHandles.Lookup` provided by Java 9 and later

Use `MethodHandles.Lookup.unreflectSpecial` to invoke the method non-virtually without any hook framework.

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

> The following example

```kotlin
// Suppose this is an instance of a subclass of Test that overrides the doTask method.
val test: Test
// Invoke the method in Test non-virtually, equivalent to super.doTask("task_name").
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(MethodHandleSpecialHandler())
    .invoke("task_name")
```

## Unsafe

[Documentation URL](https://openjdk.org/jeps/471)

> Memory access methods provided by `sun.misc.Unsafe`

Use `sun.misc.Unsafe` to modify `static final` fields, `Field.set` throws an `IllegalAccessException` when modifying such fields.

```kotlin
class UnsafeFieldHandler : FieldResolver.Handler() {

    private val unsafe = Unsafe::class.resolve().firstField { name = "theUnsafe" }.get<Unsafe>()!!

    override fun set(field: Field, instance: Any?, value: Any?) {
        if (!field.isStatic || !field.isFinal) return super.set(field, instance, value)

        // Unsafe does not initialize the class, initialize it first to
        // prevent the written value from being overwritten.
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

> The following example

```kotlin
// Modify the static final field in Test.
Test::class.resolve()
    .firstField { name = "TASK_NAME" }
    .withHandler(UnsafeFieldHandler())
    .set("new_task_name")
```

::: warning

Compile-time constants (such as `static final String TASK_NAME = "task_name"`) are inlined by the compiler where they are used, and the values there will not change after modification.

The memory access methods of `sun.misc.Unsafe` have been deprecated for removal since JDK 23 ([JEP 471](https://openjdk.org/jeps/471)), and a warning is printed on first use in JDK 24 and later ([JEP 498](https://openjdk.org/jeps/498)).

:::

## Spring Framework

[Project URL](https://github.com/spring-projects/spring-framework)

> Spring Framework

Use Spring's `ReflectionUtils` to invoke methods and read/write fields, runtime exceptions thrown by the method are thrown directly, and checked exceptions are wrapped as `UndeclaredThrowableException` instead of `InvocationTargetException`.

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

> The following example

```kotlin
// Suppose this is an instance of this Class.
val test: Test
// Invoke the method, runtime exceptions are thrown directly.
Test::class.resolve()
    .firstMethod {
        name = "doTask"
        parameters(String::class)
    }.of(test)
    .withHandler(SpringMethodHandler())
    .invoke("task_name")
```