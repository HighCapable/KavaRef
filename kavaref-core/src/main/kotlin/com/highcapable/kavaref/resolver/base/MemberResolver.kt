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
@file:Suppress("FunctionName")

package com.highcapable.kavaref.resolver.base

import com.highcapable.kavaref.extension.makeAccessible
import java.lang.reflect.Member

/**
 * Base class for resolving member [M].
 * 
 * [T] to specify the declaring class type of the member.
 * [H] to specify the handler type of the member.
 * @param self the member to be resolved.
 */
abstract class MemberResolver<M : Member, T : Any, H : MemberResolver.Handler<M>>(open val self: M) {

    /**
     * Handler for performing the operations on the member [M].
     */
    abstract class Handler<in M : Member> {

        /**
         * Makes the [member] accessible and checks if it is successful.
         *
         * Override this function without calling super to skip it.
         * @param member the member to make accessible.
         * @throws IllegalArgumentException if failed to make the [member] accessible.
         */
        open fun requireAccessible(member: M) {
            require(member.makeAccessible()) {
                "Failed to make the member \"$member\" accessible. " +
                    "Please check if the member is accessible or if the security manager allows it."
            }
        }
    }

    /**
     * Creates a new resolver with the given [handler].
     * @param handler the handler to set.
     * @return [MemberResolver]<[M], [T], [H]>
     */
    abstract fun withHandler(handler: H): MemberResolver<M, T, H>

    /**
     * Creates a copy of this resolver.
     * @return [MemberResolver]<[M], [T], [H]>
     */
    abstract fun copy(): MemberResolver<M, T, H>

    // region Binary compatibility for the inline functions compiled by previous versions

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @PublishedApi
    @JvmSynthetic
    @JvmName("requireAccessible")
    internal fun _requireAccessible() {
        require(self.makeAccessible()) {
            "Failed to make the member \"$this\" accessible. " +
                "Please check if the member is accessible or if the security manager allows it."
        }
    }

    // endregion
}