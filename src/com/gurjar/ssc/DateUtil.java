package com.gurjar.ssc;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	static String month[] = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL",
			"AUG", "SEP", "OCT", "NOV", "DEC" };

	@SuppressWarnings("deprecation")
	private static Date getDate(String str) {
		int month = getMonthInt(str);
		int day = getDay(str);
		int year = getYear(str);
		return new Date(year - 1900, month, day);
	}

	private static Integer getYear(String str) {
		if (str.contains("2014")) {
			return 2014;

		} else if (str.contains("2015")) {
			return 2015;
		} else if (str.contains("2012")) {
			return 2012;
		} else if (str.contains("2011")) {
			return 2011;
		} else if (str.contains("2010")) {
			return 2010;
		} else if (str.contains("2009")) {
			return 2009;
		} else if (str.contains("2008")) {
			return 2008;
		} else if (str.contains("2007")) {
			return 2007;
		} else if (str.contains("2006")) {
			return 2006;
		} else if (str.contains("2013")) {
			return 2013;
		} else {
			return null;
		}
	}

	private static Integer getDay(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= '0' && c <= '9') {
				int ret = c - '0';
				if (i + 1 < str.length() && str.charAt(i + 1) >= '0'
						&& str.charAt(i + 1) <= '9') {
					ret = ret * 10 + str.charAt(i + 1) - '0';
				}
				if (ret > 0 && ret < 30) {
					return ret;
				}
			}
		}
		return null;
	}
	

	private static Integer getMonthInt(String str) {
		if (str.toLowerCase().startsWith("Declared on".toLowerCase())) {
			str = str.substring("Declared on".length(), str.length());
		}
		Integer index = null;
		for (int i = 0; i < month.length; i++) {
			if (str.toLowerCase().contains(month[i].toLowerCase())) {
				if (index == null) {
					index = i;
				} else {
					return null;
				}
			}
		}
		return index;
	}

	static String getMonth(String str) {
		if (str.toLowerCase().startsWith("Declared on".toLowerCase())) {
			str = str.substring("Declared on".length(), str.length());
		}
		Integer index = null;
		for (int i = 0; i < month.length; i++) {
			if (str.toLowerCase().contains(month[i].toLowerCase())) {
				if (index == null) {
					index = i;
				} else {
					return null;
				}
			}
		}
		if(index==null ||(index<0 || index>=month.length)){
			SimpleDateFormat sss = new SimpleDateFormat("MMM");
			sss.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
			System.out.println("index "+index+" for"+str+" returning current month");
			Date d=new Date();
			String month=sss.format(d);
			return month;
		}else{
		return month[index];
		}
	}

}
