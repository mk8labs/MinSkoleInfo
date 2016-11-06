package com.mk8labs.minskoleinfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mk8labs.minskoleinfo.R;
import com.mk8labs.minskoleinfo.loader.EventNotifier;
import com.mk8labs.minskoleinfo.loader.HttpConnection;
import com.mk8labs.minskoleinfo.loader.LektieLoader;
import com.mk8labs.minskoleinfo.loader.UpdateManager;
import com.mk8labs.minskoleinfo.scheduler.LoadScheduler;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setViewVisibility(R.id.progressBarUpdating, false);
		setViewVisibility(R.id.buttonUpdateNow, true);
		setUpdateText();
		setTextView(R.id.textUpdateDetailed, PrefsMgr.getUpdateHistory(this));

		PrefsMgr.setNextUpdateTimeNotifier(new EventNotifier() {
			@Override
			public void onCompletion(Boolean status) {
				super.onCompletion(status);
				setUpdateText();
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		PrefsMgr.setNextUpdateTimeNotifier(null);
	}

	private void test() {
		
		
		 final Context ctx=this;
		 new AsyncTask<String, String, String>() {
			 @Override
			 protected String doInBackground(String... params) {
				HttpConnection.open(ctx);
				PrefsMgr.dumpPrefs(ctx);
				new LektieLoader(ctx, new EventNotifier() {}).load();
				return null;
			 }
		 }.execute("");
		
		
		// new LektieLoader(this).load();
		// new LektieLoader().load();
		// String res=LektieTabel.parseTable(null);

		// StudentParser.parse(null);
		// ArrayList<SkemaEvent> vals=SkemaParser.parse(null);
		// Log.d("",vals.toString());

		// PrefsMgr.dumpPrefs(this);
		// CalendarList lst=CalendarHelper.getCalendars(this);
//		CalendarHelper.test(this, "23");
//
//		CalendarHelper.removeEvent(this, "23", "2164");
//		CalendarHelper.removeEvent(this, "23", "2165");
//
//		Calendar c = Calendar.getInstance();
//		c.clear();
//		c.set(2015, 02, 30, 12, 0, 0);
//		long st = c.getTimeInMillis();
//		c.set(2015, 02, 30, 12, 45, 0);
//		long stop = c.getTimeInMillis();
//
//		CalendarEvent ev = new CalendarEvent("23", "", "TEST3", st, stop);
		// ev.timezone="Europe/Copenhagen";

		// CalendarHelper.addEvent(this, ev);
		// CalendarHelper.test(this, "23");

		// PrefsMgr.clearPref(this, "1");
		// PrefsMgr.clearPref(this, "studentlist");
		// PrefsMgr.clearPref(this, "2");
		// PrefsMgr.clearPref(this, "studentcalendar-1");
		// PrefsMgr.clearPref(this, "studentcalendar-2");
		// PrefsMgr.clearPref(this, "studentcalendar2096");
		// PrefsMgr.clearPref(this, "studentcalendar2397");

		//
		// long l=CalendarHelper.getTimeMillis("06-04-2015 13:00",
		// "dd-MM-yyy hh:mm");
		// CalendarEvent ev=CalendarHelper.getEvent(this, "17", l );
		// CalendarHelper.updateEvent(this, "1715",
		// "Emil til f�dselsdag hos Lucas");
		// ev=CalendarHelper.getEvent(this, "17", l );
		// Log.d("",ev.timezone);
		// LoadScheduler.schedule(this);

	}

	// button handler
	public void onButtonStartService(View view) {
		LoadScheduler.schedule(this, false);
	}

	public void onButtonUpdateNow(View view) {
		performUpdate();
		// TODO DEBUG
//		test();
	}
	
	private void performUpdate() {

		if (UpdateManager.updateInProgress())
			return;

		setViewVisibility(R.id.progressBarUpdating, true);
		setViewVisibility(R.id.buttonUpdateNow, false);
		setTextView(R.id.textUpdateDetailed, null, false);

		final Context ctx = this;

		UpdateManager.update(this, new EventNotifier() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onWorkInfo(String msg) {
				super.onWorkInfo(msg);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				setTextView(R.id.textUpdateDetailed, sdf.format(new Date())
						+ " " + msg + "\n", true);
			}

			@Override
			public void onCompletion(Boolean status) {

				setViewVisibility(R.id.progressBarUpdating, false);
				setViewVisibility(R.id.buttonUpdateNow, true);
				setUpdateText();

				if (status) {
					PrefsMgr.setLastUpdateTime(ctx);
				}
			}
		});
	}

	private View setViewVisibility(int id, boolean visibility) {
		View v = findViewById(id);
		if (null != v) {
			if (visibility) {
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.INVISIBLE);
			}
		}
		return v;
	}

	private void setTextView(int id, String value, boolean append) {
		TextView v = (TextView) findViewById(id);
		if (null != v) {
			if (append) {
				v.append(value);
			} else {
				v.setText(value);
			}
		}
	}

	private void setTextView(int id, String value) {
		setTextView(id, value, false);
	}

	@SuppressLint("SimpleDateFormat")
	private void setUpdateText() {
		Date lastUpdate = PrefsMgr.getLastUpdateTime(this);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		if (null == lastUpdate) {
			setTextView(R.id.textLastUpdate, "Aldrig");
		} else {
			setTextView(R.id.textLastUpdate, sdf.format(lastUpdate));
		}

		Date nextUpdate = PrefsMgr.getNextUpdateTime(this);
		if (null == nextUpdate) {
			setTextView(R.id.textNextUpdateValue, "Aldrig");
		} else {
			setTextView(R.id.textNextUpdateValue, sdf.format(nextUpdate));
		}

		setTextView(R.id.textUpdateDetailed, PrefsMgr.getUpdateHistory(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		
			case R.id.action_settings:
					startActivity(new Intent(this, SettingsActivity.class));
					break;
					
			case R.id.action_about:
				startActivity(new Intent(this, AboutActivity.class));
				break;
				
			case R.id.action_update:
				performUpdate();
				break;
				
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
