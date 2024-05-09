package com.example.todoapp.scheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.example.todoapp.data.db.entity.Task
import com.example.todoapp.scheduler.receiver.AlarmReceiver
import com.example.todoapp.utils.toDate

class SchedulerImpl(private val context: Context) : Scheduler {
    private val manager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_MESSAGE, task.name)
        }
        val time = task.dueDate.toDate()
        val calendar = Calendar.getInstance()
        calendar.time = time
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.MINUTE, -10)

        // check if should show notification
        if (calendar.timeInMillis > Calendar.getInstance().timeInMillis
        )
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    task.uid.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
    }

    override fun cancel(task: Task) {
        manager.cancel(
            PendingIntent.getBroadcast(
                context.applicationContext,
                task.uid.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
    }
}