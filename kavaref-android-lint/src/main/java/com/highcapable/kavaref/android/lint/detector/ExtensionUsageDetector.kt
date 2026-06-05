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
package com.highcapable.kavaref.android.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.kavaref.android.lint.DeclaredSymbol
import com.highcapable.kavaref.android.lint.detector.extension.asCallExpression
import com.highcapable.kavaref.android.lint.detector.extension.buildReplaceFix
import com.highcapable.kavaref.android.lint.detector.extension.containsElement
import com.highcapable.kavaref.android.lint.detector.extension.createKotlinOnlyUastHandler
import com.highcapable.kavaref.android.lint.detector.extension.findClassForNameCall
import com.highcapable.kavaref.android.lint.detector.extension.findParentCastExpression
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UPrefixExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.UastBinaryExpressionWithTypeKind
import org.jetbrains.uast.UastBinaryOperator
import org.jetbrains.uast.UastPrefixOperator

class ExtensionUsageDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val EXTENSION_PACKAGE_NAME = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.extension"
        private const val EXTENSION_MARKER_CLASS = "$EXTENSION_PACKAGE_NAME.TypeRef"

        private const val JAVA_CLASS = "java.lang.Class"
        private const val JAVA_REFLECT_ARRAY_CLASS = "java.lang.reflect.Array"
        private const val JAVA_REFLECT_MODIFIER_CLASS = "java.lang.reflect.Modifier"
        private const val JAVA_CLASS_TYPE_PREFIX = "Class<"
        private const val JAVA_CLASS_TYPE_SUFFIX = ">"
        private const val JAVA_ARRAY_NEW_INSTANCE = "newInstance"
        private const val JAVA_CLASS_FOR_NAME = "forName"
        private const val JAVA_CLASS_LITERAL = "java"
        private const val JAVA_CLASS_OBJECT_TYPE = "javaObjectType"
        private const val JAVA_CLASS_PRIMITIVE_TYPE = "javaPrimitiveType"
        private const val JAVA_CLASS_PROPERTY = "javaClass"
        private const val JAVA_CLASS_IS_ASSIGNABLE_FROM = "isAssignableFrom"
        private const val JAVA_CLASS_INTERFACES = "interfaces"
        private const val JAVA_COLLECTION_IS_NOT_EMPTY = "isNotEmpty()"
        private const val JAVA_COLLECTION_IS_EMPTY = "isEmpty"
        private const val JAVA_COLLECTION_SIZE = "size"
        private const val JAVA_MEMBER_MODIFIERS_SUFFIX = ".modifiers"
        private const val JAVA_ACCESSIBLE_PROPERTY = "isAccessible"
        private const val KOTLIN_SAFE_CAST_OPERATOR = "as?"
        private const val NUMBER_ZERO = "0"
        private const val BOOLEAN_TRUE = "true"
        private const val GREATER_THAN_OPERATOR = ">"

        private const val ARRAY_CLASS = "ArrayClass"
        private const val CLASS_OF = "classOf"
        private const val TO_CLASS = "toClass"
        private const val TO_CLASS_OR_NULL = "toClassOrNull"
        private const val IS_SUBCLASS_OF = "isSubclassOf"
        private const val HAS_INTERFACES = "hasInterfaces"
        private const val MAKE_ACCESSIBLE = "makeAccessible"
        private const val PRIMITIVE_TYPE_PARAMETER = "primitiveType"

        private const val MODIFIER_IS_PUBLIC = "isPublic"
        private const val MODIFIER_IS_PRIVATE = "isPrivate"
        private const val MODIFIER_IS_PROTECTED = "isProtected"
        private const val MODIFIER_IS_STATIC = "isStatic"
        private const val MODIFIER_IS_FINAL = "isFinal"
        private const val MODIFIER_IS_SYNCHRONIZED = "isSynchronized"
        private const val MODIFIER_IS_VOLATILE = "isVolatile"
        private const val MODIFIER_IS_TRANSIENT = "isTransient"
        private const val MODIFIER_IS_NATIVE = "isNative"
        private const val MODIFIER_IS_INTERFACE = "isInterface"
        private const val MODIFIER_IS_ABSTRACT = "isAbstract"
        private const val MODIFIER_IS_STRICT = "isStrict"

        private const val ARRAY_CLASS_IMPORT = "$EXTENSION_PACKAGE_NAME.$ARRAY_CLASS"
        private const val CLASS_OF_IMPORT = "$EXTENSION_PACKAGE_NAME.$CLASS_OF"
        private const val TO_CLASS_IMPORT = "$EXTENSION_PACKAGE_NAME.$TO_CLASS"
        private const val TO_CLASS_OR_NULL_IMPORT = "$EXTENSION_PACKAGE_NAME.$TO_CLASS_OR_NULL"
        private const val IS_SUBCLASS_OF_IMPORT = "$EXTENSION_PACKAGE_NAME.$IS_SUBCLASS_OF"
        private const val HAS_INTERFACES_IMPORT = "$EXTENSION_PACKAGE_NAME.$HAS_INTERFACES"
        private const val MAKE_ACCESSIBLE_IMPORT = "$EXTENSION_PACKAGE_NAME.$MAKE_ACCESSIBLE"

        private val MODIFIER_PROPERTIES = mapOf(
            MODIFIER_IS_PUBLIC to MODIFIER_IS_PUBLIC,
            MODIFIER_IS_PRIVATE to MODIFIER_IS_PRIVATE,
            MODIFIER_IS_PROTECTED to MODIFIER_IS_PROTECTED,
            MODIFIER_IS_STATIC to MODIFIER_IS_STATIC,
            MODIFIER_IS_FINAL to MODIFIER_IS_FINAL,
            MODIFIER_IS_SYNCHRONIZED to MODIFIER_IS_SYNCHRONIZED,
            MODIFIER_IS_VOLATILE to MODIFIER_IS_VOLATILE,
            MODIFIER_IS_TRANSIENT to MODIFIER_IS_TRANSIENT,
            MODIFIER_IS_NATIVE to MODIFIER_IS_NATIVE,
            MODIFIER_IS_INTERFACE to MODIFIER_IS_INTERFACE,
            MODIFIER_IS_ABSTRACT to MODIFIER_IS_ABSTRACT,
            MODIFIER_IS_STRICT to MODIFIER_IS_STRICT
        )

        val ISSUE = Issue.create(
            id = "ReplaceWithKavaRefExtension",
            briefDescription = "Use KavaRef's extension instead.",
            explanation = """
                Common Java reflection utility patterns can be simplified by using KavaRef extension APIs from \
                `kavaref-extension` library.

                See the documentation for more details:
                - English: https://highcapable.github.io/KavaRef/en/library/kavaref-extension
                - 简体中文: https://highcapable.github.io/KavaRef/zh-cn/library/kavaref-extension

                The `JavaClass.kt`, `JavaArrayClass.kt`, and `JavaMember.kt` provide:
                - Shorter APIs for creating array classes and resolving classes by name
                - Safer Kotlin-friendly `Class` helpers with primitive and wrapper class handling
                - Direct subclass and interface checks for `Class`
                - Direct modifier checks for `Class` and `Member`
                - Better accessibility handling through `makeAccessible()`

                Examples:
                ```kotlin
                // Before
                Array.newInstance(type, 0).javaClass
                Class.forName(name)
                Class.forName(name, initialize, loader)
                Class.forName(name) as Class<Some>
                Class.forName(name) as? Class<Some>
                Some::class.java
                Some::class.javaObjectType
                Some::class.java.isAssignableFrom(value.javaClass)
                Some::class.java.interfaces.isNotEmpty()
                !Some::class.java.interfaces.isEmpty()
                Some::class.java.interfaces.size > 0
                Modifier.isPublic(member.modifiers)
                member.isAccessible = true

                // After
                ArrayClass(type)
                name.toClass()
                name.toClass(loader, initialize)
                name.toClass<Some>()
                name.toClassOrNull<Some>()
                classOf<Some>()
                classOf<Some>(primitiveType = false)
                value.javaClass isSubclassOf Some::class.java
                Some::class.java.hasInterfaces
                Some::class.java.hasInterfaces
                Some::class.java.hasInterfaces
                member.isPublic
                member.makeAccessible()
                ```
            """.trimIndent(),
            category = Category.USABILITY,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(
                ExtensionUsageDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UBinaryExpression::class.java,
        UBinaryExpressionWithType::class.java,
        UCallExpression::class.java,
        UPrefixExpression::class.java,
        UQualifiedReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        if (context.evaluator.findClass(EXTENSION_MARKER_CLASS) == null) return null

        return context.createKotlinOnlyUastHandler(object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                node.reportArrayNewInstanceJavaClass(context)
                node.reportClassForName(context)
                node.reportAssignableFrom(context)
                node.reportModifier(context)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                node.reportClassOf(context)
                node.reportHasInterfaces(context)
            }

            override fun visitBinaryExpression(node: UBinaryExpression) {
                node.reportHasInterfaces(context)
                node.reportIsAccessible(context)
            }

            override fun visitBinaryExpressionWithType(node: UBinaryExpressionWithType) {
                node.reportClassForNameAsType(context)
            }

            override fun visitPrefixExpression(node: UPrefixExpression) {
                node.reportHasInterfaces(context)
            }
        })
    }

    private fun UCallExpression.reportArrayNewInstanceJavaClass(context: JavaContext) {
        if (methodName != JAVA_ARRAY_NEW_INSTANCE) return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_REFLECT_ARRAY_CLASS)) return
        if (valueArguments.size != 2) return
        if (valueArguments[1].asSourceString() != NUMBER_ZERO) return

        val callExpression = (uastParent as? UQualifiedReferenceExpression)?.takeIf { it.selector == this } ?: this
        val parent = callExpression.uastParent as? UQualifiedReferenceExpression ?: return
        if (parent.receiver != callExpression) return
        if (parent.selector.asSourceString() != JAVA_CLASS_PROPERTY) return

        val replacement = "$ARRAY_CLASS(${valueArguments[0].asSourceString()})"
        context.report(
            issue = ISSUE,
            scope = parent,
            location = context.getLocation(parent),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'ArrayClass'", replacement, ARRAY_CLASS_IMPORT)
        )
    }

    private fun UCallExpression.reportClassForName(context: JavaContext) {
        if (methodName != JAVA_CLASS_FOR_NAME) return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return
        if (findParentCastExpression()?.containsElement(this) == true) return
        val replacement = classForNameReplacement(TO_CLASS) ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'toClass'", replacement, TO_CLASS_IMPORT)
        )
    }

    private fun UBinaryExpressionWithType.reportClassForNameAsType(context: JavaContext) {
        val call = operand.findClassForNameCall() ?: return
        if (call.methodName != JAVA_CLASS_FOR_NAME) return
        val method = call.resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return

        val functionName = when {
            operationKind == UastBinaryExpressionWithTypeKind.TypeCast.INSTANCE -> TO_CLASS
            operationKind.name == KOTLIN_SAFE_CAST_OPERATOR -> TO_CLASS_OR_NULL
            else -> return
        }
        val importTarget = if (functionName == TO_CLASS) TO_CLASS_IMPORT else TO_CLASS_OR_NULL_IMPORT
        val typeText = typeReference?.sourcePsi?.text?.toClassTypeArgument() ?: return
        val replacement = call.classForNameReplacement(functionName, typeText) ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with '$functionName'", replacement, importTarget)
        )
    }

    private fun UCallExpression.classForNameReplacement(functionName: String, typeText: String? = null): String? {
        val className = valueArguments.firstOrNull()?.asSourceString() ?: return null
        val typeParameter = typeText?.let { "<$it>" }.orEmpty()
        val arguments = when (valueArguments.size) {
            1 -> "()"
            3 -> {
                val initialize = valueArguments[1].asSourceString()
                val loader = valueArguments[2].asSourceString()
                "($loader, $initialize)"
            }
            else -> return null
        }

        return "$className.$functionName$typeParameter$arguments"
    }

    private fun String.toClassTypeArgument(): String {
        val source = trim()
        if (!source.startsWith(JAVA_CLASS_TYPE_PREFIX) || !source.endsWith(JAVA_CLASS_TYPE_SUFFIX)) return source
        return source.removePrefix(JAVA_CLASS_TYPE_PREFIX).dropLast(1).trim()
    }

    private fun UQualifiedReferenceExpression.reportClassOf(context: JavaContext) {
        val selectorName = selector.asSourceString()
        if (selectorName !in setOf(JAVA_CLASS_LITERAL, JAVA_CLASS_OBJECT_TYPE, JAVA_CLASS_PRIMITIVE_TYPE)) return
        val classLiteral = receiver as? UClassLiteralExpression ?: return
        val typeText = classLiteral.expression?.asSourceString() ?: return
        val replacement = when (selectorName) {
            JAVA_CLASS_LITERAL, JAVA_CLASS_PRIMITIVE_TYPE -> "$CLASS_OF<$typeText>()"
            JAVA_CLASS_OBJECT_TYPE -> "$CLASS_OF<$typeText>($PRIMITIVE_TYPE_PARAMETER = false)"
            else -> return
        }

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'classOf'", replacement, CLASS_OF_IMPORT)
        )
    }

    private fun UCallExpression.reportAssignableFrom(context: JavaContext) {
        if (methodName != JAVA_CLASS_IS_ASSIGNABLE_FROM) return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return
        val receiverText = receiver?.asSourceString() ?: return
        val argumentText = valueArguments.singleOrNull()?.asSourceString() ?: return
        val replacement = "$argumentText $IS_SUBCLASS_OF $receiverText"

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'isSubclassOf'", replacement, IS_SUBCLASS_OF_IMPORT)
        )
    }

    private fun UQualifiedReferenceExpression.reportHasInterfaces(context: JavaContext) {
        if (selector.asSourceString() != JAVA_COLLECTION_IS_NOT_EMPTY) return
        val interfaces = receiver as? UQualifiedReferenceExpression ?: return
        if (interfaces.selector.asSourceString() != JAVA_CLASS_INTERFACES) return
        val replacement = "${interfaces.receiver.asSourceString()}.$HAS_INTERFACES"

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'hasInterfaces'", replacement, HAS_INTERFACES_IMPORT)
        )
    }

    private fun UBinaryExpression.reportHasInterfaces(context: JavaContext) {
        val replacement = replacementForHasInterfaces() ?: return
        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'hasInterfaces'", replacement, HAS_INTERFACES_IMPORT)
        )
    }

    private fun UBinaryExpression.replacementForHasInterfaces(): String? {
        val leftQualified = leftOperand as? UQualifiedReferenceExpression
        val rightText = rightOperand.asSourceString()
        if (operator.text == GREATER_THAN_OPERATOR && leftQualified?.selector?.asSourceString() == JAVA_COLLECTION_SIZE && rightText == NUMBER_ZERO) {
            val interfaces = leftQualified.receiver as? UQualifiedReferenceExpression ?: return null
            if (interfaces.selector.asSourceString() != JAVA_CLASS_INTERFACES) return null
            return "${interfaces.receiver.asSourceString()}.$HAS_INTERFACES"
        }

        return null
    }

    private fun UPrefixExpression.reportHasInterfaces(context: JavaContext) {
        if (operator != UastPrefixOperator.LOGICAL_NOT) return
        val call = operand.asCallExpression() ?: return
        if (call.methodName != JAVA_COLLECTION_IS_EMPTY) return
        val receiver = call.receiver as? UQualifiedReferenceExpression ?: return
        if (receiver.selector.asSourceString() != JAVA_CLASS_INTERFACES) return
        val replacement = "${receiver.receiver.asSourceString()}.$HAS_INTERFACES"

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'hasInterfaces'", replacement, HAS_INTERFACES_IMPORT)
        )
    }

    private fun UCallExpression.reportModifier(context: JavaContext) {
        val propertyName = MODIFIER_PROPERTIES[methodName] ?: return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_REFLECT_MODIFIER_CLASS)) return
        val argumentText = valueArguments.singleOrNull()?.asSourceString() ?: return
        val receiverText = argumentText.removeSuffix(JAVA_MEMBER_MODIFIERS_SUFFIX).takeIf { it != argumentText } ?: return
        val replacement = "$receiverText.$propertyName"

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with '$propertyName'", replacement, "$EXTENSION_PACKAGE_NAME.$propertyName")
        )
    }

    private fun UBinaryExpression.reportIsAccessible(context: JavaContext) {
        if (operator != UastBinaryOperator.ASSIGN) return
        if (rightOperand.asSourceString() != BOOLEAN_TRUE) return
        val replacement = leftOperand.makeAccessibleReplacement() ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`.",
            quickfixData = buildReplaceFix("Replace with 'makeAccessible'", replacement, MAKE_ACCESSIBLE_IMPORT)
        )
    }

    private fun UExpression.makeAccessibleReplacement(): String? = when (this) {
        is USimpleNameReferenceExpression ->
            "$MAKE_ACCESSIBLE()".takeIf { identifier == JAVA_ACCESSIBLE_PROPERTY }
        is UQualifiedReferenceExpression -> {
            if (selector.asSourceString() != JAVA_ACCESSIBLE_PROPERTY) return null
            when (receiver) {
                is UThisExpression -> "this.$MAKE_ACCESSIBLE()"
                else -> "${receiver.asSourceString()}.$MAKE_ACCESSIBLE()"
            }
        }
        else -> null
    }
}