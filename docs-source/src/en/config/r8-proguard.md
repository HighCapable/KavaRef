# R8 & ProGuard Obfuscation

> In most scenarios, Android application installation packages can reduce size through obfuscation.
> Here is a configuration method for obfuscation rules.

The [kavaref-extension](../library/kavaref-extension) module already includes the obfuscation rules required by the `TypeRef` feature.
R8 reads them automatically, so usually you don't need any configuration.

If your build tool cannot read the obfuscation rules included in dependencies (such as older versions of ProGuard),
please manually add the following rules to your `proguard-rules.pro` file.

```
-keepattributes Signature
-keep,allowobfuscation class com.highcapable.kavaref.extension.TypeRef
-keep,allowobfuscation class * extends com.highcapable.kavaref.extension.TypeRef
```