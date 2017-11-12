package com.kezhong.libs.permission;

/**
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/13
 */

public class ForbiddenRequestPermissionException extends IllegalStateException {

	public ForbiddenRequestPermissionException(String s) {
		super(s);
	}

}
