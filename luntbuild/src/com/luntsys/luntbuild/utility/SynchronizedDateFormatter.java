package com.luntsys.luntbuild.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A synchronized wrapper around SimpleDateFormat.
 */
public class SynchronizedDateFormatter {
	
    private static HashMap formats = new HashMap();
    static {
    	formats.put("yyyy.MM.dd-hh:mm", new SynchronizedDateFormatter("yyyy.MM.dd-hh:mm"));
    	formats.put("yyyy-MM-dd hh:mm:ss zzz", new SynchronizedDateFormatter("yyyy-MM-dd hh:mm:ss zzz"));
    	formats.put("yyyy-MM-dd'T'HH:mm:ssZ", new SynchronizedDateFormatter("yyyy-MM-dd'T'HH:mm:ssZ"));
    	formats.put("dd-MMMM-yyyy.HH:mm:ss", new SynchronizedDateFormatter("dd-MMMM-yyyy.HH:mm:ss"));
    	formats.put("yyyy/MM/dd HH:mm:ss", new SynchronizedDateFormatter("yyyy/MM/dd HH:mm:ss"));
    	formats.put("yyyy-MM-dd HH:mm:ss", new SynchronizedDateFormatter("yyyy-MM-dd HH:mm:ss"));
    	formats.put("yyyy-MM-dd'T'HH:mm:ss'Z'", new SynchronizedDateFormatter("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    	formats.put("yyyy-MM-dd'T'HH:mm:ss.SSS", new SynchronizedDateFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    	formats.put("M/dd/yy;h:mm:ssa", new SynchronizedDateFormatter("M/dd/yy;h:mm:ssa"));
    	formats.put("yyyy-MM-dd HH:mm", new SynchronizedDateFormatter("yyyy-MM-dd HH:mm"));
    	
    	formats.put("MM/dd/yyyy", new SynchronizedDateFormatter("MM/dd/yyyy"));
        formats.put("MM-dd-yyyy", new SynchronizedDateFormatter("MM-dd-yyyy"));
        formats.put("d MMM yyyy", new SynchronizedDateFormatter("d MMM yyyy"));
        formats.put("d-MMM-yyyy", new SynchronizedDateFormatter("d-MMM-yyyy"));
        formats.put("d MMM. yyyy", new SynchronizedDateFormatter("d MMM. yyyy"));
        formats.put("MMM d, yyyy", new SynchronizedDateFormatter("MMM d, yyyy"));
        formats.put("MMM. d, yyyy", new SynchronizedDateFormatter("MMM. d, yyyy"));
        
        formats.put("hh:mm", new SynchronizedDateFormatter("hh:mm"));
        
        formats.put("default", new SynchronizedDateFormatter(DateFormat.getDateInstance(SimpleDateFormat.DEFAULT)));
        formats.put("short", new SynchronizedDateFormatter(DateFormat.getDateInstance(SimpleDateFormat.SHORT)));
        formats.put("medium", new SynchronizedDateFormatter(DateFormat.getDateInstance(SimpleDateFormat.MEDIUM)));
        formats.put("long", new SynchronizedDateFormatter(DateFormat.getDateInstance(SimpleDateFormat.LONG)));
        formats.put("full", new SynchronizedDateFormatter(DateFormat.getDateInstance(SimpleDateFormat.FULL)));
    }

	private ThreadLocal delegate;
    
	/**
	 * Instantiates a new synchronized date formatter.
	 * 
	 * @param format the format
	 */
	public SynchronizedDateFormatter(final DateFormat format) {
        delegate = new ThreadLocal() {
            protected synchronized Object initialValue() {
                return format;
            }
        };
	}
	
    /**
     * Instantiates a new synchronized date formatter.
     * 
     * @param formatStr the format str
     */
    public SynchronizedDateFormatter(final String formatStr) {
        delegate = new ThreadLocal() {
            protected synchronized Object initialValue() {
                return new SimpleDateFormat(formatStr);
            }
        };
    }
    
    /**
     * Format the date.
     * 
     * @param date the date
     * 
     * @return the string
     */
    public synchronized String format(Date date) {
        return ((SimpleDateFormat)delegate.get()).format(date);
    }
    
    /**
     * Gets the simple date format.
     * 
     * @return the simple date format
     */
    public synchronized SimpleDateFormat get() {
    	return (SimpleDateFormat)delegate.get();
    }
    
    /**
     * Parses the date.
     * 
     * @param dateStr the date str
     * 
     * @return the date
     * 
     * @throws ParseException the parse exception
     */
    public synchronized Date parse(String dateStr) throws ParseException {
        return ((SimpleDateFormat)delegate.get()).parse(dateStr);
    }
    
    /**
     * Gets the format.
     * 
     * @param formatStr the format str
     * 
     * @return the format
     */
    public static synchronized SimpleDateFormat getFormat(String formatStr) {
		SynchronizedDateFormatter formatter = (SynchronizedDateFormatter)formats.get(formatStr);
		if (formatter == null)
			formatter = (SynchronizedDateFormatter)formats.get("medium");
		return formatter.get();
    }
    
    /**
     * Format date.
     * 
     * @param date the date
     * 
     * @return the string
     */
    public static final String formatDate(Date date) {
    	return formatDate(date, null);
    }
    
    /**
     * Format date.
     * 
     * @param date the date
     * @param defaultFormat the default format
     * 
     * @return the string
     */
    public static synchronized final String formatDate(Date date, String defaultFormat) {
    	if (defaultFormat != null) {
			SynchronizedDateFormatter formatter = (SynchronizedDateFormatter)formats.get(defaultFormat);
			try {
				String dateStr = formatter.format(date);
				if (dateStr != null && dateStr.trim().length() > 0) return dateStr;
			} catch (Exception e) {
		           // try again with another formats
			}
    	}
    	for (Iterator it = formats.keySet().iterator(); it.hasNext();) {
			SynchronizedDateFormatter formatter = (SynchronizedDateFormatter)formats.get(it.next());
			try {
				String dateStr = formatter.format(date);
				if (dateStr != null && dateStr.trim().length() > 0) return dateStr;
			} catch (Exception e) {
		           // try again with another format
			}
		}
    	return null;
    }
    
    /**
     * Parses the date.
     * 
     * @param dateStr the date str
     * 
     * @return the date
     */
    public static final Date parseDate(String dateStr) {
    	return parseDate(dateStr, null);
    }
    
    /**
     * Parses the date.
     * 
     * @param dateStr the date str
     * @param defaultFormat the default format
     * 
     * @return the date
     */
    public static synchronized final Date parseDate(String dateStr, String defaultFormat) {
    	if (defaultFormat != null) {
			SynchronizedDateFormatter formatter = (SynchronizedDateFormatter)formats.get(defaultFormat);
			try {
				Date date = formatter.parse(dateStr);
				if (date != null) return date;
			} catch (Exception e) {
		           // try again with another format
			}
    	}
    	for (Iterator it = formats.keySet().iterator(); it.hasNext();) {
			SynchronizedDateFormatter formatter = (SynchronizedDateFormatter)formats.get(it.next());
			try {
				Date date = formatter.parse(dateStr);
				if (date != null) return date;
			} catch (Exception e) {
		           // try again with another format
			}
		}
    	return null;
    }
}