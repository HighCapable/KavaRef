# R8 & ProGuard Obfuscation

> In most scenarios, Android application installation packages can reduce size through obfuscation.
> Here is a configuration method for obfuscation rules.

If you are using the [kavaref-extension](../library/kavaref-extension) module and using the `TypeRef` feature, please add the following rule to your `proguard-rules.pro` file.

```
-keepattributes Signature
-keep class * extends com.highcapable.kavaref.extension.TypeRef {
    <init>(...);
}
```

`TypeRef` has been annotated with `Keep`. If it doesn't work, please manually add the following rules to keep the class.

```
-keep class com.highcapable.kavaref.extension.TypeRef {*;}
-keep class,allowobfuscation * extends com.highcapable.kavaref.extension.TypeRef
```