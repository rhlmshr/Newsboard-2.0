package com.newsboard.utils.impls

import android.view.MotionEvent
import android.view.View
import android.widget.EditText

/**
 * Impl class to implement OnTouchListener and provide method for drawable clicks
 */
open class IntrinsicDrawableClickListenerImpl : View.OnTouchListener {
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val drawableLeft = 0
        val drawableTop = 1
        val drawableRight = 2
        val drawableBottom = 3

        val editText: EditText = v as EditText

        if (event.action == MotionEvent.ACTION_UP) {
            when {
                editText.compoundDrawables[drawableLeft] != null && event.rawX >= v.right - editText.compoundDrawables[drawableLeft].bounds.width() -> {
                    onDrawableStartClicked()
                    return true
                }
                editText.compoundDrawables[drawableRight] != null && event.rawX >= v.right - editText.compoundDrawables[drawableRight].bounds.width() -> {
                    onDrawableEndClicked()
                    return true
                }
                editText.compoundDrawables[drawableTop] != null && event.rawX >= v.right - editText.compoundDrawables[drawableTop].bounds.width() -> {
                    onDrawableTopClicked()
                    return true
                }
                editText.compoundDrawables[drawableBottom] != null && event.rawX >= v.right - editText.compoundDrawables[drawableBottom].bounds.width() -> {
                    onDrawableBottomClicked()
                    return true
                }
            }
        }
        return false
    }

    open fun onDrawableStartClicked() {
    }

    open fun onDrawableEndClicked() {
    }

    open fun onDrawableTopClicked() {
    }

    open fun onDrawableBottomClicked() {
    }
}