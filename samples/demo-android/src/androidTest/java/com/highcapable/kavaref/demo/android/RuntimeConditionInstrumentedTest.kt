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

@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.highcapable.kavaref.demo.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.runtime.KavaRefRuntime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.ParameterizedType

@RunWith(AndroidJUnit4::class)
class RuntimeConditionInstrumentedTest {

    private val resolver = RuntimeConditionFixtures::class.resolve()

    @Test
    fun conditionExceptionIsNotMatched() {
        val typeMatched = resolver.field { type { it.componentType.name == "java.lang.String" } }.map { it.self.name }
        val genericMatched = resolver.field { genericType { (it as ParameterizedType).rawType == List::class.java } }.map { it.self.name }
        assertEquals(listOf("names"), typeMatched)
        assertEquals(listOf("items"), genericMatched)
    }

    @Test
    fun conditionExceptionsAreReportedWhenNotOptional() {
        val exception = assertThrows(NoSuchMethodException::class.java) {
            resolver.method {
                modifiers { if (Modifiers.STATIC in it) error("static") else true }
                returnType { it.componentType.name == "x" }
            }
        }
        val message = exception.message.orEmpty()
        assertTrue(message, message.contains("| [!] (Runtime Condition)"))
        assertTrue(message, message.contains("[!] Exceptions thrown by runtime conditions:"))
        // Count the reported members under each "[conditionName]" header, the order of members is not guaranteed.
        val sections = message.substringAfter("Exceptions thrown by runtime conditions:").substringBefore("If you want to ignore")
            .lines().fold(mutableListOf<Pair<String, Int>>()) { sections, line ->
                when {
                    line.startsWith("[") -> sections += line to 0
                    line.startsWith("- ") -> sections[sections.lastIndex] = sections.last().let { it.first to it.second + 1 }
                }
                sections
            }
        assertEquals(message, listOf("[modifiersCondition]" to 1, "[returnTypeCondition]" to 2), sections)
        assertTrue(message, message.contains("java.lang.NullPointerException") && message.contains("\tat "))
    }

    @Test
    fun conditionExceptionsAreLoggedWhenOptional() {
        val logs = captureLogs { resolver.optional().method { returnType { it.componentType.name == "x" } } }
        val warn = logs.single()
        assertTrue(warn.first, warn.first.contains("| [!] (Runtime Condition)"))
        assertTrue(warn.first, warn.first.contains("[!] Exceptions thrown by runtime conditions:\n[returnTypeCondition]"))
        assertTrue(warn.first, warn.first.contains("java.lang.NullPointerException") && warn.first.contains("\tat "))
        assertNull(warn.second)
        assertTrue(captureLogs { resolver.optional(silent = true).method { returnType { it.componentType.name == "x" } } }.isEmpty())
    }

    @Test
    fun runtimeConditionStatesAreMarked() {
        val exception = assertThrows(NoSuchMethodException::class.java) {
            resolver.method {
                name = "instanceValue"
                modifiers { true }
                returnType { it.componentType.name == "x" }
                genericReturnType { it is ParameterizedType }
            }
        }
        val message = exception.message.orEmpty()
        val rows = message.lines().filter { it.startsWith("| ") }.associate { line -> line.split("|").let { it[1].trim() to it[2].trim() } }
        assertEquals(message, "(Runtime Condition)", rows["modifiersCondition"])
        assertEquals(message, "[!] (Runtime Condition)", rows["returnTypeCondition"])
        assertEquals(message, "[-] (Runtime Condition)", rows["genericReturnTypeCondition"])
    }

    /** Capture the warning logs of KavaRef during [block], then restore the previous logger. */
    private fun captureLogs(block: () -> Unit): List<Pair<String, Throwable?>> {
        val logger = KavaRefRuntime::class.resolve().firstField { name = "logger" }
        val previous = logger.get()
        val logs = mutableListOf<Pair<String, Throwable?>>()
        KavaRef.setLogger(object : KavaRefRuntime.Logger {
            override val tag = "Capture"
            override fun debug(msg: Any?, throwable: Throwable?) = Unit
            override fun info(msg: Any?, throwable: Throwable?) = Unit
            override fun warn(msg: Any?, throwable: Throwable?) { logs += msg.toString() to throwable }
            override fun error(msg: Any?, throwable: Throwable?) = Unit
        })
        try {
            block()
        } finally {
            logger.set(previous)
        }
        return logs
    }
}