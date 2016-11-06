package com.mk8labs.minskoleinfo.loader;

import java.io.InputStream;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StudentParser {

	public static HashMap<String, String> parse(InputStream res) {


		HashMap<String, String> result = new HashMap<String, String>();

		Document doc;
		try {
			doc = Jsoup.parse(res, "UTF8", "");
//			 doc = Jsoup.parse(RES,"");
			res.close();
		} catch (Exception e) {
			return null;
		}

		// get current student
		Element elem = doc.select("a[id=sk-personal-menu-button]").first();
		if (null != elem) {
			String name = elem.attr("title");
//			elem = doc.select("a[class=sk-l-active-menu-item]").first();
			elem = doc.select("div#sk-personal-menu-container div.sk-personal-menu-item-settings").first().parents().first();
			if (null != elem) {
				String path = elem.attr("href");
				if (null != path) {
					// /parent/1234/Hanne/Index
					String[] locs = path.split("/");
					result.put(locs[2], name);
				}
			}
		}

		// get students from selection list
		elem = doc.select("div[id=sk-personal-menu-container]").first();
		if (null != elem) {

			Elements elems = elem.select("a[href^=/parent/]");

			for (Element e : elems) {
				String href = e.attr("href");
				String name = e.attr("title");
				String[] n = href.split("/");
				if (!result.containsKey(n[2])) {
				  result.put(n[2], name);
				}
			}
		}
		return result;

	}

}