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
 * This file is created by fankes on 2026/6/4.
 */
@file:Suppress("SetTextI18n")

package com.highcapable.kavaref.demo.ui

import android.os.Bundle
import com.highcapable.betterandroid.ui.component.activity.AppBindingActivity
import com.highcapable.kavaref.demo.databinding.ActivityMainBinding
import com.highcapable.kavaref.demo.runner.JavaRunner
import com.highcapable.kavaref.demo.runner.Runner

class MainActivity : AppBindingActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.buttonRunDemo.setOnClickListener {
            runDemo()
        }
        binding.buttonRunJavaDemo.setOnClickListener {
            runJavaDemo()
        }
    }

    private fun runDemo() {
        val result = Runner.run()
        binding.textResult.text = "${result.first}\n${result.second}"
    }

    private fun runJavaDemo() {
        val result = JavaRunner.run()
        binding.textResult.text = result
    }
}