package com.example.translib.windows.chatwindows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.example.translib.R
import com.example.translib.adapters.InstaChatListAdapter
import com.example.translib.databinding.LayoutInstaChatWindowBinding
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.userName
import com.example.translib.utils.exfuns.getMaterialColorRef
import com.example.translib.utils.exfuns.getMyColor
import com.example.translib.utils.models.chat.ChatModel

class FloatingInstaWindow(
    private val context: Context,
    override var windowEventListener: ChatWindowEventListener,
) : FloatingBaseChatWindow(context, windowEventListener) {

    var instaChatAdapter: InstaChatListAdapter? = null

    private val binding: LayoutInstaChatWindowBinding by lazy {
        LayoutInstaChatWindowBinding.inflate(LayoutInflater.from(context))
    }

    init {
        clicksAndTouchInsta()
    }

    override val chatLayout: ViewGroup = binding.chatLayout
    override val floatingChatIcon: View = binding.floatingChatIcon
    override val getInputText: EditText
        get() = binding.chatInput


    private fun updateLayoutData() {
        userName?.let {
            binding.headerUserName.text = it
        }
//        updateAdapter(conversationList)
        setupSpinner(binding.langSpinnner, context.getMyColor(R.color.black_or_white))
    }

    // INSTA CHAT ADAPTER
    override fun updateAdapter(chatList: MutableList<ChatModel>) {
        instaChatAdapter?.apply {
//            convList.add(chatModel)
//            notifyItemInserted(convList.lastIndex)


            if (chatList.isNotEmpty()) {
                submitList(chatList)
//                setChatList(chatList)

//                try {
                binding.rvChat.scrollToPosition(chatList.lastIndex)
//                } catch (e: Exception) {
//                }
            }
        } ?: kotlin.run {
            createAdapter(chatList)
        }
    }

    override fun updateStatusOrMembers(string: String) {

    }


    // CREATE INSTA CHAT ADAPTER
    override fun createAdapter(chatList: MutableList<ChatModel>) {
        InstaChatListAdapter { chatModel, text, pos ->  /// click callback from adapter
            performItemClick(chatModel, text, pos)
        }.also {

            instaChatAdapter = it
            binding.rvChat.apply {
                adapter = it
//                attachAdapter(
//                    it as RecyclerView.Adapter<RecyclerView.ViewHolder>
//                )
                itemAnimator = null
            }

//            it.submitList(chatList)
        }

//        chatAdapter?.let {
//            if (chatList.isNotEmpty()) {
//                val lastItem = chatList.lastIndex
//                updatedChatList.add(chatList[lastItem])
//                it.notifyItemInserted(lastItem)
//            }
//
//        } ?: kotlin.run {
//
//            updatedChatList = chatList
//            ChatAdapter(updatedChatList).also {
//                chatAdapter = it
//                binding.rvChat.adapterAndManager(it as RecyclerView.Adapter<RecyclerView.ViewHolder>)
//            }
//        }
    }


    override fun createFloatingWidget(isUpdate: Boolean) {
        shuffleViews(isWidget = true)

        super.createFloatingWidget(isUpdate)

        if (!isUpdate)
            parentView.addViewToParent(binding.root)
    }

    override fun loadAdOnOpen() {

    }

    private fun clicksAndTouchInsta() {
        with(binding) {

            chatInput.makeFocusableOnTouch()

            sendBtn.performSendCLick(this)

            back.convertToWidgetOnCLick()
        }
    }

    override fun openChatWindow() {
        shuffleViews(isWidget = false)
        super.openChatWindow()
        updateLayoutData()
    }


//    override fun isChatHidden(): Boolean =
//        binding.chatLayout.visibility != View.VISIBLE

    override fun removeWindow() {
        shuffleViews(isWidget = true)
        super.removeWindow()
    }

//    private fun shuffleViews(isWidget: Boolean) {
//        with(binding) {
//            floatingChatIcon.isVisible = isWidget
//            chatLayout.isVisible = !isWidget
//        }
//    }
}