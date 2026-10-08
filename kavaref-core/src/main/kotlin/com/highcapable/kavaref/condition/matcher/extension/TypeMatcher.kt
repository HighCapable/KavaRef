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
 * This file is created by fankes on 2025/5/20.
 */
@file:Suppress("unused")

package com.highcapable.kavaref.condition.matcher.extension

import com.highcapable.kavaref.condition.matcher.ClassTypeMatcher
import com.highcapable.kavaref.condition.matcher.GenericArrayTypeMatcher
import com.highcapable.kavaref.condition.matcher.ParameterizedTypeMatcher
import com.highcapable.kavaref.condition.matcher.TypeVariableMatcher
import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * Creates a [TypeMatcher] for a type variable with the given name.
 * @param name the name of the type variable.
 * @return [TypeVariableMatcher]
 */
fun typeVar(name: String) = TypeVariableMatcher(name)

/**
 * Converts a [Class] to a [ClassTypeMatcher].
 * @receiver [Class]
 * @return [ClassTypeMatcher]
 */
fun Class<*>.toTypeMatcher() = ClassTypeMatcher(clazz = this)

/**
 * Converts a [KClass.java] to a [ClassTypeMatcher].
 * @receiver [KClass]
 * @return [ClassTypeMatcher]
 */
fun KClass<*>.toTypeMatcher() = java.toTypeMatcher()

/**
 * Creates a [ParameterizedTypeMatcher] for a specific [Class] type with the given type arguments.
 * @receiver [Class]
 * @param arguments the type arguments for the parameterized type.
 * @return [ParameterizedTypeMatcher]
 */
fun Class<*>.parameterizedBy(vararg arguments: TypeMatcher) =
    ParameterizedTypeMatcher(rawType = this, arguments.toList())

/**
 * Creates a [ParameterizedTypeMatcher] for a specific [KClass.java] type with the given type arguments.
 * @receiver [KClass]
 * @param arguments the type arguments for the parameterized type.
 * @return [ParameterizedTypeMatcher]
 */
fun KClass<*>.parameterizedBy(vararg arguments: TypeMatcher) = java.parameterizedBy(*arguments)

/**
 * Creates a [GenericArrayTypeMatcher] from [TypeMatcher].
 * @receiver [TypeMatcher]
 * @return [GenericArrayTypeMatcher]
 */
fun TypeMatcher.asGenericArray() = GenericArrayTypeMatcher(componentMatcher = this)

/**
 * Checks if the list of [Type] matches all the given [TypeMatcher].
 * @receiver the list of [Type] to be checked.
 * @param matchers the list of [TypeMatcher] to be checked.
 * @return [Boolean]
 */
@JvmSynthetic
internal fun List<Type>.matchesAll(matchers: List<TypeMatcher>): Boolean {
    if (this.size != matchers.size) return false
    return this.zip(matchers).all { (t, m) -> m.matches(t) }
}