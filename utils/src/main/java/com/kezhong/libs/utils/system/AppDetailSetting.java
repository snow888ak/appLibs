package com.kezhong.libs.utils.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * 系统App详情设置
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/12
 */

public class AppDetailSetting {

	/**
	 * 根据包名，获取跳转应用详情界面的Intent
	 * @param pkgname 应用包名
	 * @return
	 */
	public static Intent getAppDetailSettingIntent(String pkgname) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 9) {
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			Uri uri = Uri.fromParts("package", pkgname, null);
			intent.setData(uri);
		} else {
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra("com.android.settings.ApplicationPkgName", pkgname);
		}
		return intent;
	}

	/**
	 * 跳转应用详情界面
	 * @param context
	 */
	public static void toAppDetailSetting(@NonNull Context context){
		if (context == null) {
			throw new IllegalStateException("context not be null!");
		}
		Intent intent = getAppDetailSettingIntent(context.getPackageName());
		context.startActivity(intent);
	}

	/**
	 * 有回调，但是结果不是想要的结果。方法调用完成后，activity的onActivityResult马上就被调用，而且界面还是应用界面，未显示系统界面。
	 * 当应用结果后发现系统界面已显示了。与需求不一致。
	 * @param activity
	 * @param requestCode
	 */
	@Deprecated
	public static void toAppDetailSettingForResult(@NonNull Activity activity, int requestCode) {
		if (activity == null) {
			throw new IllegalStateException("Activity not be null!");
		}
		Intent intent = getAppDetailSettingIntent(activity.getPackageName());
		activity.startActivityForResult(intent, requestCode);
	}

}
