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
 * This file is created by fankes on 2025/6/7.
 */
@file:Suppress("unused", "UNCHECKED_CAST", "RemoveExplicitTypeArguments", "FunctionName")
@file:JvmName("TypeUtils")

package com.highcapable.kavaref.extension

import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Converts [Type] to [Class].
 *
 * Generic arrays use the raw class of their component type, preserving array dimensions.
 * Type variables and wildcard types cannot be converted.
 * @see Type.toClassOrNull
 * @receiver the [Type] to be converted.
 * @return [Class]
 * @throws TypeCastException if the conversion fails.
 */
fun Type.toClass(): Class<Any> = when (this) {
    is Class<*> -> this as Class<Any>
    is ParameterizedType -> rawType.toClass()
    is GenericArrayType -> ArrayClass(genericComponentType.toClass()) as Class<Any>
    else -> throw TypeCastException("Cannot cast type $this to a java.lang.Class object.")
}

/**
 * Converts [Type] to [Class], then casts it to [Class]<[T]>.
 * @see Type.toClass
 * @see Type.toClassOrNull
 * @return [Class]<[T]>
 * @throws TypeCastException if the conversion fails.
 * @throws IllegalStateException if the class is not assignable to [T].
 */
@JvmName("toClassAsTyped")
inline fun <reified T : Any> Type.toClass(): Class<T> {
    val type = classOf<T>(primitiveType = false)
    return toClass().also { check(it isSubclassOf type) { "$it is not a subclass of $type" } } as Class<T>
}

/**
 * Safely converts [Type] to [Class] or returns null if it fails.
 * @see Type.toClass
 * @receiver the [Type] to be converted.
 * @return [Class] or null.
 */
fun Type.toClassOrNull() = runCatching { toClass() }.getOrNull()

/**
 * Safely converts [Type] to [Class] and casts it to [Class]<[T]>, or returns null if it fails.
 * @see Type.toClass
 * @see Type.toClassOrNull
 * @return [Class]<[T]> or null if the conversion fails or the class is not assignable to [T].
 */
@JvmName("toClassOrNullAsTyped")
inline fun <reified T : Any> Type.toClassOrNull() = toClassOrNull()?.takeIf { it isSubclassOf classOf<T>(primitiveType = false) } as Class<T>?

/**
 * Converts [Type] to [ParameterizedType].
 * @see Type.asParameterizedTypeOrNull
 * @receiver the [Type] to be converted.
 * @return [ParameterizedType]
 */
inline fun <reified T : Type> T.asParameterizedType() = this as ParameterizedType

/**
 * Safely converts [Type] to [ParameterizedType] or returns null if it fails.
 * @see Type.asParameterizedType
 * @receiver the [Type] to be converted.
 * @return [ParameterizedType] or null.
 */
inline fun <reified T : Type> T.asParameterizedTypeOrNull() = this as? ParameterizedType?

/**
 * Gets the type arguments of the superclass of this [Class] or returns an empty array if it fails.
 *
 * This function is equivalent to the following code:
 *
 * ```kotlin
 * (Class.genericSuperclass as ParameterizedType).actualTypeArguments
 * ```
 * @receiver the [Class] to get the type arguments of its superclass.
 * @return [Array]<[Type]>
 */
fun <T : Any> Class<T>.genericSuperclassTypeArguments(): Array<Type> = runCatching {
    genericSuperclass.asParameterizedTypeOrNull()?.actualTypeArguments ?: emptyArray()
}.getOrDefault(emptyArray())

/**
 * Gets the type arguments of the superclass of this [KClass.java] or returns an empty array if it fails.
 * @see Class.genericSuperclassTypeArguments
 */
@JvmSynthetic
fun <T : Any> KClass<T>.genericSuperclassTypeArguments() = java.genericSuperclassTypeArguments()

// region Binary compatibility for the non-inline functions compiled by previous versions

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmName("toClassTyped")
fun <T : Any> Type._toClass() = toClass() as Class<T>

@Deprecated(message = "", level = DeprecationLevel.HIDDEN)
@JvmName("toClassTypedOrNull")
fun <T : Any> Type._toClassOrNull() = toClassOrNull() as Class<T>?

// endregion