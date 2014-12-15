package com.global.hbc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class AliPayPlugin extends CordovaPlugin {
	public static final String ACTION_ENTRY = "pay";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			Result resultObj = new Result((String) msg.obj);
			String resultStatus = resultObj.resultStatus;

			// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
			if (TextUtils.equals(resultStatus, "9000")) {
				Toast.makeText(webView.getContext(), "支付成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				// 判断resultStatus 为非“9000”则代表可能支付失败
				// “8000”
				// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
				if (TextUtils.equals(resultStatus, "8000")) {
					Toast.makeText(webView.getContext(), "支付结果确认中",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(webView.getContext(), "支付失败",
							Toast.LENGTH_SHORT).show();
				}
			}

		};
	};

	public boolean execute(String action, String arg,
			CallbackContext callbackContext) throws JSONException {
		if (ACTION_ENTRY.equals(action)) {
			try {
				final String info = URLDecoder.decode(arg, "utf-8");

				Runnable payRunnable = new Runnable() {
					@Override
					public void run() {
						PayTask alipay = new PayTask(webView.getActivity());
						String result = alipay.pay(info);

						Message msg = new Message();
						msg.obj = result;
						mHandler.sendMessage(msg);

					}
				};

				Thread payThread = new Thread(payRunnable);
				payThread.start();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}
}
