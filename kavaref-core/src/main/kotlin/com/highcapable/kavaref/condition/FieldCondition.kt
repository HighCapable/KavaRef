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
@file:Suppress("unused", "DuplicatedCode")

package com.highcapable.kavaref.condition

import com.highcapable.kavaref.condition.base.MemberCondition
import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import java.lang.reflect.Field
import java.lang.reflect.Type

/**
 * Condition for [Field] of [FieldResolver].
 */
class FieldCondition<T : Any> : MemberCondition<Field, FieldResolver<T>, T>() {

    /** @see Field.isEnumConstant */
    var isEnumConstant: Boolean? = null

    /** @see Field.isEnumConstant */
    var isEnumConstantNot: Boolean? = null

    /** @see Field.getType */
    var type: Any? = null

    /** @see Field.getType */
    var typeCondition: ((Class<*>) -> Boolean)? = null

    /** @see Field.getGenericType */
    var genericType: TypeMatcher? = null

    /** @see Field.getGenericType */
    var genericTypeCondition: ((Type) -> Boolean)? = null

    override fun name(name: String) = apply { super.name(name) }
    override fun name(condition: (String) -> Boolean) = apply { super.name(condition) }
    override fun modifiers(vararg modifiers: Modifiers) = apply { super.modifiers(*modifiers) }
    override fun modifiersNot(vararg modifiers: Modifiers) = apply { super.modifiersNot(*modifiers) }
    override fun modifiers(condition: (Set<Modifiers>) -> Boolean) = apply { super.modifiers(condition) }
    override fun isSynthetic(isSynthetic: Boolean) = apply { super.isSynthetic(isSynthetic) }
    override fun isSyntheticNot(isSynthetic: Boolean) = apply { super.isSyntheticNot(isSynthetic) }
    override fun annotations(vararg annotations: Any) = apply { super.annotations(*annotations) }
    override fun annotationsNot(vararg annotations: Any) = apply { super.annotationsNot(*annotations) }
    override fun emptyAnnotations() = apply { super.emptyAnnotations() }
    override fun emptyAnnotationsNot() = apply { super.emptyAnnotationsNot() }
    override fun genericString(genericString: String) = apply { super.genericString(genericString) }

    override fun superclass() = apply { super.superclass() }
    override fun superclass(interfaces: Boolean) = apply { super.superclass(interfaces) }

    /** @see Field.isEnumConstant */
    fun isEnumConstant(isEnumConstant: Boolean) = apply {
        this.isEnumConstant = isEnumConstant
    }

    /** @see Field.isEnumConstant */
    fun isEnumConstantNot(isEnumConstant: Boolean) = apply {
        this.isEnumConstantNot = isEnumConstant
    }

    /** @see Field.getType */
    fun type(type: Any) = apply {
        this.type = type
    }

    /** @see Field.getType */
    fun type(condition: (Class<*>) -> Boolean) = apply {
        this.typeCondition = condition
    }

    /** @see Field.getGenericType */
    fun genericType(type: TypeMatcher) = apply {
        this.genericType = type
    }

    /** @see Field.getGenericType */
    fun genericType(condition: (Type) -> Boolean) = apply {
        this.genericTypeCondition = condition
    }

    override fun initializeCopiedData(newSelf: MemberCondition<Field, FieldResolver<T>, T>) {
        super.initializeCopiedData(newSelf)

        (newSelf as? FieldCondition)?.also {
            it.isEnumConstant = isEnumConstant
            it.isEnumConstantNot = isEnumConstantNot
            it.type = type
            it.typeCondition = typeCondition
            it.genericType = genericType
            it.genericTypeCondition = genericTypeCondition
        }
    }

    override fun initializeMergedData(other: MemberCondition<Field, FieldResolver<T>, T>) {
        super.initializeMergedData(other)

        (other as? FieldCondition)?.also { condition ->
            condition.isEnumConstant?.let { isEnumConstant = it }
            condition.isEnumConstantNot?.let { isEnumConstantNot = it }
            condition.type?.let { type = it }
            condition.typeCondition?.let { typeCondition = it }
            condition.genericType?.let { genericType = it }
            condition.genericTypeCondition?.let { genericTypeCondition = it }
        }
    }

    override fun copy() = FieldCondition<T>().also {
        initializeCopiedData(it)
    }

    override fun build(configuration: Configuration<T>?): List<FieldResolver<T>> {
        configuration?.let { checkAndSetConfiguration(it) }
        return MemberProcessor.resolve(condition = this, this.configuration)
    }

    override val conditionStringMap
        get() = super.conditionStringMap + mapOf(
            IS_ENUM_CONSTANT to isEnumConstant,
            IS_ENUM_CONSTANT_NOT to isEnumConstantNot,
            TYPE to type,
            TYPE_CONDITION to typeCondition,
            GENERIC_TYPE to genericType,
            GENERIC_TYPE_CONDITION to genericTypeCondition
        )

    companion object {
        const val IS_ENUM_CONSTANT = "isEnumConstant"
        const val IS_ENUM_CONSTANT_NOT = "isEnumConstantNot"
        const val TYPE = "type"
        const val TYPE_CONDITION = "typeCondition"
        const val GENERIC_TYPE = "genericType"
        const val GENERIC_TYPE_CONDITION = "genericTypeCondition"
    }
}