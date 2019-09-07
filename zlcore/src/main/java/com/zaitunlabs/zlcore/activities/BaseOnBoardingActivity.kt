package com.zaitunlabs.zlcore.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.widget.Button

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseActivity
import com.zaitunlabs.zlcore.utils.CommonUtils
import com.zaitunlabs.zlcore.utils.Prefs

/**
 * Created by ahsai on 4/6/2018.
 */

abstract class BaseOnBoardingActivity : BaseActivity() {
    val ONBOARDING_COMPLETED_STATE = TAG + "_onboarding_complete"
    private var pager: ViewPager? = null
    private var skip: Button? = null
    private var next: Button? = null
    private var onBoardingOnlyShown: Boolean = false

    protected abstract val fragmentList: List<Fragment>?
    protected abstract fun doGetStarted()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fragmentList == null) {
            finishOnboarding()
            return
        }

        if (fragmentList!!.size <= 0) {
            finishOnboarding()
            return
        }

        onBoardingOnlyShown = CommonUtils.getBooleanIntent(intent, ONBOARDING_ONLY_SHOWN, false)
        if (!onBoardingOnlyShown) {
            if (Prefs.with(this).getBoolean(ONBOARDING_COMPLETED_STATE, false)) {
                finishOnboarding()
                return
            }
        }



        setContentView(R.layout.activity_onboarding)

        pager = findViewById<View>(R.id.onboarding_pager) as ViewPager
        skip = findViewById<View>(R.id.onboarding_skip) as Button
        next = findViewById<View>(R.id.onboarding_next) as Button

        val adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList!![position]
            }

            override fun getCount(): Int {
                return fragmentList!!.size
            }
        }

        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == fragmentList!!.size - 1) {
                    if (onBoardingOnlyShown) {
                        next!!.setText(R.string.zlcore_onboarding_close)
                    } else {
                        next!!.setText(R.string.zlcore_onboarding_get_started)
                    }
                } else {
                    next!!.setText(R.string.zlcore_onboarding_next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        pager!!.adapter = adapter

        skip!!.setOnClickListener { finishOnboarding() }

        next!!.setOnClickListener {
            if (pager!!.currentItem == fragmentList!!.size - 1) { // The last screen
                finishOnboarding()
            } else {
                pager!!.setCurrentItem(
                        pager!!.currentItem + 1,
                        true
                )
            }
        }
    }

    private fun finishOnboarding() {
        Prefs.with(this).save(ONBOARDING_COMPLETED_STATE, true)
        if (!onBoardingOnlyShown) {
            doGetStarted()
        }
        finish()
    }

    companion object {
        val ONBOARDING_ONLY_SHOWN = "onboarding_only_shown"

        fun start(context: Context, OnBoardingClass: Class<*>) {
            val i = Intent(context, OnBoardingClass)
            i.putExtra(ONBOARDING_ONLY_SHOWN, false)
            context.startActivity(i)
        }

        fun showOnly(context: Context, OnBoardingClass: Class<*>) {
            val i = Intent(context, OnBoardingClass)
            i.putExtra(ONBOARDING_ONLY_SHOWN, true)
            context.startActivity(i)
        }
    }

}
