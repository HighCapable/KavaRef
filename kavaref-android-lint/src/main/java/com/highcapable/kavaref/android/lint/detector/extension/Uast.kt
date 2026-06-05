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
 * This file is created by fankes on 2026/6/6.
 */
package com.highcapable.kavaref.android.lint.detector.extension

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.JavaContext
import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UQualifiedReferenceExpression

internal fun JavaContext.createKotlinOnlyUastHandler(handler: UElementHandler) =
    handler.takeIf { file.name.endsWith(".kt") }

internal fun UExpression.asCallExpression() = when (this) {
    is UCallExpression -> this
    is UQualifiedReferenceExpression -> selector as? UCallExpression
    else -> null
}

internal fun UExpression.findClassForNameCall(): UCallExpression? = when (this) {
    is UCallExpression -> this
    is UQualifiedReferenceExpression -> selector.findClassForNameCall()
    else -> null
}

internal fun UElement.findParentCastExpression(): UBinaryExpressionWithType? {
    var current = uastParent
    while (current != null) {
        if (current is UBinaryExpressionWithType) return current
        current = current.uastParent
    }
    return null
}

internal fun UElement.containsElement(target: UElement): Boolean {
    var current: UElement? = target
    while (current != null) {
        if (current == this) return true
        current = current.uastParent
    }
    return false
}