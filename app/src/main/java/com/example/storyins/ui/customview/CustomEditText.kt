package com.example.storyins.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyins.R

class CustomEditText : AppCompatEditText , View.OnTouchListener {

    private lateinit var clearButtonImage : Drawable
    private lateinit var backgroundEditText : Drawable

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs : AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context , attrs: AttributeSet , defStyleAttr: Int) : super(context, attrs,defStyleAttr){
        init()
    }


    private fun init(){

        backgroundEditText = ContextCompat.getDrawable(context , R.drawable.shape_rectangel_edit_text) as Drawable
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(cs: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence, start: Int, count: Int, after: Int) {
                if (cs.toString().isNotEmpty()) showClearButton() else hideClearButton()


            }

            override fun afterTextChanged(et: Editable) {

            }

        })
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 15f
        background = backgroundEditText



    }

    override fun onTouch(v: View?, mv: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    mv.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    mv.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (mv.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        when {
                            text != null -> text?.clear()

                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }


    private fun setButtonDrawable(
        startOfTheText : Drawable?=null,
        topOfTheText : Drawable?=null,
        endOfTheText : Drawable?=null,
        bottomOfTheText : Drawable?=null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun showClearButton(){
        setButtonDrawable( endOfTheText = clearButtonImage)
    }

    private fun hideClearButton(){
        setButtonDrawable()
    }

}