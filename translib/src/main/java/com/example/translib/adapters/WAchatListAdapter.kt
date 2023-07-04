package com.example.chattranslatornew.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.translib.R
import com.example.translib.databinding.LayoutWaChatItemLeftBinding
import com.example.translib.databinding.LayoutWaChatItemRightBinding
import com.example.translib.utils.exfuns.changeBgColor
import com.example.translib.utils.exfuns.makeHide
import com.example.translib.utils.exfuns.makeVisible
import com.example.translib.utils.models.chat.ChatModel

class WAchatListAdapter(
    val itemClick: (ChatModel, TextView, Int) -> Unit
) : ListAdapter<ChatModel, WAchatListAdapter.WAchatViewHolder>(DiffUtilCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WAchatViewHolder(
        LayoutInflater.from(parent.context).inflate(viewType, parent, false)
    )

    override fun onBindViewHolder(holder: WAchatViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val sentItem = try {
            if (position < currentList.count())
                currentList[position].isSent // sometime crash on isSent
            else
                false
        } catch (ignored: NullPointerException) {
            false
        }

        return when (sentItem) {
            true -> R.layout.layout_wa_chat_item_right
            else -> R.layout.layout_wa_chat_item_left
        }
    }

    inner class WAchatViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(chatModel: ChatModel) {
            if (view.id == R.id.right_wa_chat)
                populateSentMessages(chatModel)
            else
                populateRecieveMessages(chatModel)
        }


        fun populateRecieveMessages(chatModel: ChatModel) {
            with(LayoutWaChatItemLeftBinding.bind(view)) {
                setDataOnViews(
                    chatModel,
                    chatMsg,
                    msgTime,
                    groupMsgSendertxt,
                    groupMsgSenderUnsavedtxt,
                    qoutedMsgLayout,
                    qoutedMsgText,
                    qoutedMsgTitle
                )

                viewForClick.setOnClickListener {
                    itemClick.invoke(chatModel, chatMsg, adapterPosition)
                }

//                innerLayout.setOnClickListener {
//                    itemClick.invoke(chatModel, chatMsg, adapterPosition)
//                }
//
//                chatMsg.setOnClickListener {
//                    itemClick.invoke(chatModel, chatMsg, adapterPosition)
//                }
//                viewForClick.setOnClickListener {
//                    itemClick.invoke(chatModel, chatMsg, adapterPosition)
//                }
            }
        }


        fun populateSentMessages(chatModel: ChatModel) {
            with(LayoutWaChatItemRightBinding.bind(view)) {
                setDataOnViews(
                    chatModel,
                    chatMsg,
                    msgTime,
                    repliedLayout = qoutedMsgLayout,
                    qoutedTextView = qoutedMsgText,
                    qoutedTitle = qoutedMsgTitle
                )
//                root.setOnClickListener {
//                    itemClick.invoke(chatModel, chatMsg, adapterPosition)
//                }

                chatModel.msgStatus?.let { Log.d("msgStatus", it) }

                innerLayout.changeBgColor(R.color.wa_chat_color_right)

//                 msgStatus.apply {
//                     setColorFilter(Color.GRAY)
//
//                     setImageResource(
//                         when (chatModel.msgStatus) {
//                             Constants.SENT -> R.drawable.ic_tick
//                             Constants.DELIVERED -> R.drawable.ic_done_all
//                             Constants.READ -> {
//                                 setColorFilter(context.getMyColor(R.color.color_primary))
//                                 R.drawable.ic_done_all
//                             }
//                             else -> R.drawable.ic_pending
//                         }
//                     )
//                 }
            }
        }

        private fun setDataOnViews(
            chatModel: ChatModel,
            chatMsgTextView: TextView,
            messageTimeText: TextView,
            groupMsgSenderNameOrNumberText: TextView? = null,
            groupMsgSenderUnsavedNameText: TextView? = null,
            repliedLayout: LinearLayout,
            qoutedTextView: TextView,
            qoutedTitle: TextView
        ) {
            chatModel.also {
                chatMsgTextView.text = it.msg
                messageTimeText.text = it.msgTime

                // setting text of message sender name in group chats
                it.chatMetadataModel?.let {

                    groupMsgSenderNameOrNumberText?.apply {
                        makeVisible()
                        text = it.groupMsgSenderNameOrNumber
                    }

                    groupMsgSenderUnsavedNameText?.apply {
                        makeVisible()
                        text = it.groupMsgSenderUnSavedName
                    }
                }

                ////// setting up replied message//////////////

                it.repliedChatModel?.let { repModel ->

                    repliedLayout.makeVisible()
                    qoutedTitle.text = repModel.repliedChatNameOrNumber
                    qoutedTextView.text = repModel.repliedChatMsg
                } ?: kotlin.run { repliedLayout.makeHide() }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.msgId == newItem.msgId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }

}