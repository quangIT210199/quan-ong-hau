package com.codelovers.quanonghau.cmd;

public class AbstractQueueMessage implements IQueueMessage, Comparable<Object> {
	protected String code = null;
	protected int timeToLive = 3;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.equals(o)?1:0;
	}	
}

