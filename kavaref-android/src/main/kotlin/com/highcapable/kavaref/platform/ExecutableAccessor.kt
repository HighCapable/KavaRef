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

import android.os.Build
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Accessor for [Executable] to provide platform-specific features.
 */
internal class ExecutableAccessor(override val member: Member) : MemberAccessor(member) {

    val parameterTypes: Array<Class<*>> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).parameterTypes
        member is Constructor<*> -> member.parameterTypes
        member is Method -> member.parameterTypes
        else -> error("Unsupported member type: $member")
    }
    val parameterCount: Int get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).parameterCount
        member is Constructor<*> -> member.parameterTypes.size
        member is Method -> member.parameterTypes.size
        else -> error("Unsupported member type: $member")
    }
    val typeParameters: Array<out TypeVariable<*>> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).typeParameters
        member is Constructor<*> -> member.typeParameters
        member is Method -> member.typeParameters
        else -> error("Unsupported member type: $member")
    }
    val exceptionTypes: Array<Class<*>> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).exceptionTypes
        member is Constructor<*> -> member.exceptionTypes
        member is Method -> member.exceptionTypes
        else -> error("Unsupported member type: $member")
    }
    val genericExceptionTypes: Array<Type> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).genericExceptionTypes
        member is Constructor<*> -> member.genericExceptionTypes
        member is Method -> member.genericExceptionTypes
        else -> error("Unsupported member type: $member")
    }
    val genericParameterTypes: Array<Type> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).genericParameterTypes
        member is Constructor<*> -> member.genericParameterTypes
        member is Method -> member.genericParameterTypes
        else -> error("Unsupported member type: $member")
    }
    val isVarArgs: Boolean get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).isVarArgs
        member is Constructor<*> -> member.isVarArgs
        member is Method -> member.isVarArgs
        else -> error("Unsupported member type: $member")
    }
    val parameterAnnotations: Array<Array<Annotation>> get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> (member as Executable).parameterAnnotations
        member is Constructor<*> -> member.parameterAnnotations
        member is Method -> member.parameterAnnotations
        else -> error("Unsupported member type: $member")
    }
    val annotatedReturnType: AnnotatedElement get() = error("getAnnotatedReturnType is not supported on Android.")
    val annotatedReceiverType: AnnotatedElement get() = error("getAnnotatedReceiverType is not supported on Android.")
    val annotatedParameterTypes: Array<AnnotatedElement> get() = error("getAnnotatedParameterTypes is not supported on Android.")
    val annotatedExceptionTypes: Array<AnnotatedElement> get() = error("getAnnotatedExceptionTypes is not supported on Android.")

    override fun toString() = member.toString()
}