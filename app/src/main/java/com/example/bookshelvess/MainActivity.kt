package com.example.bookshelvess

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.videom))
        videoView.setMediaController(MediaController(this))
        videoView.start()

        videoyuGec.setOnClickListener {
            val intent=Intent(this,IntroductionFrag::class.java)
            startActivity(intent)
        }

    }
}