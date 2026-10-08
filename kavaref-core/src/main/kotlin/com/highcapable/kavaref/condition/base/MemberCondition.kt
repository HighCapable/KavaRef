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
@file:Suppress("DuplicatedCode")

package com.highcapable.kavaref.condition.base

import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Executable
import java.lang.reflect.Field
import java.lang.reflect.Member

/**
 * Base class for defining conditions on members [M], [R].
 * 
 * [T] to specify the declaring class type of the member.
 */
abstract class MemberCondition<M : Member, R : MemberResolver<M, T, *>, T : Any> {

    /**
     * Configures initial conditions.
     * @param declaringClass the class that declares the member.
     * @param memberInstance the instance of the resolved member, default is null.
     * @param processorResolver the resolver for processing members, if set,
     * it will be used to resolve members instead of the default resolver.
     * If you want to change the global processor resolver, you can set it using [MemberProcessor.globalResolver].
     * @param superclass the superclass mode, which means that when the condition cannot find the corresponding member,
     * it will search the [declaringClass]'s superclasses (and interfaces), default is [Superclass.NO].
     * It applies to every condition built with this configuration, use [MemberCondition.superclass] to enable it for a single condition.
     * @param optional the optional mode, which means that when the condition cannot find the corresponding member,
     * do not throw an exception but return an empty list, whether to print logs depends on [Optional], default is [Optional.NO].
     */
    data class Configuration<T : Any>(
        val declaringClass: Class<T>,
        val memberInstance: T? = null,
        var processorResolver: MemberProcessor.Resolver? = null,
        var superclass: Superclass = Superclass.NO,
        var optional: Optional = Optional.NO
    ) {

        /**
         * Optional mode for handling member resolution.
         */
        enum class Optional {
            /** Does not use optional mode. */
            NO,

            /**
             * Enables optional mode to minimize exception prompts (does not throw
             * exceptions), but the warning log will still be printed.
             */
            NOTICE,

            /**
             * Enables optional mode to minimize exception prompts (does not throw
             * exceptions) and does not print any logs.
             */
            SILENT
        }

        /**
         * Superclass mode for searching members in the inheritance hierarchy.
         */
        enum class Superclass {
            /** Does not use superclass mode. */
            NO,

            /** Searches the superclasses until `java.lang.Object` (including `java.lang.Object` itself). */
            NORMAL,

            /**
             * Searches the superclasses like [NORMAL], then searches all the interfaces,
             * a sub-interface is always searched before its super-interfaces.
             */
            INCLUDE_INTERFACES
        }

        companion object {

            /**
             * Creates a new instance of [Configuration].
             * @return [Configuration]
             */
            @JvmStatic
            @JvmOverloads
            fun <T : Any> Class<T>.createConfiguration(
                memberInstance: T? = null,
                processorResolver: MemberProcessor.Resolver? = null,
                superclass: Superclass = Superclass.NO,
                optional: Optional = Optional.NO
            ) = Configuration(declaringClass = this, memberInstance, processorResolver, superclass, optional)
        }
    }

    /**
     * The configuration of this condition,
     * which can only be set by the [build] function.
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    internal var configuration: Configuration<T>? = null

    /** The superclass mode set by [superclass] for this condition only. */
    @get:JvmSynthetic
    @set:JvmSynthetic
    internal var superclassMode = Configuration.Superclass.NO

    /** The names of the empty conditions that have been set. */
    @get:JvmSynthetic
    internal val emptyConditions = mutableSetOf<String>()

    /** @see Member.getName */
    var name: String? = null

    /** @see Member.getName */
    var nameCondition: ((String) -> Boolean)? = null

    /** @see Member.getModifiers */
    val modifiers = mutableSetOf<Modifiers>()

    /** @see Member.getModifiers */
    val modifiersNot = mutableSetOf<Modifiers>()

    /** @see Member.getModifiers */
    var modifiersCondition: ((Set<Modifiers>) -> Boolean)? = null

    /** @see Member.isSynthetic */
    var isSynthetic: Boolean? = null

    /** @see Member.isSynthetic */
    var isSyntheticNot: Boolean? = null

    /** @see AnnotatedElement.getDeclaredAnnotations */
    val annotations = mutableSetOf<Any>()

    /** @see AnnotatedElement.getDeclaredAnnotations */
    val annotationsNot = mutableSetOf<Any>()

    /**
     * @see Executable.toGenericString
     * @see Field.toGenericString
     */
    var genericString: String? = null

    /** @see Member.getName */
    open fun name(name: String) = apply {
        this.name = name
    }

    /** @see Member.getName */
    open fun name(condition: (String) -> Boolean) = apply {
        this.nameCondition = condition
    }

    /** @see Member.getModifiers */
    open fun modifiers(vararg modifiers: Modifiers) = apply {
        this.modifiers.addAll(modifiers)
    }

    /** @see Member.getModifiers */
    open fun modifiersNot(vararg modifiers: Modifiers) = apply {
        this.modifiersNot.addAll(modifiers)
    }

    /** @see Member.getModifiers */
    open fun modifiers(condition: (Set<Modifiers>) -> Boolean) = apply {
        this.modifiersCondition = condition
    }

    /** @see Member.isSynthetic */
    open fun isSynthetic(isSynthetic: Boolean) = apply {
        this.isSynthetic = isSynthetic
    }

    /** @see Member.isSynthetic */
    open fun isSyntheticNot(isSynthetic: Boolean) = apply {
        this.isSyntheticNot = isSynthetic
    }

    /** @see AnnotatedElement.getDeclaredAnnotations */
    open fun annotations(vararg annotations: Any) = apply {
        this.annotations.addAll(annotations.toList())
    }

    /** @see AnnotatedElement.getDeclaredAnnotations */
    open fun annotationsNot(vararg annotations: Any) = apply {
        this.annotationsNot.addAll(annotations.toList())
    }

    /** @see AnnotatedElement.getDeclaredAnnotations */
    open fun emptyAnnotations() = apply {
        emptyConditions += EMPTY_ANNOTATIONS
    }

    /** @see AnnotatedElement.getDeclaredAnnotations */
    open fun emptyAnnotationsNot() = apply {
        emptyConditions += EMPTY_ANNOTATIONS_NOT
    }

    /**
     * @see Executable.toGenericString
     * @see Field.toGenericString
     */
    open fun genericString(genericString: String) = apply {
        this.genericString = genericString
    }

    /**
     * Enables superclass mode for this condition only.
     *
     * It does not affect other conditions created from the same scope.
     * @see Configuration.Superclass.NORMAL
     */
    open fun superclass() = apply {
        superclassMode = maxOf(superclassMode, Configuration.Superclass.NORMAL)
    }

    /**
     * Enables superclass mode for this condition only.
     *
     * It does not affect other conditions created from the same scope.
     * @param interfaces whether to search all the interfaces after the superclasses.
     * @see Configuration.Superclass.NORMAL
     * @see Configuration.Superclass.INCLUDE_INTERFACES
     */
    open fun superclass(interfaces: Boolean) = apply {
        val mode = if (interfaces) Configuration.Superclass.INCLUDE_INTERFACES else Configuration.Superclass.NORMAL
        superclassMode = maxOf(superclassMode, mode)
    }

    /**
     * Initializes the copied data from this condition to the new condition.
     * @param newSelf the new condition instance.
     */
    protected open fun initializeCopiedData(newSelf: MemberCondition<M, R, T>) {
        newSelf.name = name
        newSelf.nameCondition = nameCondition
        newSelf.modifiers.addAll(modifiers)
        newSelf.modifiersNot.addAll(modifiersNot)
        newSelf.modifiersCondition = modifiersCondition
        newSelf.isSynthetic = isSynthetic
        newSelf.isSyntheticNot = isSyntheticNot
        newSelf.annotations.addAll(annotations)
        newSelf.annotationsNot.addAll(annotationsNot)
        newSelf.genericString = genericString
        newSelf.superclassMode = superclassMode
        newSelf.emptyConditions.addAll(emptyConditions)
    }

    /**
     * Merges the data set in the [other] condition into this condition.
     * @param other the other condition instance to merge from.
     */
    @JvmSynthetic
    internal open fun initializeMergedData(other: MemberCondition<M, R, T>) {
        other.name?.let { name = it }
        other.nameCondition?.let { nameCondition = it }
        other.modifiers.takeIf { it.isNotEmpty() }?.let {
            modifiers.clear()
            modifiers.addAll(it)
        }
        other.modifiersNot.takeIf { it.isNotEmpty() }?.let {
            modifiersNot.clear()
            modifiersNot.addAll(it)
        }
        other.modifiersCondition?.let { modifiersCondition = it }
        other.isSynthetic?.let { isSynthetic = it }
        other.isSyntheticNot?.let { isSyntheticNot = it }
        other.annotations.takeIf { it.isNotEmpty() }?.let {
            annotations.clear()
            annotations.addAll(it)
        }
        other.annotationsNot.takeIf { it.isNotEmpty() }?.let {
            annotationsNot.clear()
            annotationsNot.addAll(it)
        }
        other.genericString?.let { genericString = it }
        superclassMode = maxOf(superclassMode, other.superclassMode)
        emptyConditions.addAll(other.emptyConditions)
    }

    /**
     * Creates a copy of this condition.
     * @return [MemberCondition]<[M], [R], [T]>
     */
    abstract fun copy(): MemberCondition<M, R, T>

    /**
     * Builds the condition with the given [configuration].
     *
     * - Note: If this condition is not created manually, then you cannot set [configuration] again.
     * @param configuration the configuration to build the condition with.
     * @return [List]<[R]>
     */
    @JvmOverloads
    open fun build(configuration: Configuration<T>? = null): List<R> =
        TODO("Implement the build function in a subclass.")

    /**
     * Gets the condition string map.
     * @return [Map]<[String], [Any] or null>
     */
    @get:JvmSynthetic
    internal open val conditionStringMap get() = mapOf(
        NAME to name,
        NAME_CONDITION to nameCondition,
        MODIFIERS to modifiers,
        MODIFIERS_NOT to modifiersNot,
        MODIFIERS_CONDITION to modifiersCondition,
        IS_SYNTHETIC to isSynthetic,
        IS_SYNTHETIC_NOT to isSyntheticNot,
        ANNOTATIONS to annotations,
        ANNOTATIONS_NOT to annotationsNot,
        EMPTY_ANNOTATIONS to emptyConditionOf(EMPTY_ANNOTATIONS),
        EMPTY_ANNOTATIONS_NOT to emptyConditionOf(EMPTY_ANNOTATIONS_NOT),
        GENERIC_STRING to genericString
    )

    /**
     * Gets the display value of the empty condition [name].
     * @param name the name of the empty condition.
     * @return [Boolean] or null.
     */
    @JvmSynthetic
    internal fun emptyConditionOf(name: String) = true.takeIf { name in emptyConditions }

    companion object {
        const val NAME = "name"
        const val NAME_CONDITION = "nameCondition"
        const val MODIFIERS = "modifiers"
        const val MODIFIERS_NOT = "modifiersNot"
        const val MODIFIERS_CONDITION = "modifiersCondition"
        const val IS_SYNTHETIC = "isSynthetic"
        const val IS_SYNTHETIC_NOT = "isSyntheticNot"
        const val ANNOTATIONS = "annotations"
        const val ANNOTATIONS_NOT = "annotationsNot"
        const val EMPTY_ANNOTATIONS = "emptyAnnotations"
        const val EMPTY_ANNOTATIONS_NOT = "emptyAnnotationsNot"
        const val GENERIC_STRING = "genericString"
    }

    /**
     * Checks if the [configuration] is null and sets it.
     * If the [configuration] is not null, throw an exception.
     * @param configuration the configuration to set.
     * @throws IllegalStateException if the [configuration] is not null.
     */
    protected fun checkAndSetConfiguration(configuration: Configuration<T>) {
        check(this.configuration == null) {
            "Configuration already set for this condition \"$javaClass\" of \"${this.configuration}\". " +
                "To prevent problems, the configuration can only be set once in a condition, " +
                "otherwise use copy() to reuse the condition."
        }

        this.configuration = configuration
    }
}