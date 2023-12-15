package com.example.yourjourney.editviewrespones

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.yourjourney.R.string

class Default : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString().trim()
                val errorResId = when {
                    name.isBlank() -> string.empty_name
                    else -> 0
                }

                if (errorResId != 0) {
                    error = context.getString(errorResId)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
