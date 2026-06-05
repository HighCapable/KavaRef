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
 * This file is created by fankes on 2026/6/4.
 */
package com.highcapable.kavaref.demo.runner

import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.demo.test.Test
import com.highcapable.kavaref.runtime.KavaRefRuntime

object Runner {

    @JvmStatic
    fun run(): Pair<String?, String?> {
        KavaRef.logLevel = KavaRefRuntime.LogLevel.DEBUG

        val test = Test::class.resolve()
            .firstConstructor {
                emptyParameters()
            }.create()

        Test::class.resolve()
            .firstMethod {
                name = "test"
                parameters(String::class)
            }.of(test).invoke("reflection test")

        test.asResolver()
            .firstMethod {
                name = "test"
                parameters(String::class)
            }.invoke("reflection test")

        Test::class.resolve()
            .firstField {
                name = "myTest"
                type = String::class
            }.of(test).set("Hello modified reflection test")

        test.asResolver()
            .firstField {
                name = "myTest"
                type = String::class
            }.set("Hello modified reflection test")

        val testString1 = Test::class.resolve()
            .firstMethod {
                name = "getTest"
                emptyParameters()
            }.of(test).invoke<String>()

        val testString2 = test.asResolver()
            .firstMethod {
                name = "getTest"
                emptyParameters()
            }.invoke<String>()

        return testString1 to testString2
    }
}