package com.mk8labs.minskoleinfo.scheduler;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mk8labs.minskoleinfo.PrefsMgr;

public class LoadScheduler {

	private static final long WINDOWLENGHT = 60;

	public static void schedule(Context context, boolean forceTomorrow) {

		AlarmManager a = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getService(context, 1199, new Intent(
				context, LoaderService.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		int hour = PrefsMgr.getSyncHour(context);
		if (hour == -1) {
			PrefsMgr.setNextUpdateTime(context, -1);
			a.cancel(pi);
		} else {
			// set trigger at random within the selected hour
			int skew = (int) (new Random().nextFloat() * WINDOWLENGHT);

			GregorianCalendar windowStart = new GregorianCalendar(
					Locale.getDefault());
			windowStart.set(GregorianCalendar.HOUR_OF_DAY, hour);
			windowStart.set(GregorianCalendar.MINUTE, 0);
			windowStart.set(GregorianCalendar.SECOND, 0);
			windowStart.add(GregorianCalendar.MINUTE, skew);

			if (new GregorianCalendar().after(windowStart) || forceTomorrow) {

				windowStart.add(GregorianCalendar.DAY_OF_MONTH, 1);
			}

			//TODO: debug set time
//			windowStart.setTimeInMillis(new Date().getTime()+10000);
			PrefsMgr.setNextUpdateTime(context, windowStart.getTimeInMillis());
			a.set(AlarmManager.RTC_WAKEUP, windowStart.getTimeInMillis(), pi);
		}
	}

}
