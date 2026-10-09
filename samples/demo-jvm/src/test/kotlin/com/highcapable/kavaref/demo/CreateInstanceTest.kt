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

import com.highcapable.kavaref.extension.createInstance
import com.highcapable.kavaref.extension.createInstanceAsType
import com.highcapable.kavaref.extension.createInstanceAsTypeOrNull
import com.highcapable.kavaref.extension.createInstanceOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.ref.WeakReference
import java.net.URLClassLoader

class CreateInstanceTest {

    @Test
    fun createInstancePicksMostSpecificConstructor() {
        assertEquals("String", CreateInstanceFixtures.Overloads::class.createInstance("x").picked)
        assertEquals("Object", CreateInstanceFixtures.Overloads::class.createInstance(1).picked)
        assertEquals("Object", CreateInstanceFixtures.Overloads::class.createInstance(1L).picked)
    }

    @Test
    fun createInstanceAllowsWidening() {
        assertEquals("long", CreateInstanceFixtures.Widening::class.createInstance(1).picked)
        assertEquals("long", CreateInstanceFixtures.Widening::class.createInstance(1L).picked)
        assertEquals("String", CreateInstanceFixtures.Widening::class.createInstance("x").picked)
    }

    @Test
    fun createInstanceThrowsOnAmbiguity() {
        assertThrows(IllegalArgumentException::class.java) { CreateInstanceFixtures.Ambiguous::class.createInstance("a", "b") }
        assertNull(CreateInstanceFixtures.Ambiguous::class.createInstanceOrNull("a", "b"))
    }

    @Test
    fun createInstanceAsTypeWorksWithClassTypeParameters() {
        val creator = TypedCreator<Any>()
        assertEquals("String", (creator.createInstanceAsType(CreateInstanceFixtures.Overloads::class.java, "x") as CreateInstanceFixtures.Overloads).picked)
        assertEquals("String", (creator.createInstanceAsTypeOrNull(CreateInstanceFixtures.Overloads::class.java, "x") as CreateInstanceFixtures.Overloads).picked)
        assertNull(creator.createInstanceAsTypeOrNull(CreateInstanceFixtures.Ambiguous::class.java, "a", "b"))
    }

    @Test
    fun createInstanceCacheSurvivesGc() {
        assertEquals("String", CreateInstanceFixtures.Overloads::class.createInstance("x").picked)
        repeat(3) { Runtime.getRuntime().gc() }
        assertEquals("String", CreateInstanceFixtures.Overloads::class.createInstance("x").picked)
        assertEquals("Object", CreateInstanceFixtures.Overloads::class.createInstance(1).picked)
    }

    @Test
    fun createInstanceReleasesClassLoader() {
        fun createInIsolatedLoader(): WeakReference<ClassLoader> {
            val loader = isolatedLoader()
            loader.loadClass(CreateInstanceFixtures.LeakTarget::class.java.name).createInstance()
            return WeakReference(loader)
        }
        assertTrue(createInIsolatedLoader().isCollected())
    }

    @Test
    fun createInstanceReleasesClassLoaderOfArguments() {
        fun createInIsolatedLoader(): WeakReference<ClassLoader> {
            val loader = isolatedLoader()
            val argument = loader.loadClass(CreateInstanceFixtures.LeakArgument::class.java.name).createInstance()
            loader.loadClass(CreateInstanceFixtures.LeakTargetWithArgument::class.java.name).createInstance(argument)
            return WeakReference(loader)
        }
        assertTrue(createInIsolatedLoader().isCollected())
    }

    private fun isolatedLoader(): ClassLoader {
        val classesPath = CreateInstanceFixtures::class.java.protectionDomain.codeSource.location
        return URLClassLoader(arrayOf(classesPath), null)
    }

    private fun WeakReference<ClassLoader>.isCollected(): Boolean {
        repeat(10) {
            if (isCleared()) return true
            Runtime.getRuntime().gc()
            @Suppress("DEPRECATION")
            System.runFinalization()
            Thread.sleep(100)
        }
        return isCleared()
    }

    /** Read the referent in its own frame, otherwise ART may keep it alive in a stale register during GC. */
    private fun WeakReference<*>.isCleared() = get() == null

    /** Calls the typed functions with a type parameter of the class, which cannot be reified. */
    private class TypedCreator<T> {

        fun createInstanceAsType(clazz: Class<*>, vararg args: Any?) = clazz.createInstanceAsType<T>(*args)

        fun createInstanceAsTypeOrNull(clazz: Class<*>, vararg args: Any?) = clazz.createInstanceAsTypeOrNull<T>(*args)
    }
}