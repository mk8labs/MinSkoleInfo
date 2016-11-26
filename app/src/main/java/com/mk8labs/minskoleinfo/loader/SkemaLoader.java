package com.mk8labs.minskoleinfo.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.PrefsMgr;
import com.mk8labs.minskoleinfo.calendar.CalendarEvent;
import com.mk8labs.minskoleinfo.calendar.CalendarHelper;

class SkemaLoader {

	private final static String SKEMAPAGECONTENT = "calendar/schedule";

	private SkemaParser skemaParser = new SkemaParser();
	private Context ctx;
	private EventNotifier notif;
	
	SkemaLoader(final Context ctx, EventNotifier notif) {
		this.ctx=ctx;
		this.notif=notif;
	}
	
	@SuppressLint("SimpleDateFormat")
	void load() {

		// get students
		Set<String> studentlist = PrefsMgr.getStudentList(ctx);

		// update schedule for each student
		for (String sid : studentlist) {
			String calendarID = PrefsMgr.getScheduleCalendarID(ctx, sid);

			if (calendarID.equals(PrefsMgr.NOSYNC))
				continue;

			notif.onWorkInfo("Indlæser denne uges skemainformation for "
					+ PrefsMgr.getStudentName(ctx, sid));
			HttpConnection.setStudentContext(sid);

			// load this week "/scheme?period=428&_=" + new Date().getTime()
			ArrayList<SkemaEvent> lst=loadSkemaUge("");
			storeSkemaUge(lst,calendarID,0);

			// load next week
			ArrayList<String> ids = skemaParser.getWeeksIds();
			if (null != ids && ids.size()>1) { //id defined and more than one week then load next week data
				notif.onWorkInfo("Indlæser næste uges skemainformation for "
						+ PrefsMgr.getStudentName(ctx, sid));
				lst=loadSkemaUge("/scheme?period=" + ids.get(1));
				storeSkemaUge(lst,calendarID,0);
			} else {
				//schedule is static for all weeks, load same schedule but store in next week
				storeSkemaUge(lst,calendarID,1);
			}
			if (null != notif)
				notif.onCompletion(true);

		}
	}

	private ArrayList<SkemaEvent> loadSkemaUge(String path) {

		InputStream resp = HttpConnection.get(SKEMAPAGECONTENT + path, notif);

		if (null == resp) {
			notif.onWorkInfo("Fejl ved læsning: ingen data modtaget");
			return null;
		}

		notif.onWorkInfo("Data modtaget, indlæser...");
		ArrayList<SkemaEvent> lst = skemaParser.parse(ctx, resp);
		try {
			resp.close();
		} catch (IOException e) {
			notif.onWorkInfo("Fejl ved læsning: " + e.getClass().getName()
					+ " - " + e.getMessage());
			return null;
		}

		return lst;
	}
	
	private void storeSkemaUge(ArrayList<SkemaEvent> lst,String calendarID, int weekOffset) {

		notif.onWorkInfo("Indlæser timeplan i kalenderen");
		for (SkemaEvent ev : lst) {

			if (weekOffset!=0) {
				GregorianCalendar cal=new GregorianCalendar();
				cal.clear();
				cal.setTimeInMillis(ev.dtstart.getTime());
				cal.add(GregorianCalendar.WEEK_OF_YEAR, weekOffset);
				ev.dtstart.setTime(cal.getTimeInMillis());
				cal.setTimeInMillis(ev.dtend.getTime());
				cal.add(GregorianCalendar.WEEK_OF_YEAR, weekOffset);
				ev.dtend.setTime(cal.getTimeInMillis());
			}

			//add teacher to subject
			String title=ev.subject + " (" + ev.teacher + ")";
			
			Log.d("Processing event " + title + " " + ev.dtstart + " "
					+ ev.dtstart.getTime());

			CalendarEvent calEv = CalendarHelper.getEvent(ctx, calendarID, ev.dtstart.getTime());
			
			if (null == calEv) {
				calEv = new CalendarEvent(calendarID, "", title,ev.dtstart.getTime(), ev.dtend.getTime(),false);
				calEv.setDescription("Tilføjet " + new Date());
				CalendarHelper.addEvent(ctx, calEv);
			} else {
				calEv.title = title;
				calEv.setDescription("Opdateret " + new Date());
				CalendarHelper.updateEvent(ctx, calEv);
			}
		}
	}
}
