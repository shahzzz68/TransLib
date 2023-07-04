package com.example.translib.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.translib.R
import com.example.translib.utils.NotificationHelper.showOrHideNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun ComponentActivity.onPermissionResult(onGranted: () -> Unit): ActivityResultLauncher<String> {

    return registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            onGranted()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.

//            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
            showPermissionRequiredDialog(getString(R.string.notification_permission_required_message))
        }
    }
}


fun ComponentActivity.requestNotification(launcher: ActivityResultLauncher<String>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        //                shouldShowRequestPermissionRationale(permission)
        //
    } else {
        showOrHideNotification()
    }

}

fun Activity.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED
}


fun Context.showPermissionRequiredDialog(message: String) {
    MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.notification_permission_required))
        .setMessage(getString(R.string.permission_required_msg, message))
        .setPositiveButton(getString(R.string.allow)) { d, _ ->
            d.dismiss()

            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", packageName, null)
            }.also {
                startActivity(it)
            }
        }
        .setNegativeButton(getString(R.string.cancel)) { d, _ ->
            d.dismiss()
        }.show()
}