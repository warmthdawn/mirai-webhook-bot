package com.warmthdawn.bot.webhookbot.core

import com.warmthdawn.bot.webhookbot.plugin.PluginConfig
import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.Message
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */
class WebhookServer(
    val port: Int,
    ctx: CoroutineContext = Dispatchers.Main,
    private val handler: IServerEventHandler = object : IServerEventHandler {},
) : CoroutineScope {
    companion object {
        private val logger =  PluginMain.logger
    }
    var secretKey: String = ""
    var started = false
        private set
    override val coroutineContext: CoroutineContext = SupervisorJob(ctx.job) + Dispatchers.IO




    private val app = embeddedServer(CIO, port) {
        routing {

            post("/webhooks/general/{name}") {
                handler.onRequest(call)
                val name = call.parameters["name"]
                val hook = PluginConfig.hooks[name]

                if (hook == null) {
                    call.respond(HttpStatusCode.NotFound)
                    logger.warning("hook$name not found")
                    return@post
                }
                val processor = hook.type.processor

                val payload = call.receiveStream().bufferedReader(charset = Charsets.UTF_8).readText()

                if (secretKey.isEmpty() || !processor.validate(payload, secretKey, call.request)) {
                    call.respond(HttpStatusCode.Forbidden)
                    logger.warning("hook $name 校验出错")
                    return@post
                }
                val result: Message?
                try {
                    result = processor.process(payload, call.request)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e)
                    logger.error("解析webhook失败", e)
                    return@post
                }

                if(result == null) {
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }

                if (handler.onHookResult(name!!, result)) {
                    call.respond(HttpStatusCode.Accepted)
                } else {
                    call.respond(HttpStatusCode.ServiceUnavailable)
                }
            }
        }
    }

    suspend fun restart() {
        stop()
        start()
    }

    suspend fun start() = coroutineScope {
        launch {
            handler.onServerStarting()
            app.start()
            handler.onServerStarted()
            started = true
        }
    }

    suspend fun stop() = coroutineScope {
        launch {
            handler.onServerStopping()
            app.stop(2000, 3000)
            handler.onServerStopped()
            started = false
        }
    }
}

interface IServerEventHandler {
    suspend fun onServerStarting() {}
    suspend fun onServerStarted() {}
    suspend fun onServerStopping() {}
    suspend fun onServerStopped() {}
    suspend fun onRequest(call: ApplicationCall) {}
    suspend fun onHookResult(hookName: String, result: Any): Boolean = false
}
