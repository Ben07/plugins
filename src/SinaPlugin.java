package com.global.hbc;

import java.io.IOException;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.Utility;

public class SinaPlugin extends CordovaPlugin implements IWeiboHandler.Response {
	public static final String ACTION_ENTRY = "share";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_WEBPAGEURL = "webpageUrl";
	public static final String DIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	private IWeiboShareAPI mWeiboShareAPI = null;
	private WeiboAuth mWeiboAuth;
	private CallbackContext mCallbackContext;

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
								Toast.makeText(webView.getContext(), "取消！",
										Toast.LENGTH_LONG).show();
							}
						});
			}

			WeiboMessage weiboMessage = new WeiboMessage();
			JSONObject params = args.getJSONObject(0);

			try {

				if (params.getString(KEY_ARG_MESSAGE_THUMB) != null
						&& !"".equals(params.getString(KEY_ARG_MESSAGE_THUMB))
						&& !"null".equals(params
								.getString(KEY_ARG_MESSAGE_THUMB))) {
					weiboMessage.mediaObject = getWebpageObj(
							params.getString(KEY_ARG_MESSAGE_TITLE),
							params.getString(KEY_ARG_MESSAGE_DESCRIPTION),
							params.getString(KEY_ARG_MESSAGE_THUMB),
							params.getString(KEY_ARG_MESSAGE_WEBPAGEURL));
				} else {
					weiboMessage.mediaObject = getTextObj(
							params.getString(KEY_ARG_MESSAGE_TITLE),
							params.getString(KEY_ARG_MESSAGE_DESCRIPTION),
							params.getString(KEY_ARG_MESSAGE_WEBPAGEURL));
				}

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
		} else if ("login".equals(action)) {
			mCallbackContext = callbackContext;
			mWeiboAuth = new WeiboAuth(webView.getContext(), webView
					.getContext().getString(R.string.sina_app_id), DIRECT_URL,
					"");
			mWeiboAuth
					.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
			return true;
		}
		return false;
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Toast.makeText(webView.getContext(), "取消！", Toast.LENGTH_LONG)
					.show();

		}

		@SuppressWarnings("static-access")
		@Override
		public void onComplete(Bundle arg0) {
			// TODO Auto-generated method stub
			try {
				String code = arg0.getString("code");
				// mCallbackContext.success(code);
				WeiboParameters requestParams = new WeiboParameters();
				requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_ID, webView
						.getContext().getString(R.string.sina_app_id));
				requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_SECRET,
						webView.getContext()
								.getString(R.string.sina_app_srcret));
				requestParams.put(WBConstants.AUTH_PARAMS_GRANT_TYPE,
						"authorization_code");
				requestParams.put(WBConstants.AUTH_PARAMS_CODE, code);
				requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL,
						DIRECT_URL);

				new AsyncWeiboRunner().requestAsync(
						"https://open.weibo.cn/oauth2/access_token",
						requestParams, "POST", new RequestListener() {
							@Override
							public void onComplete(String response) {
								Oauth2AccessToken token = Oauth2AccessToken
										.parseAccessToken(response);
								if (token != null && token.isSessionValid()) {
									JSONObject obj = new JSONObject();
									try {
										obj.put("token", token.getToken());
										obj.put("uid", token.getUid());
										mCallbackContext.success(obj);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
								}
							}

							@Override
							public void onWeiboException(WeiboException e) {
							}
						});
			} catch (Exception e) {

			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}

	}

	private TextObject getTextObj(String title, String des, String webpageUrl) {
		TextObject obj = new TextObject();
		obj.title = title;
		obj.description = des;
		obj.actionUrl = webpageUrl;
		obj.text = des;

		return obj;
	}

	private WebpageObject getWebpageObj(String title, String des, String thumb,
			String webpageUrl) throws IOException {
		WebpageObject obj = new WebpageObject();
		obj.identify = Utility.generateGUID();
		obj.title = title;
		obj.description = des;
		try {
			URL thumbnailUrl = null;
			Bitmap thumbnail = null;

			thumbnailUrl = new URL(thumb);
			thumbnail = BitmapFactory.decodeStream(thumbnailUrl
					.openConnection().getInputStream());
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 150, 150, true);

			obj.setThumbImage(thumbnail);
		} catch (Exception e) {
		}

		obj.actionUrl = webpageUrl;
		obj.defaultText = des;
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