# TypeRef reads the generic signature of its direct subclass at runtime.
-keepattributes Signature

# Keep TypeRef and its (anonymous) subclasses with their generic signatures.
-keep,allowobfuscation class com.highcapable.kavaref.extension.TypeRef
-keep,allowobfuscation class * extends com.highcapable.kavaref.extension.TypeRef