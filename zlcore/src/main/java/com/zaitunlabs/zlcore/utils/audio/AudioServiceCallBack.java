package com.zaitunlabs.zlcore.utils.audio;

public class AudioServiceCallBack {
	int index;
	int total;
	String url;
	int state;
	public AudioServiceCallBack(int index, int total, String url, int state) {
		setIndex(index);
		setTotal(total);
		setUrl(url);
		setState(state);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
