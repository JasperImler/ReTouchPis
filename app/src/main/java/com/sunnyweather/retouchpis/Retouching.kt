package com.sunnyweather.retouchpis

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.retouchpis.ReTouchTool.Tool
import com.sunnyweather.retouchpis.ReTouchTool.Tooladapter
import com.sunnyweather.retouchpis.databinding.ActivityRetouchingBinding
import java.io.OutputStream
import kotlin.concurrent.thread

class Retouching : AppCompatActivity() {
    companion object{
        const val REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1000
    }

    private val ToolList = ArrayList<Tool>()
    private val Tools =mutableListOf(
        Tool("亮度",R.drawable.light_notselected), Tool("锐化",R.drawable.ruihua_notelected),
        Tool("对比度",R.drawable.cpy_notselected), Tool("颗粒",R.drawable.noise_notselected),
        Tool("灰度",R.drawable.gray_notselected), Tool("",R.drawable.darkimage)

    )
    private fun DownloadsImage(){
        if (Build.VERSION.SDK_INT<29&&ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUEST_WRITE_EXTERNAL_STORAGE_CODE
                )
        }else
        {
            saveImage()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //权限申请成功可以保存图片
             saveImage()
        }
    }
    private fun saveImage()
    {
        val imageView = findViewById<ImageView>(R.id.retouchingphoto)

            // Start a new thread to load the animation and save the image
        Thread {
                // Load the animation
            val showProgressBar = findViewById<ProgressBar>(R.id.progressBar)
                // Start the animation
            runOnUiThread {
                showProgressBar.visibility = View.VISIBLE
                Handler().postDelayed({
                    showProgressBar.visibility = View.GONE
                }, 1000)
            }
                // Save the image to the gallery
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", "Description")
                // Stop the animation
            runOnUiThread {
                showProgressBar.visibility = View.GONE
                Toast.makeText(this,"You save the image",Toast.LENGTH_SHORT).show()
            }

        }.start()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            android.R.id.home ->finish()
            R.id.downloads -> DownloadsImage()
        }
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRetouchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.TRANSPARENT

       if (intent.hasExtra("imageUri")) {
            val imageUri = intent.getParcelableExtra<Uri>("imageUri")
            binding.retouchingphoto.setImageURI(imageUri)
        }

        initToolList()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.toolList.layoutManager = layoutManager
        val adapter = Tooladapter(ToolList,binding.retouchingphoto)
        binding.toolList.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    private fun initToolList(){
        val index:Int = 0
        for(index in 0..Tools.size-1)
        {
            ToolList.add(Tools[index])
        }
    }


}

