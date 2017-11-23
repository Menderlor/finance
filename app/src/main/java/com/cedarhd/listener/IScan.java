package com.cedarhd.listener;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;

import com.cedarhd.zxing.camera.CameraManager;
import com.google.zxing.Result;

/** Zxin扫描需要实现的接口 */
public interface IScan {

	void handleDecode(Result rawResult, Bundle bundle);

	Handler getHandler();

	void setResult(int resultCode, Intent data);

	Rect getCropRect();

	CameraManager getCameraManager();
}
