package com.sunnyweather.retouchpis

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.renderscript.Allocation
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicConvolve3x3
import com.google.android.material.snackbar.Snackbar
import com.sunnyweather.retouchpis.databinding.ActivityRetouchingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditImageActivity : AppCompatActivity() {
    var imageHelper: ImageHelper = ImageHelper()
    var lassSeekar: SeekBar? = null
    lateinit var btn_bright: RadioButton
    lateinit var btn_cpy: RadioButton
    lateinit var btn_noise: RadioButton
    lateinit var btn_Hue: RadioButton
    lateinit var btn_ruihua: RadioButton
    lateinit var btn_tou:RadioButton
    lateinit var seekBar_brightness: SeekBar
    lateinit var seekbar_baohe: SeekBar
    lateinit var seekBar_contrast: SeekBar
    lateinit var seekBar_noise: SeekBar
    lateinit var seekBar_Hue: SeekBar
    lateinit var seekBar_Tou:SeekBar
    lateinit var seekBar_ruihua: SeekBar
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var originalBitmap: Bitmap
    private lateinit var sharpenedBitmap: Bitmap

    companion object {
        const val REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1000
    }


    private fun DownloadsImage() {
        if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE_CODE
            )
        } else {
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

    private fun saveImage() {
        val imageView = findViewById<ImageView>(R.id.retouchingphoto)

        // Start a new thread to load the animation and save the image
        Thread {
            // Load the animation
            val showProgressBar = findViewById<ProgressBar>(R.id.progressbar)
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
                Toast.makeText(this, "You save the image", Toast.LENGTH_SHORT).show()
            }

        }.start()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.downloads -> DownloadsImage()
        }
        return true
    }

    private fun Apply() {
        bitmap = (imageView.drawable as BitmapDrawable).bitmap
        imageView.setImageBitmap(bitmap);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRetouchingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.TRANSPARENT

        if (intent.hasExtra("imageUri")) {
            val imageUri = intent.getParcelableExtra<Uri>("imageUri")
            binding.retouchingphoto.setImageURI(imageUri)
        }else if (intent.hasExtra("photo")){
            val imageUri = intent.getParcelableExtra<Uri>("photo")
            binding.retouchingphoto.setImageURI(imageUri)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        init()
        binding.apply?.setOnClickListener{ view->
            run {
                Snackbar.make(view, "确认要应用吗？", Snackbar.LENGTH_SHORT)
                    .setAction("确认！") {
                        Apply()
                        Toast.makeText(this, "应用成功！", Toast.LENGTH_SHORT).show()
                    }.show()
            }

//            Toast.makeText(this,"FAB clicked",Toast.LENGTH_SHORT).show()
        }
        val radioGroup = findViewById<RadioGroup>(R.id.layout_tab)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.button_brightness ->  {
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekBar_brightness

                    seekBar_brightness.visibility=View.VISIBLE
                    showSeekbar(seekBar_brightness,"brightness")
                }
                R.id.button_ruihua ->  {
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }

                    lassSeekar=seekBar_ruihua
                    seekBar_ruihua.visibility=View.VISIBLE
                    showSeekbar(seekBar_ruihua,"ruihua")
                }
                R.id.button_Hue ->  {
                    if (lassSeekar!=null) {

                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekBar_Hue
                    seekBar_Hue.visibility=View.VISIBLE
                    showSeekbar(seekBar_Hue,"Hue")
                }
                R.id.button_noise ->  {
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekBar_noise
                    seekBar_noise.visibility=View.VISIBLE
                    seekBar_noise.max = 255
                    showSeekbar(seekBar_noise,"noise")
                }
                R.id.button_contrast ->  {
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekBar_contrast
                    seekBar_contrast.visibility=View.VISIBLE
                    showSeekbar(seekBar_contrast,"contrast")
                }
                R.id.button_baohe -> {
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekbar_baohe
                    seekbar_baohe.visibility=View.VISIBLE
                    showSeekbar(seekbar_baohe,"saturation")
                }
                R.id.button_tou->{
                    if (lassSeekar!=null) {
                        lassSeekar!!.visibility = View.INVISIBLE
                    }
                    lassSeekar=seekBar_Tou
                    seekBar_Tou.visibility=View.VISIBLE
                    showSeekbar(seekBar_Tou,"tou")
                }
            }
        }
    }



    private fun showSeekbar(seekBar:SeekBar,label:String)
    {

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                handler.removeCallbacksAndMessages(null) // 移除之前的消息
                val value = when (label) {
                    "brightness", "saturation" -> progress.toFloat() / 127
                    "Hue" -> (progress - 127) * 1.0F / 127 * 50
                    "noise" -> progress.toFloat()
                    "contrast" -> progress.toFloat()
                    else->progress.toFloat()
                }
                handler.postDelayed({
                    editImage(value,label,bitmap) // 延迟执行图片编辑操作
                }, 100) // 延迟时间，单位为毫秒

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                    Log.d("ReTouching","当前的值为${temp}")
//                bitmap = (imageView.drawable as BitmapDrawable).bitmap
            }
        })

    }
    private fun editImage(value:Float,label:String,bitmap:Bitmap) {
        val editedImage = imageHelper.handleImage(bitmap,value,label)
        imageView.setImageBitmap(editedImage)
    }

    private fun init()
    {
        /*初始化*/

        btn_bright = findViewById(R.id.button_brightness)
        btn_cpy = findViewById(R.id.button_contrast)
        btn_noise = findViewById(R.id.button_noise)
        btn_ruihua = findViewById(R.id.button_ruihua)
        btn_Hue = findViewById(R.id.button_Hue)
        btn_tou = findViewById(R.id.button_tou)
        seekBar_brightness = findViewById(R.id.seekBar_bright)
        seekBar_contrast = findViewById(R.id.seekBar_cpy)
        seekBar_ruihua = findViewById(R.id.seekBar_ruihua)
        seekBar_noise = findViewById(R.id.seekBar_noise)
        seekBar_Hue = findViewById(R.id.seekBar_Hue)
        seekbar_baohe = findViewById(R.id.seekBar_baohe)
        seekBar_Tou = findViewById(R.id.seekBar_tou)
        imageView = findViewById(R.id.retouchingphoto)
        bitmap = (imageView.drawable as BitmapDrawable).bitmap
        originalBitmap = imageView.drawable.toBitmap()
        sharpenedBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, originalBitmap.config)

    }


}
