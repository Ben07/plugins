/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.global.hbc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.cordova.CordovaActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.global.hbc.upyun.CompleteListener;
import com.global.hbc.upyun.ProgressListener;
import com.global.hbc.upyun.UpYunUtils;
import com.global.hbc.upyun.UploaderManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

@SuppressLint("NewApi")
public class HbcActivity extends CordovaActivity {
	private final String mPageName = "HbcActivity";
	private String bucket;
	private String secret;
	private String savePath;
	private static File mFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog()
				.build());
		super.init();

		UmengUpdateAgent.update(this);
		// MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		// MobclickAgent.updateOnlineConfig(this);

		String index = launchUrl;
		// super.loadUrl(index + "index.html");
		// Set by <content src="index.html" /> in config.xml
		Bundle bun = getIntent().getExtras();
		// String index = Config.getStartUrl();

		// 推送打开
		String type = bun.getString("type");
		String id = bun.getString("id");

		// 链接打开
		Intent i_getValue = getIntent();
		String action = i_getValue.getAction();

		if (type != null) {
			if ("product".equals(type)) {
				super.loadUrl(index + "#notification?type=nomal&product=" + id);
			}
			if ("article".equals(type)) {
				super.loadUrl(index + "#notification?type=nomal&article=" + id);
			}
			if ("idcard".equals(type)) {
				super.loadUrl(index + "#notification?type=nomal&idcard=" + id);
			}
			if ("home".equals(type)) {
				// super.loadUrl(index + "debugMain.html");
				super.loadUrl(index);
			}
			if ("event".equals(type)) {
				String eventType = bun.getString("eventType");
				int intType = Integer.valueOf(eventType);
				switch (intType) {
				case 1:
					super.loadUrl(index + "#notification?type=event&product="
							+ id);
					break;
				case 2:
					super.loadUrl(index
							+ "#notification?type=event&productlist=" + id);
				case 3:
					super.loadUrl(index + "#notification?type=event&midpage="
							+ id);
				case 4:
					super.loadUrl(index + "#notification?type=event&article="
							+ id);
				}
			}
		} else if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = i_getValue.getData();
			if (uri != null) {
				String linkType = uri.getQueryParameter("type");
				String linkValue = uri.getQueryParameter("value");
				int intLink = Integer.valueOf(linkType);
				switch (intLink) {
				case 1:
					super.loadUrl(index + "#applink?product=" + linkValue);
					break;
				case 2:
					super.loadUrl(index + "#applink?productlist=" + linkValue);
				case 3:
					super.loadUrl(index + "#applink?midpage=" + linkValue);
				case 4:
					super.loadUrl(index + "#applink?article=" + linkValue);
				}
			}
		} else {
			// super.loadUrl(index + "debugMain.html");
			super.loadUrl(index);
		}
		// super.loadUrl("file:///android_asset/www/index.html");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 身份证图片，需要打码
		if (CameraPlugin.hasWaterMask) {
			bucket = getString(R.string.id_card_bucket);
			secret = getString(R.string.id_card_secret);
			savePath = CameraPlugin.userId + "_";

			Bitmap bitmap_2 = BitmapFactory.decodeResource(getResources(),
					R.drawable.watermask);
			bitmap_2 = Bitmap.createScaledBitmap(bitmap_2, 800, 800, true);

			// 从相册中获得
			if (requestCode == 1
					&& resultCode == android.app.Activity.RESULT_OK
					&& null != data) {
				try {
					Uri uri = data.getData();
					InputStream stream = getContentResolver().openInputStream(
							uri);
					Bitmap bitmap_1 = BitmapFactory.decodeStream(stream);
					stream.close();

					bitmap_1 = Bitmap.createScaledBitmap(bitmap_1, 800, 800,
							true);
					Bitmap bitmap = makeBitmap(bitmap_1, bitmap_2);
					persistImage(bitmap, "scaledBitmap");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

				if (requestCode == 1
						&& resultCode == android.app.Activity.RESULT_OK) {
					// 拍照
					try {
						Bitmap bitmap_1 = BitmapFactory
								.decodeFile(CameraPlugin.mPath);

						bitmap_1 = Bitmap.createScaledBitmap(bitmap_1, 800,
								800, true);
						Bitmap bitmap = makeBitmap(bitmap_1, bitmap_2);
						persistImage(bitmap, "scaledBitmap");
					} catch (Exception e) {
						Log.e("------image error", e.getMessage());
					}
				}
			}
		} else {
			bucket = getString(R.string.image_bucket);
			secret = getString(R.string.image_secret);
			savePath = getString(R.string.image_path);

			if (requestCode == 1
					&& resultCode == android.app.Activity.RESULT_OK
					&& null != data) {
				try {
					Uri uri = data.getData();
					InputStream stream = getContentResolver().openInputStream(
							uri);
					Bitmap bitmap = BitmapFactory.decodeStream(stream);
					stream.close();
					bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
					persistImage(bitmap, "scaledBitmap");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				if (requestCode == 1
						&& resultCode == android.app.Activity.RESULT_OK) {
					// 拍照
					try {
						Bitmap bitmap = BitmapFactory
								.decodeFile(CameraPlugin.mPath);

						bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800,
								true);

						persistImage(bitmap, "scaledBitmap");
					} catch (Exception e) {
						Log.e("------image error", e.getMessage());
					}
				}
			}
		}
	}

	private Bitmap makeBitmap(Bitmap src, Bitmap watermask) {
		Bitmap bm = Bitmap.createBitmap(800, 800, Config.ARGB_8888);
		Canvas cv = new Canvas(bm);
		cv.drawBitmap(src, 0, 0, null);
		cv.drawBitmap(watermask, 0, 800 - watermask.getHeight(), null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return bm;
	}

	private void persistImage(Bitmap bitmap, String name) {
		File filesDir = this.getFilesDir();
		File imageFile = new File(filesDir, name + ".jpg");
		OutputStream os;
		try {
			os = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
			os.flush();
			os.close();
			mFile = imageFile;
			new UploadTask().execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
		}
	}

	class UploadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			File localFile = mFile;
			try {
				ProgressListener progressListener = new ProgressListener() {
					@Override
					public void transferred(long transferedBytes,
							long totalBytes) {
					}
				};
				CompleteListener completeListener = new CompleteListener() {
					@Override
					public void result(boolean isComplete, String result,
							String error) {

						mFile.deleteOnExit();
						try {
							JSONObject obj = new JSONObject(result);
							int code = obj.getInt("code");
							if (code == 200) {
								CameraPlugin.mCallbackContext.success(obj
										.getString("path"));
							} else {
								CameraPlugin.mCallbackContext
										.error("哎呀，图片上传出错");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				UploaderManager uploaderManager = UploaderManager
						.getInstance(bucket);
				uploaderManager.setConnectTimeout(60);
				uploaderManager.setResponseTimeout(60);
				String sPath = savePath + System.currentTimeMillis() + ".jpg";
				Map<String, Object> paramsMap = uploaderManager
						.fetchFileInfoDictionaryWith(localFile, sPath);
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap,
						secret);
				uploaderManager.upload(policyForInitial, signatureForInitial,
						localFile, progressListener, completeListener);
			} catch (Exception e) {
				Log.e("-------------upload err", e.getMessage());
			}
			return "result";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
			} else {
			}
		}
	}
}
