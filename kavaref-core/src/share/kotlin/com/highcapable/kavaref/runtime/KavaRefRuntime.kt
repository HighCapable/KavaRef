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
 * This file is created by fankes on 2025/5/19.
 */
@file:Suppress("unused")

package com.highcapable.kavaref.runtime

import com.highcapable.kavaref.generated.KavaRefProperties

/**
 * Runtime class for `KavaRef` logging.
 */
object KavaRefRuntime {

    private const val TAG = KavaRefProperties.PROJECT_NAME

    private var logger: Logger = DefaultLogger()

    /**
     * Log levels for `KavaRef`.
     * @param levelName the name of the log level.
     */
    enum class LogLevel(val levelName: String) {
        /** DEBUG */
        DEBUG("debug"),

        /** INFO */
        INFO("info"),

        /** WARN */
        WARN("warn"),

        /** ERROR */
        ERROR("error"),

        /** OFF (Turn off logging) */
        OFF("off")
    }

    /**
     * Logger interface for `KavaRef`.
     *
     * You can implement this interface to create your own logger and set it to `KavaRef`.
     */
    interface Logger {

        /** Logger tag. */
        val tag: String

        /**
         * Log a debug message.
         * @param msg the message to log.
         * @param throwable an optional throwable to log.
         */
        fun debug(msg: Any?, throwable: Throwable? = null)

        /**
         * Log an info message.
         * @param msg the message to log.
         * @param throwable an optional throwable to log.
         */
        fun info(msg: Any?, throwable: Throwable? = null)

        /**
         * Log a warning message.
         * @param msg the message to log.
         * @param throwable an optional throwable to log.
         */
        fun warn(msg: Any?, throwable: Throwable? = null)

        /**
         * Log an error message.
         * @param msg the message to log.
         * @param throwable an optional throwable to log.
         */
        fun error(msg: Any?, throwable: Throwable? = null)
    }

    /**
     * Get or set the log level for `KavaRef`.
     *
     * Use `KavaRef` to control it.
     * @return [LogLevel]
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    internal var logLevel = LogLevel.WARN
        set(value) {
            DefaultLogger.init(value)
            field = value
        }

    /**
     * Set the logger for `KavaRef`.
     *
     * Use `KavaRef` to control it.
     * @param logger the logger to be set.
     */
    @JvmSynthetic
    internal fun setLogger(logger: Logger) {
        this.logger = logger
    }

    init {
        // Initialize the log level to WARN if not set.
        logLevel = logLevel
    }

    /**
     * DEBUG
     */
    @JvmSynthetic
    internal fun debug(msg: Any?, throwable: Throwable? = null) {
        if (shouldLog(LogLevel.DEBUG)) logger.debug(msg, throwable)
    }

    /**
     * INFO
     */
    @JvmSynthetic
    internal fun info(msg: Any?, throwable: Throwable? = null) {
        if (shouldLog(LogLevel.INFO)) logger.info(msg, throwable)
    }

    /**
     * WARN
     */
    @JvmSynthetic
    internal fun warn(msg: Any?, throwable: Throwable? = null) {
        if (shouldLog(LogLevel.WARN)) logger.warn(msg, throwable)
    }

    /**
     * ERROR
     */
    @JvmSynthetic
    internal fun error(msg: Any?, throwable: Throwable? = null) {
        if (shouldLog(LogLevel.ERROR)) logger.error(msg, throwable)
    }

    private fun shouldLog(level: LogLevel) = logLevel.ordinal <= level.ordinal
}