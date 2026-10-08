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
@file:Suppress("unused")
@file:JvmName("MemberUtils")

package com.highcapable.kavaref.extension

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * A flag to indicate whether the [AccessibleObject.trySetAccessible] method
 * is supported in the current environment.
 */
@Volatile
private var isTrySetAccessibleSupported = true

/**
 * Makes the [Member] that extends [AccessibleObject] accessible.
 *
 * If it is already accessible, it returns directly without checking again.
 * @receiver the [Member] to be made accessible.
 * @return [Boolean]
 */
fun Member.makeAccessible() = (this as? AccessibleObject?)?.let {
    // Avoid calling trySetAccessible every time, it checks the caller on each call.
    @Suppress("DEPRECATION")
    if (it.isAccessible) return@let true

    @Suppress("DEPRECATION")
    fun doAccessible() = runCatching { it.isAccessible = true }.isSuccess

    if (!isTrySetAccessibleSupported) return@let doAccessible()

    runCatching {
        it.trySetAccessible()
    }.getOrElse { _ ->
        isTrySetAccessibleSupported = false
        doAccessible()
    }
} == true

// Member extension properties for checking modifiers.

/**
 * Checks if the [Member] is public.
 * @see Modifier.isPublic
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is public, `false` otherwise.
 */
val Member.isPublic get() = Modifier.isPublic(sourceModifiers)

/**
 * Checks if the [Member] is private.
 * @see Modifier.isPrivate
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is private, `false` otherwise.
 */
val Member.isPrivate get() = Modifier.isPrivate(sourceModifiers)

/**
 * Checks if the [Member] is protected.
 * @see Modifier.isProtected
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is protected, `false` otherwise.
 */
val Member.isProtected get() = Modifier.isProtected(sourceModifiers)

/**
 * Checks if the [Member] is static.
 * @see Modifier.isStatic
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is static, `false` otherwise.
 */
val Member.isStatic get() = Modifier.isStatic(sourceModifiers)

/**
 * Checks if the [Member] is final.
 * @see Modifier.isFinal
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is final, `false` otherwise.
 */
val Member.isFinal get() = Modifier.isFinal(sourceModifiers)

/**
 * Checks if the [Member] is synchronized.
 * @see Modifier.isSynchronized
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is synchronized, `false` otherwise.
 */
val Member.isSynchronized get() = Modifier.isSynchronized(sourceModifiers)

/**
 * Checks if the [Member] is volatile.
 * @see Modifier.isVolatile
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is volatile, `false` otherwise.
 */
val Member.isVolatile get() = Modifier.isVolatile(sourceModifiers)

/**
 * Checks if the [Member] is transient.
 * @see Modifier.isTransient
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is transient, `false` otherwise.
 */
val Member.isTransient get() = Modifier.isTransient(sourceModifiers)

/**
 * Checks if the [Member] is native.
 * @see Modifier.isNative
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is native, `false` otherwise.
 */
val Member.isNative get() = Modifier.isNative(sourceModifiers)

/**
 * Checks if the [Member] is an interface.
 * @see Modifier.isInterface
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is an interface, `false` otherwise.
 */
val Member.isInterface get() = Modifier.isInterface(sourceModifiers)

/**
 * Checks if the [Member] is abstract.
 * @see Modifier.isAbstract
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is abstract, `false` otherwise.
 */
val Member.isAbstract get() = Modifier.isAbstract(sourceModifiers)

/**
 * Checks if the [Member] is strict.
 * @see Modifier.isStrict
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is strict, `false` otherwise.
 */
val Member.isStrict get() = Modifier.isStrict(sourceModifiers)

/**
 * Gets the source-level modifiers of [Member].
 *
 * The JVM reuses some modifier bits for bridge and varargs methods,
 * so they are masked out by the member type to avoid false matches.
 * @return [Int]
 */
private val Member.sourceModifiers get() = modifiers and when (this) {
    is Method -> Modifier.methodModifiers()
    is Constructor<*> -> Modifier.constructorModifiers()
    is Field -> Modifier.fieldModifiers()
    else -> modifiers
}