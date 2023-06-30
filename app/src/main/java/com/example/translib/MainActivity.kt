package com.example.translib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.translib.utils.Constants
import com.example.translib.utils.accessibility.AccessibilityServiceUtils
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractTextFromId
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.extractViewFromID
import com.example.translib.utils.accessibility.AccessibilityServiceUtils.isInstaPkj
import com.example.translib.utils.accessibility.WAaccessibilityUtils.getContactNameId
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp
import com.example.translib.utils.accessibility.WAaccessibilityUtils.isWhatsapp4B
import com.example.translib.utils.exfuns.isTranslatorOn

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        TranslationUtilBuilder.setChatWindows {
            when {
                isInstaPkj() && isTranslatorOn(Constants.INSTA_TRANSLATOR_ON) -> {
                    // if not in chat activity then return
                    if (extractViewFromID(Constants.INSTAGRAM_RECYCLER_VIEW_ID) == null) return@setChatWindows

                    // extracting user name of current chat in insta

                    AccessibilityServiceUtils.userName =
                        extractTextFromId(Constants.INSTAGRAM_HEADER_USER_NAME_ID)


                    // initializing insta floating window
//        FloatingInstaWindow(
//            this@MyAccessibilityService, this@MyAccessibilityService
//        ).apply {
//            onWindowOpenCLose = windowOpenCloseCallback
//
//        }.also {
//            floatingWindow = it
//        }


                }

                isWhatsapp() && isTranslatorOn(Constants.WA_TRANSLATOR_ON) || isWhatsapp4B() && isTranslatorOn(
                    Constants.WA_4B_TRANSLATOR_ON
                ) -> {
                    if (extractViewFromID(Constants.WA_LIST) == null) return@setChatWindows

                    AccessibilityServiceUtils.userName = extractTextFromId(getContactNameId())

                    // initializing WA floating window

//        FloatingWAwindow(
//            this@MyAccessibilityService, this@MyAccessibilityService
//        ).apply {/// on window open callback
//            onWindowOpenCLose = windowOpenCloseCallback
//
//        }.also {
//            floatingWindow = it
//        }
                }
            }
        }
    }


}