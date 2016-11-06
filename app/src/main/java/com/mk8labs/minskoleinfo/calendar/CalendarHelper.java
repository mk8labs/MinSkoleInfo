package com.mk8labs.minskoleinfo.calendar;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.PrefsMgr;

public class CalendarHelper {

	public static final String[] CALENDAR_PROJECTION = new String[] {
			Calendars._ID, // 0
			Calendars.ACCOUNT_NAME, // 1
			Calendars.CALENDAR_DISPLAY_NAME, // 2
			Calendars.OWNER_ACCOUNT // 3
	};

	public static final String[] EVENT_PROJECTION = new String[] {
			CalendarContract.Events._ID, // 0
			CalendarContract.Events.TITLE, // 1
			CalendarContract.Events.DTSTART, // 2
			CalendarContract.Events.DTEND, // 3
			CalendarContract.Events.EVENT_TIMEZONE, // 4
			CalendarContract.Events.CALENDAR_ID,
			CalendarContract.Events.ALL_DAY};

	private static final String EVENT_SELECTION = CalendarContract.Events.CALENDAR_ID
			+ "=? "
			+ " AND "
			+ CalendarContract.Events.DTSTART
			+ "=?"
			+ " AND "
			+ CalendarContract.Events.DESCRIPTION
			+ " LIKE \""
			+ CalendarEvent.CALENTRYTAG + "%\"";

	public static CalendarList getCalendars(Context ctx) {
		// Run query
		Cursor cur = null;
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;

		// Submit the query and get a Cursor object back.
		cur = cr.query(uri, CALENDAR_PROJECTION, null, null, null);
		int count = cur.getCount();
		CalendarList calLst = new CalendarList(count + 1);

		calLst.calNames[0] = "Ingen synkronisering";
		calLst.calIDs[0] = PrefsMgr.NOSYNC;

		for (int i = 0; i < count; i++) {
			cur.moveToNext();
			calLst.calNames[i + 1] = cur.getString(2);
			calLst.calIDs[i + 1] = cur.getString(0);
		}
		cur.close();
		return calLst;
	}

	public static CalendarEvent getEvent(Context ctx, String calendarID,
			long dtstart) {
		// Run query
		Cursor cur = null;
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = CalendarContract.Events.CONTENT_URI;

		// Submit the query and get a Cursor object back.
		String[] selArgs = new String[] { calendarID, Long.toString(dtstart) };
		cur = cr.query(uri, EVENT_PROJECTION, EVENT_SELECTION, selArgs, null);
//		int count = cur.getCount();

		CalendarEvent ev = null;
		if (cur.moveToNext()) {
			ev = new CalendarEvent(calendarID, cur.getString(0),
					cur.getString(1), cur.getLong(2), cur.getLong(3),cur.getInt(6)!=0);
			ev.timezone = cur.getString(4);
		}
		cur.close();
		return ev;
	}

	public static void removeEvent(Context ctx, String calendarID,
			String eventID) {
		ContentResolver cr = ctx.getContentResolver();

		cr.delete(CalendarContract.Events.CONTENT_URI, "_id=?",
				new String[] { eventID });
	}

	public static void removeAllEvents(Context ctx, String calendarID) {
		ContentResolver cr = ctx.getContentResolver();

		cr.delete(CalendarContract.Events.CONTENT_URI, "calendar_ID=?",
				new String[] { calendarID });
	}
	
	public static int updateEvent(Context ctx, CalendarEvent event) {// String
																		// eventID,String
																		// title,
																		// String
																		// description)
																		// {

		ContentResolver cr = ctx.getContentResolver();
		Uri uri = ContentUris.withAppendedId(
				CalendarContract.Events.CONTENT_URI,
				Long.parseLong(event.eventID));

		ContentValues vals = new ContentValues();
		vals.put(CalendarContract.Events.TITLE, event.title);
		vals.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
		int num = cr.update(uri, vals, null, null);
		return num;
	}

	public static String addEvent(Context ctx, CalendarEvent event) {
		ContentResolver cr = ctx.getContentResolver();

		ContentValues vals = new ContentValues();
		vals.put(CalendarContract.Events.CALENDAR_ID, event.calendarID);
		vals.put(CalendarContract.Events.TITLE, event.title);
		vals.put(CalendarContract.Events.DTSTART, event.dtstart);
		vals.put(CalendarContract.Events.DTEND, event.dtend);
		vals.put(CalendarContract.Events.EVENT_TIMEZONE, event.timezone);
		vals.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
		vals.put(CalendarContract.Events.ALL_DAY, event.allDay);

		Uri num = cr.insert(CalendarContract.Events.CONTENT_URI, vals);
		// Log.d("",num.toString());
		return num.getLastPathSegment();
	}
	
	public static void dumpCalendar(Context ctx, String calendarID) {
		// Run query
		Cursor cur = null;
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = CalendarContract.Events.CONTENT_URI;

		// Submit the query and get a Cursor object back.
		cur = cr.query(uri, EVENT_PROJECTION, "calendar_id=?",
				new String[] { calendarID }, null);

		while (cur.moveToNext()) {
			Log.d(cur.getString(5) + "  -  " + cur.getString(0) + "  -  "
					+ cur.getString(1) + "  -  dtstart "
					+ new Date(cur.getLong(2)) + "  " + cur.getLong(2)
					+ "  -  " + new Date(cur.getLong(3)) + "  "
					+ cur.getString(4));
		}
		cur.close();
	}

//	public static long getTimeMillis(String dateString, String dateFormat) {
//		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//		try {
//			Date d = sdf.parse(dateString);
//			return d.getTime();
//		} catch (ParseException e) {
//		}
//		return -1;
//
//	}

}
