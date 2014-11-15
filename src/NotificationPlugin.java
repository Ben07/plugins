package com.global.hbc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

public class NotificationPlugin extends CordovaPlugin {
	public static final String ACTION_ENTRY = "open";
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext){
		
		if(ACTION_ENTRY.equals(action)){
			PushAgent mPushAgent = PushAgent.getInstance(webView.getContext());
			mPushAgent.enable();
			mPushAgent.onAppStart();
			
			String device_token = UmengRegistrar.getRegistrationId(webView.getContext());
			callbackContext.success(device_token);
			return true;
		}
		return false;
	} 
}
