package com.kezhong.libs.permission;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/12
 */

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.addLogAdapter(new AndroidLogAdapter());
	}
}
