package com.mk8labs.minskoleinfo.loader;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.PrefsMgr;
import com.mk8labs.minskoleinfo.loader.TaskProgress.ProgressType;

public class UpdateManager {

	private static Boolean isUpdating = false;

	public static boolean updateStudentList(final Context ctx,
			EventNotifier notif) {

		if (isUpdating) {
			return false;
		}

		isUpdating = true;

		AsyncTask<EventNotifier, String, Boolean> task = new AsyncTask<EventNotifier, String, Boolean>() {

			EventNotifier ev = null;

			@Override
			protected Boolean doInBackground(EventNotifier... params) {

				ev = params[0];
				HttpConnection.open(ctx);
				boolean res = StudentLoader.load(ctx, ev);
				HttpConnection.close();
				return res;

			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				isUpdating = false;
				ev.onCompletion(result);
			}

		};

		task.execute(notif);
		return true;
	}

	public static boolean update(final Context ctx, EventNotifier notif) {

		if (isUpdating) {
			return false;
		}

		isUpdating = true;

		AsyncTask<EventNotifier, TaskProgress, Boolean> task = new AsyncTask<EventNotifier, TaskProgress, Boolean>() {

			private EventNotifier parentProcessNotifier = null;
			private String history = "";

			@SuppressLint("SimpleDateFormat")
			private SimpleDateFormat sdf = new SimpleDateFormat(
					"dd/MM/yy HH:mm:ss");

			@Override
			protected Boolean doInBackground(EventNotifier... params) {

				HttpConnection.open(ctx);

				parentProcessNotifier = params[0];

				EventNotifier notif = new EventNotifier() {

					@Override
					public void onWorkInfo(String msg) {
						super.onWorkInfo(msg);
						TaskProgress p = new TaskProgress(ProgressType.STRING,
								msg);
						Log.d("TASK: " + msg);
						publishProgress(p);
					}

				};

				notif.onWorkInfo("Opdatering starter");

				new SkemaLoader(ctx, notif).load();
				new LektieLoader(ctx, notif).load();

				notif.onWorkInfo("Opdatering afsluttet");
				PrefsMgr.setLastUpdateTime(ctx);

				HttpConnection.close();

				return true;
			}

			@Override
			protected void onProgressUpdate(TaskProgress... values) {
				super.onProgressUpdate(values);

				if (values[0].type == ProgressType.STRING) {
					parentProcessNotifier.onWorkInfo((String) values[0].value);
					history += sdf.format(new Date()) + " "
							+ (String) values[0].value + "\n";
					PrefsMgr.setUpdateHistory(ctx, history);
				}

			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				PrefsMgr.setUpdateHistory(ctx, history);
				parentProcessNotifier.onCompletion(result);
				isUpdating = false;
			}

		};

		task.execute(notif);
		return true;
	}

	public static boolean updateInProgress() {
		return isUpdating;
	}
}
