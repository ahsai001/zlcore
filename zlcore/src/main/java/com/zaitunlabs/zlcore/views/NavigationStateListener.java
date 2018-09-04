package com.zaitunlabs.zlcore.views;

import android.view.View;

public interface NavigationStateListener {
	public boolean navigationStateIndex(View outputView, View navView, int index, int counts);
}
