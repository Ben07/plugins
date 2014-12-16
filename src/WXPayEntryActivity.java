package com.global.hbc.wxapi;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.global.hbc.R;
import com.global.hbc.WeChatPlugin;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private IWXAPI api;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	api = WXAPIFactory.createWXAPI(this, this.getString(R.string.wx_app_id));
        api.handleIntent(getIntent(), this);
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		Runtime runtime = Runtime.getRuntime();
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
//			Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT)
//					.show();
			WeChatPlugin.currentCallbackContext.success();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(getApplicationContext(), "取消！", Toast.LENGTH_SHORT)
					.show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(getApplicationContext(), "失败！", Toast.LENGTH_SHORT)
					.show();
			WeChatPlugin.currentCallbackContext.error("失败");
			break;
		}
		try {
			runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
