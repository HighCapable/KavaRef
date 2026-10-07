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
package com.highcapable.kavaref.condition.type

import com.highcapable.kavaref.extension.classOf
import kotlin.reflect.KClass

/**
 * You can fill this type in the reflection lookup condition as a placeholder.
 *
 * It is a vague type that you can use when the signature of the JVM type is too long, and you do not want to declare the type.
 *
 * Usage:
 *
 * ```
 * public void a(java.lang.String, some.name.like.too.long.Type, int);
 * ```
 *
 * ```kotlin
 * method {
 *    name = "a"
 *    parameters(String::class, VagueType, Int::class)
 * }
 * ```
 */
object VagueType {

    private const val TAG = "VagueType"

    /**
     * Format the placeholder to a string.
     * @param placeholder the placeholder to be formatted.
     * @return [String]
     */
    @JvmSynthetic
    internal fun format(placeholder: Any?): String? = when (placeholder) {
        null -> null
        is VagueType -> TAG
        is Class<*> -> if (placeholder != classOf<VagueType>())
            placeholder.toString()
        else TAG
        is KClass<*> -> if (placeholder != VagueType::class)
            placeholder.toString()
        else TAG
        is Collection<*> ->
            placeholder.map {
                if (it != null) format(it) else null
            }.toString()
        else -> placeholder.toString()
    }
}