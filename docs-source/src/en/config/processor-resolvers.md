# Third-party Member Resolvers

> Here are some third-party Member resolvers for reference and use.
>
> Please read [Custom Resolver](../library/kavaref-core.md#custom-resolver) for usage instructions.

## AndroidHiddenApiBypass

[Project URL](https://github.com/LSPosed/AndroidHiddenApiBypass)

> LSPosed: Bypass restrictions on non-SDK interfaces

```kotlin
class AndroidHiddenApiBypassResolver : MemberProcessor.Resolver() {

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