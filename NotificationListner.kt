import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val geminiApiKey = BuildConfig.GEMINI_API_KEY

    private val gemini = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = geminiApiKey
    )

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val packageName = statusBarNotification.packageName
                val notification = statusBarNotification.notification
                val originalTitle = notification.extras.getString(Notification.EXTRA_TITLE) ?: "Notification"
                val originalText = notification.extras.getString(Notification.EXTRA_TEXT) ?: ""
                val notificationKey = statusBarNotification.key

                val summary = gemini.generateContent(
                    "Summarize this notification in one short sentence: ${originalText.take(1000)}"
                ).text ?: "No summary available"

                cancelNotification(notificationKey)

                createSummaryNotification(originalTitle, summary, packageName)

            } catch (e: Exception) {
                Log.e("NotificationListener", "Error processing notification: ${e.message}", e)
            }
        }
    }

    private fun getAppIcon(packageName: String): Bitmap? {
        return try {
            val pm = packageManager
            val drawable = pm.getApplicationIcon(packageName)
            (drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error fetching app icon: ${e.message}")
            null
        }
    }

    private fun createSummaryNotification(originalTitle: String, summaryText: String, packageName: String) {
        val channelId = "summary_$packageName"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Summarized Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Summarized: $originalTitle")
            .setContentText(summaryText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(getAppIcon(packageName))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(packageName.hashCode(), notification)
    }

    private fun cancelNotification(key: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cancelNotification(key)
            } else {
                activeNotifications.firstOrNull { it.key == key }?.let {
                    cancelNotification(it.packageName, it.tag, it.id)
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error canceling notification: ${e.message}")
        }
    }
}
