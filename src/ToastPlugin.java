package com.global.hbc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

public class ToastPlugin extends CordovaPlugin{
	public static final String ACTION_ENTRY = "show";
	public static final String KEY_ARG_MESSAGE = "message";
	public static final String KEY_ARG_TIME = "time";
	public static final String KEY_ARG_POS = "position";
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException{
		if(ACTION_ENTRY.equals(action)){
			JSONObject params = args.getJSONObject(0);
			String message = params.getString(KEY_ARG_MESSAGE);
			int time = params.getInt(KEY_ARG_TIME);
			if(time == 0){
				Toast.makeText(webView.getContext(), message,
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(webView.getContext(), message,
						Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return false;
	}
}
