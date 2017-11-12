package com.kezhong.libs.permission;

/**
 * 权限回调
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/12
 */

public interface PermissionCallback {

	/**
	 * 已授权
	 */
	void onPermissionsGranted();

	// TODO: 2017/11/13 列出未授权的权限
	/**
	 * 未授权
	 */
	void onPermissionsDenied();

	// TODO: 2017/11/13 列表出哪些权限已被勾选“永不显示”
	/**
	 * 被禁止显示权限请求框
	 * @param
	 */
	void forbiddenPermissionsRequest();

}
