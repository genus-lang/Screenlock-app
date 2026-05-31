package com.screenlock.app.features.lock.presentation

import android.app.admin.DevicePolicyManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import com.screenlock.app.features.lock.receiver.MyDeviceAdminReceiver
import com.screenlock.app.features.lock.service.LockAccessibilityService
import com.screenlock.app.features.voice_lock.service.VoiceLockService
import com.screenlock.app.features.scheduler.receiver.SchedulerReceiver
import java.util.Calendar

class LockViewModel : ViewModel() {

    companion object {
        private const val PREFS_NAME = "screenlock_prefs"
        private const val KEY_VOICE_LOCK_ENABLED = "voice_lock_enabled"
        private const val KEY_SCHEDULER_ENABLED = "scheduler_enabled"
        private const val KEY_SCHEDULER_START_HOUR = "scheduler_start_hour"
        private const val KEY_SCHEDULER_START_MINUTE = "scheduler_start_minute"
        private const val KEY_SCHEDULER_END_HOUR = "scheduler_end_hour"
        private const val KEY_SCHEDULER_END_MINUTE = "scheduler_end_minute"
        private const val KEY_FLOATING_LOCK_ENABLED = "floating_lock_enabled"
        private const val KEY_FLOATING_LOCK_SIZE = "floating_lock_size"
        private const val KEY_FLOATING_LOCK_ALPHA = "floating_lock_alpha"
        private const val KEY_LOCK_EVENTS = "lock_events"
        
        private const val KEY_SETTING_VIBRATION = "setting_vibration"
        private const val KEY_SETTING_SOUND = "setting_sound"
        private const val KEY_SETTING_LOCK_SCREEN_OVERLAY = "setting_lock_screen_overlay"

        fun logLockEvent(context: Context, source: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val events = prefs.getStringSet(KEY_LOCK_EVENTS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            events.add("${System.currentTimeMillis()}|$source")
            prefs.edit().putStringSet(KEY_LOCK_EVENTS, events).apply()
        }

        fun getLockEvents(context: Context): List<LockEvent> {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val events = prefs.getStringSet(KEY_LOCK_EVENTS, emptySet()) ?: emptySet()
            return events.mapNotNull {
                val parts = it.split("|", limit = 2)
                if (parts.size == 2) {
                    val ts = parts[0].toLongOrNull()
                    if (ts != null) LockEvent(ts, parts[1]) else null
                } else null
            }.sortedByDescending { it.timestamp }
        }

        fun clearLockEvents(context: Context) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_LOCK_EVENTS)
                .apply()
        }

        fun isVibrationEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SETTING_VIBRATION, true)
        }

        fun setVibrationEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_SETTING_VIBRATION, enabled).apply()
        }

        fun isSoundEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SETTING_SOUND, true)
        }

        fun setSoundEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_SETTING_SOUND, enabled).apply()
        }

        fun isShowOnLockScreenEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SETTING_LOCK_SCREEN_OVERLAY, false)
        }

        fun setShowOnLockScreenEnabled(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_SETTING_LOCK_SCREEN_OVERLAY, enabled).apply()
        }

        fun resetAllSettings(context: Context) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        }
    }

    fun lockDevice(context: Context, source: String = "App Button") {
        logLockEvent(context, source)
        // Try Accessibility Service first (Android 9+) as it doesn't break fingerprint unlock
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && LockAccessibilityService.instance != null) {
            LockAccessibilityService.instance?.lockDeviceSecurely()
        } else {
            // Fallback to Device Admin
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

            if (devicePolicyManager.isAdminActive(componentName)) {
                devicePolicyManager.lockNow()
            }
        }
    }

    fun canLockNow(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && LockAccessibilityService.instance != null) {
            return true
        }
        return isDeviceAdminActive(context)
    }

    fun isAccessibilityActive(): Boolean {
        return LockAccessibilityService.instance != null
    }

    fun isDeviceAdminActive(context: Context): Boolean {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        return devicePolicyManager.isAdminActive(componentName)
    }
    
    fun getDeviceAdminComponent(context: Context): ComponentName {
        return ComponentName(context, MyDeviceAdminReceiver::class.java)
    }

    fun toggleVoiceLock(context: Context, enable: Boolean) {
        val intent = Intent(context, VoiceLockService::class.java)
        if (enable) {
            context.startForegroundService(intent)
        } else {
            context.stopService(intent)
        }
    }

    fun setVoiceLockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_VOICE_LOCK_ENABLED, enabled)
            .apply()
    }

    fun isVoiceLockEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_VOICE_LOCK_ENABLED, false)
    }

    fun setSchedulerEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SCHEDULER_ENABLED, enabled)
            .apply()
    }

    fun isSchedulerEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SCHEDULER_ENABLED, false)
    }

    fun saveSchedulerWindow(context: Context, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_SCHEDULER_START_HOUR, startHour)
            .putInt(KEY_SCHEDULER_START_MINUTE, startMinute)
            .putInt(KEY_SCHEDULER_END_HOUR, endHour)
            .putInt(KEY_SCHEDULER_END_MINUTE, endMinute)
            .apply()
    }

    fun getSchedulerWindow(context: Context): SchedulerWindow {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return SchedulerWindow(
            startHour = prefs.getInt(KEY_SCHEDULER_START_HOUR, 22),
            startMinute = prefs.getInt(KEY_SCHEDULER_START_MINUTE, 0),
            endHour = prefs.getInt(KEY_SCHEDULER_END_HOUR, 6),
            endMinute = prefs.getInt(KEY_SCHEDULER_END_MINUTE, 0)
        )
    }

    fun setFloatingLockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_FLOATING_LOCK_ENABLED, enabled)
            .apply()
    }

    fun isFloatingLockEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_FLOATING_LOCK_ENABLED, false)
    }

    fun setFloatingLockSize(context: Context, size: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_FLOATING_LOCK_SIZE, size)
            .apply()
    }

    fun getFloatingLockSize(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_FLOATING_LOCK_SIZE, 0.52f)
    }

    fun setFloatingLockAlpha(context: Context, alpha: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_FLOATING_LOCK_ALPHA, alpha)
            .apply()
    }

    fun getFloatingLockAlpha(context: Context): Float {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_FLOATING_LOCK_ALPHA, 0.72f)
    }

    fun toggleFloatingLock(context: Context, enable: Boolean) {
        val intent = Intent(context, Class.forName("com.screenlock.app.features.floating_lock.service.FloatingLockService"))
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.stopService(intent)
        }
    }

    fun toggleSmartScheduler(
        context: Context,
        enable: Boolean,
        startHour: Int = 23,
        startMinute: Int = 0,
        endHour: Int = 7,
        endMinute: Int = 0
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startIntent = Intent(context, SchedulerReceiver::class.java).apply {
            action = SchedulerReceiver.ACTION_SCHEDULE_START
        }
        val endIntent = Intent(context, SchedulerReceiver::class.java).apply {
            action = SchedulerReceiver.ACTION_SCHEDULE_END
        }

        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            2002,
            endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (enable) {
            val startCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val endCalendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If time has passed today, schedule for tomorrow
            if (startCalendar.timeInMillis <= System.currentTimeMillis()) {
                startCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (endCalendar.timeInMillis <= System.currentTimeMillis()) {
                endCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                startPendingIntent
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                endCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                endPendingIntent
            )
        } else {
            alarmManager.cancel(startPendingIntent)
            alarmManager.cancel(endPendingIntent)
        }
    }
}

data class SchedulerWindow(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
)

data class LockEvent(
    val timestamp: Long,
    val source: String
)