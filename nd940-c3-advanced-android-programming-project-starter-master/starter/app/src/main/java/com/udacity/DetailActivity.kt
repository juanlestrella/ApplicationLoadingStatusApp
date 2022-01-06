package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        val status = intent.getStringExtra("status")
        val fileName = intent.getStringExtra("filename")

        filename_value_id.text = fileName
        status_value_id.text = status
        if (status == getString(R.string.success)){
            status_value_id.setTextColor(Color.GREEN)
        }else{
            status_value_id.setTextColor(Color.RED)
        }

        back_button_id.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        notificationManager.cancelAll()
    }

}
