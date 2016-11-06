package com.mk8labs.minskoleinfo.loader;

public abstract class EventNotifier {
	public void onCompletion(Boolean status) {
	};

	public void onWorkloadKnown(int items) {
	};

	public void onWorkloadProcessed(int items) {
	};

	public void onWorkInfo(String msg) {
	};
}
