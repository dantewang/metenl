package io.dante.metenl

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import java.io.File

/**
 * Created by dante on 2017/7/18.
 */
class ApiStorage : TelegramApiStorage {

    val AUTH_KEY_FILE = File("auth.key")
    val NEAREST_DC_FILE = File("dc.save")

    override fun deleteAuthKey() {
        AUTH_KEY_FILE.delete()
    }

    override fun deleteDc() {
        NEAREST_DC_FILE.delete()
    }

    override fun loadAuthKey(): AuthKey? {
       if (!AUTH_KEY_FILE.exists()) {
           return null
       }

        return AuthKey(key = AUTH_KEY_FILE.readBytes())
    }

    override fun loadDc(): DataCenter? {
        if (!NEAREST_DC_FILE.exists()) {
            return null
        }

        val dcInfo = NEAREST_DC_FILE.readText()
            .split(regex = ":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        return DataCenter(ip = dcInfo[0], port = Integer.parseInt(dcInfo[1]))
    }

    override fun loadSession(): MTSession? {
        return null
    }

    override fun saveAuthKey(authKey: AuthKey) {
        AUTH_KEY_FILE.writeBytes(array = authKey.key)
    }

    override fun saveDc(dataCenter: DataCenter) {
        NEAREST_DC_FILE.writeText(text = dataCenter.toString())
    }

    override fun saveSession(session: MTSession?) {
    }
}