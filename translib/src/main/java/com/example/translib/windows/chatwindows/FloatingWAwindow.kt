package com.example.translib.windows.chatwindows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.example.chattranslatornew.adapters.WAchatListAdapter
import com.example.translib.R
import com.example.translib.databinding.LayoutWaChatWindowBinding
import com.example.translib.utils.accessibility.AccessibilityServiceUtils
import com.example.translib.utils.exfuns.getMaterialColorRef
import com.example.translib.utils.exfuns.getMyColor
import com.example.translib.utils.exfuns.getThemeColor
import com.example.translib.utils.exfuns.makeHide
import com.example.translib.utils.exfuns.makeVisible
import com.example.translib.utils.models.chat.ChatModel

class FloatingWAwindow(
    private val context: Context,
    override var windowEventListener: ChatWindowEventListener,
) : FloatingBaseChatWindow(context, windowEventListener) {


    private var wAchatAdapter: WAchatListAdapter? = null
    private val binding: LayoutWaChatWindowBinding by lazy {
        LayoutWaChatWindowBinding.inflate(LayoutInflater.from(context))
    }

    init {
        clicksAndTouchWA()
    }

    override val chatLayout: ViewGroup = binding.chatLayout
    override val floatingChatIcon: View = binding.floatingChatIcon
    override val getInputText: EditText get() = binding.chatInput

    override fun createFloatingWidget(isUpdate: Boolean) {
        shuffleViews(isWidget = true)

        super.createFloatingWidget(isUpdate)

        if (!isUpdate)
            parentView.addViewToParent(binding.root)
    }

    override fun loadAdOnOpen() {

    }

    ///CREATE WA CHAT ADAPTER
    override fun createAdapter(chatList: MutableList<ChatModel>) {

        WAchatListAdapter { chatModel, text, pos ->  /// click callback from adapter
            performItemClick(chatModel, text, pos)
        }.also {

            wAchatAdapter = it
            binding.rvChat.apply {
                adapter = it
//                attachAdapter(
//                    it as RecyclerView.Adapter<RecyclerView.ViewHolder>
//                )
                itemAnimator = null
            }

//            it.submitList(chatList)

//            if (chatList.isNotEmpty())
//                it.setChatList(chatList)
        }
    }

    /// WA CHAT ADAPTER
    override fun updateAdapter(chatList: MutableList<ChatModel>) {

        wAchatAdapter?.apply {
//            convList.add(chatModel)
//            notifyItemInserted(convList.lastIndex)

            if (chatList.isNotEmpty()) {
//                submitList(chatList.toList())
                submitList(chatList)
//                wAchatAdapter?.setChatList(chatList)
                try {
                    binding.rvChat.scrollToPosition(chatList.lastIndex)
                } catch (ignored: Exception) {
                }
            }

        } ?: kotlin.run {
            createAdapter(chatList)
        }
    }

    override fun openChatWindow() {
        shuffleViews(isWidget = false)
        super.openChatWindow()
        updateLayoutData()
        /////// loading ad when window is open
    }


    override fun updateStatusOrMembers(string: String) {
        binding.onlineStatusOrMembersName.apply {
            text = if (string.isBlank()) {
                makeHide()
                ""
            } else {
                makeVisible()
                string
            }
        }
    }


//    override fun isChatHidden(): Boolean =
//        binding.chatLayout.visibility != View.VISIBLE


    override fun removeWindow() {
        shuffleViews(isWidget = true)
        super.removeWindow()
    }

//    override fun sendMessage(binding: ViewBinding) {
//        with(binding) {
//            if (this is LayoutWaChatWindowBinding) {
//                chatInput.getTextAndSendMessage()
//            }
//            super.sendMessage(binding)
//        }
//    }

    private fun clicksAndTouchWA() {
        with(binding) {

            chatInput.makeFocusableOnTouch()

            sendBtn.performSendCLick(this)

            back.convertToWidgetOnCLick()
        }
    }

    private fun updateLayoutData() {
        AccessibilityServiceUtils.userName?.let {
            binding.headerUserName.text = it
        }
//        updateAdapter(AccessibilityServiceUtils.conversationList)
        setupSpinner(
            binding.langSpinnner, context.getMyColor(R.color.white)
        )
    }

//    private fun shuffleViews(isWidget: Boolean) {
//        with(binding) {
//            floatingChatIcon.visibility = if (isWidget) View.VISIBLE else View.GONE
//            chatLayout.visibility = if (isWidget) View.GONE else View.VISIBLE
//        }
//    }


}