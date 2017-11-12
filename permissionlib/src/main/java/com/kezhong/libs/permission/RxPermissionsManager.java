package com.kezhong.libs.permission;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 通过RxPermission实现对运行时权限的管理
 * Created by kezhong.
 * QQ:396926020@qq.com
 * on 2017/11/12
 */

public class RxPermissionsManager implements PermissionManager {

	private Activity mActivity;
	private PermissionCallback mCallback;

	protected RxPermissionsManager(Activity activity, PermissionCallback callback) {
		this.mActivity = activity;
		this.mCallback = callback;
	}

	@Override
	public void requestPermissions(final String... permissions) {
		if (permissions == null || permissions.length == 0) {
			return;
		}
		RxPermissions rxPermissions = new RxPermissions(mActivity);
		rxPermissions.requestEach(permissions)
				.filter(new Predicate<Permission>() {
					@Override
					public boolean test(Permission permission) throws Exception {
						return !permission.granted;
					}
				})
				.map(new Function<Permission, Permission>() {
					@Override
					public Permission apply(Permission permission) throws Exception {
						if (!permission.shouldShowRequestPermissionRationale) {
							throw new ForbiddenRequestPermissionException("已勾选“永不显示”");
						}
						return permission;
					}
				})
				.count()
				.compose(new SingleTransformer<Long, Boolean>() {
					@Override
					public SingleSource<Boolean> apply(Single<Long> upstream) {
						return upstream.map(new Function<Long, Boolean>() {
							@Override
							public Boolean apply(Long aLong) throws Exception {
								if (aLong == null || aLong <= 0) {
									return true;
								} else {
									return false;
								}
							}
						});
					}
				})
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) throws Exception {
						if (mCallback != null) {
							if (aBoolean) {
								mCallback.onPermissionsGranted();
							} else {
								mCallback.onPermissionsDenied();
							}
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						if (throwable instanceof ForbiddenRequestPermissionException) {
							if (mCallback != null) {
								mCallback.forbiddenPermissionsRequest();
							}
						}
					}
				});
	}

}
