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
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.ULiteralExpression
import org.jetbrains.uast.UParenthesizedExpression
import org.jetbrains.uast.UPostfixExpression
import org.jetbrains.uast.UPrefixExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.UastPrefixOperator
import org.jetbrains.uast.expressions.UInjectionHost
import org.jetbrains.uast.toUElementOfType

internal fun JavaContext.createKotlinOnlyUastHandler(handler: UElementHandler) =
    handler.takeIf { file.name.endsWith(".kt") }

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

internal fun ULambdaExpression.singleExpression(): UExpression? {
    val block = body as? UBlockExpression ?: return body
    val expression = block.expressions.singleOrNull() ?: return null

    return (expression as? UReturnExpression)?.returnExpression ?: expression
}

internal fun UExpression.isMethodOf(context: JavaContext, className: String, methodName: String): Boolean {
    val method = (this as? UResolvable)?.resolve() as? PsiMethod ?: return false
    return method.name == methodName && context.evaluator.isMemberInClass(method, className)
}

internal fun UExpression.isNullableType(): Boolean {
    // The type of elvis expression is nullable in UAST, its nullability depends on the right operand.
    val elvis = (sourcePsi as? KtBinaryExpression)?.takeIf { it.operationToken == KtTokens.ELVIS }
    if (elvis != null) return elvis.right?.toUElementOfType<UExpression>()?.isNullableType() ?: true

    return getExpressionType()?.annotations?.any { it.qualifiedName?.endsWith(".Nullable") == true } == true
}

internal fun UExpression.unwrapParentheses(): UExpression = if (this is UParenthesizedExpression) expression.unwrapParentheses() else this

internal fun UExpression.parentLogicalNot(): UPrefixExpression? {
    var current: UElement? = uastParent
    while (current is UParenthesizedExpression) current = current.uastParent

    return (current as? UPrefixExpression)?.takeIf { it.operator == UastPrefixOperator.LOGICAL_NOT }
}

internal fun UExpression.operandText(): String {
    val text = sourcePsi?.text ?: asSourceString()
    return when (this) {
        is USimpleNameReferenceExpression, is UQualifiedReferenceExpression, is UCallExpression,
        is UClassLiteralExpression, is UParenthesizedExpression, is UPostfixExpression,
        is UThisExpression, is ULiteralExpression, is UInjectionHost -> text
        else -> "($text)"
    }
}

internal fun UExpression.wrapForParent(replacement: String): String {
    val parent = uastParent
    return if (parent is UQualifiedReferenceExpression && parent.receiver == this) "($replacement)" else replacement
}

internal fun UExpression.wrapInfixForParent(replacement: String): String {
    val needsParentheses = when (val parent = uastParent) {
        is UQualifiedReferenceExpression -> parent.receiver == this
        is UBinaryExpressionWithType, is UPrefixExpression, is UPostfixExpression -> true
        is UBinaryExpression -> parent.operator.text in setOf("*", "/", "%", "+", "-", "..", "..<")
        else -> false
    }

    return if (needsParentheses) "($replacement)" else replacement
}

internal fun UElement.resultOfLambda(): ULambdaExpression? {
    val parent = uastParent

    // The result of a lambda is wrapped in an implicit return.
    if (parent is UReturnExpression && parent.sourcePsi == null) return parent.uastParent?.uastParent as? ULambdaExpression
    val block = parent as? UBlockExpression ?: return null

    val lambda = block.uastParent as? ULambdaExpression ?: return null
    return lambda.takeIf { block.expressions.lastOrNull() == this }
}

internal fun ULambdaExpression.isPassedAsUnitFunction(context: JavaContext): Boolean {
    val call = uastParent as? UCallExpression ?: return false
    val method = call.resolve() ?: return false
    val parameter = context.evaluator.computeArgumentMapping(call, method)[this] ?: return false

    val type = parameter.type as? PsiClassType ?: return false
    return type.parameters.lastOrNull()?.canonicalText?.removePrefix("? extends ") == "kotlin.Unit"
}