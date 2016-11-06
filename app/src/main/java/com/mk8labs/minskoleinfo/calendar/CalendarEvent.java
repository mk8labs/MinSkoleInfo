package com.mk8labs.minskoleinfo.calendar;

import java.util.GregorianCalendar;

import android.text.format.Time;

public class CalendarEvent {

	public static final String CALENTRYTAG = "MINSKOLEINFO";

	public String calendarID;
	public String eventID;
	public String title;
	public long dtstart;
	public long dtend;
	public int allDay=0;
	
	public String timezone;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = CALENTRYTAG + "\n" + description;
	}
	
	public CalendarEvent(String calid, String evtid, String title,
			long dtstart, long dtend,boolean allDay) {
		init(calid, evtid, title, dtstart, dtend, allDay);
	}
	
	private void init(String calid, String evtid, String title,
			long dtstart, long dtend,boolean allDay) {
		this.calendarID = calid;
		this.eventID = evtid;
		this.title = title;
		this.dtstart = dtstart;
		this.dtend = dtend;
		this.allDay=allDay?1:0;
		this.timezone=allDay?Time.TIMEZONE_UTC:"Europe/Copenhagen";
	}
	

}
