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
 * This file is created by fankes on 2026/10/6.
 */

package com.highcapable.kavaref.demo;

@SuppressWarnings("unused")
public class SuperclassFixtures {

    public interface BaseApi {

        String BASE_CONSTANT = "base";

        default String baseDefault() {
            return "baseDefault";
        }

        default String overridden() {
            return "base";
        }
    }

    public interface Api extends BaseApi {

        static String apiStatic() {
            return "apiStatic";
        }

        default String apiDefault() {
            return "apiDefault";
        }

        default String parentValue() {
            return "api";
        }

        @Override
        default String overridden() {
            return "api";
        }
    }

    public static class Parent {

        public int parentField = 1;

        public String parentValue() {
            return "parent";
        }

        public void onlyInParent() {
        }
    }

    public static class Child extends Parent implements BaseApi, Api {
    }
}
