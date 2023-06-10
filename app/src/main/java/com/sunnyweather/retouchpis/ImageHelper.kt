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
            "Contrast" -> {
                val width = bitmap.width
                val height = bitmap.height
                val pixels = IntArray(width * height)
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                val maxRGB = 255
                val factor = maxRGB / ln(maxRGB.toDouble() + 1)
                for (i in pixels.indices) {
                    val color = pixels[i]
                    val r = Color.red(color)
                    val g = Color.green(color)
                    val b = Color.blue(color)
                    val logR = (factor * ln(r.toDouble() + 1) * value).toInt()
                    val logG = (factor * ln(g.toDouble() + 1) * value).toInt()
                    val logB = (factor * ln(b.toDouble() + 1) *
                            value).toInt()
                    pixels[i] = Color.rgb(
                        min(logR, maxRGB),
                        min(logG, maxRGB),
                        min(logB, maxRGB)
                    )
                    bm.setPixels(pixels,0,width,0,0,width,height)
                    return bm
                }
            }
            "ruihua" -> {

            }
            "noise" ->{
                paint.maskFilter = BlurMaskFilter(value,BlurMaskFilter.Blur.NORMAL)
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
}