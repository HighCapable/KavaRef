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
import com.highcapable.kavaref.android.lint.detector.extension.buildReplaceFix
import com.highcapable.kavaref.android.lint.detector.extension.containsElement
import com.highcapable.kavaref.android.lint.detector.extension.createKotlinOnlyUastHandler
import com.highcapable.kavaref.android.lint.detector.extension.findClassForNameCall
import com.highcapable.kavaref.android.lint.detector.extension.findParentCastExpression
import com.highcapable.kavaref.android.lint.detector.extension.isMethodOf
import com.highcapable.kavaref.android.lint.detector.extension.isNullableType
import com.highcapable.kavaref.android.lint.detector.extension.isPassedAsUnitFunction
import com.highcapable.kavaref.android.lint.detector.extension.operandText
import com.highcapable.kavaref.android.lint.detector.extension.parentLogicalNot
import com.highcapable.kavaref.android.lint.detector.extension.resultOfLambda
import com.highcapable.kavaref.android.lint.detector.extension.singleExpression
import com.highcapable.kavaref.android.lint.detector.extension.unwrapParentheses
import com.highcapable.kavaref.android.lint.detector.extension.wrapForParent
import com.highcapable.kavaref.android.lint.detector.extension.wrapInfixForParent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.PsiVariable
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtTypeAlias
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
import org.jetbrains.uast.UPrefixExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.USuperExpression
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.UastBinaryExpressionWithTypeKind
import org.jetbrains.uast.UastBinaryOperator
import org.jetbrains.uast.UastPrefixOperator

class ExtensionUsageDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val EXTENSION_PACKAGE_NAME = "${DeclaredSymbol.KAVAREF_PACKAGE_NAME}.extension"
        private const val EXTENSION_MARKER_CLASS = "$EXTENSION_PACKAGE_NAME.TypeRef"

        private const val JAVA_CLASS = "java.lang.Class"
        private const val JAVA_OBJECT_CLASS = "java.lang.Object"
        private const val JAVA_CLASS_ANY_TYPE = "Class<Any>"
        private const val JAVA_REFLECT_ARRAY_CLASS = "java.lang.reflect.Array"
        private const val JAVA_REFLECT_MODIFIER_CLASS = "java.lang.reflect.Modifier"
        private const val JAVA_REFLECT_ACCESSIBLE_OBJECT_CLASS = "java.lang.reflect.AccessibleObject"
        private const val JAVA_REFLECT_MEMBER_CLASS = "java.lang.reflect.Member"
        private const val JAVA_REFLECT_PARAMETERIZED_TYPE_CLASS = "java.lang.reflect.ParameterizedType"
        private const val JAVA_ARRAY_NEW_INSTANCE = "newInstance"
        private const val JAVA_CLASS_FOR_NAME = "forName"
        private const val JAVA_CLASS_LITERAL = "java"
        private const val JAVA_CLASS_OBJECT_TYPE = "javaObjectType"
        private const val JAVA_CLASS_PRIMITIVE_TYPE = "javaPrimitiveType"
        private const val JAVA_CLASS_PROPERTY = "javaClass"
        private const val JAVA_CLASS_IS_ASSIGNABLE_FROM = "isAssignableFrom"
        private const val JAVA_CLASS_GET_INTERFACES = "getInterfaces"
        private const val JAVA_CLASS_GET_GENERIC_SUPERCLASS = "getGenericSuperclass"
        private const val JAVA_PARAMETERIZED_TYPE_GET_RAW_TYPE = "getRawType"
        private const val JAVA_PARAMETERIZED_TYPE_GET_ACTUAL_TYPE_ARGUMENTS = "getActualTypeArguments"
        private const val KOTLIN_RUN_CATCHING = "runCatching"
        private const val KOTLIN_RESULT_GET_OR_NULL = "getOrNull"
        private const val JAVA_COLLECTION_IS_NOT_EMPTY = "isNotEmpty"
        private const val JAVA_COLLECTION_IS_EMPTY = "isEmpty"
        private const val JAVA_COLLECTION_SIZE = "size"
        private const val JAVA_ACCESSIBLE_PROPERTY = "isAccessible"
        private const val JAVA_MODIFIERS_PROPERTY = "modifiers"

        private val JAVA_WRAPPER_CLASSES = setOf(
            "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short",
            "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void"
        )
        private val KOTLIN_PRIMITIVE_TYPES = setOf("Boolean", "Char", "Byte", "Short", "Int", "Long", "Float", "Double")

        private const val ARRAY_CLASS = "ArrayClass"
        private const val CLASS_OF = "classOf"
        private const val TO_CLASS = "toClass"
        private const val TO_CLASS_OR_NULL = "toClassOrNull"
        private const val IS_SUBCLASS_OF = "isSubclassOf"
        private const val IS_NOT_SUBCLASS_OF = "isNotSubclassOf"
        private const val GENERIC_SUPERCLASS_TYPE_ARGUMENTS = "genericSuperclassTypeArguments"
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
        private const val IS_NOT_SUBCLASS_OF_IMPORT = "$EXTENSION_PACKAGE_NAME.$IS_NOT_SUBCLASS_OF"
        private const val GENERIC_SUPERCLASS_TYPE_ARGUMENTS_IMPORT = "$EXTENSION_PACKAGE_NAME.$GENERIC_SUPERCLASS_TYPE_ARGUMENTS"
        private const val HAS_INTERFACES_IMPORT = "$EXTENSION_PACKAGE_NAME.$HAS_INTERFACES"
        private const val MAKE_ACCESSIBLE_IMPORT = "$EXTENSION_PACKAGE_NAME.$MAKE_ACCESSIBLE"

        private val MODIFIER_PROPERTIES = setOf(
            MODIFIER_IS_PUBLIC,
            MODIFIER_IS_PRIVATE,
            MODIFIER_IS_PROTECTED,
            MODIFIER_IS_STATIC,
            MODIFIER_IS_FINAL,
            MODIFIER_IS_SYNCHRONIZED,
            MODIFIER_IS_VOLATILE,
            MODIFIER_IS_TRANSIENT,
            MODIFIER_IS_NATIVE,
            MODIFIER_IS_INTERFACE,
            MODIFIER_IS_ABSTRACT,
            MODIFIER_IS_STRICT
        )

        val ISSUE = Issue.create(
            id = "ReplaceWithKavaRefExtension",
            briefDescription = "Use KavaRef's extension instead",
            explanation = """
                Common Java reflection utility patterns can be simplified by using KavaRef extension APIs from \
                `kavaref-extension` library.

                See the documentation for more details:
                - English: https://highcapable.github.io/KavaRef/en/library/kavaref-extension
                - 简体中文: https://highcapable.github.io/KavaRef/zh-cn/library/kavaref-extension

                The `JavaClass.kt`, `JavaArrayClass.kt`, and `JavaMember.kt` provide:
                - Shorter APIs for creating array classes and resolving classes by name
                - Shorter APIs for converting `Type` to `Class` and getting generic superclass type arguments
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
                runCatching { Class.forName(name) }.getOrNull()
                Some::class.java
                Some::class.javaObjectType
                Some::class.java.isAssignableFrom(value.javaClass)
                !Some::class.java.isAssignableFrom(value.javaClass)
                Some::class.java.interfaces.isNotEmpty()
                !Some::class.java.interfaces.isEmpty()
                Some::class.java.interfaces.size > 0
                Some::class.java.interfaces.isEmpty()
                (type as ParameterizedType).rawType as Class<*>
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
                Modifier.isPublic(member.modifiers)
                member.isAccessible = true

                // After
                ArrayClass(type)
                name.toClass()
                name.toClass(loader, initialize)
                name.toClass<Some>()
                name.toClassOrNull<Some>()
                name.toClassOrNull()
                classOf<Some>()
                classOf<Some>(primitiveType = false)
                value.javaClass isSubclassOf Some::class.java
                value.javaClass isNotSubclassOf Some::class.java
                Some::class.java.hasInterfaces
                Some::class.java.hasInterfaces
                Some::class.java.hasInterfaces
                !Some::class.java.hasInterfaces
                type.toClass()
                javaClass.genericSuperclassTypeArguments()
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
                node.reportClassForNameOrNull(context)
                node.reportGenericSuperclassTypeArguments(context)
            }

            override fun visitBinaryExpression(node: UBinaryExpression) {
                node.reportHasInterfaces(context)
                node.reportIsAccessible(context)
            }

            override fun visitBinaryExpressionWithType(node: UBinaryExpressionWithType) {
                node.reportClassForNameAsType(context)
                node.reportRawTypeToClass(context)
            }

            override fun visitPrefixExpression(node: UPrefixExpression) {
                node.reportHasInterfaces(context)
                node.reportNotAssignableFrom(context)
            }
        })
    }

    private fun UCallExpression.reportArrayNewInstanceJavaClass(context: JavaContext) {
        if (methodName != JAVA_ARRAY_NEW_INSTANCE) return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_REFLECT_ARRAY_CLASS)) return
        if (valueArguments.size != 2) return
        if ((valueArguments[1] as? ULiteralExpression)?.value != 0) return

        val callExpression = (uastParent as? UQualifiedReferenceExpression)?.takeIf { it.selector == this } ?: this
        val parent = callExpression.uastParent as? UQualifiedReferenceExpression ?: return
        if (parent.receiver != callExpression) return
        if (parent.selector.asSourceString() != JAVA_CLASS_PROPERTY) return

        val arrayClass = "$ARRAY_CLASS(${valueArguments[0].asSourceString()})"
        // "ArrayClass" returns a different generic type, keep the original type where it can be affected.
        val replacement = if (parent.isTypeIndependent(context)) arrayClass else "$arrayClass as $JAVA_CLASS_ANY_TYPE"
        context.report(
            issue = ISSUE,
            scope = parent,
            location = context.getLocation(parent),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'ArrayClass'", replacement, ARRAY_CLASS_IMPORT)
        )
    }

    private fun UExpression.isTypeIndependent(context: JavaContext): Boolean {
        fun PsiType.acceptsAnyClass() = canonicalText == "$JAVA_CLASS<?>" || canonicalText == JAVA_OBJECT_CLASS

        resultOfLambda()?.let { return it.isPassedAsUnitFunction(context) }
        return when (val parent = uastParent) {
            is UQualifiedReferenceExpression -> parent.receiver == this
            is UBlockExpression -> true
            is UCallExpression -> {
                val method = parent.resolve() ?: return false
                context.evaluator.computeArgumentMapping(parent, method)[this]?.type?.acceptsAnyClass() == true
            }
            is UVariable -> parent.uastInitializer == this &&
                (parent.sourcePsi as? KtCallableDeclaration)?.typeReference != null && parent.type.acceptsAnyClass()
            else -> false
        }
    }

    private fun UCallExpression.reportClassForName(context: JavaContext) {
        if (methodName != JAVA_CLASS_FOR_NAME) return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return
        if (findParentCastExpression()?.containsElement(this) == true) return
        // The form wrapped by "runCatching { ... }.getOrNull()" is reported by reportClassForNameOrNull.
        if (enclosingRunCatchingGetOrNull() != null) return
        val replacement = classForNameReplacement(TO_CLASS) ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
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
            operationKind.name == "as?" -> TO_CLASS_OR_NULL
            else -> return
        }
        val importTarget = if (functionName == TO_CLASS) TO_CLASS_IMPORT else TO_CLASS_OR_NULL_IMPORT
        val typeText = typeReference?.sourcePsi?.text?.toClassTypeArgument() ?: return
        val replacement = call.classForNameReplacement(functionName, typeText) ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with '$functionName'", replacement, importTarget)
        )
    }

    private fun UCallExpression.classForNameReplacement(functionName: String, typeText: String? = null): String? {
        val className = valueArguments.firstOrNull()?.operandText() ?: return null
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
        if (!source.startsWith("Class<") || !source.endsWith(">")) return source
        return source.removePrefix("Class<").dropLast(1).trim()
    }

    private fun UQualifiedReferenceExpression.reportClassOf(context: JavaContext) {
        val selectorName = selector.asSourceString()
        if (selectorName !in setOf(JAVA_CLASS_LITERAL, JAVA_CLASS_OBJECT_TYPE, JAVA_CLASS_PRIMITIVE_TYPE)) return

        val classLiteral = receiver as? UClassLiteralExpression ?: return
        val receiverExpression = classLiteral.expression ?: return
        if (!receiverExpression.isTypeReceiver()) return

        val literalClass = (classLiteral.type as? PsiClassType)?.resolve()
        val typeText = classLiteral.classOfTypeText(literalClass) ?: return
        val isWrapperClass = literalClass?.qualifiedName in JAVA_WRAPPER_CLASSES
        val isKotlinPrimitive = isWrapperClass && classLiteral.receiverName() in KOTLIN_PRIMITIVE_TYPES &&
            classLiteral.resolveReceiverTypeAlias() == null
        val replacement = when (selectorName) {
            JAVA_CLASS_LITERAL ->
                if (isWrapperClass && !isKotlinPrimitive) "$CLASS_OF<$typeText>($PRIMITIVE_TYPE_PARAMETER = false)"
                else "$CLASS_OF<$typeText>()"
            JAVA_CLASS_PRIMITIVE_TYPE -> if (isWrapperClass) "$CLASS_OF<$typeText>()" else return
            JAVA_CLASS_OBJECT_TYPE -> "$CLASS_OF<$typeText>($PRIMITIVE_TYPE_PARAMETER = false)"
            else -> return
        }

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'classOf'", replacement, CLASS_OF_IMPORT)
        )
    }

    private fun UExpression.isTypeReceiver(): Boolean = when (this) {
        is UParenthesizedExpression -> expression.isTypeReceiver()
        is UThisExpression, is USuperExpression -> false
        is UCallExpression -> resolve() == null
        is UResolvable -> resolve().let { it !is PsiVariable && it !is PsiMethod }
        else -> false
    }

    private fun UClassLiteralExpression.receiverPsi() =
        (sourcePsi as? KtClassLiteralExpression)?.receiverExpression?.let { KtPsiUtil.safeDeparenthesize(it) }

    private fun UClassLiteralExpression.receiverNameReference() = when (val receiver = receiverPsi()) {
        is KtNameReferenceExpression -> receiver
        is KtDotQualifiedExpression -> receiver.selectorExpression as? KtNameReferenceExpression
        else -> null
    }

    private fun UClassLiteralExpression.receiverName() = receiverNameReference()?.getReferencedName()

    private fun UClassLiteralExpression.resolveReceiverTypeAlias() =
        receiverNameReference()?.references?.firstNotNullOfOrNull { it.resolve() } as? KtTypeAlias

    private fun UClassLiteralExpression.classOfTypeText(literalClass: PsiClass?): String? {
        val receiver = receiverPsi() ?: return null
        if (receiver is KtCallExpression) return receiver.text
        if (literalClass == null) return null
        val typeAlias = resolveReceiverTypeAlias()
        val typeParameterCount = typeAlias?.typeParameters?.size ?: literalClass.typeParameters.size
        if (typeAlias == null && typeParameterCount > 0 &&
            receiverName()?.removePrefix("Mutable") != literalClass.name
        ) return null
        if (typeParameterCount == 0) return receiver.text

        return "${receiver.text}<${List(typeParameterCount) { "*" }.joinToString()}>"
    }

    private fun UCallExpression.reportAssignableFrom(context: JavaContext) {
        val (target, receiverText, argumentText) = assignableFromTarget(context) ?: return
        // The negated form is reported by reportNotAssignableFrom.
        if (target.parentLogicalNot() != null) return
        val replacement = target.wrapInfixForParent("$argumentText $IS_SUBCLASS_OF $receiverText")

        context.report(
            issue = ISSUE,
            scope = target,
            location = context.getLocation(target),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'isSubclassOf'", replacement, IS_SUBCLASS_OF_IMPORT)
        )
    }

    private fun UPrefixExpression.reportNotAssignableFrom(context: JavaContext) {
        if (operator != UastPrefixOperator.LOGICAL_NOT) return
        val call = (operand.unwrapParentheses() as? UQualifiedReferenceExpression)?.selector as? UCallExpression ?: return
        val (_, receiverText, argumentText) = call.assignableFromTarget(context) ?: return
        val replacement = wrapInfixForParent("$argumentText $IS_NOT_SUBCLASS_OF $receiverText")

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'isNotSubclassOf'", replacement, IS_NOT_SUBCLASS_OF_IMPORT)
        )
    }

    private fun UCallExpression.assignableFromTarget(context: JavaContext): Triple<UQualifiedReferenceExpression, String, String>? {
        if (methodName != JAVA_CLASS_IS_ASSIGNABLE_FROM) return null
        val method = resolve() ?: return null
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return null
        val qualified = (uastParent as? UQualifiedReferenceExpression)?.takeIf { it.selector == this } ?: return null
        if (qualified.accessType.name == "?.") return null
        val argument = valueArguments.singleOrNull() ?: return null
        if (argument.isNullableType()) return null

        return Triple(qualified, qualified.receiver.operandText(), argument.operandText())
    }

    private fun UQualifiedReferenceExpression.reportHasInterfaces(context: JavaContext) {
        val call = selector as? UCallExpression ?: return
        if (call.valueArgumentCount != 0) return
        val receiverText = receiver.interfacesReceiverText(context) ?: return
        val replacement = when (call.methodName) {
            JAVA_COLLECTION_IS_NOT_EMPTY -> "$receiverText.$HAS_INTERFACES"
            // The negated form is reported by UPrefixExpression.reportHasInterfaces.
            JAVA_COLLECTION_IS_EMPTY -> if (parentLogicalNot() == null) "!$receiverText.$HAS_INTERFACES" else return
            else -> return
        }
        reportHasInterfaces(context, this, wrapForParent(replacement))
    }

    private fun UBinaryExpression.reportHasInterfaces(context: JavaContext) {
        val size = leftOperand as? UQualifiedReferenceExpression ?: return
        if (size.selector.asSourceString() != JAVA_COLLECTION_SIZE) return
        if ((rightOperand as? ULiteralExpression)?.value != 0) return
        val receiverText = size.receiver.interfacesReceiverText(context) ?: return
        val replacement = when (operator) {
            UastBinaryOperator.GREATER, UastBinaryOperator.NOT_EQUALS -> "$receiverText.$HAS_INTERFACES"
            UastBinaryOperator.EQUALS -> "!$receiverText.$HAS_INTERFACES"
            else -> return
        }
        reportHasInterfaces(context, this, wrapForParent(replacement))
    }

    private fun UPrefixExpression.reportHasInterfaces(context: JavaContext) {
        if (operator != UastPrefixOperator.LOGICAL_NOT) return
        val qualified = operand.unwrapParentheses() as? UQualifiedReferenceExpression ?: return
        val call = qualified.selector as? UCallExpression ?: return
        if (call.methodName != JAVA_COLLECTION_IS_EMPTY || call.valueArgumentCount != 0) return
        val receiverText = qualified.receiver.interfacesReceiverText(context) ?: return
        reportHasInterfaces(context, this, wrapForParent("$receiverText.$HAS_INTERFACES"))
    }

    private fun reportHasInterfaces(context: JavaContext, scope: UExpression, replacement: String) {
        context.report(
            issue = ISSUE,
            scope = scope,
            location = context.getLocation(scope),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'hasInterfaces'", replacement, HAS_INTERFACES_IMPORT)
        )
    }

    private fun UExpression.interfacesReceiverText(context: JavaContext): String? {
        val interfaces = this as? UQualifiedReferenceExpression ?: return null
        if (!interfaces.selector.isMethodOf(context, JAVA_CLASS, JAVA_CLASS_GET_INTERFACES)) return null
        return interfaces.receiver.operandText()
    }

    private fun UCallExpression.reportModifier(context: JavaContext) {
        val propertyName = methodName?.takeIf { it in MODIFIER_PROPERTIES } ?: return
        val method = resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_REFLECT_MODIFIER_CLASS)) return
        val argument = valueArguments.singleOrNull()?.unwrapParentheses() as? UQualifiedReferenceExpression ?: return
        if (argument.accessType.name == "?.") return
        if (argument.selector.asSourceString() != JAVA_MODIFIERS_PROPERTY) return
        // Only Class and Member have the modifier extensions.
        val receiverClass = (argument.receiver.getExpressionType() as? PsiClassType)?.resolve() ?: return
        if (receiverClass.qualifiedName != JAVA_CLASS &&
            !context.evaluator.implementsInterface(receiverClass, JAVA_REFLECT_MEMBER_CLASS, false)
        ) return
        val replacement = "${argument.receiver.operandText()}.$propertyName"

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with '$propertyName'", replacement, "$EXTENSION_PACKAGE_NAME.$propertyName")
        )
    }

    private fun UBinaryExpression.reportIsAccessible(context: JavaContext) {
        if (operator != UastBinaryOperator.ASSIGN) return
        if ((rightOperand as? ULiteralExpression)?.value != true) return
        val makeAccessible = leftOperand.makeAccessibleReplacement(context) ?: return
        // An assignment has no value, keep the result of the lambda as Unit if it may be used.
        val isUsedResult = resultOfLambda()?.isPassedAsUnitFunction(context) == false
        val replacement = if (isUsedResult) "$makeAccessible.let {}" else makeAccessible

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'makeAccessible'", replacement, MAKE_ACCESSIBLE_IMPORT)
        )
    }

    private fun UExpression.makeAccessibleReplacement(context: JavaContext): String? {
        val property = when (this) {
            is USimpleNameReferenceExpression -> this.takeIf { identifier == JAVA_ACCESSIBLE_PROPERTY }
            is UQualifiedReferenceExpression -> selector.takeIf { it.asSourceString() == JAVA_ACCESSIBLE_PROPERTY }
            else -> null
        } ?: return null
        val declaringClass = when (val resolved = (property as? UResolvable)?.resolve()) {
            is PsiMethod -> resolved.containingClass
            is PsiField -> resolved.containingClass
            else -> null
        } ?: return null
        if (!context.evaluator.extendsClass(declaringClass, JAVA_REFLECT_ACCESSIBLE_OBJECT_CLASS, false)) return null

        return when (this) {
            is UQualifiedReferenceExpression -> {
                val receiverClass = (receiver.getExpressionType() as? PsiClassType)?.resolve() ?: return null
                if (!context.evaluator.implementsInterface(receiverClass, JAVA_REFLECT_MEMBER_CLASS, false)) return null
                "${receiver.operandText()}.$MAKE_ACCESSIBLE()"
            }
            else -> "$MAKE_ACCESSIBLE()"
        }
    }

    private fun UQualifiedReferenceExpression.reportClassForNameOrNull(context: JavaContext) {
        val call = selector as? UCallExpression ?: return
        if (call.methodName != KOTLIN_RESULT_GET_OR_NULL || call.valueArgumentCount != 0) return
        val runCatching = receiver as? UCallExpression ?: return
        if (runCatching.methodName != KOTLIN_RUN_CATCHING) return
        val lambda = runCatching.valueArguments.lastOrNull() as? ULambdaExpression ?: return
        val forName = lambda.singleExpression()?.findClassForNameCall() ?: return
        if (forName.methodName != JAVA_CLASS_FOR_NAME) return
        val method = forName.resolve() ?: return
        if (!context.evaluator.isMemberInClass(method, JAVA_CLASS)) return
        val replacement = forName.classForNameReplacement(TO_CLASS_OR_NULL)?.let { wrapForParent(it) } ?: return

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'toClassOrNull'", replacement, TO_CLASS_OR_NULL_IMPORT)
        )
    }

    private fun UBinaryExpressionWithType.reportRawTypeToClass(context: JavaContext) {
        if (operationKind != UastBinaryExpressionWithTypeKind.TypeCast.INSTANCE) return
        if (type.canonicalText != "$JAVA_CLASS<?>") return
        val rawType = operand.unwrapParentheses() as? UQualifiedReferenceExpression ?: return
        if (!rawType.selector.isMethodOf(context, JAVA_REFLECT_PARAMETERIZED_TYPE_CLASS, JAVA_PARAMETERIZED_TYPE_GET_RAW_TYPE)) return
        val typeText = rawType.receiver.parameterizedTypeCastOperand()?.operandText() ?: return
        val replacement = wrapForParent("$typeText.$TO_CLASS()")

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix("Replace with 'toClass'", replacement, TO_CLASS_IMPORT)
        )
    }

    private fun UQualifiedReferenceExpression.reportGenericSuperclassTypeArguments(context: JavaContext) {
        if (!selector.isMethodOf(context, JAVA_REFLECT_PARAMETERIZED_TYPE_CLASS, JAVA_PARAMETERIZED_TYPE_GET_ACTUAL_TYPE_ARGUMENTS)) return
        val genericSuperclass = receiver.parameterizedTypeCastOperand()?.unwrapParentheses() as? UQualifiedReferenceExpression ?: return
        if (!genericSuperclass.selector.isMethodOf(context, JAVA_CLASS, JAVA_CLASS_GET_GENERIC_SUPERCLASS)) return
        val replacement = wrapForParent("${genericSuperclass.receiver.operandText()}.$GENERIC_SUPERCLASS_TYPE_ARGUMENTS()")

        context.report(
            issue = ISSUE,
            scope = this,
            location = context.getLocation(this),
            message = "Can be replaced with `$replacement`",
            quickfixData = buildReplaceFix(
                "Replace with '$GENERIC_SUPERCLASS_TYPE_ARGUMENTS'", replacement, GENERIC_SUPERCLASS_TYPE_ARGUMENTS_IMPORT
            )
        )
    }

    private fun UExpression.parameterizedTypeCastOperand(): UExpression? {
        val cast = unwrapParentheses() as? UBinaryExpressionWithType ?: return null
        if (cast.operationKind != UastBinaryExpressionWithTypeKind.TypeCast.INSTANCE) return null
        if (cast.type.canonicalText != JAVA_REFLECT_PARAMETERIZED_TYPE_CLASS) return null

        return cast.operand
    }

    private fun UCallExpression.enclosingRunCatchingGetOrNull(): UQualifiedReferenceExpression? {
        val lambda = generateSequence(uastParent) { it.uastParent }.firstOrNull { it is ULambdaExpression } as? ULambdaExpression ?: return null
        if (lambda.singleExpression()?.findClassForNameCall() != this) return null
        val runCatching = lambda.uastParent as? UCallExpression ?: return null
        if (runCatching.methodName != KOTLIN_RUN_CATCHING) return null
        val qualified = runCatching.uastParent as? UQualifiedReferenceExpression ?: return null
        val getOrNull = qualified.selector as? UCallExpression ?: return null

        return qualified.takeIf { qualified.receiver == runCatching && getOrNull.methodName == KOTLIN_RESULT_GET_OR_NULL }
    }
}