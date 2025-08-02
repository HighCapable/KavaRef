# Looking Toward the Future

> The future is bright and uncertain, let us look forward to the future development potential of `KavaRef`.

## Future Plans

> Features that `KavaRef` may add later are included here.

### Support Class Filtering through ClassLoader

`KavaRef` currently only supports search and calls for reflection APIs such as `Method`, `Field`, and `Constructor`.
In the future, the ability to filter `Class` by specified `ClassLoader` type may be supported on Java and Android platforms according to requirements.

Currently, you can use [DexKit](https://github.com/LuckyPray/DexKit) to fulfill this requirement,
which also supports more complex searches and calls for reflective APIs such as Method, Field, and Constructor.

### Automatically Generate Reflection Code

**This is a feature that was initially established in [YukiReflection](https://github.com/HighCapable/YukiReflection), and `KavaRef` is ready to continue implementing it at some point in the future.**

Use `stub` to create a Kotlin class, and declare the parameters in it, as well as its different states in each version.

For example, the Java class below is the target class we need to reflect.

> The following example

```java:no-line-numbers
package com.example.test;

public class MyClass {
    
    private String myField = "test";

    public MyClass() {
        //...
    }

    private String myMethod1(String var1, int var2) {
        //...
    }

    private void myMethod2() {
        //...
    }

    private void myMethod3(String var1) {
        //...
    }
}
```

Through the existing usage of the current API, this class can be called reflectively in the following way.

> The following example

```kotlin
MyClass().resolve().apply {
    // Call myField.
    val value = firstField { name = "myField" }.get<String>()
    // Call myMethod1.
    val methodValue = firstMethod { name = "myMethod1" }.invoke<String>("test", 0)
    // Call myMethod2.
    firstMethod { name = "myMethod2" }.invoke()
    // Call myMethod3.
    firstMethod { name = "myMethod3" }.invoke("test")
}
```

The function to be implemented can currently be directly defined as the following Kotlin class using the reflection functionality.

> The following example

```kotlin
package com.example.test

@ReflectClass
class MyClass {

    @ReflectField
    val myField: String = fieldValueOf("none")

    @ReflectMethod
    fun myMethod1(var1: String, var2: Int): String = methodReturnValueOf("none")

    @ReflectMethod
    fun myMethod2() = MethodReturnType.Unit

    @ReflectMethod
    fun myMethod3(var1: String) = MethodReturnType.Unit
}
```

Then we can directly call this defined Kotlin class to implement the reflection functionality, and the API will automatically generate the reflection code according to the annotations.

> The following example

```kotlin
MyClass().also {
    // Call myField
    val value = it.myField
    // Call myMethod1
    val methodValue = it.myMethod1("test", 0)
    // Call myMethod2
    it.myMethod2()
    // Call myMethod3
    it.myMethod3("test")
}
```

::: tip

The above functions may change after the actual release, and the features of the actual version shall prevail.

:::