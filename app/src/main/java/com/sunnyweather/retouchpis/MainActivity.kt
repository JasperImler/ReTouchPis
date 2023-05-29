package com.sunnyweather.retouchpis


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.retouchpis.R
import com.sunnyweather.retouchpis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val fromAlbumLaucher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            val intent = Intent(this, Retouching::class.java)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fromalbum.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            fromAlbumLaucher.launch(pickPhotoIntent)
        }

    }


}
