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
 * This file is created by fankes on 2025/10/6.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")
@file:JvmName("TypeRefUtils")

package com.highcapable.kavaref.extension

import androidx.annotation.Keep
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Type reference class for getting generic parameter [T] type.
 *
 * Captures the Java type from a direct subclass's generic signature.
 *
 * Non-reified type variables remain unresolved, erased type arguments cannot be recovered.
 * Nullable types are supported, but Kotlin nullability is not retained in [Type].
 * @see typeRef
 */
@Keep
abstract class TypeRef<T> {

    /**
     * Get the generic parameter [T] type.
     *
     * Unresolved type variables are returned as-is.
     * @return [Type]
     * @throws IllegalStateException if this is not a direct subclass with a type argument.
     */
    val type by lazy {
        when (val superclass = javaClass.genericSuperclass) {
            is ParameterizedType ->
                if (superclass.rawType == classOf<TypeRef<*>>()) 
                    superclass.actualTypeArguments.firstOrNull() ?: error("Type argument cannot be null.")
                else error("Only direct subclasses of TypeRef are allowed.")
            classOf<TypeRef<*>>() -> error("TypeRef must be created with a type argument: object : TypeRef<...>() {}")
            else -> error("Only direct subclasses of TypeRef are allowed.")
        }
    }

    /**
     * Get the raw class type of the generic parameter [T], preserving array dimensions.
     * @return [Class] with erased type arguments.
     * @throws TypeCastException if the type or its array component has no concrete raw class.
     */
    val rawType: Class<*> by lazy { type.toClass() }

    override fun toString() = runCatching { type.toString() }.getOrElse { "${javaClass.name} (invalid)" }
    override fun equals(other: Any?) = other is TypeRef<*> && type == other.type
    override fun hashCode() = type.hashCode()
}

/**
 * Create a [TypeRef] instance with the reified type parameter [T].
 *
 * Nullable types are supported, but Kotlin nullability is not retained in the captured Java type.
 *
 * Usage:
 *
 * ```kotlin
 * val typeRef = typeRef<List<String>>()
 * // This will be of type `List<? extends String>`.
 * val type = typeRef.type
 * // This will be of type `List`.
 * val rawType = typeRef.rawType
 * ```
 * @see TypeRef
 * @return [TypeRef]<[T]>
 */
inline fun <reified T> typeRef() = object : TypeRef<T>() {}