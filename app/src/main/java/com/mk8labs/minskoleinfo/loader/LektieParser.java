package com.mk8labs.minskoleinfo.loader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.format.Time;

public class LektieParser {

	private static final String LISTPATH="li.ccl-rwgm-column-1-2 > a[href]";
	private static final String DIARYENTRYROOT="div.sk-white-box";
	
	public static List<String> getDiaries(InputStream page) {
		List<String> res=null;
		try {
			Document doc=Jsoup.parse(page,"UTF8","");			
			Elements elems=doc.select(LISTPATH);
			res=new ArrayList<String>();
			for (Element e : elems) {
				res.add(e.attr("href"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static HashMap<Date,String> parseDiary(InputStream res) {

		HashMap<Date,String> result=new HashMap<Date,String>();

		Document doc;
		try {
			 doc = Jsoup.parse(res,"UTF8","");
		} catch (Exception e) {
			return null;
		}
		Elements elems = doc.select(DIARYENTRYROOT);
		
		for (Element elem : elems) {
			
			Element dateElement=elem.select("b").first();
			Date date=parseDate(dateElement);
			
			String txt;
			Elements tableRows=elem.select("tr");			
			if (tableRows.isEmpty()) {
				txt=parseSingleEntry(elem);
			} else {
				txt=parseTable(tableRows);
			}
			result.put(date, txt);
		}
		return result;
	}

	private static Date parseDate(Element dateElement) {
		final List<String> MAANEDER = Arrays.asList("jan", "feb", "mar", "apr", "maj",
				"jun", "jul", "aug", "sep", "okt", "nov", "dec");

//		Tirsdag, 20. okt. 2015:
		Date date=null;
		Pattern timePattern = Pattern
				.compile("(.*?)([0-9]+)(.*?)([a-z,A-Z]+)(.*?)([0-9]+)(.*)");

		String dateString = dateElement.text().trim();
		Matcher timeMatcher = timePattern.matcher(dateString);

		if (timeMatcher.matches()) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.clear();
			cal.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
			String day = timeMatcher.group(2);
			String month = timeMatcher.group(4);
			String year = timeMatcher.group(6);

			cal.set(GregorianCalendar.DAY_OF_MONTH,
					Integer.parseInt(day));
			cal.set(GregorianCalendar.MONTH, MAANEDER.indexOf(month));
			cal.set(GregorianCalendar.YEAR, Integer.parseInt(year));
			//TODO: EVAL IF NOT REQ cal.set(GregorianCalendar.HOUR_OF_DAY,0); 
				
			date = new Date(cal.getTimeInMillis());
		}
		return date;
	}

	private static String parseSingleEntry(Element elem) {

		Elements elems=elem.select("div.sk-user-input > div");
		String res="";
		
		for (Element e : elems) {
			String txt=e.text().replace((char)160,' ').trim();
			if (!txt.isEmpty()) {
				res+=txt+ "\n";
			}
		}
		return  res;
	}

	private static String parseTable(Elements tableRows) {

		String res="";
		tableRows.remove(0); //remove header row
		
		for (Element e : tableRows ) {
			Elements cols=e.select("td");
			String subject=cols.get(0).text();
			String task=cols.get(1).text().replace((char)160, ' ').trim();
			
			if (null!=task && !task.trim().isEmpty()) {
				res+=subject + " : " + task + "\n";
			}
		}		
		return res;
	}
}
