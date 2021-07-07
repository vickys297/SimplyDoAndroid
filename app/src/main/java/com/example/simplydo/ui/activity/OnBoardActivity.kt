package com.example.simplydo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.simplydo.R
import com.example.simplydo.databinding.ActivityOnBoardBinding
import com.example.simplydo.ui.MainActivity
import com.example.simplydo.ui.activity.screens.Page1
import com.example.simplydo.ui.activity.screens.Page2
import com.example.simplydo.ui.activity.screens.Page3
import com.example.simplydo.ui.activity.screens.Page4
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppPreference
import com.example.simplydo.utli.Page4Interface
import com.google.firebase.messaging.FirebaseMessaging

internal val TAG_ONBOARD = OnBoardActivity::class.java.canonicalName

class OnBoardActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnBoardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@OnBoardActivity, R.layout.activity_on_board)

        val onScreenViewPager = OnBoardViewPagerAdapter(supportFragmentManager, lifecycle)

        onScreenViewPager.addFragment(Page1.newInstance())
        onScreenViewPager.addFragment(Page2.newInstance("", ""))
        onScreenViewPager.addFragment(Page3.newInstance("", ""))
        onScreenViewPager.addFragment(Page4.newInstance(page4Interface))


        binding.viewPager.adapter = onScreenViewPager

        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.viewPager.registerOnPageChangeCallback(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(callback)
    }

    val callback = object : OnPageChangeCallback() {

    }

    private val page4Interface = object : Page4Interface {
        override fun onStart() {
            if (AppPreference.getPreferences(
                    AppConstant.StartUp.ONBOARD_COMPLETED,
                    false,
                    this@OnBoardActivity
                )
            ) {
                startActivity(
                    Intent(this@OnBoardActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                finish()
            } else {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG_ONBOARD, "Fetching FCM registration token failed", task.exception)
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    Log.d(TAG, "New Token $token")
                    token?.let {
//                        registerNewUser(it)
                    }
                }
            }
        }

    }

}

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : ViewPager2.PageTransformer {


    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}