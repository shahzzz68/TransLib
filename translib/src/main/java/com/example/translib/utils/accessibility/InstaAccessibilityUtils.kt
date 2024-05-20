package com.example.translib.utils.accessibility

import android.content.Context
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.example.translator.models.chat.*
import com.example.translib.utils.exfuns.formattedTime
import com.example.translib.utils.Constants
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.conversationList
import com.example.translib.utils.exfuns.getWidthPixels
import com.example.translib.utils.exfuns.pixelsToDp
import com.example.translib.utils.models.chat.ChatModel
import com.example.translib.utils.models.chat.LinkPreviewModel
import com.example.translib.utils.models.chat.MessageType
import com.example.translib.utils.models.chat.RepliedChatModel

object InstaAccessibilityUtils {


    var isMatched = false
    fun Context.checkRVid(info: AccessibilityNodeInfo?) {
        info?.className?.let {
            if (info.viewIdResourceName == Constants.INSTAGRAM_RECYCLER_VIEW_ID) {
                isMatched = true

            } else {
                for (i in 0 until info.childCount) {

                    info.getChild(i)?.let {
                        checkRVid(it)
                    }
                }
            }
        }
    }

    fun AccessibilityNodeInfo.extractInstaMessages(context: Context) {
        try {
            // extracting insta messages
            className?.let {
                if (isRecyclerView()) {
                    for (i in 0 until childCount) {
                        //getListText(info.getChild(i))

                        getChild(i)?.let {
                            getMessages(context, it)
                        }
                    }
                } else {
                    for (i in 0 until childCount) {
                        getChild(i)?.let {
                            it.extractInstaMessages(context)
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        }

    }

    var chatMetadataModel: ChatMetadataModel? = null
    private fun getMessages(context: Context, info: AccessibilityNodeInfo) {

        val chatModel = ChatModel(msgId = 0)

        when (info.className) {
            Constants.FRAME_LAYOUT -> {

                for (i in 0 until info.childCount) {

                    when (info.getChild(i)?.viewIdResourceName) {
                        Constants.INSTAGRAM_DIRECT_TEXT_MESSAGE_ID -> {
                            chatModel.apply {
                                msg = info.getChild(i).text.toString()
                                isSent = info.getChild(i).isSent(context)
                                msgType = MessageType.TEXT
                            }
                        }

                        Constants.INSTAGRAM_REPLIED_MESSAGE_ID -> {
                            val repliedChatMsg = info.getChild(i).text.toString()

                            RepliedChatModel(repliedChatMsg = repliedChatMsg).also {
                                chatModel.repliedChatModel = it
                            }
                        }

                        Constants.INSTAGRAM_LIKE_MESSAGE_ID -> {
                            chatModel.apply {
                                msg = "likeHeart"
                                msgType = MessageType.LIKE_HEART
                                isSent = info.getChild(i).isSent(context)
                            }
                        }

                        Constants.INSTAGRAM_MESSAGE_STATUS_ID -> {
                            val time = info.getChild(i).text.toString()
                            chatModel.apply {
                                msgStatus =
                                    time  /// setting the time for checking the status whether it is sending or data
                                msgTime =
                                    if (time.contains(Constants.SENDING)) formattedTime() else time
                            }
                        }

                        Constants.INSTAGRAM_LINK_PREVIEW_CONTAINER_ID -> {
                            val linkModel = LinkPreviewModel()
                            for (j in 0 until info.getChild(i).childCount) {
                                when (info.getChild(i).getChild(j).viewIdResourceName) {

                                    Constants.INSTAGRAM_LINK_MESSAGE_TEXT_ID -> {
                                        val linkMsg =
                                            info.getChild(i).getChild(j)

                                        chatModel.apply {
                                            msg = linkMsg.text.toString()
                                            isSent =
                                                linkMsg.isSent(context)
                                            msgType = MessageType.LINK
                                        }
                                    }

                                    Constants.INSTAGRAM_LINK_PREVIEW_SUMMARY_ID -> {
                                        linkModel.link =
                                            info.getChild(i).getChild(j).text.toString()
                                    }

                                    Constants.INSTAGRAM_LINK_PREVIEW_TITLE_ID -> {
                                        linkModel.linkTitle =
                                            info.getChild(i).getChild(j).text.toString()
                                    }
                                }
                            }

                            chatModel.apply {
                                linkPreviewModel = linkModel
//                                isSent = info.isSent(context)
                            }
                        }
                    }
                }
            }

            Constants.TEXT_VIEW -> {
                chatMetadataModel ?: ChatMetadataModel().also {
                    chatMetadataModel = it
                }

                when (info.viewIdResourceName) {
                    null -> {
                        if (info.className == Constants.TEXT_VIEW)
                            info.text?.let {
                                chatMetadataModel?.messageDate = it.toString()
                            }
                    }

                    Constants.INSTAGRAM_GROUP_MESSAGE_SENDER_ID -> {
                        chatMetadataModel?.groupMsgSenderNameOrNumber = info.text.toString()
                    }
                }

//                chatModel.chatMetadataModel = chatMetadataModel
            }

            // default case
            else -> {
                for (i in 0 until info.childCount) {
                    info.getChild(i)?.let {
                        getMessages(context, it)
                    }
                }
            }
        }

        chatModel.apply {

            when (msgType) {

                // checks for avoiding insert extra objects in list
                MessageType.TEXT -> {
                    msg?.let {
                        addChatDataToList(this)
                    }
                }

                MessageType.LINK -> {
                    linkPreviewModel?.let {
                        addChatDataToList(this)
                    }
                }

                MessageType.LIKE_HEART -> {  // this is for adding the like/heart message
                    addChatDataToList(this)
                }
            }
        }
    }

    private fun addChatDataToList(model: ChatModel) {
        chatMetadataModel?.let {
            model.chatMetadataModel = it
            chatMetadataModel = null
        }

        if (!conversationList.contains(model))
            if (model.msgStatus?.contains("sending", true) == false)
                conversationList.add(model)

    }

    private fun AccessibilityNodeInfo.isSent(context: Context): Boolean {
        val rect = Rect()
        getBoundsInScreen(rect)

        val difference = (context.getWidthPixels() - rect.right).toFloat().pixelsToDp(context)
        return difference <= 20
    }


    fun AccessibilityNodeInfo.updateLast(context: Context) {
        if (isRecyclerView())
            getChild(childCount - 1).also { lastChild ->
                getMessages(
                    context,
                    lastChild
                )
            }
        else
            for (i in 0 until childCount)
                getChild(i).updateLast(context)
    }

    fun AccessibilityNodeInfo.isRecyclerView(): Boolean =
        className?.equals(Constants.RECYCLER_VIEW) == true
}