package com.example.translib.utils

object Constants {

    var shouldLoadTranslatorInter = true

    const val TWENTY_FOUR_HOURS_TO_MILLIS = 86400000
//    const val DAILY_SCREEN_TRANSLATION_LIMIT = 25
    const val UPGRADE_ID = 101010

    ///// remote config constants
    const val ONLY_SPLASH_APP_OPEM = "only_splash_app_open"
    const val SHOW_APP_OPEN = "show_app_open"
    const val SHOW_MAIN_NATIVE = "show_main_native"
    const val SHOW_CHAT_TRANSLATOR_NATIVE = "show_chat_translator_native"
    const val SHOW_TRANSLATOR_NATIVE = "show_translator_native"
    const val SHOW_TRANSLATOR_INTERSTITIAL = "show_translator_interstitial"
    const val SHOW_SPLASH_NATIVE = "show_splash_native"
    const val SHOW_SPLASH_INTERSTITIAL = "show_splash_interstitial"
    const val SHOW_ON_INTERSTITIAL = "show_on_interstitial"
    const val SHOW_MAX_INTERSTITIAL = "show_max_interstitial"
    const val SHOW_AD_BORDER = "show_ad_border"
    const val SHOW_UPDATE_DIALOG = "show_update_dialog"
    const val SHOW_CHAT_BANNER = "show_chat_banner"
    const val SHOW_CHAT_NATIVE = "show_chat_native"
    const val SHOW_GENERAL_TRANSLATOR = "show_general_translator"

    const val IS_HUAWIE: Boolean = true


    const val NOTI_EXTRA: String = "update"

    // prefs constants
    const val RATING_DONE = "ratingDone"
    const val FIRST_TIME = "FirstTime"
    const val TUTORIAL_DONE = "tutorialDone"

    const val APP_LANG = "appLang"

    const val REWARDED_CURRENT_MILLIS = "isRewardedMillis"
    const val APP_OPENING_TIME_MILLIS = "appOpeningTime"

    const val S_TRANSLATOR_INPUT = "sTranslatorInput"
    const val S_TRANSLATOR_OUT = "sTranslatorOutput"

    const val S_TRANSLATOR_LIMIT = "sTranslatorLimit"


    const val AGREE = "agree"


    // general constants
    const val JUST_NOW = "Just Now"
    const val SENDING = "Sending"
    const val FRAME_LAYOUT = "android.widget.FrameLayout"
    const val VIEW_GROUP = "android.view.ViewGroup"
    const val RECYCLER_VIEW = "androidx.recyclerview.widget.RecyclerView"
    const val LIST_VIEW = "android.widget.ListView"
    const val TEXT_VIEW = "android.widget.TextView"
    const val IMAGE_VIEW = "android.widget.ImageView"
    const val VIEW = "android.view.View"

    // translator pref constants
    const val MAIN_BTN_ON = "main_translator_on"
    const val INSTA_TRANSLATOR_ON = "insta_translator_on"
    const val WA_TRANSLATOR_ON = "wa_translator_on"
    const val WA_4B_TRANSLATOR_ON = "wa_4b_translator_on"
    const val SCREEN_TRANSLATOR_ON = "general_translator_on"


    /////////////////////////////////////////////  INSTA CONSTANTS ////////////////////////////////////
    // instagram pkj
    const val INSTAGRAM_PKJ = "com.instagram.android"


    // instagram ui ids
    const val INSTAGRAM_CONVERSATION_ACTIVITY = "com.instagram.modal.ModalActivity"
    const val INSTAGRAM_EDITTEXT_ID = "com.instagram.android:id/row_thread_composer_edittext"
    const val INSTAGRAM_SENDBTN_ID = "com.instagram.android:id/row_thread_composer_button_send"
    const val INSTAGRAM_RECYCLER_VIEW_ID = "com.instagram.android:id/message_list"

    // chat related ids
    const val INSTAGRAM_USER_NAME_ID = "com.instagram.android:id/other_user_full_name_or_username"
    const val INSTAGRAM_NETWORK_ATTRIBUTION_NAME_ID = "com.instagram.android:id/network_attribution"
    const val INSTAGRAM_HEADER_USER_NAME_ID = "com.instagram.android:id/thread_title"


    // messages
    const val INSTAGRAM_MESSAGE_CONTENT_ID = "com.instagram.android:id/message_content"
    const val INSTAGRAM_DIRECT_TEXT_MESSAGE_ID =
        "com.instagram.android:id/direct_text_message_text_view"
    const val INSTAGRAM_REPLIED_MESSAGE_ID =
        "com.instagram.android:id/direct_expandable_text_message_text_view"
    const val INSTAGRAM_LIKE_MESSAGE_ID = "com.instagram.android:id/direct_like_message_image_view"
    const val INSTAGRAM_MESSAGE_STATUS_ID = "com.instagram.android:id/message_status"
    const val INSTAGRAM_GROUP_MESSAGE_SENDER_ID = "com.instagram.android:id/username"


    // links
    const val INSTAGRAM_LINK_PREVIEW_CONTAINER_ID =
        "com.instagram.android:id/link_preview_container"
    const val INSTAGRAM_LINK_MESSAGE_TEXT_ID = "com.instagram.android:id/message_text"
    const val INSTAGRAM_LINK_PREVIEW_TITLE_ID = "com.instagram.android:id/link_preview_title"
    const val INSTAGRAM_LINK_PREVIEW_SUMMARY_ID = "com.instagram.android:id/link_preview_summary"
    const val INSTAGRAM_FORWARD_BTN_ID = "com.instagram.android:id/forwarding_shortcut_button"


///////////////////////////// WHATSAPP CONSTANTS/////////////////////////////////////////

    const val WA_PKJ = "com.whatsapp"
    const val WA_4B_PKJ = "com.whatsapp.w4b"

    const val PENDING = "Pending"
    const val DELIVERED = "Delivered"
    const val READ = "Read"
    const val SENT = "Sent"

    /// whatsapp ui ids

    const val WA_CONVERSATION_ACTIVITY = "com.whatsapp.Conversation"

    const val WA_EDITTEXT_ID = "com.whatsapp:id/entry"
    const val WA_SENDBTN_ID = "com.whatsapp:id/send"
    const val WA_HEADER_USER_NAME_ID = "com.whatsapp:id/conversation_contact_name"
    const val WA_CONTACT_STATUS_ID = "com.whatsapp:id/conversation_contact_status"


    // whatsapp chat list view ids
    const val WA_MSG_TEXT = "com.whatsapp:id/message_text"
    const val WA_LIST = "android:id/list"
    const val WA_MSG_STATUS = "com.whatsapp:id/status"
    const val WA_CHAT_DATE = "com.whatsapp:id/date"
    const val WA_NAME_IN_GROUP = "com.whatsapp:id/name_in_group"
    const val WA_NAME_OR_NUMBER_IN_GROUP = "com.whatsapp:id/name_in_group_tv"
    const val WA_UNSAVED_NAME_IN_GROUP = "com.whatsapp:id/pushname_in_group_tv"
    const val WA_QOUTED_MSG_FRAME = "com.whatsapp:id/quoted_message_frame"
    const val WA_QOUTED_TITLE = "com.whatsapp:id/quoted_title"
    const val WA_QOUTED_TEXT = "com.whatsapp:id/quoted_text"


    /// whatsapp4b ui ids

    const val WA_EDITTEXT_ID_4B = "com.whatsapp.w4b:id/entry"
    const val WA_SENDBTN_ID_4B = "com.whatsapp.w4b:id/send"
    const val WA_HEADER_USER_NAME_ID_4B = "com.whatsapp.w4b:id/conversation_contact_name"
    const val WA_CONTACT_STATUS_ID_4B = "com.whatsapp.w4b:id/conversation_contact_status"


    // whatsapp4b chat related ids

    const val WA_MSG_TEXT_4B = "com.whatsapp.w4b:id/message_text"
    const val WA_LIST_4B = "com.whatsapp.w4b:id/list"
    const val WA_MSG_STATUS_4B = "com.whatsapp.w4b:id/status"
    const val WA_CHAT_DATE_4B = "com.whatsapp.w4b:id/date"
    const val WA_NAME_IN_GROUP_4B = "com.whatsapp.w4b:id/name_in_group"
    const val WA_NAME_OR_NUMBER_IN_GROUP_4B = "com.whatsapp.w4b:id/name_in_group_tv"
    const val WA_UNSAVED_NAME_IN_GROUP_4B = "com.whatsapp.w4b:id/pushname_in_group_tv"
    const val WA_QOUTED_MSG_FRAME_4B = "com.whatsapp.w4b:id/quoted_message_frame"
    const val WA_QOUTED_TITLE_4B = "com.whatsapp.w4b:id/quoted_title"
    const val WA_QOUTED_TEXT_4B = "com.whatsapp.w4b:id/quoted_text"


    val constIdsArray = arrayOf(
        INSTA_TRANSLATOR_ON,
        WA_TRANSLATOR_ON,
        WA_4B_TRANSLATOR_ON,
        SCREEN_TRANSLATOR_ON
    )
}