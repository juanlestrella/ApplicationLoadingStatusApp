package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val notificationID = 0

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var filename: String
    private var status : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        custom_button.setOnClickListener {

            custom_button.buttonState = ButtonState.Clicked

            if(glide_id.isChecked){
                custom_button.buttonState = ButtonState.Loading
                filename = getString(R.string.glide)
                download(URL_GLIDE)
                //Toast.makeText(this, glide_id.text, Toast.LENGTH_SHORT).show()
            }else if (load_app_id.isChecked){
                custom_button.buttonState = ButtonState.Loading
                filename = getString(R.string.load_app)
                download(URL_APP_LOADING)
                //Toast.makeText(this, load_app_id.text, Toast.LENGTH_SHORT).show()
            }else if (retro_fit_id.isChecked){
                custom_button.buttonState = ButtonState.Loading
                filename = getString(R.string.retrofit)
                download(URL_RETRO)
                //Toast.makeText(this, retro_fit_id.text, Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(
            getString(R.string.notification_id),
            getString(R.string.notification_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if(id != null){

                downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))

                if(cursor.moveToFirst()){
                    status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when(status){
                        DownloadManager.STATUS_FAILED -> {
                            custom_button.buttonState = ButtonState.Completed
                            //send to notificationManager
                            notificationManager.sendNotification(getString(R.string.failed), filename)
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            custom_button.buttonState = ButtonState.Completed
                            notificationManager.sendNotification(getString(R.string.success), filename)
                        }
                    }
                }

            }
        }
    }

    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                description = getString(R.string.notification_description)
            }
            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }



    private fun download(URL: String) {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun NotificationManager.sendNotification(status: String, filename: String){
        // create an intent
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            .putExtra("status", status)
            .putExtra("filename", filename)

        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            getString(R.string.check_status),
            pendingIntent)
        // get an instance of NotificationCompat.Builder
        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_id)
        ).apply {
            //setContentIntent(pendingIntent).setAutoCancel(true)
            addAction(action).setAutoCancel(true)
            setSmallIcon(R.drawable.ic_assistant_black_24dp)
            setContentText(getString(R.string.notification_description))
            title = getString(R.string.notification_title)
            priority = NotificationCompat.PRIORITY_HIGH
        }
        notify(notificationID, builder.build())
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_APP_LOADING = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_RETRO = "https://github.com/square/retrofit"
    }

}