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
 * This file is created by fankes on 2025/5/10.
 */
package com.highcapable.kavaref.demo

import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.demo.test.Test
import com.highcapable.kavaref.runtime.KavaRefRuntime

fun main() {
    // Enable KavaRef debug level logging.
    KavaRef.logLevel = KavaRefRuntime.LogLevel.DEBUG

    // Resolve the Test class using KavaRef.
    // Create an instance of the Test class using the resolved class.
    val test = Test::class.resolve()
        .firstConstructor {
            emptyParameters()
        }.create()

    // Invoke a method, modify a field, and retrieve a value using the resolved class.
    // (1) Call from Class.
    Test::class.resolve()
        .firstMethod {
            name = "test"
            parameters(String::class)
        }.of(test).invoke("reflection test")

    // (2) Call from Object.
    test.asResolver()
        .firstMethod {
            name = "test"
            parameters(String::class)
        }.invoke("reflection test")

    // Modify the field 'myTest' and retrieve its value using the resolved class.
    // (1) Call from Class.
    Test::class.resolve()
        .firstField {
            name = "myTest"
            type = String::class
        }.of(test).set("Hello modified reflection test")

    // (2) Call from Object.
    test.asResolver()
        .firstField {
            name = "myTest"
            type = String::class
        }.set("Hello modified reflection test")

    // Retrieve the value of the field 'myTest' using the resolved class.
    // (1) Call from Class.
    val testString1 = Test::class.resolve()
        .firstMethod {
            name = "getTest"
            emptyParameters()
        }.of(test).invoke<String>()

    // (2) Call from Object.
    val testString2 = test.asResolver()
        .firstMethod {
            name = "getTest"
            emptyParameters()
        }.invoke<String>()

    // Print the value of the field 'myTest'.
    println(testString1)
    println(testString2)
}