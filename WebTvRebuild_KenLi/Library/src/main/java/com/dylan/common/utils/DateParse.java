package com.dylan.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateParse {

	public static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	public static String getDateDif(String oldDate, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		Date now = new Date(System.currentTimeMillis());
		Date date;
		try {
			date = dateFormat.parse(oldDate);
			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			String result = "";
			result += day >= 1 ? (((int) Math.floor(day)) + "天 ") : "";
			result += hour >= 1 ? (((int) Math.floor(hour)) + "小时 ") : "";
			result += min >= 1 ? (((int) Math.floor(min)) + "分 ") : "";
			result += s >= 1 ? (((int) Math.floor(s)) + "秒") : "";
			return result;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String getHourDif(String oldDate, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		Date now = new Date(System.currentTimeMillis());
		Date date;
		try {
			date = dateFormat.parse(oldDate);
			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			String result = "";
			result += String.format("%02d", (int) Math.floor(hour)) + ":";
			result += String.format("%02d", (int) Math.floor(min)) + ":";
			result += String.format("%02d", (int) Math.floor(s));
			return result;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String getNowDate(String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		Date now = new Date(System.currentTimeMillis());
		return dateFormat.format(now);
	}

	public static String getDate(int dayInc, int hourInc, int minInc, int secondInc, String inputDateStr, String inputFormatStr, String outputFormatStr) {
		SimpleDateFormat inputFormat;
		SimpleDateFormat outputFormat;
		if (inputFormatStr != null) {
			inputFormat = new SimpleDateFormat(inputFormatStr, Locale.getDefault());
		} else {
			inputFormat = mDateFormat;
		}
		if (outputFormatStr != null) {
			outputFormat = new SimpleDateFormat(outputFormatStr, Locale.getDefault());
		} else {
			outputFormat = mDateFormat;
		}
		Date now = new Date(System.currentTimeMillis());
		if (inputDateStr != null) {
			try {
				now = inputFormat.parse(inputDateStr);
			} catch (ParseException e) {
				return null;
			}
		}
		Date result = new Date(now.getTime() + secondInc * 1000 + minInc * 60 * 1000 + hourInc * 60 * 60 * 1000 + dayInc * 60 * 60 * 24 * 1000);
		return outputFormat.format(result);
	}

	public static Date parseDate(String dateStr, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		try {
			Date date = dateFormat.parse(dateStr);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String individualTime(String dateStr, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		try {
			Date date = dateFormat.parse(dateStr);
			Date now = new Date(System.currentTimeMillis());
			long dif = now.getTime() - date.getTime();
			if (dif <= (5 * 60 * 1000)) {
				return "刚刚";
			} else if (dif > (5 * 60 * 1000) && dif <= (1 * 60 * 60 * 24 * 1000)) {
				return "今天 " + getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "HH:mm");
			} else if (dif > (1 * 60 * 60 * 24 * 1000) && dif <= (2 * 60 * 60 * 24 * 1000)) {
				return "昨天 " + getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "HH:mm");
			} else if (dif > (2 * 60 * 60 * 24 * 1000)) {
				return getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "yyyy-MM-dd HH:mm");
			}
			return null;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String individualTime2(String dateStr, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		try {
			Date date = dateFormat.parse(dateStr);
			Date now = new Date(System.currentTimeMillis());
			long dif = now.getTime() - date.getTime();
			if (dif <= (5 * 60 * 1000)) {
				return "刚刚";
			} else if (dif > (5 * 60 * 1000) && dif <= (1 * 60 * 60 * 24 * 1000)) {
				return "今天		" + getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "HH:mm");
			} else if (dif > (1 * 60 * 60 * 24 * 1000) && dif <= (2 * 60 * 60 * 24 * 1000)) {
				return "昨天		" + getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "HH:mm");
			} else if (dif > (2 * 60 * 60 * 24 * 1000)) {
				return getDate(0, 0, 0, 0, dateStr, dateFormat.toLocalizedPattern(), "MM-dd		HH:mm");
			}
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	public static int getWeekToday() {
		return getWeek(mDateFormat.format(new Date(System.currentTimeMillis())), mDateFormat.toLocalizedPattern());
	}

	public static int getWeek(String dateStr, String formatStr) {
		SimpleDateFormat dateFormat;
		if (formatStr != null) {
			dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
		} else {
			dateFormat = mDateFormat;
		}
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(dateStr));
		} catch (ParseException e) {
			return 0;
		}
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			return 1;
		case Calendar.TUESDAY:
			return 2;
		case Calendar.WEDNESDAY:
			return 3;
		case Calendar.THURSDAY:
			return 4;
		case Calendar.FRIDAY:
			return 5;
		case Calendar.SATURDAY:
			return 6;
		case Calendar.SUNDAY:
			return 7;
		default:
			return 0;
		}
	}
}
