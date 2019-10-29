package com.zaitunlabs.zlcore.utils;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class NavigationHandler {
	private int count = 0;
	private int index = 0;

	private View nextView = null;
	private View prevView = null;
	private View outputView = null;
	

	private View.OnClickListener nextListener = null;
	private View.OnClickListener prevListener = null;
	
	private NavigationStateListener stateListener = null;
	

	public View getOutputView() {
		return outputView;
	}

	public void setOutputView(View outputView) {
		this.outputView = outputView;
	}
	
	public View getNextView() {
		return nextView;
	}

	public void setNextView(View nextView) {
		if(this.nextView != null)this.nextView.setOnClickListener(null);
		this.nextView = nextView;
		this.nextView.setOnClickListener(nextListener);
	}

	public View getPrevView() {
		return prevView;
	}
	
	public void showNavigationView(){
		if(this.nextView != null)this.nextView.setVisibility(View.VISIBLE);
		if(this.prevView != null)this.prevView.setVisibility(View.VISIBLE);
	}

	public void showNavigationViewWithState(){
		if(prevView != null){
			if(index <= 0){
				prevView.setVisibility(View.INVISIBLE);
			}else{
				prevView.setVisibility(View.VISIBLE);
			}
		}
		if(nextView != null){
			if(index >= count - 1){
				nextView.setVisibility(View.INVISIBLE);
			}else{
				nextView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void hideNavigationView(){
		if(this.nextView != null)this.nextView.setVisibility(View.INVISIBLE);
		if(this.prevView != null)this.prevView.setVisibility(View.INVISIBLE);
	}

	public void setPrevView(View prevView) {
		if(this.prevView != null)this.prevView.setOnClickListener(null);
		this.prevView = prevView;
		this.prevView.setOnClickListener(prevListener);
	}
	
	public NavigationStateListener getStateListener() {
		return stateListener;
	}

	public void setStateListener(NavigationStateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		updateOutputView();
		showNavigationViewWithState();
		if(stateListener != null) stateListener.navigationStateIndex(outputView, index, count);
	}

	private void updateOutputView() {
		if(outputView!=null){
			if(outputView instanceof TextView){
				((TextView)outputView).setText(String.format(Locale.getDefault(),"%d/%d", index + 1, count));
			}
		}
	}

	public NavigationHandler() {
		configure();
	}
	
	public NavigationHandler(int counts) {
		this.count = counts;
		configure();
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	private void configure(){
		nextListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(index < count - 1){
					index++;
					setIndex(index);
				}
			}
		};
		prevListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(index > 0){
					index--;
					setIndex(index);
				}
			}
		};
	}
	
	public void setNextPrevView(View prevView, View nextView){
		setPrevView(prevView);
		setNextView(nextView);
	}

	public void next(){
		if(index < count - 1){
			index++;
			setIndex(index);
		}
	}

	public void prev(){
		if(index > 0){
			index--;
			setIndex(index);
		}
	}

	public boolean isFirst(){
		return index == 0;
	}
	public boolean isLast(){
		return index == count -1;
	}

	public void init(){
		setIndex(0);
	}

	public interface NavigationStateListener {
		boolean navigationStateIndex(View outputView, int index, int counts);
	}
	
}
