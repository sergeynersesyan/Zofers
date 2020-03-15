package com.zofers.zofers.staff

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.EditText

import com.zofers.zofers.App
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService



object UIHelper {

    public val CURRENT_MARGIN = -1

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        var l = l
        var t = t
        var r = r
        var b = b
        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = v.layoutParams as ViewGroup.MarginLayoutParams
            if (l == CURRENT_MARGIN) {
                l = p.leftMargin
            }
            if (r == CURRENT_MARGIN) {
                r = p.rightMargin
            }
            if (t == CURRENT_MARGIN) {
                t = p.topMargin
            }
            if (b == CURRENT_MARGIN) {
                b = p.bottomMargin
            }
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }

    fun closeKeyboardOnTouch(view: View, onlySimpleClick: Boolean) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener(object : View.OnTouchListener {
                var MAX_CLICK_DISTANCE = 15
                var startClickTime: Long = 0
                var pressedX: Float = 0.toFloat()
                var pressedY: Float = 0.toFloat()

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        startClickTime = System.currentTimeMillis()
                        pressedX = event.x
                        pressedY = event.y
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (!onlySimpleClick || System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout() && distance(pressedX, pressedY, event.x, event.y) < MAX_CLICK_DISTANCE) {
                            // Touch was a simple tap.
                           hideSoftKeyboard(view)
                        }
                    }

                    return false
                }
            })
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                closeKeyboardOnTouch(innerView, onlySimpleClick)
            }
        }
    }

    fun hideSoftKeyboard (view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}
