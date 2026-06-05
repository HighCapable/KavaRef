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
@file:Suppress("unused", "UNCHECKED_CAST", "RemoveExplicitTypeArguments")
@file:JvmName("TypeUtils")

package com.highcapable.kavaref.extension

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Convert [Type] to [Class].
 * @see Type.toClassOrNull
 * @receiver the [Type] to be converted.
 * @return [Class]<[T]>
 * @throws TypeCastException if the conversion fails.
 */
@JvmName("toClassTyped")
tailrec fun <T : Any> Type.toClass(): Class<T> = when (this) {
    is Class<*> -> this as Class<T>
    is ParameterizedType -> rawType.toClass<T>()
    else -> throw TypeCastException("Cannot cast type $this to java.lang.Class object.")
}

/**
 * Convert [Type] to [Class].
 * @see Type.toClass
 * @see Type.toClassOrNull
 * @return [Class]
 */
fun Type.toClass() = toClass<Any>()

/**
 * Safely convert [Type] to [Class] or return null if it fails.
 * @see Type.toClass
 * @receiver the [Type] to be converted.
 * @return [Class]<[T]> or null.
 */
@JvmName("toClassTypedOrNull")
fun <T : Any> Type.toClassOrNull() = runCatching { toClass<T>() }.getOrNull()

/**
 * Safely convert [Type] to [Class] or return null if it fails.
 * @see Type.toClass
 * @see Type.toClassOrNull
 * @return [Class] or null.
 */
fun Type.toClassOrNull() = toClassOrNull<Any>()

/**
 * Convert [Type] to [ParameterizedType].
 * @see Type.asParameterizedTypeOrNull
 * @receiver the [Class] to get the [ParameterizedType].
 * @return [ParameterizedType]
 */
inline fun <reified T : Type> T.asParameterizedType() = this as ParameterizedType

/**
 * Safely convert [Type] to [ParameterizedType] or return null if it fails.
 * @see Type.asParameterizedType
 * @receiver the [Class] to get the [ParameterizedType].
 * @return [ParameterizedType] or null.
 */
inline fun <reified T : Type> T.asParameterizedTypeOrNull() = this as? ParameterizedType?

/**
 * Get the type arguments of the superclass of this [Class] or return an empty array if it fails.
 *
 * This function will implement the following functions:
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
 * Get the type arguments of the superclass of this [KClass.java] or return an empty array if it fails.
 * @see Class.genericSuperclassTypeArguments
 */
@JvmSynthetic
fun <T : Any> KClass<T>.genericSuperclassTypeArguments() = java.genericSuperclassTypeArguments()