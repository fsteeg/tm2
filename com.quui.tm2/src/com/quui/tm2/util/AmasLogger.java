package com.quui.tm2.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.quui.qlog.core.log4j.Appender;

public class AmasLogger {
	private static Logger instance = null;

	public static Logger singleton(Class<?> clazz) {
		if (instance == null) {
			Logger logger = Logger.getLogger(clazz);
//			logger.addAppender(new Appender());
//			logger.addAppender(new ConsoleAppender(new SimpleLayout()));
			instance = logger;
		}
		return instance;
	}

	public static Logger singleton() {
		return singleton(AmasLogger.class);
	}
}
