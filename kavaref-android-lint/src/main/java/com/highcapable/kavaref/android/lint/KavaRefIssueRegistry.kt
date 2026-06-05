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
@file:Suppress("unused")

package com.highcapable.kavaref.android.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.highcapable.kavaref.android.lint.detector.ExecutableConditionDetector
import com.highcapable.kavaref.android.lint.detector.ExtensionUsageDetector
import com.highcapable.kavaref.generated.KavaRefProperties

class KavaRefIssueRegistry : IssueRegistry() {

    override val issues get() = listOf(
        ExecutableConditionDetector.ISSUE,
        ExtensionUsageDetector.ISSUE
    )

    override val minApi = KavaRefProperties.PROJECT_KAVAREF_ANDROID_LINT_MIN_API
    override val api = CURRENT_API
    override val vendor = Vendor(
        vendorName = KavaRefProperties.PROJECT_NAME,
        identifier = KavaRefProperties.PROJECT_KAVAREF_ANDROID_LINT_IDENTIFIER,
        feedbackUrl = "${KavaRefProperties.PROJECT_URL}/issues",
        contact = KavaRefProperties.PROJECT_URL
    )
}