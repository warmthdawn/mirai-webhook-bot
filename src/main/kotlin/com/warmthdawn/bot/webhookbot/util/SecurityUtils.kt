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
fun verifySignature(sentSignature: String?, data: String, sharedSecret: String = ""): Boolean {

    if (sharedSecret.isEmpty()) { // If shared secret is not set, receive all events
        return true
    }
    if (sentSignature == null) {    //If shared secret is set, must have a signature on all events received
        return false
    }

    var isValid = false
    try {
        //If shared secret is set and a signature is passed, validate that they match
        logger.debug(
            " verifySignature sharedSecret: " + sharedSecret +
                    ", event string length: " + data.length.toString() +
                    ", event string: " + data
        )

        //Get a HmacSHA1 instance
        val hmac: Mac = Mac.getInstance("HmacSHA256")
        //Initialize with the shared secret key
        hmac.init(SecretKeySpec(sharedSecret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        //calculate the signature using the event data
        val calculatedSignature: String =
            hmac.doFinal(data.toByteArray(StandardCharsets.UTF_8)).toHex()
        logger.debug("calculatedSignature: $calculatedSignature")

        //The received signature and the calculated signatures must be equal to validate that the data is accureate.
        if ((sentSignature == calculatedSignature)) {
            isValid = true
        } else {
            logger.warning("isValid = false, sentSignature: $sentSignature, calculatedSignature: $calculatedSignature")
        }

    } catch (e: Exception) {
        logger.error("verifySignature had the following error: ", e)
    }
    return isValid
}