package com.zofers.zofers.view

import android.graphics.*
import android.graphics.drawable.Drawable


class GradientTextDrawable(private val text: String) : Drawable() {
	private val paint: Paint = Paint()

	override fun draw(canvas: Canvas) {
		canvas.drawText(text, (bounds.width() - paint.measureText(text)) / 2f, bounds.exactCenterY(), paint)
	}

	override fun setAlpha(alpha: Int) {
		paint.setAlpha(alpha)
	}

	override fun setColorFilter(cf: ColorFilter?) {
		paint.setColorFilter(cf)
	}

	override fun getOpacity(): Int {
		return PixelFormat.TRANSLUCENT
	}

	init {
		val textBounds = Rect()
		paint.getTextBounds(text, 0, text.length, textBounds)
		val textShader: Shader = LinearGradient(
				textBounds.left.toFloat(),
				textBounds.top.toFloat(),
				textBounds.right.toFloat(),
				textBounds.bottom.toFloat(),
				intArrayOf(
						Color.parseColor("#F06292"),
						Color.parseColor("#1C9BE2"),
				),
				null,
				Shader.TileMode.MIRROR
		)
		paint.textSize = 48f
		paint.shader = textShader
//		paint.isAntiAlias = true
//		paint.isFakeBoldText = true
//		paint.setShadowLayer(6f, 0f, 0f, Color.BLACK)
//		paint.style = Paint.Style.FILL
//		paint.textAlign = Paint.Align.CENTER
	}
}
