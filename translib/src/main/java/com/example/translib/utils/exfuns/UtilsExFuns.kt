package com.example.translib.utils.exfuns

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.utils.translation.TranslationApi
import com.example.translib.R
import com.example.translib.chatservice.MyAccessibilityService
import com.example.translib.utils.CoroutineUtils
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun isAndroidOreo() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isAndroidMarshmallow() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isAndroidL_MR1() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1

fun Resources.getStatusBarHeight(): Int {
    getIdentifier("status_bar_height", "dimen", "android").also {
        return if (it > 0)
            getDimensionPixelSize(it)
        else
            -1
    }
}


fun Context.getApplicationIcon(): Drawable? =
    try {
        packageManager.getApplicationIcon(applicationInfo)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }


fun Intent.makeSingleTop(): Intent {
    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    return this
}

fun Context.canDrawOverlay(): Boolean {
    // Android Version is lesser than Marshmallow or
    // the API is lesser than 23
    // doesn't need 'Display over other apps' permission enabling.

//    return if (isAndroidMarshmallow())
//        Settings.canDrawOverlays(this)
//    else
//        true

    return true // temporary disabling

}

fun Context.requestDrawOverApps() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        } catch (ignored: ActivityNotFoundException) {
            showToast("Not supported on your device", Toast.LENGTH_LONG)
        }

    }
}

fun Context.openAccessibilitySettings() {
//    startActivityForResult(, 0)
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}


fun Context.isInternetConnected(): Boolean {

    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
            else -> false
        }
    } else {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }
}


fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

fun rateUs(context: Context) {
    val intent = Intent(
        Intent.ACTION_VIEW, Uri.parse(
            "https://play.google.com/store/apps/details?id=" +
                    context.packageName
        )
    )
    context.startActivity(intent)
}

fun Context.isMyServiceRunning(serviceClass: Class<MyAccessibilityService>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

var am: AccessibilityManager? = null
fun Context.isAccessibilitySettingsOn(service: Class<out AccessibilityService?> = MyAccessibilityService::class.java): Boolean {
    var accessibilityEnabled = 0
    val service =
        packageName + "/" + MyAccessibilityService::class.java.canonicalName
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: Settings.SettingNotFoundException) {
    }
    val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        val settingValue = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    return true
                }
            }
        }
    }
    return false


//    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
//
//    val enabledServices =
//        am!!.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
//
//    for (enabledService in enabledServices) {
//        val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
//        if (enabledServiceInfo.packageName.equals(packageName) && enabledServiceInfo.name.equals(
//                service.name
//            )
//        ) return true
//    }
//
//    return false
}

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.askPermission() {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName")
    )
    startActivityForResult(intent, 102)
}


// startnewActivity
inline fun <reified A : Activity> Context.startNewActivity() {
    this.startActivity(Intent(this, A::class.java))
}

inline fun <reified A : Activity> Context.startActivitywithExtras(extras: Intent.() -> Unit) {
    val intent = Intent(this, A::class.java)
    extras(intent)
    this.startActivity(intent)
}


fun RecyclerView.adapterAndManager(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    isHorizontal: Boolean = false,
    isGrid: Boolean = false,
    spanCount: Int = 3,
    hasFixSize: Boolean = false,
    isStackFromEnd: Boolean = false
) {
    this.layoutManager =
        when {
            isGrid -> GridLayoutManager(context, spanCount)
            isHorizontal -> LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(context).apply {
                stackFromEnd = isStackFromEnd

//                 for scrolling up when keyboard open
//                addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
//                    if (bottom < oldBottom) {
//                        smoothScrollToPosition(this@adapterAndManager, null, adapter.itemCount);
//                    }
//                }
            }
        }
    setHasFixedSize(hasFixSize)
//    this.itemAnimator = DefaultItemAnimator()
    this.adapter = adapter

}

fun Context.copyText(text: String) {
    if (text.isNotEmpty()) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied", text)
        clipboard.setPrimaryClip(clip)
        showToast("text copied to clipboard")
    } else
        showToast("Text is empty")

}

fun Context.getIdFromName(drawableName: String): Int {
    return resources.getIdentifier(
        drawableName.lowercase(Locale.ENGLISH),
        "string",
        packageName
    )
}

fun EditText.isTextNotEmpty() =
    if (this.text.toString().isNotEmpty())
        true
    else {
        error = context.getString(R.string.input_error)
        false
    }


//fun AppCompatActivity.showSubsDialog() {
//    if (!PaymentSingleton.isAlreadyPurchased(this) && !isFinishing) {
//
//        //context.getString(R.string.priceSub, priceList?.priceText)
//        val dialogNewFolder = AlertDialog.Builder(this)
//        val inflater: LayoutInflater = layoutInflater
//        //this is what I did to added the layout to the alert dialog
//        val layout = inflater.inflate(R.layout.dialog_subs, null)
//        dialogNewFolder.setView(layout)
//
//        val priceList = PaymentSingleton.getInstance()!!
//            .getSubscriptionListingDetails(getString(R.string.monthlySubscriptionId))
//        val price = if (priceList?.priceText.isNullOrEmpty()) {
//            layout.perMonthTxt.visibility = View.GONE
//            "No Data "
//        } else
//            priceList?.currency + " " + priceList?.priceText + " / "
//
//        val alertDialog = dialogNewFolder.create()
//        //alertDialog.setCancelable(false);
//
//        //        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        layout.priceTxt.text = price
//
//        layout.subscribe_btn.setOnClickListener {
//            alertDialog.dismiss()
//            PaymentSingleton.subscription(this)
//        }
//
//        layout.cross.setOnClickListener {
//            alertDialog.dismiss()
//        }
//
//        alertDialog.show()
//    } else
//        showToast("Already purchased")
//}


fun String.runTranslation(
    outputCode: String,
    inputCode: String? = null,
    onTranslated: ((String) -> Unit)? = null
) {
    CoroutineUtils.executeIO(
        {
            TranslationApi.executeTranslation(this, outputCode, inputCode)
        },
        {
            onTranslated?.invoke(it.toString())
        })
}


fun View.hideKeyboard(context: Context) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}

fun Long.runAfter(onComplete: () -> Unit) {
    Looper.myLooper()?.let { Handler(it).postDelayed({ onComplete.invoke() }, this) }
}


inline fun <T : Any, R> T?.ifNotNull(callback: (T) -> R): R? {
    return this?.let(callback)
}


inline fun <T : Any, R> T?.ifNull(callback: () -> R): R? {
    return if (this == null) callback() else null
}

var safeOpMultipleClickCheck = true
fun preventMultipleInvocations(onResult: () -> Unit, delay: Long = 700) {
    if (safeOpMultipleClickCheck) {
        onResult.invoke()
        safeOpMultipleClickCheck = false
        delay.runAfter {
            safeOpMultipleClickCheck = true
        }
    }
}


fun Context.sendOnWhatsApp(
    phoneNumber: String = "123",
    messageText: String = "abc",
    useWhatsAppBusiness: Boolean = false
) {
//    val url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" +
    URLEncoder.encode(messageText, "UTF-8")
//    val intent = Intent(Intent.ACTION_VIEW)
////    intent.action = Intent.ACTION_SEND
////    if (useWhatsAppBusiness) {
////        intent.setPackage("com.whatsapp.w4b")
////    } else {
//////        intent.setPackage("com.whatsapp")
////        intent.setPackage("com.gbwhatsapp")
////    }
//    URLEncoder.encode(messageText, "UTF-8")
//    intent.data = Uri.parse(url)
//    intent.resolveActivity(packageManager)?.let {
//        startActivity(intent)
//    } ?: kotlin.run {
//        showToast(getString(R.string.app_not_installed))
//    }

    try {
        //linking for whatsapp
        val uri = Uri.parse(
            "whatsapp://send?phone=" + phoneNumber + "&text=" +
                    URLEncoder.encode(messageText, "UTF-8")
        )

        val i = Intent(Intent.ACTION_VIEW, uri)
//        i.setPackage("com.gbwhatsapp")
        startActivity(i)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(this, "Could not found app.", Toast.LENGTH_SHORT).show()
    }

}


/** "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
"hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
"EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
"yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
"yyMMddHHmmssZ"-------------------- 010704120856-0700
"K:mm a, z" ----------------------- 0:08 PM, PDT
"h:mm a" -------------------------- 12:08 PM
"EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01*/

var df: DateFormat? = null
fun formattedTime(): String {
    if (df == null)
        df = SimpleDateFormat("h:mm aaa", Locale.US)
    return df!!.format(Calendar.getInstance().time).lowercase().trim()
}