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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "UnusedReceiverParameter", "DeprecatedCallableAddReplaceWith")

package com.highcapable.kavaref

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.ConstructorCondition
import com.highcapable.kavaref.condition.FieldCondition
import com.highcapable.kavaref.condition.MethodCondition
import com.highcapable.kavaref.condition.base.MemberCondition
import com.highcapable.kavaref.condition.base.MemberCondition.Configuration.Companion.createConfiguration
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import com.highcapable.kavaref.runtime.KavaRefRuntime
import com.highcapable.kavaref.runtime.KavaRefRuntime.Logger
import kotlin.reflect.KClass

/**
 * This is the class created and managed by KavaRef.
 *
 * Try to use [KavaRef.resolve] to start a new reflection.
 */
class KavaRef private constructor() {

    companion object {

        /** Get or set the log level for KavaRef. */
        @JvmStatic
        var logLevel by KavaRefRuntime::logLevel

        /**
         * Set the logger for KavaRef.
         * @param logger the logger to be set.
         */
        @JvmStatic
        fun setLogger(logger: Logger) = KavaRefRuntime.setLogger(logger)

        /**
         * Create a [MemberScope] instance to start a new reflection.
         * @receiver the [KClass.java] to be reflected.
         * @return [MemberScope]<[T]>
         */
        @JvmSynthetic
        fun <T : Any> KClass<T>.resolve() = MemberScope(java.createConfiguration())

        /**
         * Create a [MemberScope] instance to start a new reflection.
         * @receiver the [Class] to be reflected.
         * @return [MemberScope]<[T]>
         */
        @JvmStatic
        @JvmName("resolveClass")
        fun <T : Any> Class<T>.resolve() = MemberScope(createConfiguration())

        /**
         * Create a [MemberScope] instance to start a new reflection.
         * @see KClass.resolve
         * @see Class.resolve
         * @return [MemberScope]<[T]>
         */
        @JvmStatic
        @JvmName("resolveObject")
        fun <T : Any> T.asResolver() = when (this) {
            is KClass<*> -> MemberScope((this as KClass<T>).java.createConfiguration(memberInstance = this))
            is Class<*> -> MemberScope((this as Class<T>).createConfiguration(memberInstance = this))
            else -> MemberScope(javaClass.createConfiguration(memberInstance = this))
        }

        // Below are deprecated functions to prevent internal calls.

        private const val DEPRECATED_MESSAGE = "You are calling asResolver() in KavaRef internal component, it's an error and should delete it."
        private const val EXCEPTION_MESSAGE = "Not allowed to call asResolver() in KavaRef internal component, please delete it."

        /**
         * This is a fake function call chains to avoid internal calls to themselves.
         */
        @Deprecated(message = DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberScope<*>.asResolver(): MemberScope<*> = error(EXCEPTION_MESSAGE)

        /**
         * This is a fake function call chains to avoid internal calls to themselves.
         */
        @Deprecated(message = DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberCondition<*, *, *>.asResolver(): MemberCondition<*, *, *> = error(EXCEPTION_MESSAGE)

        /**
         * This is a fake function call chains to avoid internal calls to themselves.
         */
        @Deprecated(message = DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberResolver<*, *>.asResolver(): MemberResolver<*, *> = error(EXCEPTION_MESSAGE)

        /**
         * This is a fake function call chains to avoid internal calls to themselves.
         */
        @Deprecated(message = DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun List<MemberResolver<*, *>>.asResolver(): List<MemberResolver<*, *>> = error(EXCEPTION_MESSAGE)
    }

    /**
     * The KavaRef scope for member reflection.
     *
     * [T] to specify the declaring class type of the member.
     * @param configuration the configuration to be reflected.
     */
    class MemberScope<T : Any> internal constructor(private val configuration: MemberCondition.Configuration<T>) {

        /**
         * Set the [MemberProcessor.Resolver] to be used for this reflection.
         * @see MemberCondition.Configuration.processorResolver
         * @see MemberProcessor.Resolver
         * @param resolver the resolver to be used.
         */
        fun processor(resolver: MemberProcessor.Resolver) = apply {
            configuration.processorResolver = resolver
        }

        /**
         * Enable optional mode.
         * @see MemberCondition.Configuration.optional
         * @param silent see [MemberCondition.Configuration.Optional.SILENT]
         */
        fun optional(silent: Boolean = false) = apply {
            configuration.optional = if (silent)
                MemberCondition.Configuration.Optional.SILENT
            else MemberCondition.Configuration.Optional.NOTICE
        }

        /**
         * Start a new method reflection.
         * @return [MethodCondition]
         */
        fun method() = MethodCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Start a new method reflection.
         * @see firstMethod
         * @see firstMethodOrNull
         * @param condition the condition.
         * @return [MethodResolver]
         */
        fun method(condition: MethodCondition<T>) = condition.build(configuration)

        /**
         * Start a new method reflection and return the first matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        fun firstMethod(condition: MethodCondition<T>) = method(condition).first()

        /**
         * Start a new method reflection and return the first matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        fun firstMethodOrNull(condition: MethodCondition<T>) = method(condition).firstOrNull()

        /**
         * Start a new method reflection and return the last matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        fun lastMethod(condition: MethodCondition<T>) = method(condition).last()

        /**
         * Start a new method reflection and return the last matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        fun lastMethodOrNull(condition: MethodCondition<T>) = method(condition).lastOrNull()

        /**
         * Start a new method reflection.
         * @see firstMethod
         * @see firstMethodOrNull
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun method(condition: MethodCondition<T>.() -> Unit) = method().apply(condition).build()

        /**
         * Start a new method reflection and return the first matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun firstMethod(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).first()

        /**
         * Start a new method reflection and return the first matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        inline fun firstMethodOrNull(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).firstOrNull()

        /**
         * Start a new method reflection and return the last matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun lastMethod(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).last()

        /**
         * Start a new method reflection and return the last matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        inline fun lastMethodOrNull(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).lastOrNull()

        /**
         * Start a new constructor reflection.
         * @return [ConstructorCondition]
         */
        fun constructor() = ConstructorCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Start a new constructor reflection.
         * @see firstConstructor
         * @see firstConstructorOrNull
         * @param condition the condition.
         * @return [ConstructorResolver]
         */
        fun constructor(condition: ConstructorCondition<T>) = condition.build(configuration)

        /**
         * Start a new constructor reflection and return the first matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        fun firstConstructor(condition: ConstructorCondition<T>) = constructor(condition).first()

        /**
         * Start a new constructor reflection and return the first matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        fun firstConstructorOrNull(condition: ConstructorCondition<T>) = constructor(condition).firstOrNull()

        /**
         * Start a new constructor reflection and return the last matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        fun lastConstructor(condition: ConstructorCondition<T>) = constructor(condition).last()

        /**
         * Start a new constructor reflection and return the last matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        fun lastConstructorOrNull(condition: ConstructorCondition<T>) = constructor(condition).lastOrNull()

        /**
         * Start a new constructor reflection.
         * @see firstConstructor
         * @see firstConstructorOrNull
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun constructor(condition: ConstructorCondition<T>.() -> Unit) = constructor().apply(condition).build()

        /**
         * Start a new constructor reflection and return the first matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun firstConstructor(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).first()

        /**
         * Start a new constructor reflection and return the first matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        inline fun firstConstructorOrNull(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).firstOrNull()

        /**
         * Start a new constructor reflection and return the last matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun lastConstructor(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).last()

        /**
         * Start a new constructor reflection and return the last matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        inline fun lastConstructorOrNull(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).lastOrNull()

        /**
         * Start a new field reflection.
         * @return [FieldCondition]
         */
        fun field() = FieldCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Start a new field reflection.
         * @see firstField
         * @see firstFieldOrNull
         * @param condition the condition.
         * @return [FieldResolver]
         */
        fun field(condition: FieldCondition<T>) = condition.build(configuration)

        /**
         * Start a new field reflection and return the first matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        fun firstField(condition: FieldCondition<T>) = field(condition).first()

        /**
         * Start a new field reflection and return the first matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        fun firstFieldOrNull(condition: FieldCondition<T>) = field(condition).firstOrNull()

        /**
         * Start a new field reflection and return the last matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        fun lastField(condition: FieldCondition<T>) = field(condition).last()

        /**
         * Start a new field reflection and return the last matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        fun lastFieldOrNull(condition: FieldCondition<T>) = field(condition).lastOrNull()

        /**
         * Start a new field reflection.
         * @see firstField
         * @see firstFieldOrNull
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun field(condition: FieldCondition<T>.() -> Unit) = field().apply(condition).build()

        /**
         * Start a new field reflection and return the first matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun firstField(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).first()

        /**
         * Start a new field reflection and return the first matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        inline fun firstFieldOrNull(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).firstOrNull()

        /**
         * Start a new field reflection and return the last matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun lastField(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).last()

        /**
         * Start a new field reflection and return the last matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        inline fun lastFieldOrNull(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).lastOrNull()
    }
}