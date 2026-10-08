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

import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.kavaref.extension.lazyClass
import com.highcapable.kavaref.extension.lazyClassOrNull
import com.highcapable.kavaref.extension.toClass
import com.highcapable.kavaref.extension.toClassOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class ClassLoadingTest {

    @Test
    fun toClassDoesNotInitializeByDefault() {
        ClassLoadingFixtures.ToClassDefaultTarget::class.java.name.toClass()
        assertFalse(ClassLoadingFixtures.InitFlags.toClassDefaultInitialized)
    }

    @Test
    fun toClassInitializesWhenRequested() {
        ClassLoadingFixtures.ToClassExplicitTarget::class.java.name.toClass(initialize = true)
        assertTrue(ClassLoadingFixtures.InitFlags.toClassExplicitInitialized)
    }

    @Test
    fun variousClassLoadsWithoutLoader() {
        val name = ClassLoadingFixtures::class.java.name
        assertEquals(ClassLoadingFixtures::class.java, VariousClass("com.example.Missing", name).loadOrNull())
    }

    @Test
    fun variousClassDoesNotInitializeByDefault() {
        VariousClass(ClassLoadingFixtures.VariousClassTarget::class.java.name).load()
        assertFalse(ClassLoadingFixtures.InitFlags.variousClassInitialized)
    }

    @Test
    fun typedClassLoadingChecksTheAssignableType() {
        val name = String::class.java.name
        assertEquals(String::class.java, name.toClass<CharSequence>())
        assertThrows(IllegalStateException::class.java) { name.toClass<Number>() }
        assertNull(name.toClassOrNull<Number>())
        assertEquals(String::class.java, VariousClass(name).load<CharSequence>())
        assertThrows(IllegalStateException::class.java) { VariousClass(name).load<Number>() }
        assertNull(VariousClass(name).loadOrNull<Number>())
        assertEquals(String::class.java, (String::class.java as Type).toClass<CharSequence>())
        assertThrows(IllegalStateException::class.java) { (String::class.java as Type).toClass<Number>() }
        assertNull((String::class.java as Type).toClassOrNull<Number>())
    }

    @Test
    fun typedLazyClassChecksTheAssignableType() {
        val name = String::class.java.name
        val matched by lazyClass<CharSequence>(name)
        val mismatched by lazyClass<Number>(name)
        val mismatchedOrNull by lazyClassOrNull<Number>(name)
        assertEquals(String::class.java, matched)
        assertThrows(IllegalStateException::class.java) { mismatched }
        assertNull(mismatchedOrNull)
    }

    @Test
    fun nullableLazyClassLoadsMissingClassOnce() {
        val loader = CountingClassLoader()
        val missing by lazyClassOrNull("com.example.Missing", loader = { loader })
        repeat(5) { assertNull(missing) }
        assertEquals(1, loader.count.get())
    }

    @Test
    fun lazyClassLoadsOnceOnConcurrentAccess() {
        val loader = CountingClassLoader()
        val holder = object {
            val clazz by lazyClass(ClassLoadingFixtures::class.java.name, loader = { loader })
        }
        val start = CountDownLatch(1)
        val results = arrayOfNulls<Class<*>>(8)
        val threads = results.indices.map { index -> thread { start.await(); results[index] = holder.clazz } }
        start.countDown()
        threads.forEach { it.join() }
        assertEquals(1, loader.count.get())
        assertEquals(listOf(ClassLoadingFixtures::class.java), results.distinct())
    }

    /** A [ClassLoader] that counts the loading attempts, each attempt takes a while to expose concurrent loading. */
    private class CountingClassLoader : ClassLoader(ClassLoadingTest::class.java.classLoader) {

        val count = AtomicInteger()

        override fun loadClass(name: String, resolve: Boolean): Class<*> {
            count.incrementAndGet()
            Thread.sleep(10)
            return super.loadClass(name, resolve)
        }
    }
}