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
 * This file is created by fankes on 2025/5/16.
 */
@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.highcapable.kavaref.resolver

import com.highcapable.kavaref.resolver.base.InstanceAwareResolver
import java.lang.reflect.Field

/**
 * Resolving [Field].
 * @param self the member to be resolved.
 */
class FieldResolver<T : Any> internal constructor(override val self: Field) : InstanceAwareResolver<Field, T>(self) {

    override fun of(instance: T?) = apply {
        checkAndSetInstance(instance)
    }

    override fun copy() = FieldResolver<T>(self)

    /**
     * Get the value of the field.
     * @see Field.get
     * @see getQuietly
     * @return [T] or null.
     */
    @JvmName("getTyped")
    fun <T : Any?> get(): T? {
        requireAccessible()
        return self.get(instance) as? T?
    }

    /**
     * Get the value of the field and ignore any exceptions.
     * @see Field.get
     * @see get
     * @return [T] or null.
     */
    @JvmName("getQuietlyTyped")
    fun <T : Any?> getQuietly() = runCatching { get<T>() }.getOrNull()

    /**
     * Get the value of the field.
     * @see Field.get
     * @see getQuietly
     * @return [Any] or null.
     */
    fun get(): Any? {
        requireAccessible()
        return self.get(instance)
    }

    /**
     * Get the value of the field and ignore any exceptions.
     * @see Field.get
     * @see get
     * @return [Any] or null.
     */
    fun getQuietly() = runCatching { get() }.getOrNull()

    /**
     * Set the value of the field.
     * @see Field.set
     * @see setQuietly
     * @param value the value to set.
     */
    fun set(value: Any?) {
        requireAccessible()
        self.set(instance, value)
    }

    /**
     * Set the value of the field and ignore any exceptions.
     * @see Field.set
     * @see set
     * @param value the value to set.
     */
    fun setQuietly(value: Any?) = runCatching { set(value) }.getOrNull() ?: Unit
}