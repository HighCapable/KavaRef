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

import com.highcapable.kavaref.condition.base.ExecutableCondition
import com.highcapable.kavaref.condition.base.MemberCondition
import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Condition for [Method] of [MethodResolver].
 */
class MethodCondition<T : Any> : ExecutableCondition<Method, MethodResolver<T>, T>() {

    /** @see Method.getReturnType */
    var returnType: Any? = null

    /** @see Method.getReturnType */
    var returnTypeCondition: ((Class<*>) -> Boolean)? = null

    /** @see Method.getGenericReturnType */
    var genericReturnType: TypeMatcher? = null

    /** @see Method.getGenericReturnType */
    var genericReturnTypeCondition: ((Type) -> Boolean)? = null

    /** @see Method.isBridge */
    var isBridge: Boolean? = null

    /** @see Method.isBridge */
    var isBridgeNot: Boolean? = null

    /** @see Method.isDefault */
    var isDefault: Boolean? = null

    /** @see Method.isDefault */
    var isDefaultNot: Boolean? = null

    /** @see Method.getDefaultValue */
    var defaultValue: Any? = null

    /** @see Method.getDefaultValue */
    var defaultValueCondition: ((Any?) -> Boolean)? = null

    override fun name(name: String) = apply { super.name(name) }
    override fun name(condition: (String) -> Boolean) = apply { super.name(condition) }
    override fun modifiers(vararg modifiers: Modifiers) = apply { super.modifiers(*modifiers) }
    override fun modifiersNot(vararg modifiers: Modifiers) = apply { super.modifiersNot(*modifiers) }
    override fun modifiers(condition: (Set<Modifiers>) -> Boolean) = apply { super.modifiers(condition) }
    override fun isSynthetic(isSynthetic: Boolean) = apply { super.isSynthetic(isSynthetic) }
    override fun isSyntheticNot(isSynthetic: Boolean) = apply { super.isSyntheticNot(isSynthetic) }
    override fun annotations(vararg annotations: Any) = apply { super.annotations(*annotations) }
    override fun annotationsNot(vararg annotations: Any) = apply { super.annotationsNot(*annotations) }
    override fun genericString(genericString: String) = apply { super.genericString(genericString) }

    override fun parameters(vararg types: Any) = apply { super.parameters(*types) }
    override fun parametersNot(vararg types: Any) = apply { super.parametersNot(*types) }
    override fun parameters(condition: (List<Class<*>>) -> Boolean) = apply { super.parameters(condition) }
    override fun emptyParameters() = apply { super.emptyParameters() }
    override fun emptyParametersNot() = apply { super.emptyParametersNot() }
    override fun typeParameters(vararg types: TypeMatcher) = apply { super.typeParameters(*types) }
    override fun typeParametersNot(vararg types: TypeMatcher) = apply { super.typeParametersNot(*types) }
    override fun parameterCount(count: Int) = apply { super.parameterCount(count) }
    override fun parameterCount(condition: (Int) -> Boolean) = apply { super.parameterCount(condition) }
    override fun exceptionTypes(vararg types: Any) = apply { super.exceptionTypes(*types) }
    override fun exceptionTypesNot(vararg types: Any) = apply { super.exceptionTypesNot(*types) }
    override fun genericExceptionTypes(vararg types: TypeMatcher) = apply { super.genericExceptionTypes(*types) }
    override fun genericExceptionTypesNot(vararg types: TypeMatcher) = apply { super.genericExceptionTypesNot(*types) }
    override fun genericParameters(vararg types: TypeMatcher) = apply { super.genericParameters(*types) }
    override fun genericParametersNot(vararg types: TypeMatcher) = apply { super.genericParametersNot(*types) }
    override fun isVarArgs(isVarArgs: Boolean) = apply { super.isVarArgs(isVarArgs) }
    override fun isVarArgsNot(isVarArgs: Boolean) = apply { super.isVarArgsNot(isVarArgs) }
    override fun parameterAnnotations(vararg annotations: Set<Any>) = apply { super.parameterAnnotations(*annotations) }
    override fun parameterAnnotationsNot(vararg annotations: Set<Any>) = apply { super.parameterAnnotationsNot(*annotations) }
    override fun annotatedReturnType(vararg types: Any) = apply { super.annotatedReturnType(*types) }
    override fun annotatedReturnTypeNot(vararg types: Any) = apply { super.annotatedReturnTypeNot(*types) }
    override fun annotatedReceiverType(vararg types: Any) = apply { super.annotatedReceiverType(*types) }
    override fun annotatedReceiverTypeNot(vararg types: Any) = apply { super.annotatedReceiverTypeNot(*types) }
    override fun annotatedParameterTypes(vararg types: Any) = apply { super.annotatedParameterTypes(*types) }
    override fun annotatedParameterTypesNot(vararg types: Any) = apply { super.annotatedParameterTypesNot(*types) }
    override fun annotatedExceptionTypes(vararg types: Any) = apply { super.annotatedExceptionTypes(*types) }
    override fun annotatedExceptionTypesNot(vararg types: Any) = apply { super.annotatedExceptionTypesNot(*types) }

    override fun superclass() = apply { super.superclass() }

    /** @see Method.getReturnType */
    fun returnType(type: Any) = apply {
        this.returnType = type
    }

    /** @see Method.getReturnType */
    fun returnType(condition: (Class<*>) -> Boolean) = apply {
        this.returnTypeCondition = condition
    }

    /** @see Method.getGenericReturnType */
    fun genericReturnType(type: TypeMatcher) = apply {
        this.genericReturnType = type
    }

    /** @see Method.getGenericReturnType */
    fun genericReturnType(condition: (Type) -> Boolean) = apply {
        this.genericReturnTypeCondition = condition
    }

    /** @see Method.isBridge */
    fun isBridge(isBridge: Boolean) = apply {
        this.isBridge = isBridge
    }

    /** @see Method.isBridge */
    fun isBridgeNot(isBridge: Boolean) = apply {
        this.isBridgeNot = isBridge
    }

    /** @see Method.isDefault */
    fun isDefault(isDefault: Boolean) = apply {
        this.isDefault = isDefault
    }

    /** @see Method.isDefault */
    fun isDefaultNot(isDefault: Boolean) = apply {
        this.isDefaultNot = isDefault
    }

    /** @see Method.getDefaultValue */
    fun defaultValue(value: Any?) = apply {
        this.defaultValue = value
    }

    /** @see Method.getDefaultValue */
    fun defaultValue(condition: (Any?) -> Boolean) = apply {
        this.defaultValueCondition = condition
    }

    override fun initializeCopiedData(newSelf: MemberCondition<Method, MethodResolver<T>, T>) {
        super.initializeCopiedData(newSelf)

        (newSelf as? MethodCondition)?.also {
            it.returnType = returnType
            it.returnTypeCondition = returnTypeCondition
            it.genericReturnType = genericReturnType
            it.genericReturnTypeCondition = genericReturnTypeCondition
            it.isBridge = isBridge
            it.isBridgeNot = isBridgeNot
            it.isDefault = isDefault
            it.isDefaultNot = isDefaultNot
            it.defaultValue = defaultValue
            it.defaultValueCondition = defaultValueCondition
        }
    }

    override fun initializeMergedData(other: MemberCondition<Method, MethodResolver<T>, T>) {
        super.initializeMergedData(other)

        (other as? MethodCondition)?.also { condition ->
            condition.returnType?.let { returnType = it }
            condition.returnTypeCondition?.let { returnTypeCondition = it }
            condition.genericReturnType?.let { genericReturnType = it }
            condition.genericReturnTypeCondition?.let { genericReturnTypeCondition = it }
            condition.isBridge?.let { isBridge = it }
            condition.isBridgeNot?.let { isBridgeNot = it }
            condition.isDefault?.let { isDefault = it }
            condition.isDefaultNot?.let { isDefaultNot = it }
            condition.defaultValue?.let { defaultValue = it }
            condition.defaultValueCondition?.let { defaultValueCondition = it }
        }
    }

    override fun copy() = MethodCondition<T>().also {
        initializeCopiedData(it)
    }

    override fun build(configuration: Configuration<T>?): List<MethodResolver<T>> {
        configuration?.let { checkAndSetConfiguration(it) }
        return MemberProcessor.resolve(condition = this, this.configuration)
    }

    override val conditionStringMap
        get() = super.conditionStringMap + mapOf(
            RETURN_TYPE to returnType,
            RETURN_TYPE_CONDITION to returnTypeCondition,
            GENERIC_RETURN_TYPE to genericReturnType,
            GENERIC_RETURN_TYPE_CONDITION to genericReturnTypeCondition,
            IS_BRIDGE to isBridge,
            IS_BRIDGE_NOT to isBridgeNot,
            IS_DEFAULT to isDefault,
            IS_DEFAULT_NOT to isDefaultNot,
            DEFAULT_VALUE to defaultValue,
            DEFAULT_VALUE_CONDITION to defaultValueCondition
        )

    companion object {
        const val RETURN_TYPE = "returnType"
        const val RETURN_TYPE_CONDITION = "returnTypeCondition"
        const val GENERIC_RETURN_TYPE = "genericReturnType"
        const val GENERIC_RETURN_TYPE_CONDITION = "genericReturnTypeCondition"
        const val IS_BRIDGE = "isBridge"
        const val IS_BRIDGE_NOT = "isBridgeNot"
        const val IS_DEFAULT = "isDefault"
        const val IS_DEFAULT_NOT = "isDefaultNot"
        const val DEFAULT_VALUE = "defaultValue"
        const val DEFAULT_VALUE_CONDITION = "defaultValueCondition"
    }
}