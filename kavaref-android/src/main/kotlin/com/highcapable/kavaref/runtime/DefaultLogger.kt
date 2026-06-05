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

package com.highcapable.kavaref.runtime

import android.util.Log
import com.highcapable.kavaref.android.generated.KavaRefProperties

/**
 * Default logger implementation for `KavaRef`.
 */
internal class DefaultLogger : KavaRefRuntime.Logger {

    companion object {

        /**
         * Initialize the logger.
         * @param value the log level to set for the logger.
         */
        fun init(value: KavaRefRuntime.LogLevel) = Unit
    }

    override val tag = KavaRefProperties.PROJECT_NAME

    override fun debug(msg: Any?, throwable: Throwable?) {
        Log.d(tag, msg.toString(), throwable)
    }

    override fun info(msg: Any?, throwable: Throwable?) {
        Log.i(tag, msg.toString(), throwable)
    }

    override fun warn(msg: Any?, throwable: Throwable?) {
        Log.w(tag, msg.toString(), throwable)
    }

    override fun error(msg: Any?, throwable: Throwable?) {
        Log.e(tag, msg.toString(), throwable)
    }
}