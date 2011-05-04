package com.quui.tm2.util;

import org.apache.log4j.Logger;

public class TM2Logger {
	private static Logger instance = null;

	public static Logger singleton(Class<?> clazz) {
		if (instance == null) {
			Logger logger = Logger.getLogger(clazz);
			instance = logger;
		}
		return instance;
	}

	public static Logger singleton() {
		return singleton(TM2Logger.class);
	}
}
