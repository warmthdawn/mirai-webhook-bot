package com.warmthdawn.bot.webhookbot.util

import com.warmthdawn.bot.webhookbot.plugin.PluginMain
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 *
 * @author WarmthDawn
 * @since 2021-06-27
 */

/*
     * Verify the signature and the event data.
     *
     * The shared secret and the HMAC signature is documented in the subscription REST API with swagger and can be viewed at
     * [http://sip-subscription-integration.mybluemix.net/swagger-ui.html].
     */

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

private val logger = PluginMain.logger
fun calcSignature(data: String, sharedSecret: String): ByteArray? {
    try {
        //Get a HmacSHA256 instance
        val hmac: Mac = Mac.getInstance("HmacSHA256")
        //Initialize with the shared secret key
        hmac.init(SecretKeySpec(sharedSecret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        //calculate the signature using the event data
        return hmac.doFinal(data.toByteArray(StandardCharsets.UTF_8))

    } catch (e: Exception) {
        logger.error("verifySignature had the following error: ", e)
    }
    return null
}