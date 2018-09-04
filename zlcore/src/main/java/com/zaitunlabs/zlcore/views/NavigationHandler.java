package com.zaitunlabs.zlcore.views;

import android.view.View;

public class NavigationHandler {
	private int counts = 0;
	private int index = 0;

	private View nextView = null;
	private View prevView = null;
	private View outputView = null;
	

	private View.OnClickListener nextListener = null;
	private View.OnClickListener prevListener = null;
	
	private NavigationStateListener outputListener = null;
	

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
				prevView.setVisibility(View.GONE);
			}else{
				prevView.setVisibility(View.VISIBLE);
			}
		}
		if(nextView != null){
			if(index >= counts - 1){
				nextView.setVisibility(View.GONE);
			}else{
				nextView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void hideNavigationView(){
		if(this.nextView != null)this.nextView.setVisibility(View.GONE);
		if(this.prevView != null)this.prevView.setVisibility(View.GONE);
	}

	public void setPrevView(View prevView) {
		if(this.prevView != null)this.prevView.setOnClickListener(null);
		this.prevView = prevView;
		this.prevView.setOnClickListener(prevListener);
	}
	
	public NavigationStateListener getOtputListener() {
		return outputListener;
	}

	public void setOutputListener(NavigationStateListener outputListener) {
		this.outputListener = outputListener;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		showNavigationViewWithState();
		if(outputListener != null)outputListener.navigationStateIndex(outputView, prevView, index, counts);
	}

	public NavigationHandler() {
		init();
	}
	
	public NavigationHandler(int counts) {
		this.counts = counts;
		init();
	}
	
	public int getCounts() {
		return counts;
	}
	
	public void setCounts(int counts) {
		this.counts = counts;
	}
	
	private void init(){
		nextListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(index < counts - 1){
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
		if(index < counts - 1){
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
	
}
