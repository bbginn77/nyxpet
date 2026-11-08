package com.nyxcorp.nyxpet

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/**
 * One pending reminder at a time. When you leave the app, the page works out
 * when Nyx will next need you (hungry, lonely, litter, light left on at bedtime)
 * and schedules a notification for that moment. Opening the app cancels it.
 */
object Reminders {
    const val PREFS = "nyx"
    private const val CHANNEL = "nyx_needs"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun alarmIntent(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 1, Intent(ctx, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun schedule(ctx: Context, at: Long, title: String, text: String) {
        prefs(ctx).edit().putLong("at", at).putString("title", title).putString("text", text).apply()
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        am.set(AlarmManager.RTC_WAKEUP, at, alarmIntent(ctx))   // inexact: battery friendly, no special permission
    }

    fun cancel(ctx: Context) {
        prefs(ctx).edit().remove("at").apply()
        ctx.getSystemService(AlarmManager::class.java)?.cancel(alarmIntent(ctx))
        ctx.getSystemService(NotificationManager::class.java)?.cancel(1)
    }

    fun reschedule(ctx: Context) {
        val p = prefs(ctx)
        val at = p.getLong("at", 0L)
        if (at <= 0L) return
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        am.set(AlarmManager.RTC_WAKEUP, maxOf(at, System.currentTimeMillis() + 60_000L), alarmIntent(ctx))
    }

    fun show(ctx: Context) {
        val p = prefs(ctx)
        val title = p.getString("title", null) ?: return
        val text = p.getString("text", "") ?: ""
        p.edit().remove("at").apply()

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(CHANNEL, "Nyx needs you", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "When Nyx is hungry, lonely, or needs her litter scooped"
        nm.createNotificationChannel(channel)

        val open = PendingIntent.getActivity(
            ctx, 2,
            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_nyx)
            .setColor(0xFF5585EA.toInt())
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(open)
            .build()
        nm.notify(1, n)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = Reminders.show(context)
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) Reminders.reschedule(context)
    }
}
