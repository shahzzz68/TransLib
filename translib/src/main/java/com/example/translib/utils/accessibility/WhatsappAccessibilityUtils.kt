package com.example.translib.utils.accessibility

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.example.translator.models.chat.ChatMetadataModel
import com.example.translib.utils.models.chat.ChatModel
import com.example.translib.utils.models.chat.RepliedChatModel
import com.example.translib.utils.Constants
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.conversationList
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractViewFromID

object WAaccessibilityUtils {

//    var whatsappConversation = mutableListOf<ChatModel>()

    //////////   getting text only from list

    fun AccessibilityNodeInfo?.extractWAmessages(context: Context){

        //conversationList.clear()
        this?.let {
            getTextFromList(context)
        }

//        conversationList.forEach {
//            Log.d(
//                "list",
//                it.text + " " +
//                        it.date + " " +
//                        it.sentOrRecieved + " " +
//                        it.groupMsgSenderName
//            )
//        }

    }

    fun AccessibilityNodeInfo.getTextFromList(context: Context) {

        try {
            className?.let {
                if (className == Constants.LIST_VIEW) {

                    for (i in 0 until childCount) {
                        //getListText(info.getChild(i))

                        getChild(i)?.let {
                            it.getMessages(context)
                        }
                    }
                } else {
                    for (i in 0 until childCount) {

                        getChild(i)?.let {
                            it.getTextFromList(context)
                        }
                    }
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    private fun AccessibilityNodeInfo?.getMessages(context: Context) {

        this?.let {

            val chatModel = ChatModel(msgId = 0)

            if (className == Constants.VIEW_GROUP) {
                for (i in 0 until childCount) {
                    when (getChild(i).viewIdResourceName) {
                        getMessageTxtId() -> {
                            getChild(i).text?.let {

                                chatModel.msg = it.toString()
                                if (it.toString().contains("You deleted this message")) {
                                    chatModel.isSent = true
                                }

//                                getChild(i).contentDescription?.let {
////                                    date = it.toString()
//
//                                    chatModel.msg = it.toString()
//                                }
//                                message = it.toString()
                            }
                        }
                        getMsgStatusId() -> {
//                            sentOrRecieve = "sent"
                            getChild(i).contentDescription?.let {
//                                date = it.toString()
                                chatModel.msgStatus = it.toString()
                            }
                            chatModel.isSent = true
                        }
                        getmsgSentTimeId() -> {
                            getChild(i).text?.let {
//                                date = it.toString()

                                chatModel.msgTime = it.toString()
                            }
                        }

                        getGroupMsgSenderNameId() -> {
                            getChild(i)?.extractGroupMessageSender(chatModel)
                        }

                        getQoutedFrameId() -> {
                            getChild(i)?.extractRepliedMsgInfo(chatModel)
                        }
                    }
                }

                ///// adding all extracted data to model
                chatModel.apply {
                    msg?.let { msg ->
                        if (msg.isNotBlank() && !conversationList.contains(this))
                            conversationList.add(this)
                    }
                }


//            if (message.isNotEmpty())
//                whatsappConversation.add(
//                    ConversationModel(
//                        message,
//                        date,
//                        sentOrRecieve,
//                        groupMsgSenderName
//                    )
//                )
            } else {
                for (i in 0 until childCount) {

                    getChild(i)?.let {
                        it.getMessages(context)
                    }
                }
            }
        }


    }

    private fun AccessibilityNodeInfo.extractGroupMessageSender(chatModel: ChatModel) {
        val chatMetadataModel = ChatMetadataModel()

        for (i in 0 until childCount) {
            val innerChild = getChild(i)
            when (innerChild.viewIdResourceName) {

                getNameOrNumberTextId() -> innerChild.text?.let {
                    chatMetadataModel.groupMsgSenderNameOrNumber = it.toString()
                }

                getUnSaveNameTextId() -> innerChild.text?.let {
                    chatMetadataModel.groupMsgSenderUnSavedName = it.toString()
                }
            }
        }


        chatModel.chatMetadataModel = chatMetadataModel
    }

    private fun AccessibilityNodeInfo.extractRepliedMsgInfo(chatModel: ChatModel) {

        val repliedChatModel = RepliedChatModel()

        for (i in 0 until childCount) {
            val innerChild = getChild(i)
            when (innerChild.viewIdResourceName) {

                getQoutedTitleId() -> innerChild.text?.let {
                    repliedChatModel.repliedChatNameOrNumber = it.toString()
                }

                getQoutedTextId() -> innerChild.text?.let {
                    repliedChatModel.repliedChatMsg = it.toString()
                }
            }
        }

        chatModel.repliedChatModel = repliedChatModel
    }

    fun AccessibilityNodeInfo.getContactStatusText(): CharSequence =
        extractViewFromID(getContactStatusId())?.text ?: ""


    fun AccessibilityNodeInfo?.isWhatsapp4B() =
        this?.let {
            packageName == Constants.WA_4B_PKJ
        } ?: false

    fun AccessibilityNodeInfo?.isWhatsapp() =
        this?.let {
            packageName == Constants.WA_PKJ
        } ?: false

    fun AccessibilityNodeInfo?.getContactNameId() =
        if (isWhatsapp())
            Constants.WA_HEADER_USER_NAME_ID
        else
            Constants.WA_HEADER_USER_NAME_ID_4B


    fun AccessibilityNodeInfo?.getMessageTxtId() =
        if (isWhatsapp())
            Constants.WA_MSG_TEXT
        else
            Constants.WA_MSG_TEXT_4B

    fun AccessibilityNodeInfo?.getMsgStatusId() =
        /// msg stats is sent or recieve . represents the tick of sent msgs
        if (isWhatsapp())                   // if msg have tick then it will sent msg otherwise recieve therefore
            Constants.WA_MSG_STATUS                // sent and recieve are seperate on this base
        else
            Constants.WA_MSG_STATUS_4B

    fun AccessibilityNodeInfo.getmsgSentTimeId() =
        if (isWhatsapp())
            Constants.WA_CHAT_DATE
        else
            Constants.WA_CHAT_DATE_4B

    fun AccessibilityNodeInfo.getGroupMsgSenderNameId() =
        if (isWhatsapp())
            Constants.WA_NAME_IN_GROUP
        else
            Constants.WA_NAME_IN_GROUP_4B

    fun AccessibilityNodeInfo.getNameOrNumberTextId() =
        if (isWhatsapp())
            Constants.WA_NAME_OR_NUMBER_IN_GROUP
        else
            Constants.WA_NAME_OR_NUMBER_IN_GROUP_4B

    fun AccessibilityNodeInfo.getUnSaveNameTextId() =
        if (isWhatsapp())
            Constants.WA_UNSAVED_NAME_IN_GROUP
        else
            Constants.WA_UNSAVED_NAME_IN_GROUP_4B


    fun AccessibilityNodeInfo.getEditTextId() =
        if (isWhatsapp())
            Constants.WA_EDITTEXT_ID
        else
            Constants.WA_EDITTEXT_ID_4B


    fun AccessibilityNodeInfo.getSendBtnId() =
        if (isWhatsapp())
            Constants.WA_SENDBTN_ID
        else
            Constants.WA_SENDBTN_ID_4B


    fun AccessibilityNodeInfo.getContactStatusId() =
        if (isWhatsapp())
            Constants.WA_CONTACT_STATUS_ID
        else
            Constants.WA_CONTACT_STATUS_ID_4B

    fun AccessibilityNodeInfo.getQoutedFrameId() =
        if (isWhatsapp())
            Constants.WA_QOUTED_MSG_FRAME
        else
            Constants.WA_QOUTED_MSG_FRAME_4B

    fun AccessibilityNodeInfo.getQoutedTitleId() =
        if (isWhatsapp())
            Constants.WA_QOUTED_TITLE
        else
            Constants.WA_QOUTED_TITLE_4B

    fun AccessibilityNodeInfo.getQoutedTextId() =
        if (isWhatsapp())
            Constants.WA_QOUTED_TEXT
        else
            Constants.WA_QOUTED_TEXT_4B

    fun AccessibilityNodeInfo.getMessageListId() =
        if (isWhatsapp())
            Constants.WA_LIST
        else
            Constants.WA_LIST_4B
}
