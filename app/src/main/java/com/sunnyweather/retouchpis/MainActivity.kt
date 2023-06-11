package com.sunnyweather.retouchpis


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sunnyweather.retouchpis.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var photoUri: Uri
    val REQUEST_PERMISSON_SORAGE = 1
    val REQUEST_PERMISSON_CAMERA = 2
    var touchTimes:Int = 0


    private val takePhotoLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if (result.resultCode == RESULT_OK){
            // 获取拍照后的图片
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            // 将图片传回Activity
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra("photo", photoUri)
            startActivity(intent)
        }
    }
    private val fromAlbumLaucher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ugly!!.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    it.vibrate(100)
                }
            }
            Toast.makeText(this, "你没看错，这只是张图片而已", Toast.LENGTH_SHORT).show()
        }
        binding.retouchingPis!!.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    it.vibrate(100)
                }
            }
            if (touchTimes==0)
            {
                Toast.makeText(this, "不要再摸啦！", Toast.LENGTH_SHORT).show()
                touchTimes++
            }else if (touchTimes in 1..5)
            {
                Toast.makeText(this, "你再摸？你还有${5-touchTimes}次机会", Toast.LENGTH_SHORT).show()
                touchTimes++
            }else if (touchTimes == 6)
            {
                Toast.makeText(this, "哈 哈", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        binding.fromalbum.setOnClickListener {
            openAblumWithPermissionsCheck()
        }
        binding.takePhoto.setOnClickListener {
            if (checkPermission()) {
                doTakePhoto()
            } else {
                requestPermission()
            }
//            doTakePhoto()
        }
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_PERMISSON_CAMERA
        )
    }
    private fun checkPermission(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    private fun requestTakePhotoPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSON_CAMERA
            )
            return
        }
        doTakePhoto()
    }

    private fun openAblumWithPermissionsCheck() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSON_SORAGE
            )
            return
        }
        openAblum()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSON_SORAGE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAblum()
            return
        } //end if
        if (requestCode == REQUEST_PERMISSON_CAMERA && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doTakePhoto()
            return
        } //end if
    }
    private fun openAblum(){
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fromAlbumLaucher.launch(pickPhotoIntent)
    }

    private fun doTakePhoto() {
        //处理调用摄像头的逻辑
        val outputImage = File(externalCacheDir,"output_image.jpg")//创建File对象，存放拍下图片
       //externalCacheDir叫做关联缓存目录，专门用于存放当前应用缓存数据的位置
        if (outputImage.exists()){
            outputImage.delete()
        }  //如果已经存在就删除
        outputImage.createNewFile()
        photoUri = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //将File对象转换成一个封装过的Uri对象
            FileProvider.getUriForFile(this,"com.sunnyweather.retouchpis.fileprovider",outputImage)
        }else{
            Uri.fromFile(outputImage)
        }
       //启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)//指定图片的输出地址
       //使用registerForActivityResult启动
        takePhotoLaucher.launch(intent)
    }

}
