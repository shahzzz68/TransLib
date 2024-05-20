package com.example.translib.utils.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getContactNameId
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp4B
import com.example.translib.utils.Constants
import com.example.translib.utils.models.chat.ChatModel


object AccessibilityServiceUtils {

    var conversationList: MutableList<ChatModel> = mutableListOf()
    var userName: String? = null

    fun AccessibilityNodeInfo?.isInstaPkj() =
        this?.let {
            packageName == Constants.INSTAGRAM_PKJ
        } ?: false


    fun AccessibilityNodeInfo.textOrContent(): String? =
        text?.toString() ?: contentDescription?.toString()

    var expectedInfo: AccessibilityNodeInfo? = null
    var extractedText: String? = null
    var wordDetected = false
    fun AccessibilityNodeInfo?.extractSpecificText(x: Int, y: Int): Pair<Rect, String>? {

        wordDetected = false
//        expectedInfo = null

        this?.let outerLet@{

            val rect = Rect().also { getBoundsInScreen(it) }
//            if (x in rect.left..rect.right && y in rect.top..rect.bottom) {

            if (rect.contains(x, y)) {

                it.textOrContent()?.let {
                    expectedInfo = this
                    return@outerLet
                }

            }



            for (i in 0 until childCount) {
//                if (wordDetected)
//                    break
                getChild(i).extractSpecificText(x, y)
            }

//            Log.d("extractSpecificText", className.toString())

        }

        expectedInfo?.let {
            val rect = Rect()
            it.getBoundsInScreen(rect)
            it.textOrContent()?.let { text ->
                return Pair(rect, text)
            }

        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun AccessibilityService.touchTo(x: Float, y: Float) {
        val swipePath = Path()
        swipePath.moveTo(x, y)
        swipePath.lineTo(x, y)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, 50))
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    fun Context.sendAccessibilityEvent(type: Int) {
        val accessibilityManager: AccessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            AccessibilityEvent.obtain(type).also {
                accessibilityManager.sendAccessibilityEvent(it)
            }
        }
    }

    fun AccessibilityNodeInfo.setTextOnView(text: String?) {
        Bundle().apply {
            putString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }.also {
            performAction(AccessibilityNodeInfoCompat.ACTION_SET_TEXT, it)
        }
    }

    fun AccessibilityNodeInfo?.extractTextFromId(id: String) =
        extractViewFromID(id)?.text?.toString()

    fun AccessibilityNodeInfo?.extractViewFromID(
        id: String
    ): AccessibilityNodeInfo? {
        try {
            this?.let {
                val findAccessibilityNodeInfosByViewId: MutableList<AccessibilityNodeInfo> =
                    it.findAccessibilityNodeInfosByViewId(id)

                if (findAccessibilityNodeInfosByViewId.isNotEmpty()) {
                    return findAccessibilityNodeInfosByViewId[0]
                }
            }
        } catch (ignore: Exception) {

        }

        return null
    }


    fun Context.logViewHierarchy(nodeInfo: AccessibilityNodeInfo?, depth: Int) {
        if (nodeInfo == null) return
        var spacerString = ""
//        for (i in 0 until depth) {
//            spacerString += '-'
//        }

//        if (nodeInfo.isRecyclerView())
//            nodeInfo.getChild(nodeInfo.childCount - 1).also { lastChild ->
//
//                getMessages(this, lastChild)
//                conversationList[conversationList.count() - 1].msg?.let {
//                    Log.d(
//                        "allViews__ lastchild",
//                        it
//                    )
//                }
//
//            }

        val r = Rect().also { nodeInfo.getBoundsInScreen(it) }

        //Log the info you care about here... I choce classname and view resource name, because they are simple, but interesting.
        Log.d(
            "allViews",
            spacerString + nodeInfo.className + "|---|" + r.left + " " + r.right + " " + r.top + " " + r.bottom + nodeInfo.text?.toString() + " " + nodeInfo.viewIdResourceName + " " + nodeInfo.contentDescription?.toString()
        )
        for (i in 0 until nodeInfo.childCount) {
            logViewHierarchy(nodeInfo.getChild(i), depth + 1)
        }
    }


    private fun extractImageBtn(
        conversationNodeInfo: AccessibilityNodeInfo,
        name: String,
        contentDesc: String
    ): AccessibilityNodeInfo? {
        if (conversationNodeInfo.className == "android.widget.FrameLayout") {
            for (i in 0 until conversationNodeInfo.childCount)

                if (conversationNodeInfo.getChild(i)?.className == name && conversationNodeInfo.getChild(
                        i
                    )?.contentDescription == contentDesc
                ) {
                    Log.d("edittext", conversationNodeInfo.getChild(i).viewIdResourceName)
                    return conversationNodeInfo.getChild(i)
                }
        }
        return null
    }

    fun AccessibilityNodeInfo.performClick() {
        performAction(
            AccessibilityNodeInfoCompat.ACTION_CLICK
        )
    }

    fun AccessibilityEvent?.contentChangeType() {
        when (this?.contentChangeTypes) {
            AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION -> Log.d(
                "content",
                "content desc"
            )

            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED -> Log.d(
                "content",
                "pane appeard"
            )

            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED -> Log.d(
                "content",
                "pane dissapear"
            )

            AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_TITLE -> Log.d(
                "content",
                "pane title"
            )

            AccessibilityEvent.CONTENT_CHANGE_TYPE_STATE_DESCRIPTION -> Log.d(
                "content",
                "state desc"
            )

            AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE -> Log.d("content", "subtree")
            AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT -> Log.d("content", "type text")
            else -> Log.d("content", "undefined")
        }

    }


    // function for interacting with chat send btn. This function first set the text in edit text (like WA or insta) then after send btn is visible then
    // performing click on actual send btn (of WA of insta)

    fun AccessibilityNodeInfo?.performChatSendActions(
        msg: String,
        editTextId: String,
        vararg sendBtnId: String
    ) {

        this?.let {
            // setting text on edit text
            extractViewFromID(
                editTextId
            )?.setTextOnView(
                msg
            )


            Handler(Looper.myLooper()!!).postDelayed({

                for (id in sendBtnId) {
                    val btnId = extractViewFromID(id)
                    if (btnId != null) {
                        btnId.performClick()
                        break
                    }
                }

            }, 500)
            // perfrom send action on send btn

        }
    }

    fun AccessibilityNodeInfo?.whatsOrInstaListNull(): Boolean =
        when {
            isWhatsapp() or isWhatsapp4B() -> extractViewFromID(getContactNameId()) == null
            isInstaPkj() -> extractViewFromID(Constants.INSTAGRAM_RECYCLER_VIEW_ID) == null
            else -> false
        }


//    fun Context.isWhatsapp(nodeInfo: AccessibilityNodeInfo) =
//        nodeInfo.packageName == getString(R.string.whatsapp_package_name)
//
//    fun Context.getContactNameId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.whatsapp_contact_name_id)
//        else
//            getString(R.string.whatsapp4b_contact_name_id)
//
//
//    fun Context.getMessageTxtId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.whatsapp_message_text)
//        else
//            getString(R.string.whatsapp4b_message_text)
//
//    fun Context.getStatusId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.whatsapp_status_id)
//        else
//            getString(R.string.whatsapp4b_status_id)
//
//    fun Context.getChatDateId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.chat_date_id)
//        else
//            getString(R.string.chat4b_date_id)
//
//    fun Context.getNameInGroupId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.name_in_group_id)
//        else
//            getString(R.string.name4b_in_group_id)
//
//
//    fun Context.getEditTextId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.whatsapp_edittext_id)
//        else
//            getString(R.string.whatsapp4b_edittext_id)
//
//    fun Context.getSendBtnId(nodeInfo: AccessibilityNodeInfo) =
//        if (isWhatsapp(nodeInfo))
//            getString(R.string.whatsapp_sendbtn_id)
//        else
//            getString(R.string.whatsapp4b_sendbtn_id)
}