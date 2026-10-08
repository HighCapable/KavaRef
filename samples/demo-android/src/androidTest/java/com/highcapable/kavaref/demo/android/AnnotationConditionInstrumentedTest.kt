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
import com.highcapable.kavaref.condition.MethodCondition
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnnotationConditionInstrumentedTest {

    private val resolver = AnnotationConditionFixtures::class.resolve()

    private fun names(condition: MethodCondition<AnnotationConditionFixtures>.() -> Unit) =
        resolver.optional(silent = true).method(condition).map { it.self.name }.sorted()

    private fun annotatedNames(condition: MethodCondition<AnnotationConditionFixtures>.() -> Unit) =
        names(condition).filter { it in listOf("both", "firstOnly", "none") }

    @Test
    fun annotationsMatchExactAnnotations() {
        assertEquals(listOf("firstOnly"), annotatedNames { annotations(AnnotationConditionFixtures.First::class) })
        assertEquals(listOf("both"), annotatedNames { annotations(AnnotationConditionFixtures.Second::class, AnnotationConditionFixtures.First::class) })
    }

    @Test
    fun annotationsNotExcludeExactAnnotations() {
        assertEquals(listOf("both", "none"), annotatedNames { annotationsNot(AnnotationConditionFixtures.First::class) })
        assertEquals(listOf("firstOnly", "none"), annotatedNames { annotationsNot(AnnotationConditionFixtures.Second::class, AnnotationConditionFixtures.First::class) })
    }

    @Test
    fun parameterAnnotationsMatchExactAnnotationsByPosition() {
        fun find(vararg annotations: Set<Any>) = names { name = "annotatedParameters"; parameterAnnotations(*annotations) }.size
        assertEquals(1, find(setOf(AnnotationConditionFixtures.Second::class, AnnotationConditionFixtures.First::class), setOf(AnnotationConditionFixtures.Second::class)))
        assertEquals(0, find(setOf(AnnotationConditionFixtures.First::class), setOf(AnnotationConditionFixtures.Second::class)))
        assertEquals(0, find(setOf(AnnotationConditionFixtures.Second::class), setOf(AnnotationConditionFixtures.First::class, AnnotationConditionFixtures.Second::class)))
    }

    @Test
    fun parameterAnnotationsNotExcludeExactAnnotationsByPosition() {
        fun find(vararg annotations: Set<Any>) = names { name = "annotatedParameters"; parameterAnnotationsNot(*annotations) }.size
        assertEquals(1, find(setOf(AnnotationConditionFixtures.First::class), setOf(AnnotationConditionFixtures.Second::class)))
        assertEquals(0, find(setOf(AnnotationConditionFixtures.First::class, AnnotationConditionFixtures.Second::class), setOf(AnnotationConditionFixtures.Second::class)))
    }

    @Test
    fun defaultValueMatchesArrayContent() {
        val defaults = AnnotationConditionFixtures.Defaults::class.resolve()
        assertEquals(listOf("names"), defaults.method { defaultValue(arrayOf("a", "b")) }.map { it.self.name })
        assertEquals(listOf("numbers"), defaults.method { defaultValue(intArrayOf(1)) }.map { it.self.name })
        assertEquals(listOf("count"), defaults.method { defaultValue(1) }.map { it.self.name })
    }
}