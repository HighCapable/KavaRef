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
import java.lang.reflect.Method

/**
 * Resolving [Method].
 * @param self the member to be resolved.
 */
class MethodResolver<T : Any> internal constructor(
    override val self: Method
) : InstanceAwareResolver<Method, T, MethodResolver.Handler>(self) {

    private var handler = Handler()

    /**
     * Handler for performing the operations on [Method].
     */
    open class Handler : MemberResolver.Handler<Method>() {

        /**
         * Invokes the [method] on the [instance] with the given [args].
         * @see Method.invoke
         * @param method the method to invoke.
         * @param instance the instance to invoke the method on, or null for a static method.
         * @param args the arguments to pass to the method.
         * @return [Any] or null.
         */
        open fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? = method.invoke(instance, *args)
    }

    override fun of(instance: T?) = apply {
        checkAndSetInstance(instance)
    }

    override fun withHandler(handler: Handler) = MethodResolver<T>(self).also {
        it.instance = instance
        it.handler = handler
    }

    override fun copy() = MethodResolver<T>(self).also { it.handler = handler }

    /**
     * Invokes the method with the given arguments and casts the result to [T].
     * @see Method.invoke
     * @see invokeQuietly
     * @return [T] or null.
     */
    @JvmName("invokeTyped")
    fun <T> invoke(vararg args: Any?) = invoke(*args) as T?

    /**
     * Invokes the method with the given arguments, casts the result to [T] and ignores any exceptions.
     * @see Method.invoke
     * @see invokeQuietly
     * @return [T] or null.
     */
    @JvmName("invokeQuietlyTyped")
    fun <T> invokeQuietly(vararg args: Any?) = invokeQuietly(*args) as T?

    /**
     * Invokes the method with the given arguments.
     * @see Method.invoke
     * @see invoke
     * @return [Any] or null.
     */
    fun invoke(vararg args: Any?): Any? {
        handler.requireAccessible(self)
        return handler.invoke(self, instance, args)
    }

    /**
     * Invokes the method with the given arguments and ignores any exceptions.
     * @see Method.invoke
     * @see invoke
     * @return [Any] or null.
     */
    fun invokeQuietly(vararg args: Any?) = runCatching { invoke(*args) }.getOrNull()
}