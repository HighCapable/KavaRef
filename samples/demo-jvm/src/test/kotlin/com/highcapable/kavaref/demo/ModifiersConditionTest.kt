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
 * This file is created by fankes on 2026/10/9.
 */

package com.highcapable.kavaref.demo

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.isTransient
import com.highcapable.kavaref.extension.isVolatile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ModifiersConditionTest {

    @Test
    fun modifiersMatchDeclaredModifiers() {
        val methods = ModifiersConditionFixtures::class.resolve().method { name = "varargs"; modifiers(Modifiers.PUBLIC) }
        assertEquals(1, methods.size)
    }

    @Test
    fun transientDoesNotMatchVarargsMethods() {
        val resolver = ModifiersConditionFixtures::class.resolve()
        assertEquals(0, resolver.optional(silent = true).method { modifiers(Modifiers.TRANSIENT) }.size)
        assertFalse(resolver.firstMethod { name = "varargs" }.self.isTransient)
    }

    @Test
    fun volatileDoesNotMatchBridgeMethods() {
        val resolver = ModifiersConditionFixtures.Sub::class.resolve()
        assertEquals(0, resolver.optional(silent = true).method { modifiers(Modifiers.VOLATILE) }.size)
        assertFalse(resolver.method { name = "get" }.single { it.self.isBridge }.self.isVolatile)
    }
}