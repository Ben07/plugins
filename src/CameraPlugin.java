package com.global.hbc;

import java.io.File;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class CameraPlugin extends CordovaPlugin {
	public static final String ACTION_CAMERA_ENTRY = "camera";
	public static final String ACTION_PICTURE_ENTRY = "picture";
	public static boolean hasWaterMask = false;
	public static String userId;
	public static CallbackContext mCallbackContext;
	public static String mPath;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		mCallbackContext = callbackContext;
		JSONObject obj = args.getJSONObject(0);
		int type = obj.getInt("type");
		if(type == 0){
			hasWaterMask = false;
		}else{
			hasWaterMask = true;
			userId = obj.getString("userId");
		}
		
		if (ACTION_CAMERA_ENTRY.equals(action)) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						final String dir = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
								+ "/hbcun/";
						File newdir = new File(dir);
						newdir.mkdirs();
						String file = dir + System.currentTimeMillis() + ".jpg";
						mPath = file;
						File newfile = new File(file);
						newfile.createNewFile();
						Uri outputFileUri = Uri.fromFile(newfile);
						Intent cameraIntent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								outputFileUri);
						cordova.setActivityResultCallback(CameraPlugin.this);
						cordova.getActivity().startActivityForResult(
								cameraIntent, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return true;
		}
		if (ACTION_PICTURE_ENTRY.equals(action)) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					cordova.setActivityResultCallback(CameraPlugin.this);
					cordova.getActivity().startActivityForResult(intent, 1);
				}
			});

			return true;
		}
		return false;
	}
}
