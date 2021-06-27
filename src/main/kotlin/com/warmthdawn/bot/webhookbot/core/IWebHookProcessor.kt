package com.warmthdawn.bot.webhookbot.core

import io.ktor.request.*

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
interface IWebHookProcessor {
    fun parse(payloadString: String): String
    fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean
}