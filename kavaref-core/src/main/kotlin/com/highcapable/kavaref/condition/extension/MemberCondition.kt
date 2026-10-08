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
 * This file is created by fankes on 2025/6/20.
 */
@file:Suppress("unused")

package com.highcapable.kavaref.condition.extension

import com.highcapable.kavaref.condition.base.MemberCondition
import com.highcapable.kavaref.resolver.base.MemberResolver
import java.lang.reflect.Member

/**
 * Merges the data set in the [other] condition into this condition.
 * @receiver the condition to merge into.
 * @param other the other condition to merge from.
 */
infix fun <M : Member, R : MemberResolver<M, T>, T : Any, U : MemberCondition<M, R, T>> U.mergeWith(other: U) = initializeMergedData(other)