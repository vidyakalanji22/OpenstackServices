package com.openstack;

public class Meter {
	
	private float inComingByte;
	private float outGoingByte;
	public float getInComingByte() {
		return inComingByte;
	}
	public void setInComingByte(float inComingByte) {
		this.inComingByte = inComingByte;
	}
	public float getOutGoingByte() {
		return outGoingByte;
	}
	public void setOutGoingByte(float outGoingByte) {
		this.outGoingByte = outGoingByte;
	}
	public Meter(float inComingByte, float outGoingByte) {
		super();
		this.inComingByte = inComingByte;
		this.outGoingByte = outGoingByte;
	}

}
