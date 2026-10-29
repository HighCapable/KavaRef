# 第三方 Member 解析器

> 这里收录了一些第三方的 Member 解析器，可供参考与使用。
>
> 使用方法请阅读 [自定义解析器](../library/kavaref-core.md#自定义解析器)。

## AndroidHiddenApiBypass

[项目地址](https://github.com/LSPosed/AndroidHiddenApiBypass)

> LSPass: Bypass restrictions on non-SDK interfaces

```kotlin
class AndroidHiddenApiBypassResolver : MemberProcessor.Resolver() {

    override fun <T : Any> getDeclaredFields(declaringClass: Class<T>): List<Field> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return super.getDeclaredFields(declaringClass)
        }

        val fields = HiddenApiBypass.getStaticFields(declaringClass) + HiddenApiBypass.getInstanceFields(declaringClass)
        return fields.toList()
    }

    override fun <T : Any> getDeclaredConstructors(declaringClass: Class<T>): List<Constructor<T>> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return super.getDeclaredConstructors(declaringClass)
        }

        val constructors = HiddenApiBypass.getDeclaredMethods(declaringClass)
            .filterIsInstance<Constructor<T>>()
            .toList()
        return constructors
    }

    override fun <T : Any> getDeclaredMethods(declaringClass: Class<T>): List<Method> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return super.getDeclaredMethods(declaringClass)
        }

        val methods = HiddenApiBypass.getDeclaredMethods(declaringClass)
            .filterIsInstance<Method>()
            .toList()
        return methods
    }
}
```