package com.global.hbc.upyun;

public class UpYunException extends Exception{
	private static final long serialVersionUID = 3854772125385537971L;

	public String message;

	public UpYunException(String message) {
		super(message);
	}
}
