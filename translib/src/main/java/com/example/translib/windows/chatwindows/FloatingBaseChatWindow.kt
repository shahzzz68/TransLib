package com.example.translib.windows.chatwindows

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.translib.utils.exfuns.adapterAndManager
import com.example.translib.utils.exfuns.hideKeyboard
import com.example.translib.utils.exfuns.runTranslation
import com.example.translib.utils.models.chat.ChatModel
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.userName
import com.example.translib.windows.FloatingWindowBase
import com.example.translator.windows.FloatingWindowTouch
import com.example.translib.R
import com.example.translib.utils.LangManager
import com.example.translib.utils.exfuns.editMyPrefs
import com.example.translib.utils.exfuns.getMyPrefs
import com.example.translib.utils.exfuns.ifEditTextNotBlank
import com.example.translib.utils.exfuns.makeEditTextEmpty
import com.example.translib.utils.exfuns.setUpSpinner


abstract class FloatingBaseChatWindow(
    private val context: Context,
    open var windowEventListener: ChatWindowEventListener,
    var onWindowOpenCLose: ((Boolean) -> Unit?)? = null
) : FloatingWindowBase(context) {

    lateinit var selectedLangCode: String
    private var touchListener: FloatingWindowTouch? = null


    val parentView: LinearLayout by lazy {
        initializeParent {  /// dispatch keyevent callback
            if (it.keyCode == KeyEvent.KEYCODE_BACK && it.action == KeyEvent.ACTION_UP)
                createFloatingWidget(isUpdate = true)
        }
    }


    abstract fun loadAdOnOpen()
    abstract fun updateAdapter(chatList: MutableList<ChatModel>)
    abstract fun createAdapter(chatList: MutableList<ChatModel>)

    abstract fun updateStatusOrMembers(string: String)


    abstract val getInputText: EditText
    abstract val floatingChatIcon: View
    abstract val chatLayout: ViewGroup


    fun isChatHidden(): Boolean = !chatLayout.isVisible


    fun RecyclerView.attachAdapter(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        adapterAndManager(
            adapter,
            isStackFromEnd = true
        )

//        if (chatList.isNotEmpty())
//            smoothScrollToPosition(bottom)

//        adapter.notifyDataSetChanged()

//        if (chatList.isNotEmpty())
//            smoothScrollToPosition(chatList.size - 1)

    }

    // override in base classes
    open fun sendMessage(binding: ViewBinding) {

        getInputText.getTextAndSendMessage()
        binding.root.hideKeyboard(context)
//        if (isAndroidOreo())
        parentView.makeNotFocusable()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupWindowTouchListener() {

        touchListener = FloatingWindowTouch(
            context,
            floatingWindowParams,
            { _, _, action ->
                when (action) {

                    MotionEvent.ACTION_MOVE -> updateViewLayout()

                    MotionEvent.ACTION_UP -> {
//                        if (touchListener!!.isClickDetected())
//                            openChatWindow()
                    }

                    FloatingWindowTouch.ACTION_CLICK -> openChatWindow()
                }
            })
        parentView.setOnTouchListener(touchListener)
    }


    @SuppressLint("ClickableViewAccessibility")
    open fun createFloatingWidget(isUpdate: Boolean = false) {

        // update the layout back to widget as layout binding instance is not changing
        setupWindowTouchListener()
        initializeWindowBasicParams()

        if (isUpdate)
            updateViewLayout()

    }


    fun performItemClick(chatModel: ChatModel, textView: TextView, pos: Int) {
        with(chatModel) {
            msg?.runTranslation(
                selectedLangCode,
                onTranslated = {
                    if (it.isNotBlank())
                        textView.text = it
                })
        }
    }


    open fun removeWindow() {
        shuffleViews(isWidget = true)
        parentView.apply {
            removeAllViews()
            removeViewFromParent()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    open fun openChatWindow() {

        parentView.apply {
            toMatchParent()
            setOnTouchListener(null)
        }

        onWindowOpenCLose?.invoke(true)

        loadAdOnOpen()  // adFrame coming from subclasses
    }


    fun setupSpinner(
        spinner: Spinner,
        @ColorInt textColorInt: Int
    ) {

        spinner.apply {
            setUpSpinner({

                selectedLangCode = LangManager.getLanguagesCode(context)?.get(it) ?: "en-US"
                context.editMyPrefs { putInt(userName, it) }
            }, textColorInt)
            setSelection(context.getMyPrefs().getInt(userName, 15))
        }
    }


    private fun updateViewLayout() {
        parentView.updateViewLayout()
    }


    //// click or touch events

    fun EditText.getTextAndSendMessage() {
        ifEditTextNotBlank(errorMsg = context.getString(R.string.blank_msg_error)) {
            val text = it.text.toString()

            it.makeEditTextEmpty("Sending...")

            // dummy listener for testing
//            500L.runAfter {
//                windowEventListener.onMessageSend(text)
//                it.hint = "Message..."
//            }

            // invoke when translation is done and msg sent listner called then it will perform insta actions
            text.runTranslation(
                selectedLangCode,
                onTranslated = { translatedText ->
//                if (translatedText.isBlank())
//                    it.error = "Message was not sent internet issue"
//                else
//                300L.runAfter {
                    windowEventListener.onMessageSend(translatedText)
                    it.hint = "Message..."
//                }
                })
        }
    }

    fun View.performSendCLick(binding: ViewBinding) {
        setOnClickListener {
            sendMessage(binding)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun View.makeFocusableOnTouch() {
        setOnTouchListener { _, _ ->

            parentView.makeInputFocusable()
            false
        }
    }

    fun View.convertToWidgetOnCLick() {
        setOnClickListener {
            createFloatingWidget(isUpdate = true)
            onWindowOpenCLose?.invoke(false)
        }
    }


    fun shuffleViews(isWidget: Boolean) {
        floatingChatIcon.isVisible = isWidget
        chatLayout.isVisible = !isWidget
    }


    interface ChatWindowEventListener {
        fun onMessageSend(message: String)
    }
}