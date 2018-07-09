package com.tuyennta.automation.utils;

public class Timer {

	private Timer() {
		
	}
	
	private static Timer INSTANCE;
	
	public static Timer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Timer();
		}
		return INSTANCE;
	}
	
	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	private long interval;
}
