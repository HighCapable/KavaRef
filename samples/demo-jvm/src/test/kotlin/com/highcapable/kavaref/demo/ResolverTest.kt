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

import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.MethodResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ResolverTest {

    private val resolver = ResolverFixtures::class.resolve()

    @Test
    fun asResolverUsesReceiverAsInstance() {
        val clazz: Any = ResolverFixtures::class.java
        assertEquals("ResolverFixtures", clazz.asResolver().firstMethod { name = "getSimpleName"; emptyParameters() }.invoke())
        assertEquals("KavaRef", File("KavaRef").asResolver().firstMethod { name = "getName"; emptyParameters() }.invoke())
    }

    @Test
    fun makeAccessibleSucceedsRepeatedly() {
        val method = resolver.firstMethod { name = "privateValue" }.of(ResolverFixtures())
        repeat(3) { assertEquals("private", method.invoke()) }
        assertTrue(method.self.makeAccessible())
        assertTrue(method.self.makeAccessible())
    }

    @Test
    fun typedInvokeAndGetWorkWithClassTypeParameters() {
        val method = resolver.firstMethod { name = "staticValue" }
        val field = resolver.firstField { name = "number" }.of(ResolverFixtures())
        assertEquals("static", TypedAccessor<String>().invoke(method))
        assertEquals("static", TypedAccessor<String>().invokeQuietly(method))
        assertEquals(1, TypedAccessor<Int>().get(field))
        assertEquals(1, TypedAccessor<Int>().getQuietly(field))
        val constructor = resolver.firstConstructor { emptyParameters() }
        assertEquals(ResolverFixtures::class.java, TypedCreator<Any>().createAsType(constructor).javaClass)
        assertEquals(ResolverFixtures::class.java, TypedCreator<Any>().createAsTypeQuietly(constructor)?.javaClass)
    }

    /** Calls the typed functions with a type parameter of the class, which cannot be reified. */
    private class TypedAccessor<T> {

        fun invoke(method: MethodResolver<*>) = method.invoke<T>()

        fun invokeQuietly(method: MethodResolver<*>) = method.invokeQuietly<T>()

        fun get(field: FieldResolver<*>) = field.get<T>()

        fun getQuietly(field: FieldResolver<*>) = field.getQuietly<T>()
    }

    /** Calls the typed functions of [ConstructorResolver] with a type parameter of the class, which cannot be reified. */
    private class TypedCreator<T> {

        fun createAsType(constructor: ConstructorResolver<*>) = constructor.createAsType<T>()

        fun createAsTypeQuietly(constructor: ConstructorResolver<*>) = constructor.createAsTypeQuietly<T>()
    }
}