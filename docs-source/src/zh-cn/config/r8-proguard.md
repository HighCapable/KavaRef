# R8 与 Proguard 混淆

> 大部分场景下 Android 应用程序安装包可通过混淆压缩体积，这里介绍了混淆规则的配置方法。

如果你使用了 [kavaref-extension](../library/kavaref-extension) 模块，并使用了其中的 `TypeRef` 功能，请在你的 `proguard-rules.pro` 文件中添加以下规则。

```
-keepattributes Signature
-keep class,allowobfuscation * extends com.highcapable.kavaref.extension.TypeRef {
    <init>(...);
}
```

`TypeRef` 已添加 `Keep` 注解，如果无效，请手动添加以下规则以保留类。

```
-keep class com.highcapable.kavaref.extension.TypeRef {*;}
```