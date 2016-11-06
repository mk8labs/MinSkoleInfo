package com.mk8labs.minskoleinfo;

import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.mk8labs.minskoleinfo.R;
import com.mk8labs.minskoleinfo.calendar.CalendarHelper;
import com.mk8labs.minskoleinfo.calendar.CalendarList;
import com.mk8labs.minskoleinfo.loader.EventNotifier;
import com.mk8labs.minskoleinfo.loader.UpdateManager;

public class SettingsActivity extends PreferenceActivity {

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}
	
	@SuppressLint("Override")
	public boolean isValidFragment(String s) {
		return true;
	}
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries
			bindPreferenceSummaryToValue(findPreference("baseurl"));
			bindPreferenceSummaryToValue(findPreference("userid"));

			// Configure refresh button
			Preference refreshList = findPreference("refresh_student_list");
			refreshList
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							final Preference refreshList = findPreference("refresh_student_list");
							refreshList
									.setSummary("Opdaterer listen, vent venligst...");
							UpdateManager.updateStudentList(getActivity(),
									new EventNotifier() {
										public void onCompletion(Boolean status) {
											refreshList.setSummary("");
										};
									});
							return true;
						}
					});

		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ScheduleToCalendarPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			loadStudentListPref(this, PrefsMgr.SKEMAKALENDERPFX);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class HomeworkToCalendarPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			loadStudentListPref(this, PrefsMgr.LEKTIEKALENDERPFX);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ClassPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.pref_empty);

			Set<String> keys = PrefsMgr.getClassList(getActivity());
			populateEditTextList(this, PrefsMgr.CLASSDESCRIPTION, keys);

		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class TeacherPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.pref_empty);

			Set<String> keys = PrefsMgr.getTeacherList(getActivity());
			populateEditTextList(this, PrefsMgr.TEACHERDESCRIPTION, keys);
		}
	}

	private static void populateEditTextList(PreferenceFragment ctx,
			String keypfx, Set<String> keys) {

		for (String id : keys) {
			String key = keypfx + id;

			EditTextPreference lp = (EditTextPreference) ctx
					.getPreferenceScreen().findPreference(key);
			if (null == lp) {
				lp = new EditTextPreference(ctx.getActivity());
				lp.setKey(key);
				lp.setDefaultValue(id);
				ctx.getPreferenceScreen().addPreference(lp);
				bindPreferenceSummaryToValue(lp);
			}
			lp.setTitle(id);
			bindPreferenceSummaryToValue(lp);
		}
	}

	private static void loadStudentListPref(PreferenceFragment ctx, String pfx) {

		ctx.addPreferencesFromResource(R.xml.pref_empty);

		Set<String> sl = PrefsMgr.getStudentList(ctx.getActivity());
		CalendarList calendarList = CalendarHelper.getCalendars(ctx
				.getActivity());

		// for (int i=0;i<sl.length();i++) {
		for (String ID : sl) {

			String key = pfx + ID;// sl.getID(i);

			ListPreference lp = (ListPreference) ctx.getPreferenceScreen()
					.findPreference(key);
			if (null == lp) {
				lp = new ListPreference(ctx.getActivity());
				lp.setKey(key);
				lp.setEntries(calendarList.calNames);
				lp.setEntryValues(calendarList.calIDs);
				lp.setDefaultValue(PrefsMgr.NOSYNC);
				ctx.getPreferenceScreen().addPreference(lp);
				bindPreferenceSummaryToValue(lp);
			}
			lp.setTitle(PrefsMgr.getStudentName(ctx.getActivity(), ID));// sl.getName(i));
		}
	}

}
