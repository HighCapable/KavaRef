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
package com.highcapable.kavaref.resolver.base

import com.highcapable.kavaref.extension.makeAccessible
import java.lang.reflect.Member

/**
 * Base class for resolving member [M].
 * 
 * [T] to specify the declaring class type of the member.
 * @param self the member to be resolved.
 */
abstract class MemberResolver<M : Member, T : Any>(open val self: M) {

    /** Make the member accessible and check if it is successful. */
    @PublishedApi
    @JvmSynthetic
    internal fun requireAccessible() {
        require(self.makeAccessible()) {
            "Failed to make the member \"$this\" accessible. " +
                "Please check if the member is accessible or if the security manager allows it."
        }
    }

    /**
     * Create a copy of this resolver.
     * @return [MemberResolver]<[M]>
     */
    abstract fun copy(): MemberResolver<M, T>
}