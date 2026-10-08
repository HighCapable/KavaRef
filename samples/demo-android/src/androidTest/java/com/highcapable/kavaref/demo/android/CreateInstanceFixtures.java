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

package com.highcapable.kavaref.demo.android;

@SuppressWarnings("unused")
public class CreateInstanceFixtures {

    public static class Overloads {
        public final String picked;

        public Overloads(Object value) { picked = "Object"; }

        public Overloads(String value) { picked = "String"; }

        public Overloads(long value) { picked = "long"; }
    }

    public static class Widening {
        public final String picked;

        public Widening(long value) { picked = "long"; }

        public Widening(String value) { picked = "String"; }
    }

    public static class Ambiguous {
        public Ambiguous(String first, Object second) {}

        public Ambiguous(Object first, String second) {}
    }

    public static class LeakTarget {
        public LeakTarget() {}
    }

    public static class LeakArgument {
        public LeakArgument() {}
    }

    public static class LeakTargetWithArgument {
        public LeakTargetWithArgument(LeakArgument argument) {}
    }
}
