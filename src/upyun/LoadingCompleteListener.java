package com.global.hbc.upyun;

public interface LoadingCompleteListener {
	void result(boolean isSuccess, String response, String error);
}