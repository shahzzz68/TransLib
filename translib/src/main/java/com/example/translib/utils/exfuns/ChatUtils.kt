package com.example.translib.utils.exfuns

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.example.translator.models.chat.ChatMetadataModel
import com.example.translib.R
import com.example.translib.utils.Constants
import com.example.translib.utils.models.chat.ChatModel

fun ViewGroup.convertToSent(
    chatModel: ChatModel
) {

    val layoutGravity: Int
    val bgColor: Int
    val textColor: Int

    if (chatModel.isSent || chatModel.msgStatus?.contains(Constants.SENDING) == true) {
        layoutGravity = Gravity.END
        bgColor = R.color.insta_sent_chat_color
        textColor = R.color.white
    } else {
        layoutGravity = Gravity.START
        bgColor = R.color.insta_recieve_chat_color
        textColor = R.color.black_or_white
    }

    changeChatParamsAndAttrs(layoutGravity, bgColor, textColor)
}


fun ViewGroup.convertRepliedToSent(chatModel: ChatModel, extras: (Boolean) -> Unit) {
    val layoutGravity: Int
    val bgColor: Int
    val textColor: Int

    if (chatModel.isSent || chatModel.msgStatus?.contains(Constants.SENDING) == true) {
        layoutGravity = Gravity.END
        bgColor = R.color.insta_recieve_chat_color
        textColor = R.color.black_or_white
        extras(true) // true false represents isSent or not

    } else {
        layoutGravity = Gravity.START
        bgColor = R.color.insta_sent_chat_color
        textColor = R.color.white
        extras(false)
    }

    changeChatParamsAndAttrs(layoutGravity, bgColor, textColor)
}

fun ViewGroup.changeChatParamsAndAttrs(layoutGravity: Int, bgColor: Int, textColor: Int) {
    (layoutParams as LinearLayout.LayoutParams).apply {
        gravity = layoutGravity
    }.also { layoutParams = it }

    changeBgColor(bgColor)

    children.forEach {
        if (it is TextView) {
            it.setTextColor(context.getMyColor(textColor))
        }
    }
}

fun Context.createTextview(
    string: String = "",
    marginLeft: Float = 0f,
    marginTop: Float = 0f,
    marginRight: Float = 0f,
    marginBotton: Float = 0f,
    textGravity: Int = Gravity.START
): TextView {

    val lp = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(
            marginLeft.dpToPixel(this@createTextview),
            marginTop.dpToPixel(this@createTextview),
            marginRight.dpToPixel(this@createTextview),
            marginBotton.dpToPixel(this@createTextview)
        )
    }
    return TextView(this).apply {
        layoutParams = lp
        text = string
        gravity = textGravity
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

    }
}


fun LinearLayout.setupMetadata(chatMetadataModel: ChatMetadataModel?) {

    chatMetadataModel?.let { metaData ->
        metaData.apply {

//            messageDate?.let { date ->
//
//                addView(
//                    context.createTextview(
//                        date,
//                        marginTop = 16f,
//                        marginBotton = 16f,
//                        textGravity = Gravity.CENTER
//                    ),
//                    0
//                )
//            }

            groupMsgSenderNameOrNumber?.let { senderName ->
                val index = if (childCount > 1) 1 else 0
                addView(
                    context.createTextview(
                        senderName,
                        marginLeft = 14f,
                        marginBotton = 8f
                    ), index
                )

            }
        }
    }
}