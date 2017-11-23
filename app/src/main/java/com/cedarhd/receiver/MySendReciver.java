package com.cedarhd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cedarhd.InvitationActivity;
import com.cedarhd.utils.LogUtils;

public class MySendReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i("MySendReciver", "MySendReciver----");
		Intent intent2 = new Intent(context, InvitationActivity.class);
		context.startActivity(intent2);
	}

}
