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
@file:Suppress("unused")

package com.highcapable.kavaref.platform

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Executable
import java.lang.reflect.Member
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Accessor for [Executable] to provide platform-specific features.
 */
class ExecutableAccessor(override val member: Member) : MemberAccessor(member) {
    val parameterTypes: Array<Class<*>> get() = error("Stub!")
    val parameterCount: Int get() = error("Stub!")
    val typeParameters: Array<out TypeVariable<*>> get() = error("Stub!")
    val exceptionTypes: Array<Class<*>> get() = error("Stub!")
    val genericExceptionTypes: Array<Type> get() = error("Stub!")
    val genericParameterTypes: Array<Type> get() = error("Stub!")
    val isVarArgs: Boolean get() = error("Stub!")
    val parameterAnnotations: Array<Array<Annotation>> get() = error("Stub!")
    val annotatedReturnType: AnnotatedElement get() = error("Stub!")
    val annotatedReceiverType: AnnotatedElement get() = error("Stub!")
    val annotatedParameterTypes: Array<AnnotatedElement> get() = error("Stub!")
    val annotatedExceptionTypes: Array<AnnotatedElement> get() = error("Stub!")
}