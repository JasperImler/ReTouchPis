package com.sunnyweather.retouchpis

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.sunnyweather.retouchpis.ReTouchTool.Tooladapter
import com.sunnyweather.retouchpis.databinding.ActivityRetouchingBinding

class ToolLogic(val holder: Tooladapter.ViewHolder,val retouchingphoto:ImageView,var lastProgress:Float,var rootView:View) {
    lateinit var imageView:ImageView
    lateinit var bitmap:Bitmap

    var imageHelper: ImageHelper = ImageHelper()
    var resourceID:Int = 1
    var brightness:Float = 1f
    var contrast:Float = 1f
    var noise:Float = 0f
    private fun init()
    {
        imageView = retouchingphoto
        bitmap = (imageView.drawable as BitmapDrawable).bitmap
    }
    fun getLastPregress():Float{
        return lastProgress
    }
    private fun LogicImplement()
    {
        val inflater = LayoutInflater.from(MyApplication.context)
        val view = inflater.inflate(R.layout.activity_retouching, null)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        seekBar.progress = lastProgress.toInt()
        seekBar.visibility = View.VISIBLE
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var value :Float = progress.toFloat() / 127

                when(holder.ToolName.text.toString())
                {
                    "亮度" -> brightness = value
                    "对比度" -> contrast = value
                    "颗粒" -> noise = value
                }

                imageView.setImageBitmap(imageHelper.handleImage(bitmap,brightness))

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(MyApplication.context,"开始滑动",Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Toast.makeText(MyApplication.context,"停止滑动",Toast.LENGTH_SHORT).show()
                lastProgress = seekBar?.progress?.toFloat() ?: 0F
            }
        })
//

    }
    public fun ChangeState()
    {

        when(holder.ToolName.text.toString())
        {
            "亮度" -> resourceID = R.drawable.light_selected
            "对比度" -> resourceID = R.drawable.cpy_selected
            "灰度"  -> resourceID = R.drawable.gray_elected
            "颗粒" -> resourceID = R.drawable.noise_selected
            "锐化" ->resourceID = R.drawable.ruihua_selected
            else -> resourceID=1
        }
        if (resourceID!=1)
        {
            holder.ToolImage.setImageResource(resourceID)
            holder.ToolName.setTextColor(Color.rgb(177,237,64))
        }
    }
    public fun ChangeBack()
    {
        when(holder.ToolName.text.toString())
        {
            "亮度" -> resourceID = R.drawable.light_notselected
            "对比度" -> resourceID = R.drawable.cpy_notselected
            "灰度"  -> resourceID = R.drawable.gray_notselected
            "颗粒" -> resourceID = R.drawable.noise_notselected
            "锐化" ->resourceID = R.drawable.ruihua_notelected
            else -> resourceID=1
        }
        if (resourceID!=1)
        {
            holder.ToolImage.setImageResource(resourceID)
            holder.ToolName.setTextColor(Color.WHITE)
        }
    }

    fun show()
    {
        init()
        LogicImplement()
        Toast.makeText(MyApplication.context,"You clicked the ${holder.ToolName.text}",Toast.LENGTH_SHORT).show()
    }

}