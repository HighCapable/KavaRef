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
@file:Suppress("unused")

package com.highcapable.kavaref.condition.type

import java.lang.reflect.Modifier

/**
 * Modifiers for Java reflection.
 * @param mod the modifier bitmask.
 */
enum class Modifiers(private val mod: Int) {
    PUBLIC(Modifier.PUBLIC),
    PRIVATE(Modifier.PRIVATE),
    PROTECTED(Modifier.PROTECTED),
    STATIC(Modifier.STATIC),
    FINAL(Modifier.FINAL),
    SYNCHRONIZED(Modifier.SYNCHRONIZED),
    VOLATILE(Modifier.VOLATILE),
    TRANSIENT(Modifier.TRANSIENT),
    NATIVE(Modifier.NATIVE),
    INTERFACE(Modifier.INTERFACE),
    ABSTRACT(Modifier.ABSTRACT),
    STRICT(Modifier.STRICT);

    /**
     * Checks if the modifier matches the given modifier.
     * @param modifier the modifier bitmask to check against.
     * @return [Boolean]
     */
    fun matches(modifier: Int) = (mod and modifier) != 0

    companion object {

        /**
         * Gets the set of modifiers that match the given modifier bitmask.
         * @param modifier the modifier bitmask to check against.
         * @return [Set]<[Modifiers]>
         */
        fun matching(modifier: Int) = entries.filter { it.matches(modifier) }.toSet()
    }
}