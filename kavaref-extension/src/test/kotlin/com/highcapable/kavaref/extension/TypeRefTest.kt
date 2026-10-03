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
 * This file is created by fankes on 2026/10/3.
 */

package com.highcapable.kavaref.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

class TypeRefTest {

    private open class Base<T> : TypeRef<T>()

    private class Indirect : Base<String>()

    private fun <T> unresolvedType() = object : TypeRef<T>() {}

    private fun <T> unresolvedArrayType() = object : TypeRef<Array<T>>() {}

    private fun <T> unresolvedListType() = typeRef<List<T>>()

    @Test
    fun covariantListArgumentIsWildcard() {
        val type = typeRef<List<String>>().type as ParameterizedType
        val argument = type.actualTypeArguments.single() as WildcardType
        assertSame(List::class.java, type.rawType)
        assertSame(String::class.java, argument.upperBounds.single())
        assertEquals("java.util.List<? extends java.lang.String>", type.typeName)
    }

    @Test
    fun mutableListOfOpenTypeHasNoWildcard() {
        val type = typeRef<MutableList<CharSequence>>().type as ParameterizedType
        assertSame(CharSequence::class.java, type.actualTypeArguments.single())
    }

    @Test
    fun rawTypeErasesTypeArguments() {
        assertSame(List::class.java, typeRef<List<String>>().rawType)
        assertSame(String::class.java, typeRef<String>().rawType)
        assertSame(IntArray::class.java, typeRef<IntArray>().rawType)
    }

    @Test
    fun rawTypePreservesGenericArrayDimensions() {
        val arrayRef = typeRef<Array<List<String>>>()
        assertTrue(arrayRef.type is GenericArrayType)
        assertSame(ArrayClass(List::class), arrayRef.rawType)
        assertSame(ArrayClass(ArrayClass(List::class)), typeRef<Array<Array<List<String>>>>().rawType)
    }

    @Test
    fun nullabilityIsNotRetained() {
        assertEquals(typeRef<String>(), typeRef<String?>())
        assertEquals(typeRef<String>().hashCode(), typeRef<String?>().hashCode())
        assertEquals(typeRef<List<String>>(), typeRef<List<String?>?>())
    }

    @Test
    fun differentTypesAreNotEqual() {
        val stringList: TypeRef<*> = typeRef<List<String>>()
        val intList: TypeRef<*> = typeRef<List<Int>>()
        assertNotEquals(stringList, intList)
    }

    @Test
    fun toStringUsesType() {
        val ref = typeRef<List<String>>()
        assertEquals(ref.type.toString(), ref.toString())
    }

    @Test
    fun indirectSubclassThrows() {
        assertThrows(IllegalStateException::class.java) { Indirect().type }
        assertThrows(IllegalStateException::class.java) { Indirect().rawType }
    }

    @Test
    fun invalidToStringDoesNotThrow() {
        assertEquals("${Indirect::class.java.name} (invalid)", Indirect().toString())
    }

    @Test
    fun unresolvedTypeVariableIsRetained() {
        val reference = unresolvedType<String>()
        assertTrue(reference.type is TypeVariable<*>)
        assertThrows(TypeCastException::class.java) { reference.rawType }
    }

    @Test
    fun unresolvedArrayComponentThrowsOnRawType() {
        assertThrows(TypeCastException::class.java) { unresolvedArrayType<String>().rawType }
    }

    @Test
    fun unresolvedListArgumentKeepsRawClass() {
        val reference = unresolvedListType<String>()
        val argument = (reference.type as ParameterizedType).actualTypeArguments.single()
        assertTrue(argument is TypeVariable<*> || (argument is WildcardType && argument.upperBounds.single() is TypeVariable<*>))
        assertSame(List::class.java, reference.rawType)
    }
}