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
import java.lang.reflect.Method

/**
 * Resolving [Method].
 * @param self the member to be resolved.
 */
class MethodResolver<T : Any> internal constructor(override val self: Method) : InstanceAwareResolver<Method, T>(self) {

    override fun of(instance: T?) = apply {
        checkAndSetInstance(instance)
    }

    override fun copy() = MethodResolver<T>(self)

    /**
     * Invoke the method with the given arguments.
     * @see Method.invoke
     * @see invokeQuietly
     * @return [T] or null.
     */
    @JvmName("invokeTyped")
    fun <T : Any?> invoke(vararg args: Any?): T? {
        self.requireAccessible()
        return self.invoke(instance, *args) as? T?
    }

    /**
     * Invoke the method with the given arguments and ignore any exceptions.
     * @see Method.invoke
     * @see invokeQuietly
     * @return [T] or null.
     */
    @JvmName("invokeQuietlyTyped")
    fun <T : Any?> invokeQuietly(vararg args: Any?) = runCatching { invoke<T>(*args) }.getOrNull()

    /**
     * Invoke the method with the given arguments.
     * @see Method.invoke
     * @see invoke
     * @return [Any] or null.
     */
    fun invoke(vararg args: Any?): Any? {
        self.requireAccessible()
        return self.invoke(instance, *args)
    }

    /**
     * Invoke the method with the given arguments and ignore any exceptions.
     * @see Method.invoke
     * @see invoke
     * @return [Any] or null.
     */
    fun invokeQuietly(vararg args: Any?) = runCatching { invoke(*args) }.getOrNull()
}