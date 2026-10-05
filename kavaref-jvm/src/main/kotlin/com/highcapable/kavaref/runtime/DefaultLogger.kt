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

import com.highcapable.kavaref.generated.KavaRefProperties

/**
 * Default logger implementation for `KavaRef`.
 *
 * Prints logs to [System.err], the log level is only controlled by `KavaRef.logLevel`.
 */
internal class DefaultLogger : KavaRefRuntime.Logger {

    override val tag = KavaRefProperties.PROJECT_NAME

    override fun debug(msg: Any?, throwable: Throwable?) = print(KavaRefRuntime.LogLevel.DEBUG, msg, throwable)
    override fun info(msg: Any?, throwable: Throwable?) = print(KavaRefRuntime.LogLevel.INFO, msg, throwable)
    override fun warn(msg: Any?, throwable: Throwable?) = print(KavaRefRuntime.LogLevel.WARN, msg, throwable)
    override fun error(msg: Any?, throwable: Throwable?) = print(KavaRefRuntime.LogLevel.ERROR, msg, throwable)

    private fun print(level: KavaRefRuntime.LogLevel, msg: Any?, throwable: Throwable?) {
        synchronized(System.err) {
            System.err.println("[$tag] ${level.levelName.uppercase()} $msg")
            throwable?.printStackTrace(System.err)
        }
    }
}