package com.screenlock.app.features.scheduler.receiver

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.screenlock.app.features.lock.service.LockAccessibilityService
import com.screenlock.app.features.lock.receiver.MyDeviceAdminReceiver

class SchedulerReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SCHEDULE_START = "com.screenlock.app.ACTION_SCHEDULE_START"
        const val ACTION_SCHEDULE_END = "com.screenlock.app.ACTION_SCHEDULE_END"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SCHEDULE_START -> lockDevice(context)
            ACTION_SCHEDULE_END -> {
                // End window action reserved for future logic.
                // Keeping it explicit allows clean window scheduling semantics.
            }
            else -> lockDevice(context)
        }
    }

    private fun lockDevice(context: Context) {
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