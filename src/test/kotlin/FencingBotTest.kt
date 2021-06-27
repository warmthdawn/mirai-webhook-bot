import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.io.charsets.Charset
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.mamoe.mirai.Bot

/**
 *
 * @author WarmthDawn
 * @since 2021-05-21
 */
internal class FencingBotTest {

}

/**
 * Receive the request as String.
 * If there is no Content-Type in the HTTP header specified use ISO_8859_1 as default charset, see https://www.w3.org/International/articles/http-charset/index#charset.
 * But use UTF-8 as default charset for application/json, see https://tools.ietf.org/html/rfc4627#section-3
 */
private suspend fun ApplicationCall.receiveTextWithCorrectEncoding(): String {
    fun ContentType.defaultCharset(): Charset = when (this) {
        ContentType.Application.Json -> Charsets.UTF_8
        else -> Charsets.ISO_8859_1
    }

    val contentType = request.contentType()
    val suitableCharset = contentType.charset() ?: contentType.defaultCharset()
    return receiveStream().bufferedReader(charset = suitableCharset).readText()
}

fun main() {
    embeddedServer(CIO, port = 9874) {
        routing {
            post("/webhooks/push") {
                val payload = Json
                    .parseToJsonElement(call.receiveTextWithCorrectEncoding())
                    .jsonObject

                val user = payload["pusher"]!!.jsonObject["name"]!!.jsonPrimitive.content
                val repository = payload["repository"]!!.jsonObject["name"]!!.jsonPrimitive.content

                val commits = payload["commits"]!!.jsonArray.map {
                    it.jsonObject["message"]!!.jsonPrimitive.content
                }

                val builder = StringBuilder()
                builder.append(user).append("在")
                    .append(repository).append("提交了")
                    .append(commits.size).append("个新的commit")
                    .appendLine()
                commits.forEach { builder.appendLine(it) }
                GlobalScope.launch {
                    Bot.getInstance(2397577752).getGroup(261293822)?.sendMessage(builder.toString())
                }



                call.respond(HttpStatusCode.Accepted)
            }
        }
    }.start(true)
}