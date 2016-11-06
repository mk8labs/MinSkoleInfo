package com.mk8labs.minskoleinfo.loader;

public class TaskProgress {

	public enum ProgressType {
		INTEGER, STRING
	};

	public ProgressType type;
	public Object value = null;

	public TaskProgress(ProgressType t, Object v) {
		type = t;
		value = v;
	}
}
