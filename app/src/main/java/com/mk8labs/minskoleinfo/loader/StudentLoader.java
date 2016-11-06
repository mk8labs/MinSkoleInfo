package com.mk8labs.minskoleinfo.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;

import com.mk8labs.minskoleinfo.PrefsMgr;

public class StudentLoader {

	private static String MAINPAGE = "/Index";

	protected static boolean load(final Context ctx, EventNotifier notif) {

		InputStream resp = HttpConnection.get(MAINPAGE, notif, true);

		if (null == resp) {
			return false;
		} else {

			HashMap<String, String> list = StudentParser.parse(resp);

			if (null == list) {
				return false;
			}

			for (Entry<String, String> e : list.entrySet()) {
				PrefsMgr.setStudentName(ctx, e.getKey(), e.getValue());
			}
			
			//TODO: remove students not in list

			return true;
		}
	}
}
