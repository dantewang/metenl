package io.dante.metenl

import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.api.TelegramApp
import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import java.io.File
import java.util.Scanner

/**
 * Created by dante on 2017/7/18.
 */
object MetEnl {
    fun start() {
        val apiId = 0
        val apiHash = ""
        val username = "coolcfan"
        val deviceModel = "VM"
        val systemVersion = "Ubuntu 16.10"

        val application = TelegramApp(
            apiId = apiId, apiHash = apiHash, deviceModel = deviceModel, systemVersion = systemVersion,
            appVersion = "1.0", langCode = "en")

        val client = Kotlogram.getDefaultClient(
            application = application, apiStorage = ApiStorage(), updateCallback = UpdateHandler(username))

        val userAuthenticated = File("user_authenticated")

        if (!userAuthenticated.exists()) {
            auth(client)

            userAuthenticated.createNewFile()
        }
    }

    fun auth(client : TelegramClient) {
        val scanner = Scanner(System.`in`)

        println("Phone Number: ")
        val phoneNumber = scanner.nextLine()
        val sentCode = client.authSendCode(allowFlashcall = false, phoneNumber = phoneNumber, currentNumber = true)

        var auth : TLAuthorization

        try {
            println("Authentication Code: ")
            val code = scanner.nextLine()
            auth = client.authSignIn(phoneNumber, sentCode.phoneCodeHash, code)
        }
        catch (ree : RpcErrorException) {
            if ("SESSION_PASSWORD_NEEDED" == ree.tag) {
                println("2FA Password: ")
                val password = scanner.nextLine()
                auth = client.authCheckPassword(password)
            }
            else {
                throw ree
            }
        }

        val user = auth.user.asUser
        println("You are signed in as ${user.username}")
    }
}

fun main(args: Array<String>) {
    MetEnl.start()
}