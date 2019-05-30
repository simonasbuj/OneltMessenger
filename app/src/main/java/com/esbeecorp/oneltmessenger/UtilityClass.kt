package com.esbeecorp.oneltmessenger

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar

class UtilityClass{

    // There is no static functions in Kotlin, so you must use this abomination of
    // companion object, which makes everything inside static
    companion object {

        private var startTouchX: Float = 0f
        private var startTouchY: Float = 0f

        private const val SCROLL_THRESHOLD = 100f

        fun closeKeyboardOnOutsideTouch(ev: MotionEvent?, view: View?, activity: Activity){

            when (ev!!.action){
                MotionEvent.ACTION_DOWN -> {
                    startTouchX = ev.x
                    startTouchY = ev.y
                }
                MotionEvent.ACTION_UP -> {
                    val endTouchX = ev.x
                    val endTouchY = ev.y

                    val differenceX = Math.abs(startTouchX - endTouchX)
                    val differenceY = Math.abs(startTouchY - endTouchY)

                    if (differenceX < SCROLL_THRESHOLD && differenceY < SCROLL_THRESHOLD){

                        // User didn't scroll, close the keyboard
                        if (view != null &&
                            (ev.action == MotionEvent.ACTION_UP && ev.action != MotionEvent.ACTION_MOVE) &&
                            view is EditText &&
                            !view.javaClass.name.startsWith("android.webkit.")) {

                            var scrCoords = IntArray(2)
                            view.getLocationOnScreen(scrCoords)

                            val x = ev.rawX + view.left - scrCoords[0];
                            val y = ev.rawY + view.top - scrCoords[1]

                            if (x < view.left || x > view.right || y < view.top || y > view.bottom)
                                hideKeyboard(activity)
                        }
                    }
                }
            }

        }

        private fun hideKeyboard(activity: Activity){
            // Maybe not all ifs are needed :D
            if (activity != null && activity.window != null && activity.window.decorView != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
            }
        }


        // Disables the button, hides its text and shows loading bar
        public fun startLoadingOnButton(btn: Button, pb: ProgressBar){
            btn.isClickable = false
            btn.textSize = 0f
            pb.visibility = View.VISIBLE
        }

        // Enables the button, shows its text and hides loading bar
        public fun stopLoadingOnButton(btn: Button, pb: ProgressBar){
            pb.visibility = View.GONE
            btn.textSize = 14f
            btn.isClickable = true
        }

    }

}