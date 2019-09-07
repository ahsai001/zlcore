package com.zaitunlabs.zlcore.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff.Mode
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView

class RoundedImageView : ImageView {
    private val objPaint = Paint()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onDraw(canvas: Canvas) {

        val drawable = drawable ?: return

        if (width == 0 || height == 0) {
            return
        }
        val b = (drawable as BitmapDrawable).bitmap
        if (b != null) {
            try {
                val bitmap = b.copy(Config.ARGB_8888, true)
                val w = width
                val h = height
                Log.i("TAG", "Bitmap Width:$w")

                val roundBitmap = getCroppedBitmap(bitmap, w)
                objPaint.isAntiAlias = true
                objPaint.isDither = true
                canvas.drawBitmap(roundBitmap, 0f, 0f, objPaint)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        fun getCroppedBitmap(bmp: Bitmap, radius: Int): Bitmap {
            val sbmp: Bitmap
            if (bmp.width != radius || bmp.height != radius)
                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false)
            else
                sbmp = bmp
            val output = Bitmap.createBitmap(sbmp.width, sbmp.height,
                    Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = -0x5e688c
            val paint = Paint()
            val rect = Rect(0, 0, sbmp.width, sbmp.height)

            paint.isAntiAlias = true
            paint.isFilterBitmap = true
            paint.isDither = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.parseColor("#BAB399")
            canvas.drawCircle(sbmp.width / 2 + 0.7f,
                    sbmp.height / 2 + 0.7f, sbmp.width / 2 + 0.1f, paint)
            paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
            canvas.drawBitmap(sbmp, rect, rect, paint)

            return output
        }
    }

}