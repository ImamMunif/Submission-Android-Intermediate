package com.dicoding.storysubmission.view.signup

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class SignupPassEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Do nothing.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, after: Int) {
                if (s.toString().length < 8) {
                    setError("Must not be less than 8 characters", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
            // Do nothing
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //  hint = "Input your new password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}