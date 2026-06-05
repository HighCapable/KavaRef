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
 * This file is created by fankes on 2025/5/16.
 */
package com.highcapable.kavaref.demo;

import com.highcapable.kavaref.KavaRef;
import com.highcapable.kavaref.demo.test.Test;
import com.highcapable.kavaref.runtime.KavaRefRuntime;

public class Main {

    public static void main(String[] args) {
        // Enable KavaRef debug level logging.
        KavaRef.setLogLevel(KavaRefRuntime.LogLevel.DEBUG);
        // Resolve the Test class using KavaRef.
        var memberScope = KavaRef.resolveClass(Test.class);
        // Create an instance of the Test class using the resolved class.
        var test = memberScope.constructor()
                .emptyParameters()
                .build()
                .get(0)
                .create();
        // Invoke a method, modify a field, and retrieve a value using the resolved class.
        memberScope.method()
                .name("test")
                .parameters(String.class)
                .build()
                .get(0)
                .of(test)
                .invoke("reflection test");
        // Modify the field 'myTest' and retrieve its value using the resolved class.
        memberScope.field()
                .name("myTest")
                .type(String.class)
                .build()
                .get(0)
                .of(test)
                .set("Hello modified reflection test");
        // Retrieve the value of the field 'myTest' using the resolved class.
        var testString = (String) memberScope.method()
                .name("getTest")
                .emptyParameters()
                .build()
                .get(0)
                .of(test)
                .invoke();
        // Print the value of the field 'myTest'.
        System.out.println(testString);
    }
}