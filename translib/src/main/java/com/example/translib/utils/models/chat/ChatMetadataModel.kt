package com.example.translator.models.chat

data class ChatMetadataModel(
    var groupMsgSenderNameOrNumber: String? = null,
    var groupMsgSenderUnSavedName: String? = null,// for whatsapp
    var messageDate: String? = null
)