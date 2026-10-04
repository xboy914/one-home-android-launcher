package com.example.demolauncher.ui.launcher

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.MediaStore

/** Opens the device's installed media apps; media access stays with those apps. */
object SystemMediaApps {
    fun openCamera(context: Context): Boolean = launchFirst(context, listOfNotNull(
        context.packageManager.getLaunchIntentForPackage("com.sec.android.app.camera"),
        Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA),
    ))

    fun openGallery(context: Context): Boolean = launchFirst(context, listOfNotNull(
        context.packageManager.getLaunchIntentForPackage("com.sec.android.gallery3d"),
        context.packageManager.getLaunchIntentForPackage("com.miui.gallery"),
        context.packageManager.getLaunchIntentForPackage("com.google.android.apps.photos"),
        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY),
        Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    ))

    private fun launchFirst(context: Context, intents: List<Intent>): Boolean {
        for (intent in intents) {
            try {
                if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: ActivityNotFoundException) {
                // Try the next installed gallery or camera handler.
            } catch (_: SecurityException) {
                // Some OEM apps expose an icon but keep their launcher activity private.
            }
        }
        return false
    }
}
