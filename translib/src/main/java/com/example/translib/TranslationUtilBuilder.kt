package com.example.translib

import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.example.translib.utils.NotificationHelper
import com.example.translib.windows.chatwindows.FloatingBaseChatWindow

object TranslationUtilBuilder {

    var isSingleActivity: Boolean = false
    var notificationIconId: Int = -1
    var searchIconId: Int = -1
    var searchHandleId: Int = -1
    var isSubscribed = false
    var isUnlimitedScreenTranslations = false
    var screenTranslationsLimit = 30

    var screenTranslatorWindowBgColor: Int = -1

    var initializeChatWindow: (AccessibilityNodeInfo.(/*inner lambda*/IWindowCallbacks) -> Unit)? =
        null


    fun isSingleActivity(isSingle: Boolean) = apply {
        isSingleActivity = isSingle
    }

    fun setNotificationParentClass(parentStackClass: Class<*>) = apply {
        NotificationHelper.parentStackClass = parentStackClass
    }

    fun setNotificationClickedStackClass(clickedStackClass: Class<*>) = apply {
        NotificationHelper.clickedStackClass = clickedStackClass
    }

    fun setNotificationAction(notificationAction: (NotificationCompat.Builder.() -> Unit)?) =
        apply {
            NotificationHelper.notificationAction = notificationAction
        }


    fun setChatWindows(initializeChatWindow: (AccessibilityNodeInfo.(IWindowCallbacks) -> Unit)?) =
        apply {
            this.initializeChatWindow = initializeChatWindow
        }


    fun setUnlimitedScreenTranslation(value: Boolean) {
        isUnlimitedScreenTranslations = value
    }

    fun setScreenTranslationsDailyLimit(value: Int) {
        screenTranslationsLimit = value
    }

    fun setTranslationSearchIcon(@DrawableRes iconId: Int) = apply {
        searchIconId = iconId
    }

    fun setTranslationHandleIcon(@DrawableRes iconId: Int) = apply {
        searchHandleId = iconId
    }

    fun setSubscribed(isSubscribed: Boolean) = apply {
        this.isSubscribed = isSubscribed
    }

    fun setScreenTranslatorWindowColor(@ColorInt color: Int) = apply {
        screenTranslatorWindowBgColor = color
    }


    fun setNotificationIcon(iconId: Int) = apply {
        notificationIconId = iconId
    }

    fun setWindowOpenCloseCallback() {}
}

interface IWindowCallbacks {
    fun onWindowInitialized(floatingBaseChatWindow: FloatingBaseChatWindow)
    fun onWindowOpenClosed(isOpen: Boolean)
}