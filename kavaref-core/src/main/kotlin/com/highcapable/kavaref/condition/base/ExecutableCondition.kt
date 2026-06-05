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

import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.resolver.base.MemberResolver
import java.lang.reflect.Executable
import java.lang.reflect.Member

/**
 * Base class for conditions that are applicable to executable members (methods and constructors) [E], [R].
 * 
 * [T] to specify the declaring class type of the executable.
 */
abstract class ExecutableCondition<E : Member, R : MemberResolver<E, T>, T : Any> : MemberCondition<E, R, T>() {

    /** @see Executable.getParameterTypes */
    val parameters = mutableListOf<Any>()

    /** @see Executable.getParameterTypes */
    val parametersNot = mutableListOf<Any>()

    /** @see Executable.getParameterTypes */
    var parametersCondition: ((List<Class<*>>) -> Boolean)? = null

    /** @see Executable.getTypeParameters */
    val typeParameters = mutableSetOf<TypeMatcher>()

    /** @see Executable.getTypeParameters */
    val typeParametersNot = mutableSetOf<TypeMatcher>()

    /** @see Executable.getParameterCount */
    var parameterCount: Int? = null

    /** @see Executable.getParameterCount */
    var parameterCountCondition: ((Int) -> Boolean)? = null

    /** @see Executable.getExceptionTypes */
    val exceptionTypes = mutableSetOf<Any>()

    /** @see Executable.getExceptionTypes */
    val exceptionTypesNot = mutableSetOf<Any>()

    /** @see Executable.getGenericExceptionTypes */
    val genericExceptionTypes = mutableSetOf<TypeMatcher>()

    /** @see Executable.getGenericExceptionTypes */
    val genericExceptionTypesNot = mutableSetOf<TypeMatcher>()

    /** @see Executable.getGenericParameterTypes */
    val genericParameters = mutableSetOf<TypeMatcher>()

    /** @see Executable.getGenericParameterTypes */
    val genericParametersNot = mutableSetOf<TypeMatcher>()

    /** @see Executable.isVarArgs */
    var isVarArgs: Boolean? = null

    /** @see Executable.isVarArgs */
    var isVarArgsNot: Boolean? = null

    /** @see Executable.getParameterAnnotations */
    val parameterAnnotations = mutableListOf<Set<Any>>()

    /** @see Executable.getParameterAnnotations */
    val parameterAnnotationsNot = mutableListOf<Set<Any>>()

    /** @see Executable.getAnnotatedReturnType */
    val annotatedReturnType = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedReturnType */
    val annotatedReturnTypeNot = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedReceiverType */
    val annotatedReceiverType = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedReceiverType */
    val annotatedReceiverTypeNot = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedParameterTypes */
    val annotatedParameterTypes = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedParameterTypes */
    val annotatedParameterTypesNot = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedExceptionTypes */
    val annotatedExceptionTypes = mutableSetOf<Any>()

    /** @see Executable.getAnnotatedExceptionTypes */
    val annotatedExceptionTypesNot = mutableSetOf<Any>()

    /** @see Executable.getParameterTypes */
    open fun parameters(vararg types: Any) = apply {
        this.parameters.addAll(types)
    }

    /** @see Executable.getParameterTypes */
    open fun parametersNot(vararg types: Any) = apply {
        this.parametersNot.addAll(types)
    }

    /** @see Executable.getParameterTypes */
    open fun parameters(condition: (List<Class<*>>) -> Boolean) = apply {
        this.parametersCondition = condition
    }

    /**
     * Set [parameterCount] to `0` to match methods with no parameters.
     * @see Executable.getParameterCount
     */
    open fun emptyParameters() = apply {
        this.parameterCount = 0
    }

    /**
     * Set [parameterCountCondition] to check if the method has parameters.
     * @see Executable.getParameterCount
     */
    open fun emptyParametersNot() = apply {
        this.parameterCountCondition = { it > 0 }
    }

    /** @see Executable.getTypeParameters */
    open fun typeParameters(vararg types: TypeMatcher) = apply {
        this.typeParameters.addAll(types)
    }

    /** @see Executable.getTypeParameters */
    open fun typeParametersNot(vararg types: TypeMatcher) = apply {
        this.typeParametersNot.addAll(types)
    }

    /** @see Executable.getParameterCount */
    open fun parameterCount(count: Int) = apply {
        this.parameterCount = count
    }

    /** @see Executable.getParameterCount */
    open fun parameterCount(condition: (Int) -> Boolean) = apply {
        this.parameterCountCondition = condition
    }

    /** @see Executable.getExceptionTypes */
    open fun exceptionTypes(vararg types: Any) = apply {
        this.exceptionTypes.addAll(types)
    }

    /** @see Executable.getExceptionTypes */
    open fun exceptionTypesNot(vararg types: Any) = apply {
        this.exceptionTypesNot.addAll(types)
    }

    /** @see Executable.getGenericExceptionTypes */
    open fun genericExceptionTypes(vararg types: TypeMatcher) = apply {
        this.genericExceptionTypes.addAll(types)
    }

    /** @see Executable.getGenericExceptionTypes */
    open fun genericExceptionTypesNot(vararg types: TypeMatcher) = apply {
        this.genericExceptionTypesNot.addAll(types)
    }

    /** @see Executable.getGenericParameterTypes */
    open fun genericParameters(vararg types: TypeMatcher) = apply {
        this.genericParameters.addAll(types)
    }

    /** @see Executable.getGenericParameterTypes */
    open fun genericParametersNot(vararg types: TypeMatcher) = apply {
        this.genericParametersNot.addAll(types)
    }

    /** @see Executable.isVarArgs */
    open fun isVarArgs(isVarArgs: Boolean) = apply {
        this.isVarArgs = isVarArgs
    }

    /** @see Executable.isVarArgs */
    open fun isVarArgsNot(isVarArgs: Boolean) = apply {
        this.isVarArgsNot = isVarArgs
    }

    /** @see Executable.getParameterAnnotations */
    open fun parameterAnnotations(vararg annotations: Set<Any>) = apply {
        this.parameterAnnotations.addAll(annotations)
    }

    /** @see Executable.getParameterAnnotations */
    open fun parameterAnnotationsNot(vararg annotations: Set<Any>) = apply {
        this.parameterAnnotationsNot.addAll(annotations)
    }

    /** @see Executable.getAnnotatedReturnType */
    open fun annotatedReturnType(vararg types: Any) = apply {
        this.annotatedReturnType.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedReturnType */
    open fun annotatedReturnTypeNot(vararg types: Any) = apply {
        this.annotatedReturnTypeNot.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedReceiverType */
    open fun annotatedReceiverType(vararg types: Any) = apply {
        this.annotatedReceiverType.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedReceiverType */
    open fun annotatedReceiverTypeNot(vararg types: Any) = apply {
        this.annotatedReceiverTypeNot.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedParameterTypes */
    open fun annotatedParameterTypes(vararg types: Any) = apply {
        this.annotatedParameterTypes.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedParameterTypes */
    open fun annotatedParameterTypesNot(vararg types: Any) = apply {
        this.annotatedParameterTypesNot.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedExceptionTypes */
    open fun annotatedExceptionTypes(vararg types: Any) = apply {
        this.annotatedExceptionTypes.addAll(types.toList())
    }

    /** @see Executable.getAnnotatedExceptionTypes */
    open fun annotatedExceptionTypesNot(vararg types: Any) = apply {
        this.annotatedExceptionTypesNot.addAll(types.toList())
    }

    override fun initializeCopiedData(newSelf: MemberCondition<E, R, T>) {
        super.initializeCopiedData(newSelf)

        (newSelf as? ExecutableCondition<E, R, T>)?.also {
            it.parameters.addAll(parameters)
            it.parametersNot.addAll(parametersNot)
            it.parametersCondition = parametersCondition
            it.parameterCount = parameterCount
            it.parameterCountCondition = parameterCountCondition
            it.typeParameters.addAll(typeParameters)
            it.typeParametersNot.addAll(typeParametersNot)
            it.exceptionTypes.addAll(exceptionTypes)
            it.exceptionTypesNot.addAll(exceptionTypesNot)
            it.genericExceptionTypes.addAll(genericExceptionTypes)
            it.genericExceptionTypesNot.addAll(genericExceptionTypesNot)
            it.genericParameters.addAll(genericParameters)
            it.genericParametersNot.addAll(genericParametersNot)
            it.isVarArgs = isVarArgs
            it.isVarArgsNot = isVarArgsNot
            it.parameterAnnotations.addAll(parameterAnnotations)
            it.parameterAnnotationsNot.addAll(parameterAnnotationsNot)
            it.annotatedReturnType.addAll(annotatedReturnType)
            it.annotatedReturnTypeNot.addAll(annotatedReturnTypeNot)
            it.annotatedReceiverType.addAll(annotatedReceiverType)
            it.annotatedReceiverTypeNot.addAll(annotatedReceiverTypeNot)
            it.annotatedParameterTypes.addAll(annotatedParameterTypes)
            it.annotatedParameterTypesNot.addAll(annotatedParameterTypesNot)
            it.annotatedExceptionTypes.addAll(annotatedExceptionTypes)
            it.annotatedExceptionTypesNot.addAll(annotatedExceptionTypesNot)
        }
    }

    override fun initializeMergedData(other: MemberCondition<E, R, T>) {
        super.initializeMergedData(other)

        (other as? ExecutableCondition<E, R, T>)?.also { condition ->
            condition.parameters.takeIf { it.isNotEmpty() }?.let {
                parameters.clear()
                parameters.addAll(it)
            }
            condition.parametersNot.takeIf { it.isNotEmpty() }?.let {
                parametersNot.clear()
                parametersNot.addAll(it)
            }
            condition.parametersCondition?.let { parametersCondition = it }
            condition.parameterCount?.let { parameterCount = it }
            condition.parameterCountCondition?.let { parameterCountCondition = it }
            condition.typeParameters.takeIf { it.isNotEmpty() }?.let {
                typeParameters.clear()
                typeParameters.addAll(it)
            }
            condition.typeParametersNot.takeIf { it.isNotEmpty() }?.let {
                typeParametersNot.clear()
                typeParametersNot.addAll(it)
            }
            condition.exceptionTypes.takeIf { it.isNotEmpty() }?.let {
                exceptionTypes.clear()
                exceptionTypes.addAll(it)
            }
            condition.exceptionTypesNot.takeIf { it.isNotEmpty() }?.let {
                exceptionTypesNot.clear()
                exceptionTypesNot.addAll(it)
            }
            condition.genericExceptionTypes.takeIf { it.isNotEmpty() }?.let {
                genericExceptionTypes.clear()
                genericExceptionTypes.addAll(it)
            }
            condition.genericExceptionTypesNot.takeIf { it.isNotEmpty() }?.let {
                genericExceptionTypesNot.clear()
                genericExceptionTypesNot.addAll(it)
            }
            condition.genericParameters.takeIf { it.isNotEmpty() }?.let {
                genericParameters.clear()
                genericParameters.addAll(it)
            }
            condition.genericParametersNot.takeIf { it.isNotEmpty() }?.let {
                genericParametersNot.clear()
                genericParametersNot.addAll(it)
            }
            condition.isVarArgs?.let { isVarArgs = it }
            condition.isVarArgsNot?.let { isVarArgsNot = it }
            condition.parameterAnnotations.takeIf { it.isNotEmpty() }?.let {
                parameterAnnotations.clear()
                parameterAnnotations.addAll(it)
            }
            condition.parameterAnnotationsNot.takeIf { it.isNotEmpty() }?.let {
                parameterAnnotationsNot.clear()
                parameterAnnotationsNot.addAll(it)
            }
            condition.annotatedReturnType.takeIf { it.isNotEmpty() }?.let {
                annotatedReturnType.clear()
                annotatedReturnType.addAll(it)
            }
            condition.annotatedReturnTypeNot.takeIf { it.isNotEmpty() }?.let {
                annotatedReturnTypeNot.clear()
                annotatedReturnTypeNot.addAll(it)
            }
            condition.annotatedReceiverType.takeIf { it.isNotEmpty() }?.let {
                annotatedReceiverType.clear()
                annotatedReceiverType.addAll(it)
            }
            condition.annotatedReceiverTypeNot.takeIf { it.isNotEmpty() }?.let {
                annotatedReceiverTypeNot.clear()
                annotatedReceiverTypeNot.addAll(it)
            }
            condition.annotatedParameterTypes.takeIf { it.isNotEmpty() }?.let {
                annotatedParameterTypes.clear()
                annotatedParameterTypes.addAll(it)
            }
            condition.annotatedParameterTypesNot.takeIf { it.isNotEmpty() }?.let {
                annotatedParameterTypesNot.clear()
                annotatedParameterTypesNot.addAll(it)
            }
            condition.annotatedExceptionTypes.takeIf { it.isNotEmpty() }?.let {
                annotatedExceptionTypes.clear()
                annotatedExceptionTypes.addAll(it)
            }
            condition.annotatedExceptionTypesNot.takeIf { it.isNotEmpty() }?.let {
                annotatedExceptionTypesNot.clear()
                annotatedExceptionTypesNot.addAll(it)
            }
        }
    }

    override val conditionStringMap
        get() = super.conditionStringMap + mapOf(
            PARAMETERS to parameters,
            PARAMETERS_NOT to parametersNot,
            PARAMETERS_CONDITION to parametersCondition,
            PARAMETER_COUNT to parameterCount,
            PARAMETER_COUNT_CONDITION to parameterCountCondition,
            TYPE_PARAMETERS to typeParameters,
            TYPE_PARAMETERS_NOT to typeParametersNot,
            EXCEPTION_TYPES to exceptionTypes,
            EXCEPTION_TYPES_NOT to exceptionTypesNot,
            GENERIC_EXCEPTION_TYPES to genericExceptionTypes,
            GENERIC_EXCEPTION_TYPES_NOT to genericExceptionTypesNot,
            GENERIC_PARAMETERS to genericParameters,
            GENERIC_PARAMETERS_NOT to genericParametersNot,
            IS_VAR_ARGS to isVarArgs,
            IS_VAR_ARGS_NOT to isVarArgsNot,
            PARAMETER_ANNOTATIONS to parameterAnnotations,
            PARAMETER_ANNOTATIONS_NOT to parameterAnnotationsNot,
            ANNOTATED_RETURN_TYPE to annotatedReturnType,
            ANNOTATED_RETURN_TYPE_NOT to annotatedReturnTypeNot,
            ANNOTATED_RECEIVER_TYPE to annotatedReceiverType,
            ANNOTATED_RECEIVER_TYPE_NOT to annotatedReceiverTypeNot,
            ANNOTATED_PARAMETER_TYPES to annotatedParameterTypes,
            ANNOTATED_PARAMETER_TYPES_NOT to annotatedParameterTypesNot,
            ANNOTATED_EXCEPTION_TYPES to annotatedExceptionTypes,
            ANNOTATED_EXCEPTION_TYPES_NOT to annotatedExceptionTypesNot
        )

    companion object {
        const val PARAMETERS = "parameters"
        const val PARAMETERS_NOT = "parametersNot"
        const val PARAMETERS_CONDITION = "parametersCondition"
        const val PARAMETER_COUNT = "parameterCount"
        const val PARAMETER_COUNT_CONDITION = "parameterCountCondition"
        const val TYPE_PARAMETERS = "typeParameters"
        const val TYPE_PARAMETERS_NOT = "typeParametersNot"
        const val EXCEPTION_TYPES = "exceptionTypes"
        const val EXCEPTION_TYPES_NOT = "exceptionTypesNot"
        const val GENERIC_EXCEPTION_TYPES = "genericExceptionTypes"
        const val GENERIC_EXCEPTION_TYPES_NOT = "genericExceptionTypesNot"
        const val GENERIC_PARAMETERS = "genericParameters"
        const val GENERIC_PARAMETERS_NOT = "genericParametersNot"
        const val IS_VAR_ARGS = "isVarArgs"
        const val IS_VAR_ARGS_NOT = "isVarArgsNot"
        const val PARAMETER_ANNOTATIONS = "parameterAnnotations"
        const val PARAMETER_ANNOTATIONS_NOT = "parameterAnnotationsNot"
        const val ANNOTATED_RETURN_TYPE = "annotatedReturnType"
        const val ANNOTATED_RETURN_TYPE_NOT = "annotatedReturnTypeNot"
        const val ANNOTATED_RECEIVER_TYPE = "annotatedReceiverType"
        const val ANNOTATED_RECEIVER_TYPE_NOT = "annotatedReceiverTypeNot"
        const val ANNOTATED_PARAMETER_TYPES = "annotatedParameterTypes"
        const val ANNOTATED_PARAMETER_TYPES_NOT = "annotatedParameterTypesNot"
        const val ANNOTATED_EXCEPTION_TYPES = "annotatedExceptionTypes"
        const val ANNOTATED_EXCEPTION_TYPES_NOT = "annotatedExceptionTypesNot"
    }
}