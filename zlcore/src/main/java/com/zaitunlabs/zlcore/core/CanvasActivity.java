package com.zaitunlabs.zlcore.core;

import java.util.ArrayList;

import android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;


import com.zaitunlabs.zlcore.utils.ApplicationWacther;
import com.zaitunlabs.zlcore.utils.DebugUtils;
import com.zaitunlabs.zlcore.utils.audio.BackSoundService;
import com.zaitunlabs.zlcore.utils.audio.BackSoundVolumeEvent;
import com.zaitunlabs.zlcore.views.ASMovableMenu;
import com.zaitunlabs.zlcore.views.ASTextView;
import com.zaitunlabs.zlcore.views.CanvasLayout;
import com.zaitunlabs.zlcore.views.CanvasSection;

import org.greenrobot.eventbus.EventBus;


public class CanvasActivity extends BaseActivity{
	private ASMovableMenu menuPage = null;
	private boolean isCanvasLayoutActive = false;

	private boolean isMovableMenuNeedCreated = true;


	public void disableMovableMenu() {
		isMovableMenuNeedCreated = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(menuPage != null)menuPage.ChangeStateToLastDimension();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	//***********************************************************
	
	//****************event activity******************************
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
	@Override
	public void onContentChanged() {
		super.onContentChanged();
	}
	//***********************************************************

	//****************key mapping******************************
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(menuPage != null && menuPage.isMenuOpened()){
	        	menuPage.closeMenu(true);
	        	return true;
	        }else{
	        	//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	        }
	    } else if ( keyCode == KeyEvent.KEYCODE_MENU ) {
			if (menuPage != null) {
				if (menuPage.isMenuOpened()) {
					menuPage.closeMenu(true);
				}else{
					menuPage.openMenu(true);
				}
				return true;
			}
		}else if (keyCode == KeyEvent.KEYCODE_HOME) {
            //home
        }
		return super.onKeyDown(keyCode, event);
	}
	
	

/*	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		DebugUtils.logD("Activity", this.getClass().getSimpleName()+":onKeyLongPress");
		return super.onKeyLongPress(keyCode, event);
	}*/

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	//***********************************************************

	//******************* user intercation *****************
	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
	}
	
	@Override
	public void onUserInteraction() {
		// TODO Auto-generated method stub
		super.onUserInteraction();
	}
	//*************************************************************
	//****************configuration and save-restore instance**********************
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
			super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(menuPage!=null && menuPage.isMenuOpened()){
			menuPage.closeMenu(false);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}
	//***********************************************************
	
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		if(view instanceof CanvasLayout){
			isCanvasLayoutActive = true;
			if(isMovableMenuNeedCreated) {
				createMenu((ViewGroup) view);
			}
		}
	}
	
	protected void setPortrait(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	protected void setLandScape(){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	protected ASMovableMenu createMenu(ViewGroup canvas){
		menuPage = ASMovableMenu.create(canvas, 10, CanvasSection.SAME_AS_OTHER_SIDE, 80, 80);
		return menuPage;
	}
	
	protected ASMovableMenu getMovableMenu(){
		return menuPage;
	}
	
	public void onCreateMovableMenu(ASMovableMenu menu){
		ASTextView label = new ASTextView(this);
		label.setText("Volume Suara Latar :");
		label.setGravity(Gravity.LEFT);
		label.setTextColor(Color.BLACK);

		menu.addItemMenu(label,new ASMovableMenu.LayoutViewMargin(10,0,0,0));

		SeekBar volumeBar = new SeekBar(this);
		volumeBar.setMax(10);
		volumeBar.setProgress((int) (BackSoundService.getVolume() * 10));
		menu.addItemMenu(volumeBar, new ASMovableMenu.LayoutViewMargin(0,0,0,40));
		//menu.getRectList().add(new Rect(5, 15, 5+90, 15+10));
		volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				EventBus.getDefault().post(new BackSoundVolumeEvent((float)arg1/10));
			}
		});
	}
}
