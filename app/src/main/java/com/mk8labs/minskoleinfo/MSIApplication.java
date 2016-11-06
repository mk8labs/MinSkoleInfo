package com.mk8labs.minskoleinfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import android.app.Application;

public class MSIApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		final File dir=getExternalFilesDir(null);
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				try {
					File file = new File(dir,	"MSICrash.txt");
					OutputStream os = new FileOutputStream(file, true);

					String msg=new Date() + "Crash - thread:" + thread.getId() + " - Thread name:"
							+ thread.getName() +"::"+ ex.getClass() +"\n" + ex.getMessage() +  "\n";
					
					msg+=dmpStacktrace(ex);
					msg+="\n\n";

					msg+="Cause:\n";
					Throwable cause=ex.getCause();
					msg+=dmpStacktrace(cause);
					msg+="\n-----OOOOO----\n\n";

					os.write(msg.getBytes());
					os.close();

				} catch (Exception e) {

				}

			}
		});
	}

	private String dmpStacktrace(Throwable ex) {
		StackTraceElement[] elems=ex.getStackTrace();
		String msg="";
		for (int i=0;i<elems.length;i++) {
			msg+=elems[i].getClassName() + "::" + elems[i].getMethodName() + "::" + elems[i].getLineNumber() + "\n";
		}
		return msg;
	}
	
}
