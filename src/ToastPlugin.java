package com.global.hbc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.Gravity;
import android.widget.Toast;

public class ToastPlugin extends CordovaPlugin{
	public static final String ACTION_ENTRY = "show";
	public static final String KEY_ARG_MESSAGE = "message";
	
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException{
		if(ACTION_ENTRY.equals(action)){
			JSONObject params = args.getJSONObject(0);
			Toast toast = Toast.makeText(webView.getContext(), params.getString(KEY_ARG_MESSAGE), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return true;
		}
		return false;
	}
}
