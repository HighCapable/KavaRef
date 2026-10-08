/*
 * KavaRef - A modernizing Java Reflection with Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/KavaRef
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2025/5/31.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "PLATFORM_CLASS_MAPPED_TO_KOTLIN", "FunctionName")
@file:JvmName("ClassUtils")

package com.highcapable.kavaref.extension

import java.lang.ref.WeakReference
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier
import java.util.WeakHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** The function type that provides a [ClassLoader] instance. */
private typealias ClassLoaderInitializer = () -> ClassLoader?

/**
 * The implementation of [createInstance].
 *
 * Selects the constructor like Java overload resolution and creates the instance.
 */
private object InstanceCreator {

    private val primitiveWideningTargets = mapOf<Class<*>, Set<Class<*>>>(
        JByte.TYPE to setOf(JShort.TYPE, JInteger.TYPE, JLong.TYPE, JFloat.TYPE, JDouble.TYPE),
        JShort.TYPE to setOf(JInteger.TYPE, JLong.TYPE, JFloat.TYPE, JDouble.TYPE),
        JCharacter.TYPE to setOf(JInteger.TYPE, JLong.TYPE, JFloat.TYPE, JDouble.TYPE),
        JInteger.TYPE to setOf(JLong.TYPE, JFloat.TYPE, JDouble.TYPE),
        JLong.TYPE to setOf(JFloat.TYPE, JDouble.TYPE),
        JFloat.TYPE to setOf(JDouble.TYPE)
    )

    /**
     * Cache for the selected constructors.
     *
     * Nothing in it strongly references the declaring class or the argument classes,
     * so their [ClassLoader] can still be collected.
     */
    private val constructorsCache = WeakHashMap<Class<*>, MutableMap<ArgumentTypes, CachedConstructor>>()

    /**
     * The argument types of a [create] call, held weakly as a key of [constructorsCache].
     */
    private class ArgumentTypes(args: Array<out Any?>, private val isPublic: Boolean) {

        private val types = args.map { arg -> arg?.javaClass?.let { WeakReference(it) } }
        private val hashCode = args.fold(isPublic.hashCode()) { hash, arg -> 31 * hash + System.identityHashCode(arg?.javaClass) }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) = other is ArgumentTypes && isPublic == other.isPublic &&
            types.size == other.types.size && types.indices.all { index ->
                val type = types[index]
                val otherType = other.types[index]

                if (type == null || otherType == null)
                    type == null && otherType == null
                else type.get()?.let { it === otherType.get() } == true
            }
    }

    /**
     * The selected constructor of [constructorsCache].
     *
     * It is held weakly and found again by [index] in [Class.getDeclaredConstructors] after being collected,
     * the found one is only used if the constructors [count] is unchanged, and it passes [get]'s validation,
     * otherwise the constructor needs to be selected again.
     */
    private class CachedConstructor(private val index: Int, private val count: Int, constructor: Constructor<*>) {

        @Volatile
        private var reference = WeakReference(constructor)

        fun get(declaringClass: Class<*>, isValid: (Constructor<*>) -> Boolean) = reference.get()
            ?: declaringClass.declaredConstructors.takeIf { it.size == count }?.getOrNull(index)
                ?.takeIf(isValid)?.also { reference = WeakReference(it) }
    }

    fun <T : Any> create(declaringClass: Class<T>, args: Array<out Any?>, isPublic: Boolean): T {
        // If all arguments are null, throw an exception.
        if (args.isNotEmpty() && args.all { it == null })
            error("Not allowed to create an instance with all null arguments for $declaringClass")

        val constructor = cachedOrSelect(declaringClass, args, isPublic) ?: throw NoSuchMethodError(
            "Could not find a suitable constructor for $declaringClass with arguments: ${args.describe()}"
        )
        require(constructor.makeAccessible()) {
            "Failed to make the constructor \"$constructor\" accessible. " +
                "Please check if the constructor is accessible or if the security manager allows it."
        }

        return constructor.newInstance(*args) as T
    }

    private fun cachedOrSelect(declaringClass: Class<*>, args: Array<out Any?>, isPublic: Boolean): Constructor<*>? {
        val key = ArgumentTypes(args, isPublic)
        synchronized(constructorsCache) { constructorsCache[declaringClass]?.get(key) }
            ?.get(declaringClass) { it.isApplicable(args, isPublic) }?.let { return it }
        val constructors = declaringClass.declaredConstructors
        val (index, constructor) = select(declaringClass, constructors, args, isPublic) ?: return null
        synchronized(constructorsCache) {
            constructorsCache.getOrPut(declaringClass) { HashMap() }[key] = CachedConstructor(index, constructors.size, constructor)
        }

        return constructor
    }

    private fun select(
        declaringClass: Class<*>,
        constructors: Array<Constructor<*>>,
        args: Array<out Any?>,
        isPublic: Boolean
    ): IndexedValue<Constructor<*>>? {
        // Read the parameter types only once, because they are copied on every access.
        val candidates = constructors.withIndex().mapNotNull { constructor ->
            constructor.value.parameterTypes.takeIf { (!isPublic || constructor.value.isPublic) && it.size == args.size }?.let { constructor to it }
        }
        // Same as Java overload resolution: try without unboxing first, then allow unboxing and widening.
        booleanArrayOf(false, true).forEach { allowUnboxing ->
            val applicable = candidates.filter { (_, types) -> types.indices.all { types[it].acceptsArgument(args[it], allowUnboxing) } }
            if (applicable.isEmpty()) return@forEach
            val mostSpecific = applicable.filter { (_, types) ->
                applicable.all { (_, otherTypes) -> types === otherTypes || types.isMoreSpecificThan(otherTypes) }
            }

            return mostSpecific.singleOrNull()?.first ?: throw IllegalArgumentException(
                "Ambiguous constructors for $declaringClass with arguments: ${args.describe()}, " +
                    "candidates: ${applicable.joinToString { it.first.value.toString() }}"
            )
        }

        return null
    }

    private fun Constructor<*>.isApplicable(args: Array<out Any?>, isPublic: Boolean) =
        (!isPublic || this.isPublic) && parameterTypes.let { types ->
            types.size == args.size && types.indices.all { types[it].acceptsArgument(args[it], allowUnboxing = true) }
        }

    private fun Class<*>.isWideningTo(target: Class<*>) = this == target || primitiveWideningTargets[this]?.contains(target) == true

    private fun Class<*>.acceptsArgument(arg: Any?, allowUnboxing: Boolean) = when {
        arg == null -> !isPrimitive
        isPrimitive -> allowUnboxing && arg.javaClass.kotlin.javaPrimitiveType?.isWideningTo(this) == true
        else -> isInstance(arg)
    }

    private fun Class<*>.isMoreSpecificThan(other: Class<*>) = when {
        isPrimitive && other.isPrimitive -> isWideningTo(other)
        isPrimitive -> kotlin.javaObjectType isSubclassOf other
        other.isPrimitive -> false
        else -> this isSubclassOf other
    }

    private fun Array<Class<*>>.isMoreSpecificThan(other: Array<Class<*>>) = indices.all { this[it].isMoreSpecificThan(other[it]) }
    private fun Array<out Any?>.describe() = joinToString().ifBlank { "(empty)" }
}

/**
 * Provides a [ClassLoader] for reflection operations.
 */
object ClassLoaderProvider {

    /**
     * The [ClassLoader] used for reflection operations.
     *
     * If null, the [ClassLoader] that loaded KavaRef will be used.
     */
    var classLoader: ClassLoader? = null
}

/**
 * A class that can load various classes by their names.
 * @param names the names of the classes to be loaded.
 */
class VariousClass(vararg names: String) {

    private val classNames = names.toList()

    /**
     * Loads the first class that matches the given names using the specified [ClassLoader].
     *
     * If no class is found, it throws a [NoClassDefFoundError].
     * @see loadOrNull
     * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
     * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
     * @param initialize whether to initialize the class with [loader], default is false.
     * @return [Class]
     * @throws NoClassDefFoundError if no class is found.
     */
    @JvmOverloads
    fun load(loader: ClassLoader? = null, initialize: Boolean = false) = loadOrNull(loader, initialize)
        ?: throw NoClassDefFoundError("Failed to match any class of VariousClass $classNames")

    /**
     * Loads the first class that matches the given names using the specified [ClassLoader] and casts it to [Class]<[T]>.
     * @see load
     * @see loadOrNull
     * @return [Class]<[T]>
     * @throws NoClassDefFoundError if no class is found.
     * @throws IllegalStateException if the class is not assignable to [T].
     */
    @JvmName("loadAsTyped")
    inline fun <reified T : Any> load(loader: ClassLoader? = null, initialize: Boolean = false): Class<T> {
        val type = classOf<T>(primitiveType = false)
        return load(loader, initialize).also { check(it isSubclassOf type) { "$it is not a subclass of $type" } } as Class<T>
    }

    /**
     * Loads the first class that matches the given names using the specified [ClassLoader].
     *
     * If no class is found, it returns null.
     * @see load
     * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
     * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
     * @param initialize whether to initialize the class with [loader], default is false.
     * @return [Class] or null if no class is found.
     */
    @JvmOverloads
    fun loadOrNull(loader: ClassLoader? = null, initialize: Boolean = false): Class<Any>? =
        classNames.firstOrNull { it.toClassOrNull(loader) != null }?.toClass(loader, initialize)

    /**
     * Loads the first class that matches the given names using the specified [ClassLoader] and casts it to [Class]<[T]>.
     * @see load
     * @see loadOrNull
     * @return [Class]<[T]> or null if no class is found or the class is not assignable to [T].
     */
    @JvmName("loadOrNullAsTyped")
    inline fun <reified T : Any> loadOrNull(loader: ClassLoader? = null, initialize: Boolean = false) =
        loadOrNull(loader, initialize)?.takeIf { it isSubclassOf classOf<T>(primitiveType = false) } as Class<T>?

    // region Binary compatibility for the non-inline functions compiled by previous versions

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmOverloads
    @JvmName("loadTyped")
    fun <T : Any> _load(loader: ClassLoader? = null, initialize: Boolean = false) = load(loader, initialize) as Class<T>

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmOverloads
    @JvmName("loadOrNullTyped")
    fun <T : Any> _loadOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = loadOrNull(loader, initialize) as Class<T>?

    // endregion
}

/**
 * Lazy loading [Class] instance.
 * @param classDefinition the class definition.
 * @param type the type that the class must be assignable to.
 * @param initialize whether to initialize the class with [loader].
 * @param loader the [ClassLoader] to load the class.
 */
abstract class LazyClass<T : Any> private constructor(
    private val classDefinition: Any,
    private val type: Class<T>,
    private val initialize: Boolean,
    private val loader: ClassLoaderInitializer?
) {

    @Volatile
    private var baseDefinition: Class<T>? = null

    /** Whether the class is not found, the nullable instance does not try to load it again. */
    @Volatile
    private var isMissing = false

    /**
     * Gets non-null instance of [Class].
     * @return [Class]<[T]>
     */
    @get:JvmSynthetic
    internal val nonNull get(): Class<T> = baseDefinition ?: synchronized(this) {
        baseDefinition?.let { return@synchronized it }
        val clazz = when (classDefinition) {
            is String -> classDefinition.toClass(loader?.invoke(), initialize)
            is VariousClass -> classDefinition.load(loader?.invoke(), initialize)
            else -> error("Unknown lazy class type \"$classDefinition\"")
        }

        check(clazz isSubclassOf type) {
            "$clazz is not a subclass of $type"
        }
        (clazz as Class<T>).also { baseDefinition = it }
    }

    /**
     * Gets nullable instance of [Class].
     * @return [Class]<[T]> or null.
     */
    @get:JvmSynthetic
    internal val nullable get(): Class<T>? = baseDefinition ?: if (!isMissing) synchronized(this) {
        if (isMissing) return@synchronized null

        baseDefinition?.let { return@synchronized it }
        val clazz = when (classDefinition) {
            is String -> classDefinition.toClassOrNull(loader?.invoke(), initialize)
            is VariousClass -> classDefinition.loadOrNull(loader?.invoke(), initialize)
            else -> error("Unknown lazy class type \"$classDefinition\"")
        }?.takeIf { it isSubclassOf type }
        (clazz as Class<T>?).also { if (it != null) baseDefinition = it else isMissing = true }
    } else null

    /**
     * Creates a non-null instance of [Class].
     * @param classDefinition the class definition, can be a [String] class name or a [VariousClass].
     * @param type the type that the class must be assignable to.
     * @param initialize whether to initialize the class with [loader].
     * @param loader the [ClassLoader] to load the class.
     */
    class NonNull<T : Any> private constructor(
        classDefinition: Any,
        type: Class<T>,
        initialize: Boolean,
        loader: ClassLoaderInitializer?
    ) : LazyClass<T>(classDefinition, type, initialize, loader) {

        @PublishedApi
        internal companion object {

            /**
             * Creates a non-null instance of [Class].
             * @see NonNull
             */
            @JvmSynthetic
            fun <T : Any> from(
                classDefinition: Any,
                type: Class<T>,
                initialize: Boolean,
                loader: ClassLoaderInitializer?
            ) = NonNull(classDefinition, type, initialize, loader)
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = nonNull
    }

    /**
     * Creates a nullable instance of [Class].
     * @param classDefinition the class definition, can be a [String] class name or a [VariousClass].
     * @param type the type that the class must be assignable to.
     * @param initialize whether to initialize the class with [loader].
     * @param loader the [ClassLoader] to load the class.
     */
    class Nullable<T : Any> private constructor(
        classDefinition: Any,
        type: Class<T>,
        initialize: Boolean,
        loader: ClassLoaderInitializer?
    ) : LazyClass<T>(classDefinition, type, initialize, loader) {

        @PublishedApi
        internal companion object {

            /**
             * Creates a nullable instance of [Class].
             * @see Nullable
             */
            @JvmSynthetic
            fun <T : Any> from(
                classDefinition: Any,
                type: Class<T>,
                initialize: Boolean,
                loader: ClassLoaderInitializer?
            ) = Nullable(classDefinition, type, initialize, loader)
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = nullable
    }
}

/**
 * Creates a non-null instance of [Class].
 * @see lazyClassOrNull
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
 * @return [LazyClass.NonNull]
 */
@JvmSynthetic
fun lazyClass(
    name: String,
    initialize: Boolean = false,
    loader: ClassLoaderInitializer? = null
) = LazyClass.NonNull.from(name, classOf<Any>(), initialize, loader)

/**
 * Creates a non-null instance of [Class].
 * @see lazyClass
 * @see lazyClassOrNull
 * @return [LazyClass.NonNull]<[T]>
 */
@JvmName("lazyClassAsTyped")
inline fun <reified T : Any> lazyClass(
    name: String,
    initialize: Boolean = false,
    noinline loader: ClassLoaderInitializer? = null
) = LazyClass.NonNull.from(name, classOf<T>(primitiveType = false), initialize, loader)

/**
 * Creates a non-null instance of [VariousClass].
 * @see lazyClassOrNull
 * @param variousClass the [VariousClass] to be loaded.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
 * @return [LazyClass.NonNull]
 */
@JvmSynthetic
fun lazyClass(
    variousClass: VariousClass,
    initialize: Boolean = false,
    loader: ClassLoaderInitializer? = null
) = LazyClass.NonNull.from(variousClass, classOf<Any>(), initialize, loader)

/**
 * Creates a non-null instance of [VariousClass].
 * @see lazyClass
 * @see lazyClassOrNull
 * @return [LazyClass.NonNull]<[T]>
 */
@JvmName("lazyClassAsTyped")
inline fun <reified T : Any> lazyClass(
    variousClass: VariousClass,
    initialize: Boolean = false,
    noinline loader: ClassLoaderInitializer? = null
) = LazyClass.NonNull.from(variousClass, classOf<T>(primitiveType = false), initialize, loader)

/**
 * Creates a nullable instance of [Class].
 * @see lazyClass
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
 * @return [LazyClass.Nullable]
 */
@JvmSynthetic
fun lazyClassOrNull(
    name: String,
    initialize: Boolean = false,
    loader: ClassLoaderInitializer? = null
) = LazyClass.Nullable.from(name, classOf<Any>(), initialize, loader)

/**
 * Creates a nullable instance of [Class].
 * @see lazyClass
 * @see lazyClassOrNull
 * @return [LazyClass.Nullable]<[T]>
 */
@JvmName("lazyClassOrNullAsTyped")
inline fun <reified T : Any> lazyClassOrNull(
    name: String,
    initialize: Boolean = false,
    noinline loader: ClassLoaderInitializer? = null
) = LazyClass.Nullable.from(name, classOf<T>(primitiveType = false), initialize, loader)

/**
 * Creates a nullable instance of [VariousClass].
 * @see lazyClass
 * @param variousClass the [VariousClass] to be loaded.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @param loader the [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded KavaRef will be used.
 * @return [LazyClass.Nullable]
 */
@JvmSynthetic
fun lazyClassOrNull(
    variousClass: VariousClass,
    initialize: Boolean = false,
    loader: ClassLoaderInitializer? = null
) = LazyClass.Nullable.from(variousClass, classOf<Any>(), initialize, loader)

/**
 * Creates a nullable instance of [VariousClass].
 * @see lazyClass
 * @see lazyClassOrNull
 * @return [LazyClass.Nullable]<[T]>
 */
@JvmName("lazyClassOrNullAsTyped")
inline fun <reified T : Any> lazyClassOrNull(
    variousClass: VariousClass,
    initialize: Boolean = false,
    noinline loader: ClassLoaderInitializer? = null
) = LazyClass.Nullable.from(variousClass, classOf<T>(primitiveType = false), initialize, loader)

/**
 * Converts [String] class name to [Class] with [ClassLoader].
 * @see String.toClassOrNull
 * @receiver the class name to be converted.
 * @param loader [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded `KavaRef` will be used.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @return [Class]
 */
@JvmOverloads
@JvmName("create")
fun String.toClass(loader: ClassLoader? = null, initialize: Boolean = false): Class<Any> {
    val createLoader = loader ?: ClassLoaderProvider.classLoader ?: ClassLoaderProvider::class.java.classLoader

    return Class.forName(this, initialize, createLoader) as Class<Any>
}

/**
 * Converts [String] class name to [Class] with [ClassLoader], then casts it to [Class]<[T]>.
 * @see Class.toClass
 * @see String.toClassOrNull
 * @return [Class]<[T]>
 * @throws IllegalStateException if the class is not assignable to [T].
 */
@JvmName("createAsTyped")
inline fun <reified T : Any> String.toClass(loader: ClassLoader? = null, initialize: Boolean = false): Class<T> {
    val type = classOf<T>(primitiveType = false)
    return toClass(loader, initialize).also { check(it isSubclassOf type) { "$it is not a subclass of $type" } } as Class<T>
}

/**
 * Converts [String] class name to [Class] with [ClassLoader].
 * @see String.toClassOrNull
 * @receiver the class name to be converted.
 * @param loader [ClassLoader] to load the class, default is [ClassLoaderProvider.classLoader],
 * if it is also null, the [ClassLoader] that loaded `KavaRef` will be used.
 * @param initialize whether to initialize the class with [loader], default is false.
 * @return [Class] or null if the class is not found.
 */
@JvmOverloads
@JvmName("createOrNull")
fun String.toClassOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = runCatching {
    toClass(loader, initialize)
}.getOrNull()

/**
 * Converts [String] class name to [Class] with [ClassLoader], then casts it to [Class]<[T]>.
 * @see Class.toClass
 * @see String.toClassOrNull
 * @return [Class]<[T]> or null if the class is not found or not assignable to [T].
 */
@JvmName("createOrNullAsTyped")
inline fun <reified T : Any> String.toClassOrNull(loader: ClassLoader? = null, initialize: Boolean = false) =
    toClassOrNull(loader, initialize)?.takeIf { it isSubclassOf classOf<T>(primitiveType = false) } as Class<T>?

/**
 * Creates an instance of [Class] with the given arguments.
 *
 * The constructor is selected like Java overload resolution, constructors that accept the arguments
 * without unboxing are preferred, otherwise unboxing and primitive widening are allowed,
 * then the most specific one is used.
 *
 * - Note: If you give a null argument, it will be treated as a value of any non-primitive type for the constructor parameter,
 *   but if all arguments are null, it will throw an [IllegalStateException].
 * @see Class.createInstanceOrNull
 * @see Class.createInstanceAsType
 * @see Class.createInstanceAsTypeOrNull
 * @receiver the [Class] to be instantiated.
 * @param args the arguments to be passed to the constructor.
 * @param isPublic whether to only consider public constructors, default is true.
 * @return [T]
 * @throws NoSuchMethodError if no suitable constructor is found.
 * @throws IllegalArgumentException if more than one constructor is the most specific.
 * @throws IllegalStateException if all arguments are null.
 */
@JvmOverloads
fun <T : Any> Class<T>.createInstance(vararg args: Any?, isPublic: Boolean = true) =
    InstanceCreator.create(this, args, isPublic)

/**
 * Creates an instance of [KClass.java] with the given arguments.
 * @see Class.createInstance
 */
@JvmSynthetic
fun <T : Any> KClass<T>.createInstance(vararg args: Any?, isPublic: Boolean = true) =
    java.createInstance(*args, isPublic = isPublic)

/**
 * Creates an instance of [Class] with the given arguments or returns null if failed.
 * @see Class.createInstance
 * @see Class.createInstanceAsType
 * @see Class.createInstanceAsTypeOrNull
 * @receiver the [Class] to be instantiated.
 * @param args the arguments to be passed to the constructor.
 * @param isPublic whether to only consider public constructors, default is true.
 * @return [T] or null if the instance cannot be created.
 */
@JvmOverloads
fun <T : Any> Class<T>.createInstanceOrNull(vararg args: Any?, isPublic: Boolean = true) =
    runCatching { createInstance(*args, isPublic = isPublic) }.getOrNull()

/**
 * Creates an instance of [KClass.java] with the given arguments or returns null if failed.
 * @see Class.createInstanceOrNull
 */
@JvmSynthetic
fun <T : Any> KClass<T>.createInstanceOrNull(vararg args: Any?, isPublic: Boolean = true) =
    java.createInstanceOrNull(*args, isPublic = isPublic)

/**
 * Creates an instance of [Class] with the given arguments and casts it to the specified type [T].
 * @see Class.createInstance
 * @see Class.createInstanceOrNull
 * @see Class.createInstanceAsTypeOrNull
 * @return [T]
 * @throws NoSuchMethodError if no suitable constructor is found.
 * @throws IllegalStateException if the instance cannot be cast to type [T] or if all arguments are null.
 */
inline fun <reified T : Any> Class<*>.createInstanceAsType(vararg args: Any?, isPublic: Boolean = true) =
    createInstance(*args, isPublic = isPublic) as? T ?: error("$this's instance cannot be cast to type ${classOf<T>()}")

/**
 * Creates an instance of [KClass.java] with the given arguments and casts it to the specified type [T].
 * @see Class.createInstanceAsType
 */
inline fun <reified T : Any> KClass<*>.createInstanceAsType(vararg args: Any?, isPublic: Boolean = true) =
    java.createInstanceAsType<T>(*args, isPublic = isPublic)

/**
 * Creates an instance of [Class] with the given arguments and casts it to the specified type [T] or returns null if failed.
 * @see Class.createInstance
 * @see Class.createInstanceOrNull
 * @see Class.createInstanceAsType
 * @return [T] or null if the instance cannot be created or cannot be cast to type [T].
 */
inline fun <reified T : Any> Class<*>.createInstanceAsTypeOrNull(vararg args: Any?, isPublic: Boolean = true) =
    runCatching { createInstanceAsType<T>(*args, isPublic = isPublic) }.getOrNull()

/**
 * Creates an instance of [KClass.java] with the given arguments and casts it to the specified type [T] or returns null if failed.
 * @see Class.createInstanceAsTypeOrNull
 */
inline fun <reified T : Any> KClass<*>.createInstanceAsTypeOrNull(vararg args: Any?, isPublic: Boolean = true) =
    java.createInstanceAsTypeOrNull<T>(*args, isPublic = isPublic)

/**
 * Loads the class with the given name using [ClassLoader] or returns null if not found.
 * @receiver the [ClassLoader] to be used.
 * @param name the class name to be loaded.
 * @return [Class] or null.
 */
fun ClassLoader.loadClassOrNull(name: String) = runCatching { loadClass(name) as Class<Any> }.getOrNull()

/**
 * Checks if the [ClassLoader] can load the class with the given name.
 * @receiver the [ClassLoader] to be checked.
 * @param name the class name to be checked.
 * @return [Boolean] true if the class can be loaded, false otherwise.
 */
fun ClassLoader.hasClass(name: String) = loadClassOrNull(name) != null

/**
 * Gets the [Class] of [T].
 * @param primitiveType whether to return the primitive type of [T] if it is a primitive type, default is true.
 * @return [Class]<[T]>
 */
inline fun <reified T : Any> classOf(primitiveType: Boolean = true) =
    if (primitiveType) T::class.javaPrimitiveType ?: T::class.java else T::class.javaObjectType

/**
 * Uses [Class.isAssignableFrom] to check if [Class] is a subclass of [superclass].
 * @see Class.isNotSubclassOf
 * @receiver the class to be checked.
 * @param superclass the superclass to be checked.
 * @return true if [Class] is a subclass of [superclass], false otherwise.
 */
infix fun <T : Any> Class<T>.isSubclassOf(superclass: Class<*>) = superclass.isAssignableFrom(this)

/**
 * Uses [Class.isAssignableFrom] to check if [KClass.java] is a subclass of [superclass] ([KClass.java]).
 * @see Class.isSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> KClass<T>.isSubclassOf(superclass: KClass<*>) = java isSubclassOf superclass.java

/**
 * Uses [Class.isAssignableFrom] to check if [KClass.java] is a subclass of [superclass].
 * @see Class.isSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> KClass<T>.isSubclassOf(superclass: Class<*>) = java isSubclassOf superclass

/**
 * Uses [Class.isAssignableFrom] to check if [Class] is a subclass of [superclass] ([KClass.java]).
 * @see Class.isSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> Class<T>.isSubclassOf(superclass: KClass<*>) = this isSubclassOf superclass.java

/**
 * Uses [Class.isAssignableFrom] to check if [Class] is not a subclass of [superclass].
 * @see Class.isSubclassOf
 * @receiver the class to be checked.
 * @param superclass the superclass to be checked.
 * @return true if [Class] is not a subclass of [superclass], false otherwise.
 */
infix fun <T : Any> Class<T>.isNotSubclassOf(superclass: Class<*>) = !isSubclassOf(superclass)

/**
 * Uses [Class.isAssignableFrom] to check if [KClass.java] is not a subclass of [superclass] ([KClass.java]).
 * @see Class.isNotSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> KClass<T>.isNotSubclassOf(superclass: KClass<*>) = java isNotSubclassOf superclass.java

/**
 * Uses [Class.isAssignableFrom] to check if [KClass.java] is not a subclass of [superclass].
 * @see Class.isNotSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> KClass<T>.isNotSubclassOf(superclass: Class<*>) = java isNotSubclassOf superclass

/**
 * Uses [Class.isAssignableFrom] to check if [Class] is not a subclass of [superclass] ([KClass.java]).
 * @see Class.isNotSubclassOf
 */
@JvmSynthetic
infix fun <T : Any> Class<T>.isNotSubclassOf(superclass: KClass<*>) = this isNotSubclassOf superclass.java

/**
 * Whether the current [Class] has a superclass,
 * [Any] is not considered as a superclass.
 * @receiver the [Class] to be checked.
 * @return [Boolean]
 */
val <T : Any> Class<T>.hasSuperclass get() = superclass != null && superclass != classOf<Any>()

/**
 * Whether the current [KClass.java] has a superclass,
 * [Any] is not considered as a superclass.
 * @see Class.hasSuperclass
 */
@get:JvmSynthetic
val <T : Any> KClass<T>.hasSuperclass get() = java.hasSuperclass

/**
 * Whether the current [Class] has implemented interfaces.
 * @receiver the [Class] to be checked.
 * @return [Boolean]
 */
val <T : Any> Class<T>.hasInterfaces get() = interfaces.isNotEmpty()

/**
 * Whether the current [KClass.java] has implemented interfaces.
 * @see Class.hasInterfaces
 */
@get:JvmSynthetic
val <T : Any> KClass<T>.hasInterfaces get() = java.hasInterfaces

// region Binary compatibility for the non-inline functions compiled by previous versions

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
@JvmName("lazyClassTyped")
fun <T : Any> _lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.NonNull.from(name, classOf<Any>() as Class<T>, initialize, loader)

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
@JvmName("lazyClassTyped")
fun <T : Any> _lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.NonNull.from(variousClass, classOf<Any>() as Class<T>, initialize, loader)

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
@JvmName("lazyClassOrNullTyped")
fun <T : Any> _lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.Nullable.from(name, classOf<Any>() as Class<T>, initialize, loader)

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
@JvmName("lazyClassOrNullTyped")
fun <T : Any> _lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.Nullable.from(variousClass, classOf<Any>() as Class<T>, initialize, loader)

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmOverloads
@JvmName("createTyped")
fun <T : Any> String._toClass(loader: ClassLoader? = null, initialize: Boolean = false) = toClass(loader, initialize) as Class<T>

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmOverloads
@JvmName("createOrNullTyped")
fun <T : Any> String._toClassOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = toClassOrNull(loader, initialize) as Class<T>?

// endregion

// region Class extension properties for checking modifiers

/**
 * Checks if the [Class] is public.
 * @see Modifier.isPublic
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is public, `false` otherwise.
 */
val <T : Any> Class<T>.isPublic get() = Modifier.isPublic(modifiers)

/**
 * Checks if the [Class] is private.
 * @see Modifier.isPrivate
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is private, `false` otherwise.
 */
val <T : Any> Class<T>.isPrivate get() = Modifier.isPrivate(modifiers)

/**
 * Checks if the [Class] is protected.
 * @see Modifier.isProtected
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is protected, `false` otherwise.
 */
val <T : Any> Class<T>.isProtected get() = Modifier.isProtected(modifiers)

/**
 * Checks if the [Class] is static.
 * @see Modifier.isStatic
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is static, `false` otherwise.
 */
val <T : Any> Class<T>.isStatic get() = Modifier.isStatic(modifiers)

/**
 * Checks if the [Class] is final.
 * @see Modifier.isFinal
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is final, `false` otherwise.
 */
val <T : Any> Class<T>.isFinal get() = Modifier.isFinal(modifiers)

/**
 * Checks if the [Class] is synchronized.
 * @see Modifier.isSynchronized
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is synchronized, `false` otherwise.
 */
val <T : Any> Class<T>.isSynchronized get() = Modifier.isSynchronized(modifiers)

/**
 * Checks if the [Class] is volatile.
 * @see Modifier.isVolatile
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is volatile, `false` otherwise.
 */
val <T : Any> Class<T>.isVolatile get() = Modifier.isVolatile(modifiers)

/**
 * Checks if the [Class] is transient.
 * @see Modifier.isTransient
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is transient, `false` otherwise.
 */
val <T : Any> Class<T>.isTransient get() = Modifier.isTransient(modifiers)

/**
 * Checks if the [Class] is native.
 * @see Modifier.isNative
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is native, `false` otherwise.
 */
val <T : Any> Class<T>.isNative get() = Modifier.isNative(modifiers)

/**
 * Checks if the [Class] is abstract.
 * @see Modifier.isAbstract
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is abstract, `false` otherwise.
 */
val <T : Any> Class<T>.isAbstract get() = Modifier.isAbstract(modifiers)

/**
 * Checks if the [Class] is strict.
 * @see Modifier.isStrict
 * @receiver the [Class] to be checked.
 * @return `true` if the [Class] is strict, `false` otherwise.
 */
val <T : Any> Class<T>.isStrict get() = Modifier.isStrict(modifiers)

// endregion