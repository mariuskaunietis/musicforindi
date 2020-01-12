package com.example.marius.musicbrainzforindi

import android.content.Context
import android.graphics.PixelFormat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.marius.musicbrainzforindi.utils.px

abstract class BaseActivity : AppCompatActivity() {
  private val progressText by lazy {
    TextView(this)
  }
  private val progressOverlay by lazy {
    val me = FrameLayout(this)
    val whiteBox = LinearLayout(this)
    val progressBar = ProgressBar(this)
    val lp = FrameLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    lp.gravity = Gravity.CENTER
    me.addView(whiteBox, lp)
    whiteBox.addView(progressBar, lp)
    whiteBox.addView(progressText, lp)

    whiteBox.orientation = LinearLayout.HORIZONTAL

    me.setBackgroundColor(ContextCompat.getColor(this, R.color.colorProgress))
    whiteBox.setBackgroundResource(R.drawable.bg_rounded_white)
    whiteBox.elevation = 2.px
    me

  }


  fun showProgress(count: Int) {
    runOnUiThread {
      try {
        progressText.text = getString(R.string.progress_page, count)
        val layoutParams =
          WindowManager.LayoutParams(WindowManager.LayoutParams.FIRST_SUB_WINDOW)
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        layoutParams.token = window.decorView.rootView.windowToken


        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).addView(
          progressOverlay,
          layoutParams
        )

      } catch (ignore: Exception) {
      }
    }
  }

  fun hideProgress() {
    runOnUiThread {
      try {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(progressOverlay)
      } catch (ignore: Exception) {
      }
    }
  }

}