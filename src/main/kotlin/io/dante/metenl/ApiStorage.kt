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

    private val _authKeyFile = File("auth.key")
    private val _nearestDCFile = File("dc.save")

    override fun deleteAuthKey() {
        _authKeyFile.delete()
    }

    override fun deleteDc() {
        _nearestDCFile.delete()
    }

    override fun loadAuthKey(): AuthKey? {
       if (!_authKeyFile.exists()) {
           return null
       }

        return AuthKey(key = _authKeyFile.readBytes())
    }

    override fun loadDc(): DataCenter? {
        if (!_nearestDCFile.exists()) {
            return null
        }

        val dcInfo = _nearestDCFile.readText()
            .split(regex = ":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        return DataCenter(ip = dcInfo[0], port = Integer.parseInt(dcInfo[1]))
    }

    override fun loadSession(): MTSession? {
        return null
    }

    override fun saveAuthKey(authKey: AuthKey) {
        _authKeyFile.writeBytes(array = authKey.key)
    }

    override fun saveDc(dataCenter: DataCenter) {
        _nearestDCFile.writeText(text = dataCenter.toString())
    }

    override fun saveSession(session: MTSession?) {
    }
}