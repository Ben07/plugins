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

import android.os.Bundle;
import org.apache.cordova.*;

public class HbcActivity extends CordovaActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init();

		String index = Config.getStartUrl();

		Bundle bun = getIntent().getExtras();
		if (bun != null) {
			String type = bun.getString("type");
			String id = bun.getString("id");

			if ("product".equals(type)) {
				super.loadUrl(index + "#notification?productid=" + id);
			}
			if ("article".equals(type)) {
				super.loadUrl(index + "#notification?articleid=" + id);
			}
			if ("idcard".equals(type)) {
				super.loadUrl(index + "#notification?idcard=" + id);
			}
			if ("home".equals(type)) {
				super.loadUrl(index + "index.html");
			}
			if ("event".equals(type)) {
				String eventType = bun.getString("eventType");
				int intType = Integer.valueOf(eventType);
				switch (intType) {
				case 1:
					super.loadUrl(index + "#notification?eventproductid=" + id);
					break;
				case 2:
					super.loadUrl(index + "#notification?eventproductlist="
							+ id);
					break;
				case 3:
					super.loadUrl(index + "#notification?eventmidpage=" + id);
					break;
				case 4:
					super.loadUrl(index + "#notification?eventarticleid=" + id);
					break;
				}
			}
		} else {
			super.loadUrl(index + "index.html");
		}
		// Set by <content src="index.html" /> in config.xml
		// super.loadUrl(Config.getStartUrl());
		// super.loadUrl("file:///android_asset/www/index.html");
	}
}
