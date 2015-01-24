package com.global.hbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import com.sina.weibo.sdk.auth.WeiboAuth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

public class SinaPlugin extends CordovaPlugin implements IWeiboHandler.Response {
	public static final String ACTION_ENTRY = "share";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_WEBPAGEURL = "webpageUrl";
	public static final String DIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	private IWeiboShareAPI mWeiboShareAPI = null;
	private SsoHandler mSsoHandler = null;
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
			// mCallbackContext = callbackContext;
			// mWeiboAuth = new WeiboAuth(webView.getContext(), webView
			// .getContext().getString(R.string.sina_app_id), DIRECT_URL,
			// "");
			// mWeiboAuth
			// .authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
			mCallbackContext = callbackContext;
			AuthInfo authInfo = new AuthInfo(webView.getContext(), webView

			.getContext().getString(R.string.sina_app_id), DIRECT_URL, "");
			WeiboAuth weiboAuth = new WeiboAuth(webView.getContext(), authInfo);
			mSsoHandler = new SsoHandler(cordova.getActivity(), weiboAuth);

			this.cordova.setActivityResultCallback(this);
			this.cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mSsoHandler.authorize(new AuthListener());
				}
			});
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Toast.makeText(webView.getContext(), "取消！", Toast.LENGTH_LONG)
					.show();

		}

		@Override
		public void onComplete(Bundle arg0) {
			// TODO Auto-generated method stub
			try {
				Oauth2AccessToken accessToken = Oauth2AccessToken
						.parseAccessToken(arg0);
				String uid = accessToken.getUid();
				String token = accessToken.getToken();
//				List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
//				params.add(new BasicNameValuePair("access_token", token));
//				params.add(new BasicNameValuePair("uid", uid));
//
//				String param = URLEncodedUtils.format(params, "UTF-8");
//				String baseUrl = "https://api.weibo.com/2/users/show.json";
				try {
//					HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
//					HttpClient httpClient = new DefaultHttpClient();
//					HttpResponse response = httpClient.execute(getMethod);
//					JSONObject json = new JSONObject(
//							EntityUtils.toString(response.getEntity()));
					JSONObject json = getWBMemberInfo(uid,token);
					mCallbackContext.success(json);

				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				Log.e("errrrr", e.getMessage());
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
		}
		private JSONObject getWBMemberInfo(String uid, String accessToken) throws ClientProtocolException, IOException{
			JSONObject revJson = null;
			String getTokenHttp = "https://api.weibo.com/2/users/show.json?access_token="+accessToken+"&uid="+uid;
			revJson = getRemoteData(getTokenHttp);
			return revJson;
		}
		private JSONObject getRemoteData(String getTokenHttp) throws IOException, ClientProtocolException {
			JSONObject revJson = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getTokenHttp);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 得到httpResponse的实体数据
				HttpEntity httpEntity = httpResponse.getEntity();
				revJson = getRevJson(httpEntity);
			}
			return revJson;
		}
		private JSONObject getRevJson(HttpEntity httpEntity) {
			JSONObject revJson = null;
			if (httpEntity != null) {
				try {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpEntity.getContent(),"UTF-8"), 8 * 1024);
					StringBuilder entityStringBuilder = new StringBuilder();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						entityStringBuilder.append(line + "/n");
					}
					// 利用从HttpEntity中得到的String生成JsonObject
					revJson = new JSONObject(entityStringBuilder.toString());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return revJson;
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