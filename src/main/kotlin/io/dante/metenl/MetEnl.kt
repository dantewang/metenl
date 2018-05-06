package io.dante.metenl

import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.api.TelegramApp
import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import com.ufoscout.properlty.Properlty
import java.io.File
import java.util.Scanner
import kotlin.system.exitProcess

/**
 * Created by dante on 2017/7/18.
 */
object MetEnl {
    fun start() {
        if (!File("metenl.conf").exists()) {
            println(
                """
                Configuration file not found.
                Please create a file named \"metenl.conf\" with following properties:
                   # required
                   api.id=
                   api.hash=
                   met.username=
                   # optional
                   device.model=
                   system.version=
                """.trimIndent())

            exitProcess(0)
        }

        val properties = Properlty.builder().add("./metenl.conf").build()

        val apiId: Int = properties.getInt("api.id")!!
        val apiHash = properties["api.hash"]!!
        val username = properties["met.username"]!!
        val deviceModel = properties["device.model", "PowerEdge R740"]
        val systemVersion = properties["system.version", "Red Hat Enterprise Linux 7.3"]

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

    private fun auth(client : TelegramClient) {
        val scanner = Scanner(System.`in`)

        println("Phone Number: ")
        val phoneNumber = scanner.nextLine()
        val sentCode = client.authSendCode(allowFlashcall = false, phoneNumber = phoneNumber, currentNumber = true)

        var auth : TLAuthorization

        auth = try {
            println("Authentication Code: ")
            val code = scanner.nextLine()
            client.authSignIn(phoneNumber, sentCode.phoneCodeHash, code)
        } catch (ree : RpcErrorException) {
            if ("SESSION_PASSWORD_NEEDED" == ree.tag) {
                println("2FA Password: ")
                val password = scanner.nextLine()
                client.authCheckPassword(password)
            } else {
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