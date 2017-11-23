package com.cedarhd.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cedarhd.helpers.server.DownloadHelper;

public class DownloadIntentService extends IntentService {
	public static final String URL = "url";
	public static final String FILE_NAME = "file_name";
	private DownloadHelper mDownloadHelper;

	public DownloadIntentService() {
		super("DownloadIntentService");
	}

	public DownloadIntentService(String name) {
		super(name);
		mDownloadHelper = DownloadHelper.getInstance();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("DownloadIntentService", "DownloadIntentService-->"
				+ Thread.currentThread().getName());
		String url = intent.getStringExtra(URL);
		String fileName = intent.getStringExtra(FILE_NAME);
		// mDownloadHelper.download(url, fileName, handler)
	}
}