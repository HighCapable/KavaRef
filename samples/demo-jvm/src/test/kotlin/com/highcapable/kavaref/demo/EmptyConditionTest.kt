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
import com.highcapable.kavaref.condition.MethodCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmptyConditionTest {

    private val throwing = listOf("throwing", "throwingGeneric")

    private fun names(condition: MethodCondition<EmptyConditionFixtures>.() -> Unit) =
        EmptyConditionFixtures::class.resolve().optional(silent = true).method(condition).map { it.self.name }.sorted()

    @Test
    fun emptyAnnotationsMatchMembersWithoutAnnotations() {
        assertTrue("plain" in names { emptyAnnotations() })
        assertTrue("annotated" !in names { emptyAnnotations() })
        assertEquals(listOf("annotated"), names { emptyAnnotationsNot() })
    }

    @Test
    fun emptyTypeParametersMatchNonGenericExecutables() {
        assertEquals(listOf("generic", "throwingGeneric"), names { emptyTypeParametersNot() })
        assertTrue(names { emptyTypeParameters() }.none { it == "generic" || it == "throwingGeneric" })
    }

    @Test
    fun emptyExceptionTypesMatchExecutablesWithoutExceptions() {
        assertEquals(throwing, names { emptyExceptionTypesNot() })
        assertEquals(throwing, names { emptyGenericExceptionTypesNot() })
        assertTrue(names { emptyExceptionTypes() }.none { it in throwing })
        assertTrue(names { emptyGenericExceptionTypes() }.none { it in throwing })
    }
}