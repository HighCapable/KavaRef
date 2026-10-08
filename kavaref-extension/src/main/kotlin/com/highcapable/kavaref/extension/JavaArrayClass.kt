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
 * This file is created by fankes on 2025/6/5.
 */
@file:JvmName("ArrayClassUtils")
@file:Suppress("unused", "FunctionName", "UNCHECKED_CAST")

package com.highcapable.kavaref.extension

import java.lang.reflect.Array
import kotlin.reflect.KClass

/**
 * Creates a [Class] for an array of the specified [type].
 *
 * For example:
 * `ArrayClass(String::class.java)` will
 * return `java.lang.String[]`.
 * @param type the type of the array elements.
 * @return [Class]<[Array]>
 */
@JvmName("createArrayClass")
fun ArrayClass(type: Class<*>) = Array.newInstance(type, 0).javaClass as Class<Array>

/**
 * Creates a [KClass.java] for an array of the specified [type].
 *
 * For example:
 * `ArrayClass(String::class)` will
 * return `java.lang.String[]`.
 * @param type the type of the array elements.
 * @return [Class]<[Array]>
 */
@JvmSynthetic
fun ArrayClass(type: KClass<*>) = ArrayClass(type.java)

/**
 * Creates a [Class] for an array of the specified [type].
 *
 * For example:
 * `ArrayClass("java.lang.String")` will
 * return `java.lang.String[]`.
 * @param type the class name of the array elements.
 * @param loader the [ClassLoader] to load the class.
 * @return [Class]<[Array]>
 */
@JvmOverloads
@JvmName("createArrayClass")
fun ArrayClass(type: String, loader: ClassLoader? = null) = ArrayClass(type.toClass(loader))