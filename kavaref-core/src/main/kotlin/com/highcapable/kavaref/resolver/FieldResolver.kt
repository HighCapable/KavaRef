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
@file:Suppress("UNCHECKED_CAST")

package com.highcapable.kavaref.resolver

import com.highcapable.kavaref.resolver.base.InstanceAwareResolver
import com.highcapable.kavaref.resolver.base.MemberResolver
import java.lang.reflect.Field

/**
 * Resolving [Field].
 * @param self the member to be resolved.
 */
class FieldResolver<T : Any> internal constructor(
    override val self: Field
) : InstanceAwareResolver<Field, T, FieldResolver.Handler>(self) {

    private var handler = Handler()

    /**
     * Handler for performing the operations on [Field].
     */
    open class Handler : MemberResolver.Handler<Field>() {

        /**
         * Gets the value of the [field] on the [instance].
         * @see Field.get
         * @param field the field to get.
         * @param instance the instance to get the value from, or null for a static field.
         * @return [Any] or null.
         */
        open fun get(field: Field, instance: Any?): Any? = field.get(instance)

        /**
         * Sets the [value] of the [field] on the [instance].
         * @see Field.set
         * @param field the field to set.
         * @param instance the instance to set the value to, or null for a static field.
         * @param value the value to set.
         */
        open fun set(field: Field, instance: Any?, value: Any?) = field.set(instance, value)
    }

    override fun of(instance: T?) = apply {
        checkAndSetInstance(instance)
    }

    override fun withHandler(handler: Handler) = FieldResolver<T>(self).also {
        it.instance = instance
        it.handler = handler
    }

    override fun copy() = FieldResolver<T>(self).also { it.handler = handler }

    /**
     * Gets the value of the field and casts it to [T].
     * @see Field.get
     * @see getQuietly
     * @return [T] or null.
     */
    @JvmName("getTyped")
    fun <T> get() = get() as T?

    /**
     * Gets the value of the field, casts it to [T] and ignores any exceptions.
     * @see Field.get
     * @see get
     * @return [T] or null.
     */
    @JvmName("getQuietlyTyped")
    fun <T> getQuietly() = getQuietly() as T?

    /**
     * Gets the value of the field.
     * @see Field.get
     * @see getQuietly
     * @return [Any] or null.
     */
    fun get(): Any? {
        handler.requireAccessible(self)
        return handler.get(self, instance)
    }

    /**
     * Gets the value of the field and ignores any exceptions.
     * @see Field.get
     * @see get
     * @return [Any] or null.
     */
    fun getQuietly() = runCatching { get() }.getOrNull()

    /**
     * Sets the value of the field.
     * @see Field.set
     * @see setQuietly
     * @param value the value to set.
     */
    fun set(value: Any?) {
        handler.requireAccessible(self)
        handler.set(self, instance, value)
    }

    /**
     * Sets the value of the field and ignores any exceptions.
     * @see Field.set
     * @see set
     * @param value the value to set.
     */
    fun setQuietly(value: Any?) = runCatching { set(value) }.getOrNull() ?: Unit
}