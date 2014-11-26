﻿package com.global.hbc;

import java.io.IOException;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;

public class SinaPlugin extends CordovaPlugin implements IWeiboHandler.Response {
	public static final String ACTION_ENTRY = "share";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_WEBPAGEURL = "webpageUrl";

	private IWeiboShareAPI mWeiboShareAPI = null;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (ACTION_ENTRY.equals(action)) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(webView.getContext(),
					webView.getContext().getString(R.string.sina_app_id));

			boolean registerRes = mWeiboShareAPI.registerApp();
			Log.e("register result", String.valueOf(registerRes));
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub
								Toast.makeText(webView.getContext(),
										"分享取消！", Toast.LENGTH_LONG).show();
							}
						});
			}

			WeiboMessage weiboMessage = new WeiboMessage();
			JSONObject params = args.getJSONObject(0);

			try {
				weiboMessage.mediaObject = getWebpageObj(
						params.getString(KEY_ARG_MESSAGE_TITLE),
						params.getString(KEY_ARG_MESSAGE_DESCRIPTION),
						params.getString(KEY_ARG_MESSAGE_THUMB),
						params.getString(KEY_ARG_MESSAGE_WEBPAGEURL));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
			request.transaction = String.valueOf(System.currentTimeMillis());
			request.message = weiboMessage;

			boolean sendRes = mWeiboShareAPI.sendRequest(request);
			Log.e("send result", String.valueOf(sendRes));
			return true;
		}
		return false;
	}

	private WebpageObject getWebpageObj(String title, String des, String thumb,
			String webpageUrl) throws IOException {
		WebpageObject obj = new WebpageObject();
		obj.identify = Utility.generateGUID();
		obj.title = title;
		obj.description = des;

		URL thumbnailUrl = null;
		Bitmap thumbnail = null;

		thumbnailUrl = new URL(thumb);
		thumbnail = BitmapFactory.decodeStream(thumbnailUrl.openConnection()
				.getInputStream());
		thumbnail = Bitmap.createScaledBitmap(thumbnail, 150, 150, true);

		obj.setThumbImage(thumbnail);
		obj.actionUrl = webpageUrl;
		obj.defaultText = "海豹村，专业的海外代购平台";
		return obj;
	}

	public void OnNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO Auto-generated method stub
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Log.e("ok", "ok");
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Log.e("cancel", "cancel");
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Log.e("faile", "faile");
			break;
		}
	}
}