package com.kezhong.libs.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kezhong.libs.utils.system.AppDetailSetting;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity {

	private Button mBtnCamera;
	private Button mBtnCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtnCamera = findViewById(R.id.btn_camera);
		mBtnCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(MainActivity.this,
						Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					Log.d("Permission", "已授权");
					Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
				} else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
					Log.d("Permission", "请求权限");
					Toast.makeText(MainActivity.this, "请求权限", Toast.LENGTH_SHORT).show();
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 10001);
				} else {
					Log.d("Permission", "未授权");
					Toast.makeText(MainActivity.this, "未授权", Toast.LENGTH_SHORT).show();
				}

			}
		});
		mBtnCall = findViewById(R.id.btn_call);
		mBtnCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ContextCompat.checkSelfPermission(MainActivity.this,
						Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
					Log.d("Permission", "已授权");
					Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Intent.ACTION_CALL);
					Uri data = Uri.parse("tel:" + "123456789");
					intent.setData(data);
					startActivity(intent);
				} else {
					Log.d("Permission", "未授权");
					Toast.makeText(MainActivity.this, "未授权", Toast.LENGTH_SHORT).show();
					ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 10001);
				}
			}
		});
		findViewById(R.id.btn_single_permissions).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RxPermissions permissions = new RxPermissions(MainActivity.this);
				permissions.requestEach(Manifest.permission.CAMERA)
						.subscribe(new Consumer<Permission>() {
							@Override
							public void accept(Permission permission) throws Exception {
								if (permission.granted) {
									Toast.makeText(MainActivity.this, "已授权", Toast.LENGTH_SHORT).show();
								} else if (permission.shouldShowRequestPermissionRationale) {
									Toast.makeText(MainActivity.this, "被拒绝", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(MainActivity.this, "已禁止显示询问", Toast.LENGTH_SHORT).show();
									showTipDialog();
								}
							}
						});
			}
		});
		findViewById(R.id.btn_multiple_permissions).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RxPermissions permissions = new RxPermissions(MainActivity.this);
				permissions.requestEach(Manifest.permission.CALL_PHONE,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.READ_EXTERNAL_STORAGE)
						.filter(new Predicate<Permission>() {
							@Override
							public boolean test(Permission permission) throws Exception {
								return !permission.granted;
							}
						})
						.subscribe(new io.reactivex.Observer<Permission>() {

							private Disposable mDisposable;

							@Override
							public void onSubscribe(Disposable d) {
								Logger.d("onSubscribe");
								mDisposable = d;
							}

							@Override
							public void onNext(Permission permission) {
								Logger.d("onNext + " + permission);
								if (!permission.granted) {
									//有任何权限被拒绝，停止
									mDisposable.dispose();
									if (!permission.shouldShowRequestPermissionRationale) {
										showTipDialog();
									}
									Logger.d("dispose");
									//测试是否能通过AS提交代码更新。
								}
							}

							@Override
							public void onError(Throwable e) {
								Logger.d("onError");
							}

							@Override
							public void onComplete() {
								Logger.d("onComplete");
							}
						});
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, permissions[i] + "已授权", Toast.LENGTH_SHORT).show();
			} else {
				boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
				if (isTip) {
					ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, 10001);
				} else {
					Toast.makeText(this, "禁止权限请求框", Toast.LENGTH_SHORT).show();
					showTipDialog();
					return;
				}
			}
		}
	}

	private void showTipDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("请设置相关权限！")
				.setTitle("提示！")
				.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppDetailSetting.toAppDetailSetting(MainActivity.this);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialogBuilder.create().show();

	}

}
