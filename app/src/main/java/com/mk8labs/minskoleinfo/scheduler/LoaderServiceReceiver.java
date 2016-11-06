package com.mk8labs.minskoleinfo.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LoaderServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		LoadScheduler.schedule(ctx, false);
	}
}
