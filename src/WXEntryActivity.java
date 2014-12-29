package com.global.hbc.wxapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.global.hbc.R;
import com.global.hbc.WeChatPlugin;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this,
				this.getString(R.string.wx_app_id), false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		Runtime runtime = Runtime.getRuntime();
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			try {
				// WeChatPlugin.currentCallbackContext
				// .success(((SendAuth.Resp) resp).code);
				// 获得个人信息需要的字段
				// List<BasicNameValuePair> params = new
				// LinkedList<BasicNameValuePair>();
				// params.add(new BasicNameValuePair("appid", this
				// .getString(R.string.wx_app_id)));
				// params.add(new BasicNameValuePair("secret", this
				// .getString(R.string.wx_app_secret)));
				// params.add(new BasicNameValuePair("code",
				// ((SendAuth.Resp) resp).code));
				// params.add(new BasicNameValuePair("grant_type",
				// "authorization_code"));
				// String param = URLEncodedUtils.format(params, "UTF-8");
				// String baseUrl =
				// "https://api.weixin.qq.com/sns/oauth2/access_token";
				// HttpGet getMethod = new HttpGet(baseUrl + "?" + param);
				// HttpClient httpClient = new DefaultHttpClient();
				// HttpResponse response = httpClient.execute(getMethod);
				// JSONObject json = new
				// JSONObject(EntityUtils.toString(response
				// .getEntity()));
				//
				// // 获得个人信息
				// params = new LinkedList<BasicNameValuePair>();
				// params.add(new BasicNameValuePair("access_token", json
				// .getString("access_token")));
				// params.add(new BasicNameValuePair("openid", json
				// .getString("openid")));
				// param = URLEncodedUtils.format(params, "UTF-8");
				// baseUrl = "https://api.weixin.qq.com/sns/userinfo";
				// getMethod = new HttpGet(baseUrl + "?" + param);
				// httpClient = new DefaultHttpClient();
				// response = httpClient.execute(getMethod);
				// String info = EntityUtils.toString(response.getEntity());
				// Log.e("-------info", info);
				// String info_decode =
				// java.net.URLDecoder.decode(info,"UTF-8");
				// Log.e("-------info", info_decode);
				// json = new JSONObject();
				JSONObject json = getWXAccessToken(((SendAuth.Resp) resp).code);
				json = getWXMemberInfo(json.getString("openid"),
						json.getString("access_token"));
				WeChatPlugin.currentCallbackContext.success(json);
			} catch (Exception e) {
				Log.e("exception", e.getMessage());
			}
			Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT)
					.show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(getApplicationContext(), "取消！", Toast.LENGTH_SHORT)
					.show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(getApplicationContext(), "失败！", Toast.LENGTH_SHORT)
					.show();
			break;
		}
		try {
			runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JSONObject getWXAccessToken(String code)
			throws ClientProtocolException, IOException {

		String getTokenHttp = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ this.getString(R.string.wx_app_id)
				+ "&secret="
				+ this.getString(R.string.wx_app_secret)
				+ "&code="
				+ code
				+ "&grant_type=authorization_code";
		JSONObject revJson = null;
		revJson = getRemoteData(getTokenHttp);
		return revJson;
	}

	private JSONObject getWXMemberInfo(String openId, String accessToken)
			throws ClientProtocolException, IOException {
		JSONObject revJson = null;
		String getTokenHttp = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ accessToken + "&openid=" + openId

		;
		revJson = getRemoteData(getTokenHttp);
		return revJson;
	}

	private JSONObject getRemoteData(String getTokenHttp) throws IOException,
			ClientProtocolException {
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
						new InputStreamReader(httpEntity.getContent(), "UTF-8"),
						8 * 1024);
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