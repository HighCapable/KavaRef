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
import org.slf4j.LoggerFactory

/**
 * Default logger implementation for `KavaRef`.
 */
internal class DefaultLogger : KavaRefRuntime.Logger {

    companion object {

        /**
         * Initialize the logger.
         * @param value the log level to set for the logger.
         */
        fun init(value: KavaRefRuntime.LogLevel) {
            // Enable level for SLF4J.
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", value.levelName)
        }
    }

    private val slf4jLogger by lazy { LoggerFactory.getLogger(tag) }

    override val tag = KavaRefProperties.PROJECT_NAME

    override fun debug(msg: Any?, throwable: Throwable?) {
        slf4jLogger.debug(msg.toString(), throwable)
    }

    override fun info(msg: Any?, throwable: Throwable?) {
        slf4jLogger.info(msg.toString(), throwable)
    }

    override fun warn(msg: Any?, throwable: Throwable?) {
        slf4jLogger.warn(msg.toString(), throwable)
    }

    override fun error(msg: Any?, throwable: Throwable?) {
        slf4jLogger.error(msg.toString(), throwable)
    }
}