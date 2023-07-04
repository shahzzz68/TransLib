package com.example.translib.chatservice

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.translib.windows.chatwindows.FloatingInstaWindow
import com.example.translib.windows.chatwindows.FloatingWAwindow
import com.example.translator.utils.*
import com.example.translib.utils.accessibility.InstaAccessibilityUtils.extractInstaMessages
import com.example.translib.utils.accessibility.WAaccessibilityUtils.extractWAmessages
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getContactStatusText
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getEditTextId
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getSendBtnId
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp4B
import com.example.translib.utils.exfuns.preventMultipleInvocations
import com.example.translib.utils.exfuns.runAfter
import com.example.translib.utils.Constants
import com.example.translib.utils.CoroutineUtils
import com.example.translib.utils.EventBroadcast
import com.example.translib.utils.NotificationHelper
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.conversationList
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractSpecificText
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractTextFromId
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractViewFromID
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.isInstaPkj
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.performChatSendActions
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.userName
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.whatsOrInstaListNull
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getContactNameId
import com.example.translib.utils.exfuns.isAnyTranslatorOn
import com.example.translib.utils.exfuns.isTranslatorOn
import com.example.translib.windows.FloatingSearchWidget
import com.example.translib.windows.FloatingTranslatorWindow
import com.example.translib.windows.chatwindows.FloatingBaseChatWindow
import java.util.Locale


class MyAccessibilityService : AccessibilityService(),
    FloatingBaseChatWindow.ChatWindowEventListener,
    FloatingSearchWidget.TranslateWindowEventListener {


    var chatRefreshHandler = Handler(Looper.myLooper()!!)
    var chatRefreshRunnable = Runnable {
        Log.d(
            "content", "last"
        )
        refreshChatList()
    }

    private var eventBroadcast: EventBroadcast? = null

    private var windowStateChangeSource: AccessibilityNodeInfo? = null
    private var windowContentChangeSource: AccessibilityNodeInfo? = null

    private var floatingWindow: FloatingBaseChatWindow? = null

    private var isViewInitialized = false

    private val floatingTranslatorWindow by lazy {
        FloatingTranslatorWindow(this)
    }

    var floatingSearchWidget: FloatingSearchWidget? = null

    companion object {
        var accessibilityServiceInstance: MyAccessibilityService? = null
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        accessibilityServiceInstance = this
        floatingSearchWidget = FloatingSearchWidget(this, this)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        accessibilityServiceInstance = null
        floatingSearchWidget = null
        return super.onUnbind(intent)
    }


//    override fun onServiceConnected() {
//
//        val accessibilityServiceInfo = serviceInfo
//        //performGlobalAction( GLOBAL_ACTION_BACK )
//        accessibilityServiceInfo.apply {
//            eventTypes =
//                AccessibilityEvent.TYPE_VIEW_HOVER_ENTER or AccessibilityEvent.TYPE_VIEW_SCROLLED or
//                        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//
//            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
//            //flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
//            flags =
//                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.DEFAULT
//            //We are keeping the timeout to 0 as we don’t need any delay or to pause our accessibility events
//            notificationTimeout = 0
//        }
//        serviceInfo = accessibilityServiceInfo
//
//
//    }


    private var lastTime: Long = 0

    @SuppressLint("SwitchIntDef")
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (!isAnyTranslatorOn()) return

        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {

//                logViewHierarchy(rootInActiveWindow, 0)
                if (event.packageName != Constants.INSTAGRAM_PKJ && event.packageName != Constants.WA_PKJ && event.packageName != Constants.WA_4B_PKJ) return

                Log.d("className", event.className.toString())
                when (event.className) {
                    Constants.WA_CONVERSATION_ACTIVITY, Constants.VIEW, // for instagram handling
                    Constants.INSTAGRAM_CONVERSATION_ACTIVITY -> {

//                        conversationList.clear()

//                        if (rootInActiveWindow.isWhatsapp4B() || rootInActiveWindow.isWhatsapp4B())
                        windowStateChangeSource = rootInActiveWindow ?: event.source

                        initialize()
                    }

                    else -> {
//                        InstaAccessibilityUtils.isMatched = false
//                        checkRVid(rootInActiveWindow)
                        if (rootInActiveWindow?.whatsOrInstaListNull() == true) /// check if screen is changed based on if specific view is null
                            removeWidget()
//
//                        if (!InstaAccessibilityUtils.isMatched)


//                floatingWindow = null
                    }
                }
            }

            /**
             * when any content change in window
             */
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                if (lastTime + 300 > System.currentTimeMillis()) return

                if (floatingWindow?.isChatHidden() != false) // only update when chat window is showing
                    return

                windowContentChangeSource = event.source

                when (event.packageName) {
                    Constants.INSTAGRAM_PKJ -> {
//                        Log.d("className",event.className.toString())
//                        Log.d("classNameSource",event.source.className.toString())

//                        preventMultipleInvocations({
                        refreshChatList()
                        Log.d(
                            "content", "last"
                        )
//                        })
//                        chatRefreshhandler.removeCallbacksAndMessages(null)
//                        chatRefreshhandler.post(chatRefreshRunnable)

                    }

                    Constants.WA_PKJ, Constants.WA_4B_PKJ -> {
//                        if (event.contentChangeTypes == AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT) {
                        updateContactStatus()

                        chatRefreshHandler.removeCallbacksAndMessages(null)
                        chatRefreshHandler.post(chatRefreshRunnable)
//                        }
//                        preventMultipleInvocations({
//                            Log.d(
//                                "content",
//                                "last"
//                            )
//                                refreshChatList()
//                        },1000)

                    }
                }

                lastTime = System.currentTimeMillis()
            }

        }
    }

    private fun initialize() {
        300L.runAfter {
//            logViewHierarchy(source, 0)
            // due to multiple events from accessibiliity service this method also invoke multiple times.To prevent view adding multiple time checked is chatlayout is visible or not

            if (!isViewInitialized && floatingWindow?.isChatHidden() != false) {
                createFloatingWindow()
                refreshChatList()
            }
        }
    }


    //// update the online status or last seen at run time for whatsapp
    private fun updateContactStatus() {

        val src = windowStateChangeSource ?: rootInActiveWindow
        src?.getContactStatusText()?.also {
            floatingWindow?.updateStatusOrMembers(it.toString())
        }

    }

    private fun refreshChatList() {

//        if (isAndroidOreo())
        updateList()

//        else
//            preventMultipleInvocations({
//                updateList()
//            }, 50)

//            floatingWindow?.updateAdapter(conversationList)

        //
        //                // reset to true after all processes completed
    }

    override fun onInterrupt() {
        floatingWindow = null
    }


    /////////////////////// removing the small floating widget when screen exit or home btn press etc ///////////////////////////////////
    private fun removeWidget() {
        if (isViewInitialized) {  // isViewInitilized for checking the floating window is in initilizing state to prevent view from removing due to invoking of multiple events from accessibility service

            floatingWindow?.removeWindow()
            isViewInitialized = false

            floatingWindow = null
            userName = null

            eventBroadcast?.unRegister(this)

            preventMultipleInvocations({
                try {
                    conversationList.clear() // crashing on clear due to invocation of function at multi time
                } catch (ignored: Exception) {
                }
            })
        }
    }


    /// /////////////// initialize floating windows   ///////////////////////////////
    private fun createFloatingWindow() {
        initBroadCast()
        floatingWindow ?: kotlin.run {
            rootInActiveWindow?.run {

                if (isInstaPkj() && isTranslatorOn(Constants.INSTA_TRANSLATOR_ON)) {
                    initializeInstaWindow()

                }
                else if ((isWhatsapp() && isTranslatorOn(Constants.WA_TRANSLATOR_ON)) || (isWhatsapp4B() && isTranslatorOn(
                        Constants.WA_4B_TRANSLATOR_ON
                    ))
                ) {
                    initializeWAwindow()
                }

            }
        }

        floatingWindow?.let {
            it.createFloatingWidget()
            isViewInitialized = true
        }

    }


    ///////////////////  create WA floating window ////////////////////////////////////////
    private fun AccessibilityNodeInfo.initializeWAwindow() {
        // extracting user name of current chat in WA

//        if (!isTranslatorOn(Constants.WA_TRANSLATOR_ON) && !isTranslatorOn(Constants.WA_4B_TRANSLATOR_ON))
//            return


        if (extractViewFromID(Constants.WA_LIST) == null) return

        userName = extractTextFromId(getContactNameId())

        // initializing WA floating window

        FloatingWAwindow(
            this@MyAccessibilityService, this@MyAccessibilityService
        ).apply {/// on window open callback
            onWindowOpenCLose = windowOpenCloseCallback

        }.also {
            floatingWindow = it
        }
    }

    ////////////  create insta floating window//////////////////////////////////////////
    private fun AccessibilityNodeInfo?.initializeInstaWindow() {

        // if not in chat activity then return
        if (extractViewFromID(Constants.INSTAGRAM_RECYCLER_VIEW_ID) == null) return

        // extracting user name of current chat in insta

        userName = extractTextFromId(Constants.INSTAGRAM_HEADER_USER_NAME_ID)

        // initializing insta floating window
        FloatingInstaWindow(
            this@MyAccessibilityService, this@MyAccessibilityService
        ).apply {
            onWindowOpenCLose = windowOpenCloseCallback

        }.also {
            floatingWindow = it
        }
    }

    private val windowOpenCloseCallback: (Boolean) -> Unit = { isOpen ->

        Locale.setDefault(Locale("ur"))
        if (isOpen) {
            updateContactStatus()
            refreshChatList()  /// getting the messages when chat open
        } else {
//            conversationList.clear()
//
//            if (floatingWindow is FloatingInstaWindow) {  // for resolving inconsistency issue
//                (floatingWindow as FloatingInstaWindow).instaChatAdapter?.notifyDataSetChanged()
//            }
        }
    }


    ///////////////////////////////   updating chat list according two different scenarios///////////////
    private fun updateList() {

//        val src = if (isAndroidOreo()) source else rootInActiveWindow
        val src = windowStateChangeSource ?: rootInActiveWindow ?: windowContentChangeSource
        src?.run {
            CoroutineUtils.executeIO({

                if (isInstaPkj()) extractInstaMessages(
                    this@MyAccessibilityService
                ) // extracting all msgs from screen
                else if (isWhatsapp() || isWhatsapp4B()) {
//                    conversationList.clear()
                    extractWAmessages(this@MyAccessibilityService)

                } else {
                    null
                }


            }, {
//            conversationList.forEach { model ->
//                Log.d("messages", model.toString())
//            }
                floatingWindow?.updateAdapter(conversationList)

//                floatingWindow?.createAdapter(conversationList)
            })
        }
    }


    ////////////  msg sending callback either from any chat window/////////////////////////////////////

    override fun onMessageSend(message: String) {
        // click listener of send message

//        val rootSource = if (isAndroidOreo()) rootInActiveWindow else source
        val rootSource = windowStateChangeSource ?: rootInActiveWindow
        rootSource?.run {
            if (isInstaPkj()) performChatSendActions(
                message, Constants.INSTAGRAM_EDITTEXT_ID, Constants.INSTAGRAM_SENDBTN_ID
            )
            else if (isWhatsapp() || isWhatsapp4B()) {
                performChatSendActions(
                    message, getEditTextId(), getSendBtnId()
                )
            }
//            refreshChatList()

        }
    }


    //////////////////// touch coordinates for general translator window
    override fun touchCords(x: Int, y: Int) {
        Log.d("touchXY", "$x $y")

        CoroutineUtils.executeIO({
//            logViewHierarchy(source,0)
            val source = rootInActiveWindow ?: windowStateChangeSource
            source.extractSpecificText(x, y)
//            rootInActiveWindow?.extractSpecificText(x, y) ?: source?.extractSpecificText(x, y)
        }, { dataPair ->
//            AccessibilityServiceUtils.extractedText?.let { text -> showToast(text) }
            Log.d("extractedInfo", dataPair.toString())

            dataPair?.let {
                floatingTranslatorWindow.showTranslatorWindow(it)
            }

        })
    }


    private fun initBroadCast() {
        if (eventBroadcast == null) eventBroadcast = EventBroadcast {
            removeWidget()
        }

        eventBroadcast?.register(this@MyAccessibilityService)
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationHelper.cancelNotification()
        floatingSearchWidget = null

    }

}