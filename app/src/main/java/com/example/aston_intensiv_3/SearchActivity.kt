package com.example.aston_intensiv_3

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class SearchActivity : AppCompatActivity() {

    private var imageViewModel: ImageViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]
        if (imageViewModel!!.bitmap != null || imageViewModel!!.drawable != null){
            val imageView: ImageView = findViewById(R.id.image_view)
            imageView.setImageDrawable(imageViewModel!!.drawable)
            val imageView2: ImageView = findViewById(R.id.image_view_2)
            imageView2.setImageBitmap(imageViewModel!!.bitmap)
        }
        val editText = findViewById<EditText>(R.id.input_edit_text)
        getDebounced(editText, 300L)
    }

    private fun loadPictureCoil(link: String){
        val imageView: ImageView = findViewById(R.id.image_view)
        val imageLoader = ImageLoader(this)
        lifecycleScope.launch {
            try {
                val request = ImageRequest.Builder(this@SearchActivity)
                    .data(link)
                    .build()
                val drawable = imageLoader.execute(request).drawable
                if (drawable == null){
                    Toast.makeText(
                        this@SearchActivity,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@SearchActivity,
                        getString(R.string.success),
                        Toast.LENGTH_SHORT).show()
                    imageViewModel?.drawable = drawable
                    imageView.setImageDrawable(drawable)
                    Log.d(getString(R.string.logtag), drawable.toString())
                }
            } catch (e: Exception) {
                    Log.d(getString(R.string.logtag), "${e.printStackTrace()}")
            }
        }
    }

    private fun getDebounced(
        editText: EditText,
        delay: Long,
    ){
        var lastClickTime = 0L
        editText.addTextChangedListener {
            val currentTime = SystemClock.uptimeMillis()
            if (lastClickTime == 0L || currentTime - lastClickTime > delay){
                loadPictureCoil(it.toString())
                if (it.toString().length >= 6){
                    loadPictureAndroid(
                        getString(R.string.https) + it.toString().substringAfter("//")
                    )
                }else{
                    loadPictureAndroid(getString(R.string.https))
                }
                lastClickTime = currentTime
            }
        }
    }

    private fun loadPictureAndroid(link:String){
        val imageView = findViewById<ImageView>(R.id.image_view_2)
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL(link)
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.doInput = true
                connection.connect()
                val inputString = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputString)
                imageViewModel?.bitmap = bitmap
                imageView.setImageBitmap(bitmap)
                Log.d(getString(R.string.logtag), bitmap.toString())
            } catch (e: Exception) {
                Log.d(getString(R.string.logtag), "${e.printStackTrace()}")
            } finally {
                connection.disconnect()
            }
        }
    }
}