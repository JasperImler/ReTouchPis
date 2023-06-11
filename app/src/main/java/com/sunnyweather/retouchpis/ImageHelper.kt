package com.sunnyweather.retouchpis

import android.graphics.*
import android.graphics.Bitmap.Config.ARGB_8888
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.min


class ImageHelper (){

    fun handleImage(bitmap: Bitmap, value: Float, label: String): Bitmap {
        /*该Bitmap对象的宽度和高度与给定的bitmap对象相同，
        并且像素格式为ARGB_8888（即每个像素点占4个字节，其中8位表示透明度，8位表示红色通道，8位表示绿色通道，8位表示蓝色通道）。*/
        val bm = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val matrix = ColorMatrix()

        when (label) {
            "brightness" -> {
                // 亮度调节
                matrix.setScale(value, value, value, 1f)
                // 将矩阵合并到画笔的颜色过滤器中
            }
            "saturation" -> {
                // 饱和度调节
                matrix.setSaturation(value)
                // 将矩阵合并到画笔的颜色过滤器中

            }
            "Hue" -> {
                // 色相调节
                matrix.setRotate(0, value)
                matrix.setRotate(1, value)
                matrix.setRotate(2, value)
                // 将矩阵合并到画笔的颜色过滤器中

            }
            "ruihua" -> {
                matrix.set(
                    floatArrayOf(
                        value / 100, 0f, 0f, 0f, 0f,
                        0f, value/ 100, 0f, 0f, 0f,
                        0f, 0f, value / 100, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))

            }
            "tou" ->{
                paint.alpha = value.toInt()
            }
        }
        val imgMatrix = ColorMatrix()
        imgMatrix.postConcat(matrix)
        paint.colorFilter = ColorMatrixColorFilter(imgMatrix)
        // 绘制带有颜色过滤器的图像到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return bm
    }
    private fun ColorMatrix.setTemperature(degrees: Float) {
        val r = Math.cos(Math.toRadians(degrees.toDouble())) * 255
        val b = Math.sin(Math.toRadians(degrees.toDouble())) * 255
        val array = floatArrayOf(
            r.toFloat() / 255, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, b.toFloat() / 255, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        set(array)
    }
}