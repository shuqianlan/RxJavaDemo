package com.ilifesmart.rxjavademo;

public class RxMessage {

	private String message;

	public RxMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message: " + message;
	}
}
