package com.example.aston_intensiv_3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadImageButton = findViewById<Button>(R.id.load_image_button)
        loadImageButton.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
}