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

import com.highcapable.kavaref.condition.base.MemberCondition
import java.lang.reflect.Member

/**
 * Base [instance] aware class for resolving member [M].
 * 
 * [T] to specify the declaring class type of the member.
 * [H] to specify the handler type of the member.
 * @param self the member to be resolved.
 */
abstract class InstanceAwareResolver<M : Member, T : Any, H : MemberResolver.Handler<M>>(
    override val self: M
) : MemberResolver<M, T, H>(self) {

    /** The instance of [self]. */
    protected var instance: T? = null

    /**
     * Sets the instance of [self].
     *
     * If you have already set it in [MemberCondition.Configuration.memberInstance],
     * then you cannot set it here again, otherwise an [IllegalStateException] will be thrown.
     * If you want to reuse the resolver, please use [copy] to create a new resolver.
     * @param instance the instance to set.
     */
    abstract fun of(instance: T?): InstanceAwareResolver<M, T, H>

    /**
     * Checks if the [instance] is null and sets it.
     * If the [instance] is not null, throw an exception.
     * @param instance the instance to set.
     * @throws IllegalStateException if the [instance] is not null.
     */
    protected fun checkAndSetInstance(instance: T?) {
        check(this.instance == null) {
            "Instance already set for this resolver \"$javaClass\" of \"$self(${this.instance})\". " +
                "To prevent problems, the instance object can only be set once in a resolver, " +
                "otherwise use copy() to reuse the resolver."
        }

        this.instance = instance
    }

    // region Binary compatibility for the internal accessors called by previous versions

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmSynthetic
    @JvmName($$"getInstance$com_highcapable_kavaref_kavaref_core")
    internal fun _getInstance() = instance

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmSynthetic
    @JvmName($$"setInstance$com_highcapable_kavaref_kavaref_core")
    internal fun _setInstance(instance: T?) {
        this.instance = instance
    }

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmSynthetic
    @JvmName($$"getInstance$kavaref_core")
    internal fun __getInstance() = instance

    @Deprecated(message = "", level = DeprecationLevel.HIDDEN)
    @JvmSynthetic
    @JvmName($$"setInstance$kavaref_core")
    internal fun __setInstance(instance: T?) {
        this.instance = instance
    }

    // endregion
}