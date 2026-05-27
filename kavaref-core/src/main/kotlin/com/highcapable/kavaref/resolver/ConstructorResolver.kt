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
class ConstructorResolver<T : Any> internal constructor(override val self: Constructor<T>) : MemberResolver<Constructor<T>, T>(self) {

    override fun copy() = ConstructorResolver(self)

    /**
     * Create a new instance of the class represented by this constructor.
     * @see Constructor.newInstance
     * @see createQuietly
     * @see createAsType
     * @see createAsTypeQuietly
     * @return [T]
     */
    fun create(vararg args: Any?): T {
        requireAccessible()
        return self.newInstance(*args)
    }

    /**
     * Create a new instance of the class represented by this constructor and cast it to the specified type [T].
     * @see Constructor.newInstance
     * @see createAsTypeQuietly
     * @see createQuietly
     * @return [T]
     */
    inline fun <reified T : Any> createAsType(vararg args: Any?): T {
        requireAccessible()
        return self.newInstance(*args) as? T ?: error("$this's instance cannot be cast to type ${classOf<T>()}.")
    }

    /**
     * Create a new instance of the class represented by this constructor and ignore any exceptions.
     * @see Constructor.newInstance
     * @see create
     * @see createAsType
     * @see createAsTypeQuietly
     * @return [T] or null.
     */
    fun createQuietly(vararg args: Any?) = runCatching { create(*args) }.getOrNull()

    /**
     * Create a new instance of the class represented by this constructor and cast it to the
     * specified type [T] and ignore any exceptions.
     * @see Constructor.newInstance
     * @see create
     * @see createAsType
     * @see createQuietly
     * @return [T] or null.
     */
    inline fun <reified T : Any> createAsTypeQuietly(vararg args: Any?) = runCatching { createAsType<T>(*args) }.getOrNull()
}