package com.example.translib.utils.models.chat

import com.example.translator.models.chat.ChatMetadataModel

data class ChatModel(
    var msg: String? = null,
    var msgStatus: String? = null,
    var msgTime: String? = null,
    var msgType: MessageType = MessageType.TEXT,
    var isSent: Boolean = false,
    var chatMetadataModel: ChatMetadataModel? = null,
    var linkPreviewModel: LinkPreviewModel? = null,
    var repliedChatModel: RepliedChatModel? = null,
    var msgId: Long
) {


    override fun equals(other: Any?): Boolean {
        other as ChatModel
        return msgId == other.msgId && msg?.equals(other.msg) == true && msgTime?.equals(other.msgTime) == true && linkPreviewModel == other.linkPreviewModel
//        return msgId == other.msgId && msg?.equals(other.msg) == true && msgTime?.equals(other.msgTime) == true
    }


    override fun hashCode(): Int {
        var result = msg?.hashCode() ?: 0
        result = 31 * result + (msgTime?.hashCode() ?: 0)
        result = 31 * result + (linkPreviewModel?.hashCode() ?: 0)
        result = 31 * result + msgId.hashCode()
        return result
    }


//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as ChatModel
//
//        if (msg != other.msg) return false
//        if (msgTime != other.msgTime) return false
//        if (msgType != other.msgType) return false
//        if (isSent != other.isSent) return false
//        if (chatMetadataModel != other.chatMetadataModel) return false
//        if (linkPreviewModel != other.linkPreviewModel) return false
//        if (repliedChatModel != other.repliedChatModel) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = msg?.hashCode() ?: 0
//        result = 31 * result + (msgTime?.hashCode() ?: 0)
//        result = 31 * result + msgType.hashCode()
//        result = 31 * result + isSent.hashCode()
//        result = 31 * result + (chatMetadataModel?.hashCode() ?: 0)
//        result = 31 * result + (linkPreviewModel?.hashCode() ?: 0)
//        result = 31 * result + (repliedChatModel?.hashCode() ?: 0)
//        return result
//    }
}