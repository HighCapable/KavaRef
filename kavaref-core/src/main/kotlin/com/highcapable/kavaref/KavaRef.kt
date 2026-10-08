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

        /** Gets or sets the log level for KavaRef. */
        @JvmStatic
        var logLevel by KavaRefRuntime::logLevel

        /**
         * Sets the logger for KavaRef.
         * @param logger the logger to be set.
         */
        @JvmStatic
        fun setLogger(logger: Logger) = KavaRefRuntime.setLogger(logger)

        /**
         * Creates a [MemberScope] instance to start a new reflection.
         * @receiver the [KClass.java] to be reflected.
         * @return [MemberScope]<[T]>
         */
        @JvmSynthetic
        fun <T : Any> KClass<T>.resolve() = MemberScope(java.createConfiguration())

        /**
         * Creates a [MemberScope] instance to start a new reflection.
         * @receiver the [Class] to be reflected.
         * @return [MemberScope]<[T]>
         */
        @JvmStatic
        @JvmName("resolveClass")
        fun <T : Any> Class<T>.resolve() = MemberScope(createConfiguration())

        /**
         * Creates a [MemberScope] instance to start a new reflection with the receiver as the instance.
         *
         * The receiver is always treated as an instance, even if it is a [KClass] or [Class] object,
         * then the members of [KClass] or [Class] itself will be reflected.
         * To reflect the members of a class, use [KClass.resolve] or [Class.resolve] instead.
         * @see KClass.resolve
         * @see Class.resolve
         * @return [MemberScope]<[T]>
         */
        @JvmStatic
        @JvmName("resolveObject")
        fun <T : Any> T.asResolver() = MemberScope(javaClass.createConfiguration(memberInstance = this))

        // region Deprecated functions to prevent misuse

        private const val EXTERNAL_DEPRECATED_MESSAGE = "You are calling asResolver() on a class, this is not allowed, use resolve() instead."
        private const val EXTERNAL_EXCEPTION_MESSAGE = "Not allowed to call asResolver() on a class, please use resolve() instead."

        private const val INTERNAL_DEPRECATED_MESSAGE = "You are calling asResolver() in a KavaRef internal component, this is not allowed, please delete it."
        private const val INTERNAL_EXCEPTION_MESSAGE = "Not allowed to call asResolver() in a KavaRef internal component, please delete it."

        /**
         * This is a fake function to prevent calling on a [KClass], use [KClass.resolve] instead.
         */
        @Deprecated(
            message = EXTERNAL_DEPRECATED_MESSAGE,
            replaceWith = ReplaceWith("resolve()", "com.highcapable.kavaref.KavaRef.Companion.resolve"),
            level = DeprecationLevel.ERROR
        )
        @JvmSynthetic
        fun <T : Any> KClass<T>.asResolver(): MemberScope<T> = error(EXTERNAL_EXCEPTION_MESSAGE)

        /**
         * This is a fake function to prevent calling on a [Class], use [Class.resolve] instead.
         */
        @Deprecated(
            message = EXTERNAL_DEPRECATED_MESSAGE,
            replaceWith = ReplaceWith("resolve()", "com.highcapable.kavaref.KavaRef.Companion.resolve"),
            level = DeprecationLevel.ERROR
        )
        @JvmSynthetic
        fun <T : Any> Class<T>.asResolver(): MemberScope<T> = error(EXTERNAL_EXCEPTION_MESSAGE)

        /**
         * This is a fake function to prevent KavaRef internal components from calling it.
         */
        @Deprecated(message = INTERNAL_DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberScope<*>.asResolver(): MemberScope<*> = error(INTERNAL_EXCEPTION_MESSAGE)

        /**
         * This is a fake function to prevent KavaRef internal components from calling it.
         */
        @Deprecated(message = INTERNAL_DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberCondition<*, *, *>.asResolver(): MemberCondition<*, *, *> = error(INTERNAL_EXCEPTION_MESSAGE)

        /**
         * This is a fake function to prevent KavaRef internal components from calling it.
         */
        @Deprecated(message = INTERNAL_DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun MemberResolver<*, *, *>.asResolver(): MemberResolver<*, *, *> = error(INTERNAL_EXCEPTION_MESSAGE)

        /**
         * This is a fake function to prevent KavaRef internal components from calling it.
         */
        @Deprecated(message = INTERNAL_DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
        @JvmSynthetic
        fun List<MemberResolver<*, *, *>>.asResolver(): List<MemberResolver<*, *, *>> = error(INTERNAL_EXCEPTION_MESSAGE)

        // endregion
    }

    /**
     * The KavaRef scope for member reflection.
     *
     * [T] to specify the declaring class type of the member.
     * @param configuration the configuration to be reflected.
     */
    class MemberScope<T : Any> internal constructor(private val configuration: MemberCondition.Configuration<T>) {

        /**
         * Sets the [MemberProcessor.Resolver] to be used for this reflection.
         * @see MemberCondition.Configuration.processorResolver
         * @see MemberProcessor.Resolver
         * @param resolver the resolver to be used.
         */
        fun processor(resolver: MemberProcessor.Resolver) = apply {
            configuration.processorResolver = resolver
        }

        /**
         * Enables optional mode.
         * @see MemberCondition.Configuration.optional
         * @param silent see [MemberCondition.Configuration.Optional.SILENT]
         */
        fun optional(silent: Boolean = false) = apply {
            configuration.optional = if (silent)
                MemberCondition.Configuration.Optional.SILENT
            else MemberCondition.Configuration.Optional.NOTICE
        }

        /**
         * Starts a new method reflection.
         * @return [MethodCondition]
         */
        fun method() = MethodCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Starts a new method reflection.
         * @see firstMethod
         * @see firstMethodOrNull
         * @param condition the condition.
         * @return [MethodResolver]
         */
        fun method(condition: MethodCondition<T>) = condition.build(configuration)

        /**
         * Starts a new method reflection and returns the first matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        fun firstMethod(condition: MethodCondition<T>) = method(condition).first()

        /**
         * Starts a new method reflection and returns the first matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        fun firstMethodOrNull(condition: MethodCondition<T>) = method(condition).firstOrNull()

        /**
         * Starts a new method reflection and returns the last matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        fun lastMethod(condition: MethodCondition<T>) = method(condition).last()

        /**
         * Starts a new method reflection and returns the last matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        fun lastMethodOrNull(condition: MethodCondition<T>) = method(condition).lastOrNull()

        /**
         * Starts a new method reflection.
         * @see firstMethod
         * @see firstMethodOrNull
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun method(condition: MethodCondition<T>.() -> Unit) = method().apply(condition).build()

        /**
         * Starts a new method reflection and returns the first matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun firstMethod(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).first()

        /**
         * Starts a new method reflection and returns the first matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        inline fun firstMethodOrNull(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).firstOrNull()

        /**
         * Starts a new method reflection and returns the last matching method.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver]
         */
        inline fun lastMethod(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).last()

        /**
         * Starts a new method reflection and returns the last matching method or null.
         * @see method
         * @param condition the condition body.
         * @return [MethodResolver] or null.
         */
        inline fun lastMethodOrNull(condition: MethodCondition<T>.() -> Unit = {}) = method(condition).lastOrNull()

        /**
         * Starts a new constructor reflection.
         * @return [ConstructorCondition]
         */
        fun constructor() = ConstructorCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Starts a new constructor reflection.
         * @see firstConstructor
         * @see firstConstructorOrNull
         * @param condition the condition.
         * @return [ConstructorResolver]
         */
        fun constructor(condition: ConstructorCondition<T>) = condition.build(configuration)

        /**
         * Starts a new constructor reflection and returns the first matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        fun firstConstructor(condition: ConstructorCondition<T>) = constructor(condition).first()

        /**
         * Starts a new constructor reflection and returns the first matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        fun firstConstructorOrNull(condition: ConstructorCondition<T>) = constructor(condition).firstOrNull()

        /**
         * Starts a new constructor reflection and returns the last matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        fun lastConstructor(condition: ConstructorCondition<T>) = constructor(condition).last()

        /**
         * Starts a new constructor reflection and returns the last matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        fun lastConstructorOrNull(condition: ConstructorCondition<T>) = constructor(condition).lastOrNull()

        /**
         * Starts a new constructor reflection.
         * @see firstConstructor
         * @see firstConstructorOrNull
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun constructor(condition: ConstructorCondition<T>.() -> Unit) = constructor().apply(condition).build()

        /**
         * Starts a new constructor reflection and returns the first matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun firstConstructor(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).first()

        /**
         * Starts a new constructor reflection and returns the first matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        inline fun firstConstructorOrNull(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).firstOrNull()

        /**
         * Starts a new constructor reflection and returns the last matching constructor.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver]
         */
        inline fun lastConstructor(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).last()

        /**
         * Starts a new constructor reflection and returns the last matching constructor or null.
         * @see constructor
         * @param condition the condition body.
         * @return [ConstructorResolver] or null.
         */
        inline fun lastConstructorOrNull(condition: ConstructorCondition<T>.() -> Unit = {}) = constructor(condition).lastOrNull()

        /**
         * Starts a new field reflection.
         * @return [FieldCondition]
         */
        fun field() = FieldCondition<T>().also {
            it.configuration = configuration
        }

        /**
         * Starts a new field reflection.
         * @see firstField
         * @see firstFieldOrNull
         * @param condition the condition.
         * @return [FieldResolver]
         */
        fun field(condition: FieldCondition<T>) = condition.build(configuration)

        /**
         * Starts a new field reflection and returns the first matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        fun firstField(condition: FieldCondition<T>) = field(condition).first()

        /**
         * Starts a new field reflection and returns the first matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        fun firstFieldOrNull(condition: FieldCondition<T>) = field(condition).firstOrNull()

        /**
         * Starts a new field reflection and returns the last matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        fun lastField(condition: FieldCondition<T>) = field(condition).last()

        /**
         * Starts a new field reflection and returns the last matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        fun lastFieldOrNull(condition: FieldCondition<T>) = field(condition).lastOrNull()

        /**
         * Starts a new field reflection.
         * @see firstField
         * @see firstFieldOrNull
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun field(condition: FieldCondition<T>.() -> Unit) = field().apply(condition).build()

        /**
         * Starts a new field reflection and returns the first matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun firstField(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).first()

        /**
         * Starts a new field reflection and returns the first matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        inline fun firstFieldOrNull(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).firstOrNull()

        /**
         * Starts a new field reflection and returns the last matching field.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver]
         */
        inline fun lastField(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).last()

        /**
         * Starts a new field reflection and returns the last matching field or null.
         * @see field
         * @param condition the condition body.
         * @return [FieldResolver] or null.
         */
        inline fun lastFieldOrNull(condition: FieldCondition<T>.() -> Unit = {}) = field(condition).lastOrNull()
    }
}