/*
 * KavaRef - A modernizing Java reflection with Kotlin.
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
package com.highcapable.kavaref.condition.matcher

import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.condition.matcher.extension.matchesAll
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [TypeMatcher] that matches a parameterized type.
 * @see ParameterizedType.getRawType
 * @see ParameterizedType.getActualTypeArguments
 * @param rawType the raw type of the parameterized type.
 * @param arguments the [TypeMatcher]s for the type arguments of the parameterized type.
 */
data class ParameterizedTypeMatcher(
    val rawType: Class<*>,
    val arguments: List<TypeMatcher>
) : TypeMatcher {

    override fun matches(type: Type): Boolean {
        if (type !is ParameterizedType) return false
        if (type.rawType != rawType) return false
        val args = type.actualTypeArguments

        return args.toList().matchesAll(arguments)
    }
}