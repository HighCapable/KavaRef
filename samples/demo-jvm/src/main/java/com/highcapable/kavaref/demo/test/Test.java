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
 * This file is created by fankes on 2025/5/24.
 */
package com.highcapable.kavaref.demo.test;

@SuppressWarnings("ALL")
public class Test {

    private String myTest = "Hello private Test";

    private Test() {
        System.out.println("Create private Test");
    }

    private void test(String myTest) {
        System.out.println("Hello private " + myTest);
    }

    public Test(String aa, Boolean bb, boolean cc) {
        System.out.println("Create Test with parameters: " + aa + ", " + bb + ", " + cc);
    }

    private String getTest() {
        return this.myTest;
    }
}