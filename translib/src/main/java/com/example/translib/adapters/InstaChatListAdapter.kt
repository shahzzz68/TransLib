package com.example.translib.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.translib.R
import com.example.translib.databinding.LayoutInstaChatItemBinding
import com.example.translib.databinding.LayoutLinkItemBinding
import com.example.translib.utils.exfuns.convertRepliedToSent
import com.example.translib.utils.exfuns.convertToSent
import com.example.translib.utils.exfuns.getMyDrawable
import com.example.translib.utils.exfuns.makeHide
import com.example.translib.utils.exfuns.makeVisible
import com.example.translib.utils.exfuns.setupMetadata
import com.example.translib.utils.models.chat.ChatModel
import com.example.translib.utils.models.chat.MessageType

class InstaChatListAdapter(
    val itemClick: (ChatModel, TextView, Int) -> Unit
) : ListAdapter<ChatModel, InstaChatListAdapter.ChatViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(
        LayoutInflater.from(parent.context).inflate(viewType, parent, false)
    )

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val msgType = try {
            if (position < currentList.count())
                currentList[position].msgType
            else
                MessageType.TEXT

        } catch (ignored: Exception) {
            MessageType.TEXT
        }
        return when (msgType) {
            MessageType.LINK -> R.layout.layout_link_item
            else -> R.layout.layout_insta_chat_item
        }
    }

    inner class ChatViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(chatModel: ChatModel) {
            when (view.id) {
                R.id.textLayout -> {
                    populateMessages(chatModel)
                }
                else -> {
                    populateLink(chatModel)
                }
            }
        }

        private fun populateMessages(chatModel: ChatModel) {
            with(LayoutInstaChatItemBinding.bind(view)) {

                chatModel.also {
                    if (it.msgType == MessageType.LIKE_HEART) {
                        chatMsg.apply {
                            background = root.context.getMyDrawable(R.drawable.ic_heart)
                            text = ""
                        }

                    } else {
                        chatMsg.apply {
                            text = it.msg
                            background = null
                        }
                        viewForClick.setOnClickListener {
                            itemClick.invoke(chatModel, chatMsg, adapterPosition)
                        }
                    }

                    textChatBubble.convertToSent(it)
                    textLayout.setupMetadata(it.chatMetadataModel)

                    msgTime.text = it.msgTime

                    /**
                     * handling replied msg layout for instagram
                     */
                    it.repliedChatModel?.let { repliedModel ->
                        qoutedMsgText.text = repliedModel.repliedChatMsg

                        qoutedMsgLayout.apply {
                            makeVisible()
                            convertRepliedToSent(chatModel) { isSent ->
                                // extra functionality based on isSent or not
                                qoutedMsgTitle.text =
                                    if (isSent) "You Replied" else "Replied to you"
                            }

                        }
                    } ?: run { qoutedMsgLayout.makeHide() }



//                    if (it.msgTime?.contains(Constants.SENDING) == true) {
//                        it.msgTime?.removeRange(0, Constants.SENDING.length+1)
//                    } else it.msgTime


                }
            }
        }

        private fun populateLink(chatModel: ChatModel) {
            with(LayoutLinkItemBinding.bind(view)) {
                chatModel.linkPreviewModel?.let {
                    linkMsg.text = chatModel.msg
                    linkTitle.text = it.linkTitle
                    link.text = it.link

                    linkChatBubble.convertToSent(chatModel)
                    linkLayout.setupMetadata(chatModel.chatMetadataModel)

                    viewForClick.setOnClickListener {
                        itemClick.invoke(chatModel, linkMsg, adapterPosition)
                    }
                }
            }
        }
    }


    class DiffUtilCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.msg == newItem.msg
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
}