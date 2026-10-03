# R8 与 Proguard 混淆

> 大部分场景下 Android 应用程序安装包可通过混淆压缩体积，这里介绍了混淆规则的配置方法。

[kavaref-extension](../library/kavaref-extension) 模块已经内置了 `TypeRef` 功能所需的混淆规则，R8 会自动读取，通常你无需进行任何配置。

如果你使用的构建工具无法读取依赖中内置的混淆规则 (例如旧版本的 ProGuard)，请在你的 `proguard-rules.pro` 文件中手动添加以下规则。

```
-keepattributes Signature
-keep,allowobfuscation class com.highcapable.kavaref.extension.TypeRef
-keep,allowobfuscation class * extends com.highcapable.kavaref.extension.TypeRef
```