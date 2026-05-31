package com.screenlock.app.features.lock.receiver

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.screenlock.app.R
import com.screenlock.app.features.lock.service.LockAccessibilityService

class LockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // Custom action triggered by clicking the widget
        if (intent.action == "com.screenlock.app.ACTION_LOCK_DEVICE") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && LockAccessibilityService.instance != null) {
                LockAccessibilityService.instance?.lockDeviceSecurely()
                return
            }

            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

            if (devicePolicyManager.isAdminActive(componentName)) {
                devicePolicyManager.lockNow()
            }
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget_lock)
    
    // Set up the intent that fires when the widget is clicked
    val intent = Intent(context, LockWidgetProvider::class.java).apply {
        action = "com.screenlock.app.ACTION_LOCK_DEVICE"
    }
    
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    // Attach the click listener to the RelativeLayout in our widget xml
    views.setOnClickPendingIntent(R.id.widget_lock_button, pendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}