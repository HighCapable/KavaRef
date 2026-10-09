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
 * This file is created by fankes on 2026/6/5.
 */
package com.highcapable.kavaref.android.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.kavaref.android.lint.DeclaredSymbol
import com.highcapable.kavaref.android.lint.detector.extension.buildReplaceFix
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UThisExpression

class ExecutableConditionDetector : Detector(), Detector.UastScanner {

    companion object {

        val UNSUPPORTED_EXECUTABLE_CONDITION_ISSUE = Issue.create(
            id = "UnsupportedExecutableCondition",
            briefDescription = "Unsupported executable condition on Android",
            explanation = "Annotated executable type conditions are not supported on Android.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                ExecutableConditionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        val EMPTY_CONDITION_ARGUMENTS_ISSUE = Issue.create(
            id = "EmptyConditionArguments",
            briefDescription = "Condition function without arguments",
            explanation = """
              A condition function without any arguments adds no condition, it matches members with anything. \
              To match members whose result is empty, use the corresponding empty condition function instead, \
              such as `emptyParameters()` for `parameters()`.
            """,
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.INFORMATIONAL,
            implementation = Implementation(
                ExecutableConditionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )

        private const val EXECUTABLE_CONDITION_CLASS = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.condition.base.ExecutableCondition"
        private const val CONSTRUCTOR_CONDITION_CLASS = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.condition.ConstructorCondition"
        private const val METHOD_CONDITION_CLASS = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.condition.MethodCondition"
        private const val MEMBER_CONDITION_CLASS = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.condition.base.MemberCondition"
        private const val FIELD_CONDITION_CLASS = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.condition.FieldCondition"

        private val EXECUTABLE_CONDITION_CLASSES = setOf(
            EXECUTABLE_CONDITION_CLASS,
            CONSTRUCTOR_CONDITION_CLASS,
            METHOD_CONDITION_CLASS
        )

        private val MEMBER_CONDITION_CLASSES = EXECUTABLE_CONDITION_CLASSES + setOf(
            MEMBER_CONDITION_CLASS,
            FIELD_CONDITION_CLASS
        )

        private val ANDROID_UNSUPPORTED_ANNOTATED_CONDITION_FUNCTIONS = setOf(
            "annotatedReturnType",
            "annotatedReturnTypeNot",
            "annotatedReceiverType",
            "annotatedReceiverTypeNot",
            "annotatedParameterTypes",
            "annotatedParameterTypesNot",
            "annotatedExceptionTypes",
            "annotatedExceptionTypesNot"
        )

        private val EMPTY_CONDITION_FUNCTIONS = mapOf(
            "annotations" to "emptyAnnotations",
            "annotationsNot" to "emptyAnnotationsNot",
            "parameters" to "emptyParameters",
            "parametersNot" to "emptyParametersNot",
            "genericParameters" to "emptyParameters",
            "genericParametersNot" to "emptyParametersNot",
            "parameterAnnotations" to "emptyParameters",
            "parameterAnnotationsNot" to "emptyParametersNot",
            "annotatedParameterTypes" to "emptyParameters",
            "annotatedParameterTypesNot" to "emptyParametersNot",
            "typeParameters" to "emptyTypeParameters",
            "typeParametersNot" to "emptyTypeParametersNot",
            "exceptionTypes" to "emptyExceptionTypes",
            "exceptionTypesNot" to "emptyExceptionTypesNot",
            "annotatedExceptionTypes" to "emptyExceptionTypes",
            "annotatedExceptionTypesNot" to "emptyExceptionTypesNot",
            "genericExceptionTypes" to "emptyGenericExceptionTypes",
            "genericExceptionTypesNot" to "emptyGenericExceptionTypesNot"
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        USimpleNameReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            val methodName = node.methodName ?: return
            if (methodName in ANDROID_UNSUPPORTED_ANNOTATED_CONDITION_FUNCTIONS) reportUnsupportedCondition(node, methodName)
            EMPTY_CONDITION_FUNCTIONS[methodName]?.let { reportEmptyConditionArguments(node, it) }
        }

        private fun reportUnsupportedCondition(node: UCallExpression, methodName: String) {
            val method = node.resolve() ?: return
            if (!method.isExecutableConditionFunction()) return

            val callLocation = context.getCallLocation(node, includeReceiver = false, includeArguments = true)
            val location = callLocation.toReportCallExpressionLocation(context)
            val lintFix = node.deleteCallLocation(callLocation)?.let { buildDeleteFix(it) }

            context.report(
                UNSUPPORTED_EXECUTABLE_CONDITION_ISSUE, node, location,
                message = "`$methodName` is not supported on Android",
                quickfixData = lintFix
            )
        }

        private fun reportEmptyConditionArguments(node: UCallExpression, emptyFunction: String) {
            if (node.valueArgumentCount != 0) return

            val method = node.resolve() ?: return
            if (method.containingClass?.qualifiedName !in MEMBER_CONDITION_CLASSES) return

            val location = context.getCallLocation(node, includeReceiver = false, includeArguments = true)
            val lintFix = buildReplaceFix(
                name = "Replace with $emptyFunction()",
                replacement = "$emptyFunction()",
                range = location
            )

            context.report(
                EMPTY_CONDITION_ARGUMENTS_ISSUE, node, location,
                message = "No arguments are found, did you mean `$emptyFunction()`?",
                quickfixData = lintFix
            )
        }

        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            val propertyName = node.identifier
            if (propertyName !in ANDROID_UNSUPPORTED_ANNOTATED_CONDITION_FUNCTIONS) return

            val property = node.resolve() ?: return
            if (!property.isExecutableConditionProperty()) return

            val reportNode = node.reportNode()
            val location = context.getLocation(reportNode)
            val lintFix = reportNode.takeIf { it.isStatement() }?.statementLocation()?.let { buildDeleteFix(it) }

            context.report(
                UNSUPPORTED_EXECUTABLE_CONDITION_ISSUE, reportNode, location,
                message = "`$propertyName` is not supported on Android",
                quickfixData = lintFix
            )
        }

        private fun PsiMethod.isExecutableConditionFunction(): Boolean {
            val containingClassName = containingClass?.qualifiedName ?: return false
            return containingClassName in EXECUTABLE_CONDITION_CLASSES
        }

        private fun Any.isExecutableConditionProperty(): Boolean {
            val containingClassName = when (this) {
                is PsiField -> containingClass?.qualifiedName
                is PsiMethod -> containingClass?.qualifiedName
                else -> null
            } ?: return false

            if (containingClassName !in EXECUTABLE_CONDITION_CLASSES) return false

            val name = when (this) {
                is PsiField -> name
                is PsiMethod -> name.takeIf { it.startsWith("get") }?.removePrefix("get")?.replaceFirstChar { it.lowercase() }
                else -> return false
            } ?: return false
            return name in ANDROID_UNSUPPORTED_ANNOTATED_CONDITION_FUNCTIONS
        }

        private fun USimpleNameReferenceExpression.reportNode(): UElement {
            val parent = uastParent
            val grandparent = parent?.uastParent

            fun UElement.outermostQualifiedReference(): UElement {
                var current: UElement = this
                while (true) {
                    val next = current.uastParent as? UQualifiedReferenceExpression ?: return current
                    if (next.receiver != current) return current
                    current = next
                }
            }

            return when {
                parent is UQualifiedReferenceExpression && parent.selector == this -> when (grandparent) {
                    is UBinaryExpression -> grandparent
                    else -> parent.outermostQualifiedReference()
                }
                parent is UQualifiedReferenceExpression && parent.receiver == this -> parent.outermostQualifiedReference()
                parent is UBinaryExpression -> parent
                grandparent is UBinaryExpression -> grandparent
                else -> this
            }
        }

        private fun Location.toReportCallExpressionLocation(context: JavaContext): Location {
            val startOffset = start?.offset ?: return this
            val endOffset = end?.offset ?: return this
            val contents = context.getContents() ?: return this
            val dotOffset = startOffset - 1
            if (dotOffset < 0 || contents[dotOffset] != '.') return this

            val lineStart = contents.lineStartOf(dotOffset)
            val lineEnd = contents.lineEndOf(endOffset)
            val firstCodeOffset = contents.firstCodeOffset(lineStart, dotOffset)

            val dotStartsChainLine = (lineStart until dotOffset).all { contents[it].isWhitespace() }
            if (dotStartsChainLine) return Location.create(context.file, contents, dotOffset, endOffset)

            val receiverText = contents.subSequence(firstCodeOffset, dotOffset)
            val trailingIsStatementEnd = (endOffset until lineEnd).all { contents[it].isWhitespace() || contents[it] == ';' }
            if (receiverText.isStandaloneReceiver() && trailingIsStatementEnd)
                return Location.create(context.file, contents, firstCodeOffset, endOffset)

            return this
        }

        private fun buildDeleteFix(location: Location) = LintFix.create()
            .name("Delete Call Expression")
            .replace()
            .range(location)
            .with("")
            .reformat(true)
            .build()

        private fun UCallExpression.deleteCallLocation(callLocation: Location): Location? {
            val contents = context.getContents() ?: return null
            val callStart = callLocation.start?.offset ?: return null
            val callEnd = callLocation.end?.offset ?: return null
            // The implicit receiver of a lambda has no source, the call has no explicit receiver.
            val receiver = receiver?.takeIf { it.sourcePsi != null }
            if (receiver != null) {
                val expression = (uastParent as? UQualifiedReferenceExpression)?.takeIf { it.selector == this } ?: this
                if (expression.isStatement() && receiver.isPureReference()) return expression.statementLocation()
                // Deletes the dot and the call after the receiver, such as ".call()" in "a.call().b()".
                val receiverEnd = receiver.sourcePsi?.textRange?.endOffset ?: return null
                return Location.create(context.file, contents, receiverEnd, callEnd)
            }

            val parent = uastParent as? UQualifiedReferenceExpression
            if (parent != null && parent.receiver == this) {
                // Deletes the call and the dot before the next selector, such as "call()." in "call().b()".
                val selectorStart = parent.selector.sourcePsi?.textRange?.startOffset ?: return null
                return Location.create(context.file, contents, callStart, selectorStart)
            }

            return if (isStatement()) statementLocation() else null
        }

        private fun UElement.statementLocation(): Location? {
            val contents = context.getContents() ?: return null
            val range = sourcePsi?.textRange ?: return null
            val lineStart = contents.lineStartOf(range.startOffset)
            val lineEnd = contents.lineEndOf(range.endOffset)
            val endOffset = contents.endOffsetWithSemicolon(range.endOffset, lineEnd)
            val isAloneOnLine = (lineStart until range.startOffset).all { contents[it].isWhitespace() } &&
                (endOffset until lineEnd).all { contents[it].isWhitespace() }

            return if (isAloneOnLine)
                Location.create(context.file, contents, lineStart, if (lineEnd < contents.length) lineEnd + 1 else lineEnd)
            else Location.create(context.file, contents, range.startOffset, endOffset)
        }

        private fun UElement.isStatement(): Boolean {
            val parent = uastParent
            // The last expression of a lambda is wrapped in an implicit return, it is only a statement if the lambda returns Unit.
            if (parent is UReturnExpression && parent.sourcePsi == null) {
                val lambda = parent.uastParent?.uastParent as? ULambdaExpression ?: return false
                val functionType = lambda.getExpressionType() as? PsiClassType ?: return false
                return functionType.parameters.lastOrNull()?.canonicalText?.removePrefix("? extends ") == "kotlin.Unit"
            }

            return parent is UBlockExpression
        }

        private fun UExpression.isPureReference(): Boolean = when (this) {
            is USimpleNameReferenceExpression, is UThisExpression -> true
            is UQualifiedReferenceExpression -> receiver.isPureReference() && selector is USimpleNameReferenceExpression
            else -> false
        }

        private fun CharSequence.lineStartOf(offset: Int): Int {
            var index = offset
            while (index > 0 && this[index - 1] != '\n') index--
            return index
        }

        private fun CharSequence.lineEndOf(offset: Int): Int {
            var index = offset
            while (index < length && this[index] != '\n') index++
            return index
        }

        private fun CharSequence.firstCodeOffset(startOffset: Int, endOffset: Int): Int {
            var index = startOffset
            while (index < endOffset && this[index].isWhitespace()) index++
            return index
        }

        private fun CharSequence.endOffsetWithSemicolon(offset: Int, lineEnd: Int): Int {
            var index = offset
            while (index < lineEnd && this[index].isWhitespace()) index++
            return if (index < lineEnd && this[index] == ';') index + 1 else offset
        }

        private fun CharSequence.isStandaloneReceiver() =
            isNotEmpty() && all { it.isJavaIdentifierPart() || it == '.' || it == ')' || it == ']' }
    }
}