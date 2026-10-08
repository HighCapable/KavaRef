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

package com.highcapable.kavaref.demo.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.matcher.WildcardTypeMatcher
import com.highcapable.kavaref.condition.matcher.extension.parameterizedBy
import com.highcapable.kavaref.condition.matcher.extension.toTypeMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TypeConditionInstrumentedTest {

    private val resolver = TypeConditionFixtures::class.resolve()

    private fun wildcardMethods(matcher: WildcardTypeMatcher) =
        resolver.method { genericParameters(List::class.parameterizedBy(matcher)) }.map { it.self.name }.sorted()

    @Test
    fun stringTypesMatchParameters() {
        assertEquals(listOf("named"), resolver.method { parameters("java.lang.String", "java.lang.String") }.map { it.self.name })
        assertEquals(listOf("mixed"), resolver.method { parameterCount = 2; parametersNot("java.lang.String", "java.lang.String") }.map { it.self.name })
    }

    @Test
    fun unresolvedStringTypeIsNotMatchedInOptionalMode() {
        assertEquals(0, resolver.optional(silent = true).method { name = "plain"; parametersNot("com.example.Missing") }.size)
    }

    @Test
    fun unresolvedStringTypeThrowsInNormalMode() {
        assertThrows(ClassNotFoundException::class.java) {
            resolver.method { name = "plain"; parameters("com.example.Missing") }
        }
    }

    @Test
    fun wildcardMatcherWithDefaultBoundsMatchesUnboundedWildcard() {
        assertEquals(listOf("unbounded"), wildcardMethods(WildcardTypeMatcher()))
        assertEquals(listOf("unbounded"), wildcardMethods(WildcardTypeMatcher(upperBounds = listOf(Any::class.toTypeMatcher()))))
    }

    @Test
    fun wildcardMatcherMatchesBounds() {
        assertEquals(listOf("upperBounded"), wildcardMethods(WildcardTypeMatcher(upperBounds = listOf(Number::class.toTypeMatcher()))))
        assertEquals(listOf("lowerBounded"), wildcardMethods(WildcardTypeMatcher(lowerBounds = listOf(Number::class.toTypeMatcher()))))
    }
}