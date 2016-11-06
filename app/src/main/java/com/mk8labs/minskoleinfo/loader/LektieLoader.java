package com.mk8labs.minskoleinfo.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.mk8labs.minskoleinfo.PrefsMgr;
import com.mk8labs.minskoleinfo.calendar.CalendarEvent;
import com.mk8labs.minskoleinfo.calendar.CalendarHelper;

import android.content.Context;

public class LektieLoader {

	private Context ctx;
	private EventNotifier ev;
	
	public LektieLoader(Context ctx,EventNotifier ev) {
		this.ctx=ctx;
		this.ev=ev;
	}
	
	
	public boolean load() {
		// get students
		Set<String> studentlist = PrefsMgr.getStudentList(ctx);

		// update schedule for each student
		for (String sid : studentlist) {
			
			String calendarID = PrefsMgr.getDiaryCalendarID(ctx, sid);
			if (calendarID.equals(PrefsMgr.NOSYNC))
				continue;
		
			ev.onWorkInfo("Indlæser lektier for " + PrefsMgr.getStudentName(ctx, sid));
			HttpConnection.setStudentContext(sid);
			
			String urlList=getDiaryURL(ctx,sid);
			if (loadList(calendarID,urlList)) {
				ev.onWorkInfo("Lektier er opdateret");
			} else {
				ev.onWorkInfo("Fejl ved indlæsning af lektier");
			}
		}
		return true;
	}

	private boolean loadList(String calendarID, String url) {
		InputStream lektier=HttpConnection.getAbsolute(url, ev);
		
		if (null!=lektier) {
			HashMap<Date, String> list = LektieParser.parseDiary(lektier);
			Set<Entry<Date, String>> list1 = list.entrySet();
		
			for (Entry<Date, String> entry : list1) {
				
				Date dtstart=entry.getKey();
				String txt=entry.getValue();
				String subject="Lektie\n" + txt;
				
				CalendarEvent calEv=CalendarHelper.getEvent(ctx, calendarID, dtstart.getTime());
				if (null == calEv) {
					calEv = new CalendarEvent(calendarID, "", subject,dtstart.getTime(), dtstart.getTime(),true);
					calEv.setDescription("Tilføjet " + new Date());
					CalendarHelper.addEvent(ctx, calEv);
				} else {
					calEv.title = subject;
					calEv.setDescription("Tilføjet " + new Date());
					CalendarHelper.updateEvent(ctx, calEv);
				}
			}
			
		} else {
		
			return false;
		}		
		return true;
	}
	
	private String getDiaryURL(Context ctx,String sid) {

		List<String> lst=new ArrayList<String>();
		
		String name=PrefsMgr.getStudentName(ctx, sid);
		name=name.split(" ")[0].toLowerCase();
		String listpage="/parent/" + sid + "/"+ name + "item/weeklyplansandhomework/diary";
		InputStream resp=HttpConnection.getAbsolute(listpage, ev);
		if (null!=resp) {
			lst.addAll(LektieParser.getDiaries(resp));
		}
		String w=lst.get(0);
		w=w.replace("/diary/", "/diary/notes/");
		return w;
	}
}
