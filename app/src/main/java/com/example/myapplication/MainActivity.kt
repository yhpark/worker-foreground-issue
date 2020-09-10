package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class TestWorker(private val appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun buildNotification(): Notification {
        val channelId = "channel_id"

        val channel = NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_LOW).apply {
            description = "Test Channel"
        }
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(appContext, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("title")
            setContentText("body")
            setAutoCancel(false)
            setOngoing(true)
            setOnlyAlertOnce(true)
        }.build()
    }

    override suspend fun doWork(): Result {
        val notification = buildNotification()
        setForeground(ForegroundInfo(123, notification))

        Log.i("TestWorker", "doWork")

        return Result.success()
    }

}

class MainActivity : AppCompatActivity() {
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager
                .beginWith(OneTimeWorkRequest.from(TestWorker::class.java))
                .enqueue()

        Thread.sleep(100)

        workManager
                .beginWith(OneTimeWorkRequest.from(TestWorker::class.java))
                .enqueue()
    }
}