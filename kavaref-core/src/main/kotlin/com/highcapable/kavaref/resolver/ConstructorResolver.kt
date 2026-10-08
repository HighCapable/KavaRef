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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.highcapable.kavaref.resolver

import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.resolver.base.MemberResolver
import java.lang.reflect.Constructor

/**
 * Resolving [Constructor].
 * @param self the member to be resolved.
 */
class ConstructorResolver<T : Any> internal constructor(
    override val self: Constructor<T>
) : MemberResolver<Constructor<T>, T, ConstructorResolver.Handler>(self) {

    private var handler = Handler()

    /**
     * Handler for performing the operations on [Constructor].
     */
    open class Handler : MemberResolver.Handler<Constructor<*>>() {

        /**
         * Creates a new instance with the [constructor] and the given [args].
         * @see Constructor.newInstance
         * @param constructor the constructor to invoke.
         * @param args the arguments to pass to the constructor.
         * @return [T]
         */
        open fun <T> newInstance(constructor: Constructor<T>, args: Array<out Any?>): T = constructor.newInstance(*args)
    }

    override fun withHandler(handler: Handler) = ConstructorResolver(self).also { it.handler = handler }

    override fun copy() = ConstructorResolver(self).also { it.handler = handler }

    /**
     * Creates a new instance of the class represented by this constructor.
     * @see Constructor.newInstance
     * @see createQuietly
     * @see createAsType
     * @see createAsTypeQuietly
     * @return [T]
     */
    fun create(vararg args: Any?): T {
        handler.requireAccessible(self)
        return handler.newInstance(self, args)
    }

    /**
     * Creates a new instance of the class represented by this constructor and casts it to the specified type [T].
     * @see Constructor.newInstance
     * @see createAsTypeQuietly
     * @see createQuietly
     * @return [T]
     */
    inline fun <reified T : Any> createAsType(vararg args: Any?) =
        create(*args) as? T ?: error("$this's instance cannot be cast to type ${classOf<T>()}")

    /**
     * Creates a new instance of the class represented by this constructor and ignores any exceptions.
     * @see Constructor.newInstance
     * @see create
     * @see createAsType
     * @see createAsTypeQuietly
     * @return [T] or null.
     */
    fun createQuietly(vararg args: Any?) = runCatching { create(*args) }.getOrNull()

    /**
     * Creates a new instance of the class represented by this constructor and casts it to the
     * specified type [T] and ignores any exceptions.
     * @see Constructor.newInstance
     * @see create
     * @see createAsType
     * @see createQuietly
     * @return [T] or null.
     */
    inline fun <reified T : Any> createAsTypeQuietly(vararg args: Any?) = runCatching { createAsType<T>(*args) }.getOrNull()
}