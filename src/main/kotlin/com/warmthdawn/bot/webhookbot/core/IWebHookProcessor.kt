package com.warmthdawn.bot.webhookbot.core

import io.ktor.request.*
import net.mamoe.mirai.message.data.Message

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
interface IWebHookProcessor {


    fun process(payloadString: String, request: ApplicationRequest): Message?
    fun validate(payload: String, secret: String, request: ApplicationRequest): Boolean
}