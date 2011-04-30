package com.quui.tm2.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Preferences {
	public static boolean edgeLabels=false;
    interface Key {
        public String getKey();
    }

    public enum Default implements Key {
    	PROJECT("project"),
        GOLD("default_gold"),
        NAME("default_name"),
        CORPUS("default_corpus"),
        ROOT("default_root"),
        RESULT("default_result");
        private String key;

        public String getKey() {
            return key;
        }
        private Default(String key) {
            this.key = key;
        }
    }

    public enum Environment implements Key{
        DOT_HOME("dot_home"), DOT_FORMAT("dot_format"), SOURCES("sources");
        private String key;

        public String getKey() {
            return key;
        }
        private Environment(String key) {
            this.key = key;
        }
    }

    private static volatile Properties properties;
    static {
        InputStream stream;
        try {
            stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("tm2.properties");
            if (stream == null) {
                throw new IllegalStateException(
                        "Please copy the tm2.properties.template file to tm2.properties and set your local settings");
            }
            properties = new Properties();
            properties.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static String get(Key key) {
		String property = "";
		if (key.equals(Default.PROJECT) || key.equals(Environment.DOT_HOME)
				|| key.equals(Environment.DOT_FORMAT)
				|| key.equals(Default.NAME)) {
			property = properties.getProperty(key.getKey());
		} else {
			property = properties.getProperty(Default.PROJECT.getKey()) + "/"
					+ properties.getProperty(key.getKey());
		}
		return property;
	}
}
