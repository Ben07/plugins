package com.global.hbc.upyun;

public interface LoadingProgressListener {
	void onProgress(int bytesWritten, int totalSize);
}