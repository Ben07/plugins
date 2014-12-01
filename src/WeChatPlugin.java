package com.global.hbc;

import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeChatPlugin extends CordovaPlugin {
	public static final String ACTION_ENTRY = "share";
	public static final String KEY_ARG_SCENE = "scene";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_WEBPAGEURL = "webpageUrl";
	public static CallbackContext currentCallbackContext = null;
	private IWXAPI api;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		currentCallbackContext = callbackContext;
		if (ACTION_ENTRY.equals(action)) {
			return share(args, callbackContext);
		} else if ("login".equals(action)) {
			login(args, callbackContext);
			return true;
		}
		return false;
	}

	private void login(JSONArray args, CallbackContext callbackContext)
			throws JSONException {
		String id = webView.getContext().getString(R.string.wx_app_id);
		api = WXAPIFactory.createWXAPI(this.webView.getContext(), id, true);
		api.registerApp(id);
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		api.sendReq(req);
	}

	private boolean share(JSONArray args, CallbackContext callbackContext)
			throws JSONException {
		JSONObject params = args.getJSONObject(0);
		String appid = webView.getContext().getString(R.string.wx_app_id);
		api = WXAPIFactory.createWXAPI(webView.getContext(), appid, true);
		boolean registerRes = api.registerApp(appid);
		Log.i("register result", String.valueOf(registerRes));

		if (!api.isWXAppInstalled()) {
			Toast.makeText(webView.getContext(), "请您先安装微信客户端",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = params.getString(KEY_ARG_MESSAGE_WEBPAGEURL);

		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = params.getString(KEY_ARG_MESSAGE_TITLE);
		msg.description = params.getString(KEY_ARG_MESSAGE_DESCRIPTION);

		try {
			URL thumbnailUrl = null;
			Bitmap thumbnail = null;

			thumbnailUrl = new URL(params.getString(KEY_ARG_MESSAGE_THUMB));
			thumbnail = BitmapFactory.decodeStream(thumbnailUrl
					.openConnection().getInputStream());
			thumbnail = Bitmap.createScaledBitmap(thumbnail, 150, 150, true);
			msg.setThumbImage(thumbnail);

		} catch (Exception e) {
			e.printStackTrace();
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = params.getInt(KEY_ARG_SCENE);
		boolean sendRes = api.sendReq(req);
		return sendRes;
	}

}
