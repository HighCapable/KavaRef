/*
 * KavaRef - A modernizing Java reflection with Kotlin.
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
 * This file is created by fankes on 2026/6/4.
 */
package com.highcapable.kavaref.demo.ui

import android.os.Bundle
import android.view.Gravity
import androidx.core.view.setPadding
import com.highcapable.betterandroid.ui.component.activity.AppViewsActivity
import com.highcapable.betterandroid.ui.extension.view.padding
import com.highcapable.hikage.core.attribute.android
import com.highcapable.hikage.core.layout.LayoutParams
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.runtime.mutableStateOf
import com.highcapable.hikage.runtime.setState
import com.highcapable.hikage.widget.android.widget.Button
import com.highcapable.hikage.widget.android.widget.FrameLayout
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.kavaref.demo.R
import com.highcapable.kavaref.demo.runner.JavaRunner
import com.highcapable.kavaref.demo.runner.Runner

class MainActivity : AppViewsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView {
            val textResultState = mutableStateOf(stringResource(R.string.result_template))
            var textResult by textResultState

            LinearLayout(
                lparams = LayoutParams(matchParent = true),
                attrs = {
                    android {
                        set("background", "@color/background")
                        set("orientation", "vertical")
                    }
                }
            ) {
                TextView(
                    lparams = LayoutParams(widthMatchParent = true)
                ) {
                    text = stringResource(R.string.app_name)
                    textSize = 20f
                    setPadding(20.dp)
                }
                FrameLayout(
                    lparams = LayoutParams(widthMatchParent = true, height = 0.dp) {
                        weight = 1f
                    }
                ) {
                    TextView(
                        lparams = LayoutParams {
                            gravity = Gravity.CENTER
                        }
                    ) {
                        setState(textResultState) {
                            text = it
                        }
                        textSize = 15f
                    }
                }

                fun runDemo() {
                    val result = Runner.run()
                    textResult = "${result.first}\n${result.second}"
                }

                fun runJavaDemo() {
                    val result = JavaRunner.run()
                    textResult = result
                }

                LinearLayout(
                    lparams = LayoutParams(widthMatchParent = true) {
                        gravity = Gravity.CENTER
                    },
                    init = {
                        padding.horizontal.set(20.dp)
                        padding.bottom = 20.dp
                    }
                ) {
                    Button(
                        lparams = LayoutParams(width = 0.dp) {
                            weight = 1f
                            marginEnd = 5.dp
                        }
                    ) {
                        text = stringResource(R.string.run_demo)
                        setOnClickListener {
                            runDemo()
                        }
                    }
                    Button(
                        lparams = LayoutParams(width = 0.dp) {
                            weight = 1f
                            marginStart = 5.dp
                        }
                    ) {
                        text = stringResource(R.string.run_java_demo)
                        setOnClickListener {
                            runJavaDemo()
                        }
                    }
                }
            }
        }
    }
}