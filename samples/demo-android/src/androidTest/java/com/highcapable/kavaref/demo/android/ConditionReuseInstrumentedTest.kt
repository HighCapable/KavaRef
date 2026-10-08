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
import com.highcapable.kavaref.condition.extension.mergeWith
import com.highcapable.kavaref.condition.type.Modifiers
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConditionReuseInstrumentedTest {

    private fun names(condition: MethodCondition<ConditionReuseFixtures>) =
        ConditionReuseFixtures::class.resolve().method(condition).map { it.self.name }

    private fun staticOnly() = MethodCondition<ConditionReuseFixtures>().modifiers { Modifiers.STATIC in it }

    private fun throwingOnly() = MethodCondition<ConditionReuseFixtures>().emptyExceptionTypesNot()

    @Test
    fun copyKeepsConditions() {
        assertEquals(listOf("staticValue"), names(staticOnly().copy()))
        assertEquals(listOf("throwing"), names(throwingOnly().copy()))
    }

    @Test
    fun mergeWithKeepsConditions() {
        assertEquals(listOf("staticValue"), names(MethodCondition<ConditionReuseFixtures>().also { it mergeWith staticOnly() }))
        assertEquals(listOf("throwing"), names(MethodCondition<ConditionReuseFixtures>().also { it mergeWith throwingOnly() }))
    }
}