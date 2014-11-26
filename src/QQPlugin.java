package com.global.hbc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQPlugin extends CordovaPlugin {
	public static final String ACTION_ENTRY = "share";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_WEBPAGEURL = "webpageUrl";
	public Bundle bundle = new Bundle();
	private static CordovaWebView myWebView;
	public static Tencent mTencent;

	IUiListener BaseUiListener = new IUiListener() {
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(QQPlugin.myWebView.getContext(), "成功！",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(QQPlugin.myWebView.getContext(), "失败！",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(QQPlugin.myWebView.getContext(), "取消！",
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (ACTION_ENTRY.equals(action)) {
			JSONObject params = args.getJSONObject(0);
			myWebView = webView;
			String appid = webView.getContext().getString(R.string.qq_app_id);

			mTencent = Tencent.createInstance(appid, webView.getContext());

			bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
					params.getString(KEY_ARG_MESSAGE_WEBPAGEURL));
			bundle.putString(QQShare.SHARE_TO_QQ_TITLE,
					params.getString(KEY_ARG_MESSAGE_TITLE));
			bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
					params.getString(KEY_ARG_MESSAGE_THUMB));
			bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY,
					params.getString(KEY_ARG_MESSAGE_DESCRIPTION));

			mTencent.shareToQQ(webView.getActivity(), bundle, BaseUiListener);
			return true;

		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.REQUEST_QQ_SHARE)
			if (resultCode == Constants.ACTIVITY_OK) {
				Tencent.handleResultData(data, BaseUiListener);
			}
	}
}
