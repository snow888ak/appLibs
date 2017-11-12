package com.kezhong.libs.permission;

import android.app.Activity;

/**
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/12
 */

public class PermissionHelper {

	public static PermissionManager getPermissionManager(Activity activity, PermissionCallback callback) {
		return new RxPermissionsManager(activity, callback);
	}

}
