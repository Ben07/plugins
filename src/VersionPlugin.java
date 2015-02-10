package com.global.hbc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class VersionPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if("get".equals(action)){
			PackageManager pm = webView.getContext().getPackageManager();
			try {
				PackageInfo info = pm.getPackageInfo(webView.getContext().getPackageName(),0);
				String version = info.versionName;
				callbackContext.success(version);
				return true;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if("check".equals(action)){
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			    @Override
			    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
			        switch (updateStatus) {
			        case UpdateStatus.Yes: // has update
			            UmengUpdateAgent.showUpdateDialog(webView.getContext(), updateInfo);
			            break;
			        case UpdateStatus.No: // has no update
			        	callbackContext.error(0);
			            break;
			        case UpdateStatus.NoneWifi: // none wifi
			            Toast.makeText(webView.getContext(), "不要任性哦，只在wifi情况下才检查更新", Toast.LENGTH_SHORT).show();
			            break;
			        case UpdateStatus.Timeout: // time out
			            Toast.makeText(webView.getContext(), "超时", Toast.LENGTH_SHORT).show();
			            break;
			        }
			    }
			});
			UmengUpdateAgent.forceUpdate(webView.getContext());
			return true;
		}
		return false;
	}
}
