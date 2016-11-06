package com.mk8labs.minskoleinfo.loader;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mk8labs.minskoleinfo.Log;
import com.mk8labs.minskoleinfo.PrefsMgr;

public class SkemaParser {

	private ArrayList<String> weekIds = null;
	private boolean scheduleIsDateBased=false;
	protected ArrayList<String> getWeeksIds() {
		return weekIds;
	}

	@SuppressLint("SimpleDateFormat")
	public ArrayList<SkemaEvent> parse(Context ctx, InputStream res) {

		Document doc;
		try {
			doc = Jsoup.parse(res, "UTF8", "");
		} catch (Exception e) {
			return null;
		}

		// parse the week selection menu
		parseWeekList(doc);

		// parse the schedule values
		return parseSkema(ctx, doc);

	}

	public boolean isScheduleDateBased() {
		return scheduleIsDateBased;
	}
	
	@SuppressLint("SimpleDateFormat")
	private ArrayList<SkemaEvent> parseSkema(Context ctx, Document doc) {

		ArrayList<SkemaEvent> result = new ArrayList<SkemaEvent>();

		Elements elems = doc
				.select("ul[class=h-hlist sk-schedule-table-container] > li > table");
		
		Log.v(elems.toString());
		if (null != elems) {
			for (Element elem : elems) {
				parseDayTable(ctx, elem,result);
			}
		}
		return result;
	}

	private void parseDayTable(Context ctx, Element table,
			ArrayList<SkemaEvent> result) {

		Date baseDate = parseDayHeader(ctx, table);
		parseDayEntries(ctx, table, baseDate, result);
	}

	final List<String> MAANEDER = Arrays.asList("jan", "feb", "mar", "apr", "maj",
			"jun", "jul", "aug", "sep", "okt", "nov", "dec");

	final List<String> DAGE = Arrays.asList("mandag", "tirsdag", "onsdag", "torsdag", "fredag");
	
	private Date parseDayHeader(Context ctx, Element table) {

		Elements elems = table.select("span.sk-table-header-extra-info");

		Date date = null;

		if (null != elems && elems.size()>0) { //this is when schedule is date tagged
			try {
				scheduleIsDateBased=true;
				
				Pattern timePattern = Pattern
						.compile("(.*?)([0-9]+)(.*?)([a-z,A-Z]+)(.*?)([0-9]+)(.*)");

				String dateString = elems.first().text().trim();
				Matcher timeMatcher = timePattern.matcher(dateString);

				if (timeMatcher.matches()) {
					GregorianCalendar cal = new GregorianCalendar();
					cal.clear();
					String day = timeMatcher.group(2);
					String month = timeMatcher.group(4);
					String year = timeMatcher.group(6);

					cal.set(GregorianCalendar.DAY_OF_MONTH,
							Integer.parseInt(day));
					cal.set(GregorianCalendar.MONTH, MAANEDER.indexOf(month));
					cal.set(GregorianCalendar.YEAR, Integer.parseInt(year));

					date = new Date(cal.getTimeInMillis());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { //general schedule - no date specifics
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(GregorianCalendar.HOUR_OF_DAY,0);
			cal.set(GregorianCalendar.MINUTE,0);
			cal.set(GregorianCalendar.SECOND,0);
			cal.set(GregorianCalendar.MILLISECOND, 0);
			
			cal.add(GregorianCalendar.DAY_OF_WEEK,- (cal.get(GregorianCalendar.DAY_OF_WEEK)-GregorianCalendar.MONDAY));
			
			elems = table.select("span");
			Element elem=elems.get(1);
			String day=elem.text();
			cal.add(GregorianCalendar.DAY_OF_MONTH, DAGE.indexOf(day));
			
			date = new Date(cal.getTimeInMillis());
			
		}

		return date;

	}

	private void parseDayEntries(Context ctx, Element table, Date baseDate,
			ArrayList<SkemaEvent> result) {

		Elements rows = table.select("tbody > tr.sk-table-content");

		for (Element entry : rows) {
			parseDayEntry(ctx, entry, baseDate, result);
		}
	}

	private void parseDayEntry(Context ctx, Element entry, Date baseDate,
			ArrayList<SkemaEvent> result) {

		/*
		 * <tr class="sk-table-content"> <td
		 * class="sk-schedule-table-bell-times"> 09:50-10:35 </td>
		 * 
		 * <td colspan="1"> <span> RB MUS MUS </span>
		 * 
		 * </td>
		 * 
		 * <td class="sk-schedule-table-substitution">TDH</td> </tr>
		 */
		SkemaEvent evt = new SkemaEvent();
		Elements elems = entry.select("td");
		if (elems.size() >= 2) {
			Date[] time = parseTime(elems.get(0), baseDate);
			evt.dtstart = time[0];
			evt.dtend = time[1];

			if (parseEvent(ctx, elems, evt)) {
				result.add(evt);
			}
		}
	}

	private boolean parseEvent(Context ctx, Elements entry, SkemaEvent evt) {

		Element elem = entry.get(1).select("span").first();
		String[] info = null;
		if (null!=elem)
		{
			info = elem.text().trim().split(" ");
		} else {
			return false;
		}

		if (info.length < 2)
			return false;

		String teaShort=info[0];
		String teaDetailed = PrefsMgr.getTeacherDescription(ctx, teaShort);
		if (null == teaDetailed) {
			PrefsMgr.setTeacherDescription(ctx, teaShort, teaShort);
			teaDetailed = teaShort;
		}
		evt.teacher = teaDetailed;

		// xlate class
		String classShort=info[1];
		String classDetailed = PrefsMgr.getClassDescription(ctx, classShort);
		if (null == classDetailed) {
			PrefsMgr.setClassDescription(ctx, classShort, classShort);
			evt.subject = classShort;
		} else {
			evt.subject = classDetailed;
		}
		
		elem = entry.select("td.sk-schedule-table-substitution").first();
		if (null != elem) {
			String sub = elem.text();
			String subDetailed = PrefsMgr.getTeacherDescription(ctx, sub);
			if (null == subDetailed) {
				PrefsMgr.setTeacherDescription(ctx, sub, sub);
				subDetailed = sub;
			}
			evt.teacher = subDetailed + " (" + teaDetailed + ")";
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	private Date[] parseTime(Element elem, Date baseDate) {

		String timePeriod = elem.text();

		if (timePeriod.length()==1) {
			switch (Integer.parseInt(timePeriod)) {
			
			case 1 : 
				timePeriod="8:00-8:45";
				break;
			case 2 : 
				timePeriod="8:45-9:30";
				break;
			case 3 : 
				timePeriod="9:50-10:35";
				break;
			case 4 : 
				timePeriod="10:35-11:20";
				break;
			case 5 : 
				timePeriod="12:00-12:45 ";
				break;
			case 6 : 
				timePeriod="12:45-13:30 ";
				break;
			case 7 : 
				timePeriod="13:40-14:25 ";
				break;
			case 8 : 
				timePeriod="14:25-15:10 ";
				break;
			}
		}
		
		
		Pattern timePattern = Pattern
				.compile("(.*?)([0-9]+:[0-9]+)(.*?)([0-9]+:[0-9]+)(.*)");
		SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm");
		timeParser.setTimeZone(TimeZone.getTimeZone("GMT"));

		// calc the time and date

		try {
			Date[] res = new Date[2];

			Matcher timeMatcher = timePattern.matcher(timePeriod);
			timeMatcher.matches();

			String t = timeMatcher.group(2);
			res[0] = new Date(timeParser.parse(t).getTime()
					+ baseDate.getTime());

			t = timeMatcher.group(4);
			res[1] = new Date(timeParser.parse(t).getTime()
					+ baseDate.getTime());
			return res;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void parseWeekList(Document doc) {

		Elements elems = doc
				.select("select#sk-schedule-scheme-period-filter > option");
		if (null != elems) {
			weekIds = new ArrayList<String>();
			for (Element e : elems) {
				weekIds.add(e.attr("value"));
			}

		}

	}

}