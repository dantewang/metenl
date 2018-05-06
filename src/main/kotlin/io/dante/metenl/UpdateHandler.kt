package io.dante.metenl

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.UpdateCallback
import com.github.badoualy.telegram.api.utils.getFromId
import com.github.badoualy.telegram.api.utils.getMessageOrEmpty
import com.github.badoualy.telegram.api.utils.getToAsPeer
import com.github.badoualy.telegram.tl.api.TLChannel
import com.github.badoualy.telegram.tl.api.TLInputPeerChannel
import com.github.badoualy.telegram.tl.api.TLPeerChannel
import com.github.badoualy.telegram.tl.api.TLUpdateNewChannelMessage
import com.github.badoualy.telegram.tl.api.TLUpdateShort
import com.github.badoualy.telegram.tl.api.TLUpdateShortChatMessage
import com.github.badoualy.telegram.tl.api.TLUpdateShortMessage
import com.github.badoualy.telegram.tl.api.TLUpdateShortSentMessage
import com.github.badoualy.telegram.tl.api.TLUpdates
import com.github.badoualy.telegram.tl.api.TLUpdatesCombined
import java.util.Random

/**
 * Created by dante on 2017/7/18.
 */
class UpdateHandler(private val username : String) : UpdateCallback {

    private val _channels : MutableMap<Int, TLChannel> = HashMap()
    private val _metUserNames : MutableSet<String> = HashSet()
    private var _seq = 0

    override fun onShortChatMessage(client: TelegramClient, message: TLUpdateShortChatMessage) {
        println("onShortChatMessage")
    }

    override fun onShortMessage(client: TelegramClient, message: TLUpdateShortMessage) {
        println("onShortMessage")
    }

    override fun onShortSentMessage(client: TelegramClient, message: TLUpdateShortSentMessage) {
        println("onShortSentMessage")
    }

    override fun onUpdateShort(client: TelegramClient, update: TLUpdateShort) {
        println("onUpdateShort")
    }

    override fun onUpdateTooLong(client: TelegramClient) {
        println("onUpdateTooLong")
    }

    override fun onUpdates(client: TelegramClient, updates: TLUpdates) {
        println("################################ ${updates.seq} ###### ${updates.date}")

        updates.chats.filterIsInstance<TLChannel>().forEach {
            if (_channels.putIfAbsent(it.id, it) == null) {
                println("onUpdates, channel: ${it.title} / ${it.id}")
            }
        }

        updates.updates.filterIsInstance<TLUpdateNewChannelMessage>().map {
            it.message
        }.filter {
            it.getMessageOrEmpty() != null
        }.filter {
            it.getMessageOrEmpty().contains(other = "/met @$username", ignoreCase = true)
        }.forEach {
            val username = updates.users.filter { user -> user.id == it.getFromId() }[0].asUser.username

            if (!_metUserNames.add(username)) return

            val channel = _channels[(it.getToAsPeer() as TLPeerChannel).channelId] ?: return

            val inputPeer = TLInputPeerChannel(channel.id, channel.accessHash)

            client.messagesSendMessage(
                peer = inputPeer, message = "/met @$username", randomId = Math.abs(Random().nextLong()))
        }

        _seq = updates.seq
    }

    override fun onUpdatesCombined(client: TelegramClient, updates: TLUpdatesCombined) {
        println("onUpdatesCombined")
    }
}