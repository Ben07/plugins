package com.global.hbc;

import java.io.File;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Environment;

public class ClearPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ("clear".equals(action)) {
			Context context = webView.getContext();
			cleanInternalCache(context);
			cleanExternalCache(context);
			cleanDatabases(context);
			cleanSharedPreference(context);
			cleanFiles(context);
			return true;
		}
		return false;
	}

	public void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	public void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	public void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	public void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	public void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	public void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	public void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

//	public void cleanApplicationData(Context context, String... filepath) {
//		cleanInternalCache(context);
//		cleanExternalCache(context);
//		cleanDatabases(context);
//		cleanSharedPreference(context);
//		cleanFiles(context);
//		for (String filePath : filepath) {
//			cleanCustomCache(filePath);
//		}
//	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}
}
