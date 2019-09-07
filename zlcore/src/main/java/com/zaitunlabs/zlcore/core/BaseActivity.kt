package com.zaitunlabs.zlcore.core

import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.transition.Transition

import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.core.BaseActivity.Companion.EXTRA_REQUEST_CODE
import com.zaitunlabs.zlcore.utils.ApplicationWacther
import com.zaitunlabs.zlcore.utils.CommonUtils

import java.util.ArrayList

/**
 * Created by ahsai on 7/14/2017.
 */

open class BaseActivity : AppCompatActivity() {
    protected val TAG = this@BaseActivity.javaClass.getSimpleName()

    protected val currentRequestCode: Int
        get() = getRequestCode(intent)

    private var canExit = false
    private val defaultMaxIntervalBackToExit = 3000


    private val asyncTaskList = ArrayList<AsyncTask<*, *, *>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationWacther.getInstance(this).registerActivity(this)
    }

    override fun onStart() {
        super.onStart()

        ApplicationWacther.getInstance(this).reportActivityStartEvent(this)
    }

    override fun onResume() {
        super.onResume()
        ApplicationWacther.getInstance(this).setLastActivityResumed(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        ApplicationWacther.getInstance(this).reportActivityStopEvent(this)
        super.onStop()
    }

    override fun onDestroy() {
        //unregister this new activity from watcher
        ApplicationWacther.getInstance(this).unregisterActivity(this)
        if (isTaskRoot) {

        }

        for (asyncTask in asyncTaskList) {
            if (asyncTask.status == AsyncTask.Status.RUNNING) {
                asyncTask.cancel(true)
            }
            asyncTaskList.remove(asyncTask)
        }

        super.onDestroy()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        ApplicationWacther.getInstance(this).setConfigurationChanged(true)
        super.onConfigurationChanged(newConfig)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        ApplicationWacther.getInstance(this).setConfigurationChanged(false)
        super.onRestoreInstanceState(savedInstanceState)
    }


    protected fun enableUpNavigation() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    protected fun <T : BaseFragment> showFragment(frameLayoutID: Int, clazz: Class<T>, postFragmentInstantiation: PostFragmentInstantiation<T>, savedInstanceState: Bundle, TAG: String): T? {
        return showFragment(frameLayoutID, clazz, postFragmentInstantiation, savedInstanceState, false, TAG)
    }

    protected fun <T : BaseFragment> showFragment(frameLayoutID: Int, clazz: Class<T>, postFragmentInstantiation: PostFragmentInstantiation<T>?, savedInstanceState: Bundle?, ignoreStateOrAlwaysCreateNew: Boolean, TAG: String): T? {
        var fragment: T? = null
        if (savedInstanceState == null || ignoreStateOrAlwaysCreateNew) {
            try {
                fragment = clazz.newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            if (fragment == null) return null

            postFragmentInstantiation?.postInstantiation(fragment)

            supportFragmentManager.beginTransaction()
                    .replace(frameLayoutID, fragment, TAG).commit()
        } else {
            fragment = supportFragmentManager.findFragmentByTag(TAG) as T?
            if (fragment == null) {
                return showFragment(frameLayoutID, clazz, postFragmentInstantiation, null, true, TAG)
            }
        }
        return fragment
    }

    abstract inner class PostFragmentInstantiation<T : BaseFragment> {
        abstract fun postInstantiation(fragment: T)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode)
        super.startActivityForResult(intent, requestCode)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode)
        super.startActivityForResult(intent, requestCode, options)
    }

    protected fun getRequestCode(intent: Intent): Int {
        return CommonUtils.getIntIntent(intent, EXTRA_REQUEST_CODE, -1)
    }


    fun setupEnterWindowAnimations(enterTransition: Transition) {
        window.enterTransition = enterTransition
        window.allowEnterTransitionOverlap = false
    }

    fun setupEnterWindowAnimations(enterTransition: Transition, returnTransition: Transition) {
        window.enterTransition = enterTransition
        window.returnTransition = returnTransition
        window.allowEnterTransitionOverlap = false
        window.allowReturnTransitionOverlap = false
    }

    fun setupExitWindowAnimations(exitTransition: Transition) {
        window.exitTransition = exitTransition
    }

    fun setupExitWindowAnimations(exitTransition: Transition, reEnterTransition: Transition) {
        window.exitTransition = exitTransition
        window.reenterTransition = reEnterTransition
    }

    protected fun onBackPressedTwiceToExit(message: String) {
        onBackPressedTwiceToExit(defaultMaxIntervalBackToExit, message)
    }

    @JvmOverloads
    protected fun onBackPressedTwiceToExit(maxInterval: Int = defaultMaxIntervalBackToExit, message: String? = null) {
        if (!canExit) {
            canExit = true
            if (TextUtils.isEmpty(message)) {
                CommonUtils.showSnackBar(this, getString(R.string.zlcore_warning_press_once_again_to_close_app))
            } else {
                CommonUtils.showSnackBar(this, message)
            }
            Handler().postDelayed({ canExit = false }, maxInterval.toLong())
        } else {
            super.onBackPressed()
        }
    }

    protected fun addAsync(asyncTask: AsyncTask<*, *, *>) {
        asyncTaskList.add(asyncTask)
    }

    protected fun removeAsync(asyncTask: AsyncTask<*, *, *>) {
        asyncTaskList.remove(asyncTask)
    }

    companion object {
        protected val EXTRA_REQUEST_CODE = "extra_requestCode"
    }
}
