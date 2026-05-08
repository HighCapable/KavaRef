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
package com.highcapable.kavaref.condition

import com.highcapable.kavaref.condition.base.ExecutableCondition
import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import java.lang.reflect.Constructor

/**
 * Condition for [Constructor] of [ConstructorResolver].
 */
class ConstructorCondition<T : Any> : ExecutableCondition<Constructor<T>, ConstructorResolver<T>, T>() {

    companion object {

        private const val SUPERCLASS_DEPRECATED_MESSAGE =
            "Constructors are not inherited from superclass in Java, so ConstructorCondition does not support superclass()."

        private const val SUPERCLASS_EXCEPTION_MESSAGE =
            "Not allowed to call superclass() in ConstructorCondition because constructors are not inherited from superclass."
    }

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

    @Deprecated(message = SUPERCLASS_DEPRECATED_MESSAGE, level = DeprecationLevel.ERROR)
    override fun superclass(): Nothing = error(SUPERCLASS_EXCEPTION_MESSAGE)

    override fun copy() = ConstructorCondition<T>().also {
        initializeCopiedData(it)
    }

    override fun build(configuration: Configuration<T>?): List<ConstructorResolver<T>> {
        configuration?.let { checkAndSetConfiguration(it) }
        return MemberProcessor.resolve(condition = this, this.configuration)
    }

    override val conditionStringMap get() = super.conditionStringMap
}