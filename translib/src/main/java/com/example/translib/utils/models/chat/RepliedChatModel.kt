package com.example.translib.utils.models.chat

data class RepliedChatModel(
    var repliedChatNameOrNumber: String? = null,
    var repliedChatUnsavedName: String? = null,
    var repliedChatMsg: String? = null
)