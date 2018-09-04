package com.zaitunlabs.zlcore.utils.audio;

public final class AudioVolumeEvent {
	private float volume = -1;
	public AudioVolumeEvent(float volume) {
		this.volume = volume;
	}
	public float getVolume() {
		return volume;
	}
	public void setVolume(float volume) {
		this.volume = volume;
	}
}
