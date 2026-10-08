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
 * This file is created by fankes on 2026/10/6.
 */

package com.highcapable.kavaref.demo.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.MethodCondition
import com.highcapable.kavaref.condition.base.MemberCondition
import com.highcapable.kavaref.condition.base.MemberCondition.Configuration.Companion.createConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuperclassInstrumentedTest {

    private val child = SuperclassFixtures.Child()

    private fun findOnChild(name: String) =
        SuperclassFixtures.Child::class.resolve().firstMethod { this.name = name; superclass(interfaces = true) }.of(child)

    @Test
    fun superclassDoesNotAffectOtherFiltersInSameScope() {
        val scope = SuperclassFixtures.Child::class.resolve().optional(silent = true)
        assertEquals(1, scope.method { name = "onlyInParent"; superclass() }.size)
        assertEquals(0, scope.method { name = "onlyInParent" }.size)
        assertEquals(0, scope.field { name = "parentField" }.size)
    }

    @Test
    fun superclassWorksOnStandaloneConditionAndItsCopy() {
        val condition = MethodCondition<SuperclassFixtures.Child>().name("onlyInParent").superclass()
        assertEquals(1, SuperclassFixtures.Child::class.resolve().method(condition).size)
        assertEquals(1, SuperclassFixtures.Child::class.resolve().method(condition.copy()).size)
    }

    @Test
    fun superclassReachesObject() {
        assertEquals(Any::class.java, SuperclassFixtures.Child::class.resolve().firstMethod { name = "hashCode"; superclass() }.self.declaringClass)
    }

    @Test
    fun objectMembersAreFoundFromObject() {
        assertEquals(Any::class.java, Any::class.resolve().firstMethod { name = "toString" }.self.declaringClass)
    }

    @Test
    fun interfaceDefaultMethodIsFound() {
        assertEquals("apiDefault", findOnChild("apiDefault").invoke())
        assertEquals("baseDefault", findOnChild("baseDefault").invoke())
    }

    @Test
    fun subInterfaceIsSearchedBeforeSuperInterface() {
        assertEquals("api", findOnChild("overridden").invoke())
    }

    @Test
    fun superclassIsSearchedBeforeInterfaces() {
        val resolver = findOnChild("parentValue")
        assertEquals(SuperclassFixtures.Parent::class.java, resolver.self.declaringClass)
        assertEquals("parent", resolver.invoke())
    }

    @Test
    fun interfaceConstantIsFound() {
        val field = SuperclassFixtures.Child::class.resolve().firstField { name = "BASE_CONSTANT"; superclass(interfaces = true) }
        assertEquals("base", field.get())
    }

    @Test
    fun superInterfaceIsSearchedFromInterface() {
        val resolver = SuperclassFixtures.Api::class.resolve().firstMethod { name = "baseDefault"; superclass(interfaces = true) }
        assertEquals("baseDefault", resolver.of(child).invoke())
    }

    @Test
    fun configurationIncludesInterfaces() {
        val configuration = SuperclassFixtures.Child::class.java.createConfiguration(
            memberInstance = child,
            superclass = MemberCondition.Configuration.Superclass.INCLUDE_INTERFACES
        )
        val resolvers = MethodCondition<SuperclassFixtures.Child>().name("apiDefault").build(configuration)
        assertEquals("apiDefault", resolvers.single().invoke())
    }

    @Test
    fun superclassModeKeepsTheWidest() {
        val condition = MethodCondition<SuperclassFixtures.Child>().name("apiDefault").superclass(interfaces = true).superclass()
        assertEquals(1, SuperclassFixtures.Child::class.resolve().method(condition).size)
    }

    @Test
    fun notFoundMessageMentionsInterfaces() {
        val exception = assertThrows(NoSuchMethodException::class.java) {
            SuperclassFixtures.Child::class.resolve().method { name = "missing"; superclass(interfaces = true) }
        }
        val message = exception.message.orEmpty()
        assertTrue(message, message.contains("(Also tried in superclasses and interfaces)"))
    }
}