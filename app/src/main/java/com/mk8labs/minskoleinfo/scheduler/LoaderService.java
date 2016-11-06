package com.mk8labs.minskoleinfo.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.loader.EventNotifier;
import com.mk8labs.minskoleinfo.loader.UpdateManager;

public class LoaderService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("Loader service starting");

		new AsyncTask<LoaderService, Object, Boolean>() {

			LoaderService ctx;

			@Override
			protected Boolean doInBackground(LoaderService... params) {

				Log.i("Loader thread starting");
				ctx = params[0];
				UpdateManager.update(ctx, new EventNotifier() { });
				Log.i("Loader thread complete");
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				LoadScheduler.schedule(ctx, true);
				Log.i("Loader service complete");

				ctx.stopSelf();
			}

		}.execute(this);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
