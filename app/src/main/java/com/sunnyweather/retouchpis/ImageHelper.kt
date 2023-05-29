package com.sunnyweather.retouchpis

import android.graphics.*
import android.graphics.Bitmap.Config.ARGB_8888


class ImageHelper (){
    fun handleImage( bitmap:Bitmap, lum: Float):Bitmap{
        val bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), ARGB_8888)
        val canvas  = Canvas(bm)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val lumMatrix = ColorMatrix()
        lumMatrix.setScale(lum, lum, lum, 1f)

        val imgMatrix = ColorMatrix()

        imgMatrix.postConcat(lumMatrix)

        paint.setColorFilter(ColorMatrixColorFilter(imgMatrix))
        canvas.drawBitmap(bitmap,(0).toFloat(),(0).toFloat(),paint)
        return bm;
    }
}