package com.global.hbc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class WBAuthActivity extends Activity {
	public static final String DIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;

	public void OnCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWeiboAuth = new WeiboAuth(this, this.getString(R.string.sina_app_id), DIRECT_URL, "");

		mSsoHandler = new SsoHandler(WBAuthActivity.this, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());

		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			Log.e("token", "token");
		}
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
			Log.e("cance", "cancel");
		}

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(WBAuthActivity.this,
						mAccessToken);
			} else {
				String code = values.getString("code");
				if (!TextUtils.isEmpty(code)) {
					Log.e("code", code);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
			Log.e("exception", arg0.getMessage());
		}
	}

}
