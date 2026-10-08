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
 * This file is created by fankes on 2026/10/8.
 */

package com.highcapable.kavaref.demo

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.MethodResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Method

class HandlerTest {

    private val target = HandlerFixtures.Target("target")
    private val other = HandlerFixtures.Target("other")

    private fun greet() = HandlerFixtures.Target::class.resolve().firstMethod { name = "greet" }
    private fun value() = HandlerFixtures.Target::class.resolve().firstField { name = "value" }
    private fun staticValue() = HandlerFixtures.Target::class.resolve().firstField { name = "staticValue" }

    private class RecordingMethodHandler(private val result: Any? = null) : MethodResolver.Handler() {

        var method: Method? = null
        var instance: Any? = null
        var args: List<Any?>? = null
        var accessible = false

        override fun invoke(method: Method, instance: Any?, args: Array<out Any?>): Any? {
            this.method = method
            this.instance = instance
            this.args = args.toList()
            @Suppress("DEPRECATION")
            accessible = method.isAccessible
            return result ?: super.invoke(method, instance, args)
        }
    }

    private class RecordingFieldHandler : FieldResolver.Handler() {

        val operations = mutableListOf<String>()
        var instance: Any? = null

        override fun get(field: Field, instance: Any?): Any? {
            operations += "get"
            this.instance = instance
            return super.get(field, instance)
        }

        override fun set(field: Field, instance: Any?, value: Any?) {
            operations += "set"
            this.instance = instance
            super.set(field, instance, value)
        }
    }

    private object FailingMethodHandler : MethodResolver.Handler() {
        override fun invoke(method: Method, instance: Any?, args: Array<out Any?>) = error("failed")
    }

    @Test
    fun methodHandlerReceivesMemberInstanceAndArguments() {
        val handler = RecordingMethodHandler()
        val resolver = greet().of(target).withHandler(handler)
        assertEquals("hi target", resolver.invoke("hi "))
        assertSame(resolver.self, handler.method)
        assertSame(target, handler.instance)
        assertEquals(listOf("hi "), handler.args)
        assertTrue(handler.accessible)
    }

    @Test
    fun methodHandlerResultIsReturned() {
        val resolver = greet().of(target).withHandler(RecordingMethodHandler(result = "handled"))
        assertEquals("handled", resolver.invoke("hi "))
        assertEquals("handled", resolver.invoke<String>("hi "))
        assertEquals("handled", resolver.invokeQuietly<String>("hi "))
    }

    @Test
    fun methodHandlerFailureIsIgnoredQuietly() {
        val resolver = greet().of(target).withHandler(FailingMethodHandler)
        assertThrows(IllegalStateException::class.java) { resolver.invoke("hi ") }
        assertNull(resolver.invokeQuietly("hi "))
    }

    @Test
    fun withHandlerReturnsNewResolverAndKeepsOriginal() {
        val original = greet().of(target)
        val derived = original.withHandler(RecordingMethodHandler(result = "handled"))
        assertTrue(original !== derived)
        assertEquals("hi target", original.invoke("hi "))
        assertEquals("handled", derived.invoke("hi "))
    }

    @Test
    fun withHandlerReplacesPreviousHandler() {
        val first = RecordingMethodHandler(result = "first")
        val second = RecordingMethodHandler(result = "second")
        val resolver = greet().of(target).withHandler(first).withHandler(second)
        assertEquals("second", resolver.invoke("hi "))
        assertNull(first.method)
    }

    @Test
    fun withHandlerKeepsInstanceThatCannotBeSetAgain() {
        val resolver = greet().of(target).withHandler(RecordingMethodHandler())
        assertEquals("hi target", resolver.invoke("hi "))
        assertThrows(IllegalStateException::class.java) { resolver.of(other) }
    }

    @Test
    fun copyKeepsHandlerWithoutInstance() {
        val handler = RecordingMethodHandler()
        val copied = greet().of(target).withHandler(handler).copy()
        assertEquals("hi other", copied.of(other).invoke("hi "))
        assertSame(other, handler.instance)
    }

    @Test
    fun fieldHandlerReceivesGetAndSet() {
        val handler = RecordingFieldHandler()
        val resolver = value().of(target).withHandler(handler)
        resolver.set("changed")
        assertEquals("changed", resolver.get())
        assertEquals("changed", resolver.get<String>())
        assertEquals(listOf("set", "get", "get"), handler.operations)
        assertSame(target, handler.instance)
    }

    @Test
    fun fieldHandlerReceivesNullInstanceForStaticField() {
        val handler = RecordingFieldHandler()
        assertEquals("static", staticValue().withHandler(handler).get())
        assertNull(handler.instance)
    }

    @Test
    fun fieldCopyKeepsHandlerWithoutInstance() {
        val handler = RecordingFieldHandler()
        val copied = value().of(target).withHandler(handler).copy()
        assertEquals("value", copied.of(other).get())
        assertSame(other, handler.instance)
    }
}