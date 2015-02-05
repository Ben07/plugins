package com.global.hbc;

import java.io.File;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ClearPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ("clear".equals(action)) {
			clearApplicationData();
			callbackContext.success();
			return true;
		}
		return false;
	}

	public void clearApplicationData() {
	    File cache = webView.getContext().getCacheDir();
	    File appDir = new File(cache.getParent());
	    if (appDir.exists()) {
	        String[] children = appDir.list();
	        for (String s : children) {
	            if (!s.equals("lib")) {
	                deleteDir(new File(appDir, s));
	            }
	        }
	    }
	}

	public boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
}
