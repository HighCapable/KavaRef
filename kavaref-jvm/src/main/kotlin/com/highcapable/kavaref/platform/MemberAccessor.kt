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
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Accessor for [Member] to provide platform-specific features.
 */
internal open class MemberAccessor(open val member: Member) {

    val name: String get() = member.name
    val modifiers: Int get() = member.modifiers
    val isSynthetic: Boolean get() = member.isSynthetic
    val annotations: Array<Annotation> get() = when (member) {
        is AnnotatedElement -> (member as AnnotatedElement).declaredAnnotations
        else -> error("Unsupported member type: $this")
    }
    val genericString: String get() = when (member) {
        is Method -> (member as Method).toGenericString()
        is Constructor<*> -> (member as Constructor<*>).toGenericString()
        is Field -> (member as Field).toGenericString()
        else -> error("Unsupported member type: $member")
    }

    override fun toString() = member.toString()
}