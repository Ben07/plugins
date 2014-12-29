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

import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;

import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class HbcActivity extends CordovaActivity {
	private final String mPageName = "HbcActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		super.init();

		// MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		// MobclickAgent.updateOnlineConfig(this);

		String index = Config.getStartUrl();
		// super.loadUrl(index + "index.html");
		// Set by <content src="index.html" /> in config.xml
		Bundle bun = getIntent().getExtras();
		// String index = Config.getStartUrl();

		String type = bun.getString("type");
		String id = bun.getString("id");

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
}
