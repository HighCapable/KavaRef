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

package com.highcapable.kavaref.condition.matcher

import com.highcapable.kavaref.condition.matcher.base.TypeMatcher
import com.highcapable.kavaref.condition.matcher.extension.matchesAll
import com.highcapable.kavaref.condition.matcher.extension.toTypeMatcher
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * A [TypeMatcher] that matches a wildcard type.
 * @see WildcardType.getUpperBounds
 * @see WildcardType.getLowerBounds
 * @param upperBounds the upper bounds of the wildcard type.
 * @param lowerBounds the lower bounds of the wildcard type.
 */
data class WildcardTypeMatcher(
    val upperBounds: List<TypeMatcher> = listOf(Any::class.toTypeMatcher()),
    val lowerBounds: List<TypeMatcher> = emptyList()
) : TypeMatcher {

    override fun matches(type: Type): Boolean {
        if (type !is WildcardType) return false
        return type.upperBounds.toList().matchesAll(upperBounds) &&
            type.lowerBounds.toList().matchesAll(lowerBounds)
    }
}