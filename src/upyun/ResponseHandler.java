package com.global.hbc.upyun;

import org.apache.http.Header;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class ResponseHandler extends AsyncHttpResponseHandler{
	private LoadingCompleteListener loadingCompleteListener;
	private LoadingProgressListener loadingProgressListener;
	
	public ResponseHandler(LoadingCompleteListener loadingCompleteListener, LoadingProgressListener loadingProgressListener) {
		super(Looper.getMainLooper());
		this.loadingCompleteListener = loadingCompleteListener;
		this.loadingProgressListener = loadingProgressListener;
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
			 error) {
		String standardResponse = ResponseJson.errorResponseJsonFormat(statusCode, headers, responseBody);
		this.loadingCompleteListener.result(false, null, standardResponse);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		String standardResponse = ResponseJson.okResposneJsonFormat(statusCode, headers, responseBody);
		this.loadingCompleteListener.result(true, standardResponse, null);
	}
	
    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (loadingProgressListener != null) {
        	loadingProgressListener.onProgress(bytesWritten, totalSize);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
