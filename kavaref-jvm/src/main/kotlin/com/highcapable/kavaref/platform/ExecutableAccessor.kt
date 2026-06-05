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
 * This file is created by fankes on 2026/6/5.
 */
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.kavaref.platform

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Executable
import java.lang.reflect.Member
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Accessor for [Executable] to provide platform-specific features.
 */
internal class ExecutableAccessor(override val member: Member) : MemberAccessor(member) {

    private val executable = member as Executable

    val parameterTypes: Array<Class<*>> get() = executable.parameterTypes
    val parameterCount: Int get() = executable.parameterCount
    val typeParameters: Array<out TypeVariable<*>> get() = executable.typeParameters
    val exceptionTypes: Array<Class<*>> get() = executable.exceptionTypes
    val genericExceptionTypes: Array<Type> get() = executable.genericExceptionTypes
    val genericParameterTypes: Array<Type> get() = executable.genericParameterTypes
    val isVarArgs: Boolean get() = executable.isVarArgs
    val parameterAnnotations: Array<Array<Annotation>> get() = executable.parameterAnnotations
    val annotatedReturnType: AnnotatedElement get() = executable.annotatedReturnType
    val annotatedReceiverType: AnnotatedElement get() = executable.annotatedReceiverType
    val annotatedParameterTypes: Array<AnnotatedElement> get() = executable.annotatedParameterTypes as Array<AnnotatedElement>
    val annotatedExceptionTypes: Array<AnnotatedElement> get() = executable.annotatedExceptionTypes as Array<AnnotatedElement>

    override fun toString() = member.toString()
}