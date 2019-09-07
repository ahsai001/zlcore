package com.zaitunlabs.zlcore.core

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar


import com.zaitunlabs.zlcore.R
import com.zaitunlabs.zlcore.utils.audio.BackSoundService
import com.zaitunlabs.zlcore.utils.audio.BackSoundVolumeEvent
import com.zaitunlabs.zlcore.views.ASMovableMenu
import com.zaitunlabs.zlcore.views.ASTextView
import com.zaitunlabs.zlcore.views.CanvasLayout
import com.zaitunlabs.zlcore.views.CanvasSection

import org.greenrobot.eventbus.EventBus


open class CanvasActivity : BaseActivity() {
    protected var movableMenu: ASMovableMenu? = null
        private set
    private var isCanvasLayoutActive = false

    private var isMovableMenuNeedCreated = true


    fun disableMovableMenu() {
        isMovableMenuNeedCreated = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (movableMenu != null) {
            if (!movableMenu!!.isMenuOpened) {
                movableMenu!!.ChangeStateToLastDimension()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    //***********************************************************

    //****************event activity******************************
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    //***********************************************************

    //****************cycle window and content******************************
    /*@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		DebugUtils.logD("Activity", this.getClass().getSimpleName()+":onAttachedToWindow");
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		DebugUtils.logD("Activity", this.getClass().getSimpleName()+":onDetachedFromWindow");
	}
	*/
    override fun onContentChanged() {
        super.onContentChanged()
    }
    //***********************************************************

    //****************key mapping******************************
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (movableMenu != null && movableMenu!!.isMenuOpened) {
                movableMenu!!.closeMenu(true)
                return true
            } else {
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (movableMenu != null) {
                if (movableMenu!!.isMenuOpened) {
                    movableMenu!!.closeMenu(true)
                } else {
                    movableMenu!!.openMenu(true)
                }
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            //home
        }
        return super.onKeyDown(keyCode, event)
    }


    /*	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		DebugUtils.logD("Activity", this.getClass().getSimpleName()+":onKeyLongPress");
		return super.onKeyLongPress(keyCode, event);
	}*/

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyUp(keyCode, event)
    }
    //***********************************************************

    //******************* user intercation *****************
    override fun onUserLeaveHint() {
        // TODO Auto-generated method stub
        super.onUserLeaveHint()
    }

    override fun onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction()
    }

    //*************************************************************
    //****************configuration and save-restore instance**********************
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }
    //***********************************************************


    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        if (view is CanvasLayout) {
            isCanvasLayoutActive = true
            if (isMovableMenuNeedCreated) {
                createMenu(view as ViewGroup)
            }
        }
    }

    protected fun setPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    protected fun setLandScape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    protected fun createMenu(canvas: ViewGroup): ASMovableMenu? {
        movableMenu = ASMovableMenu.create(canvas, 10, CanvasSection.SAME_AS_OTHER_SIDE, 80, 80)
        return movableMenu
    }

    open fun onCreateMovableMenu(menu: ASMovableMenu) {
        val label = ASTextView(this)
        label.text = getString(R.string.zlcore_canvasactivity_volume_background_sound)
        label.gravity = Gravity.LEFT
        label.setTextColor(Color.BLACK)

        menu.addItemMenu(label, ASMovableMenu.LayoutViewMargin(10, 0, 0, 0))

        val volumeBar = SeekBar(this)
        volumeBar.max = 10
        volumeBar.progress = (BackSoundService.volume * 10).toInt()
        menu.addItemMenu(volumeBar, ASMovableMenu.LayoutViewMargin(0, 0, 0, 40))
        //menu.getRectList().add(new Rect(5, 15, 5+90, 15+10));
        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(arg0: SeekBar) {}

            override fun onStartTrackingTouch(arg0: SeekBar) {}

            override fun onProgressChanged(arg0: SeekBar, arg1: Int, arg2: Boolean) {
                EventBus.getDefault().post(BackSoundVolumeEvent(arg1.toFloat() / 10))
            }
        })
    }
}
