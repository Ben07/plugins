package com.global.hbc.wxapi;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.global.hbc.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, this.getString(R.string.wx_app_id), false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			// 分享成功
			Toast.makeText(getApplicationContext(), "分享成功！", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			Toast.makeText(getApplicationContext(), "分享取消！", Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// 分享拒绝
			Toast.makeText(getApplicationContext(), "分享拒绝！", Toast.LENGTH_SHORT).show();
			break;
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("input keyevent "+KeyEvent.KEYCODE_BACK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
