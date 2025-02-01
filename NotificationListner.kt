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

    // Replace with your Gemini API key (step 7 will show how to secure this)
    private val gemini = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyB6y9bBQRtQgOl_mVzCNspTP2n6Gx11LQ0"
    )

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Step 1: Get notification details
                val packageName = statusBarNotification.packageName
                val notification = statusBarNotification.notification
                val originalTitle = notification.extras.getString(Notification.EXTRA_TITLE) ?: "Notification"
                val originalText = notification.extras.getString(Notification.EXTRA_TEXT) ?: ""
                val notificationKey = statusBarNotification.key

                // Step 2: Summarize with Gemini
                val summary = gemini.generateContent(
                    "Summarize this notification in one short sentence: ${originalText.take(1000)}"
                ).text ?: "No summary available"

                // Step 3: Dismiss original notification
                cancelNotification(notificationKey)

                // Step 4: Create new summarized notification
                createSummaryNotification(
                    originalTitle = originalTitle,
                    summaryText = summary,
                    packageName = packageName
                )

            } catch (e: Exception) {
                Log.e("NotificationListener", "Error: ${e.message}")
            }
        }
    }

    private fun getAppIcon(packageName: String): Bitmap? {
        return try {
            val pm = packageManager
            val drawable = pm.getApplicationIcon(packageName)
            (drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun createSummaryNotification(
        originalTitle: String,
        summaryText: String,
        packageName: String
    ) {
        val channelId = "summary_$packageName"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                channelId,
                "Summarized Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { notificationManager.createNotificationChannel(this) }
        }

        // Build notification
        NotificationCompat.Builder(this, channelId)
            .setContentTitle("Summarized: $originalTitle")
            .setContentText(summaryText)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Default icon
            .apply {
                getAppIcon(packageName)?.let { icon ->
                    setLargeIcon(icon)
                }
            }
            .setAutoCancel(true)
            .build()
            .also { notificationManager.notify(packageName.hashCode(), it) }
    }

    private fun cancelNotification(key: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cancelNotification(key)
        } else {
            activeNotifications.firstOrNull { it.key == key }?.let {
                cancelNotification(it.packageName, it.tag, it.id)
            }
        }
    }
}