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
import java.lang.reflect.Member
import java.lang.reflect.Modifier

/**
 * Make [AccessibleObject] implements [Member] accessible.
 * @receiver the [Member] to be made accessible.
 * @return [Boolean]
 */
inline fun <reified T : Member> T.makeAccessible() = (this as? AccessibleObject?)?.trySetAccessible() == true

// Member extension properties for checking modifiers.

/**
 * Check if the [Member] is public.
 * @see Modifier.isPublic
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is public, `false` otherwise.
 */
val Member.isPublic get() = Modifier.isPublic(modifiers)

/**
 * Check if the [Member] is private.
 * @see Modifier.isPrivate
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is private, `false` otherwise.
 */
val Member.isPrivate get() = Modifier.isPrivate(modifiers)

/**
 * Check if the [Member] is protected.
 * @see Modifier.isProtected
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is protected, `false` otherwise.
 */
val Member.isProtected get() = Modifier.isProtected(modifiers)

/**
 * Check if the [Member] is static.
 * @see Modifier.isStatic
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is static, `false` otherwise.
 */
val Member.isStatic get() = Modifier.isStatic(modifiers)

/**
 * Check if the [Member] is final.
 * @see Modifier.isFinal
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is final, `false` otherwise.
 */
val Member.isFinal get() = Modifier.isFinal(modifiers)

/**
 * Check if the [Member] is synchronized.
 * @see Modifier.isSynchronized
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is synchronized, `false` otherwise.
 */
val Member.isSynchronized get() = Modifier.isSynchronized(modifiers)

/**
 * Check if the [Member] is volatile.
 * @see Modifier.isVolatile
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is volatile, `false` otherwise.
 */
val Member.isVolatile get() = Modifier.isVolatile(modifiers)

/**
 * Check if the [Member] is transient.
 * @see Modifier.isTransient
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is transient, `false` otherwise.
 */
val Member.isTransient get() = Modifier.isTransient(modifiers)

/**
 * Check if the [Member] is native.
 * @see Modifier.isNative
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is native, `false` otherwise.
 */
val Member.isNative get() = Modifier.isNative(modifiers)

/**
 * Check if the [Member] is an interface.
 * @see Modifier.isInterface
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is an interface, `false` otherwise.
 */
val Member.isInterface get() = Modifier.isInterface(modifiers)

/**
 * Check if the [Member] is abstract.
 * @see Modifier.isAbstract
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is abstract, `false` otherwise.
 */
val Member.isAbstract get() = Modifier.isAbstract(modifiers)

/**
 * Check if the [Member] is strict.
 * @see Modifier.isStrict
 * @receiver the [Member] to be checked.
 * @return `true` if the [Member] is strict, `false` otherwise.
 */
val Member.isStrict get() = Modifier.isStrict(modifiers)