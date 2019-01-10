package com.skies.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * 日期工具类
 * 
 */
public class DateUtils {
	/**
	 * 给日期加年
	 * @param date 日期
	 * @param year 年数
	 * @return 加后日期
	 */
	public static Date addYears(Date date,int year){
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.YEAR, year);
		return calender.getTime();
	}

	/**
	 * 给日期加月
	 * @param date 日期
	 * @param month 月数
	 * @return 加后日期
	 */
	public static Date addMonths(Date date,int month){
          Calendar calender = Calendar.getInstance();
          calender.setTime(date);
          calender.add(Calendar.MONTH, month);
          return calender.getTime();
	}
	
	/**
	 * 给日期加小时
	 * @param date 日期
	 * @param hour 小时数
	 * @return 加后日期
	 */
	public static Date addHour(Date date,int hour){
          Calendar calender = Calendar.getInstance();
          calender.setTime(date);
          calender.add(Calendar.HOUR, hour);
          return calender.getTime();
	}
	
	/**
	 * 给日期加分钟
	 * @param date 日期
	 * @param minute 分钟数
	 * @return 加后日期
	 */
	public static Date addMinute(Date date,int minute){
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.MINUTE, minute);
		return calender.getTime();
	}

    /**
     * 根据当前时间获取相邻的五分钟
     * @param date date
     * @return date
     */
    public static Date getMinuteByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int min = calendar.get(Calendar.MINUTE);
        if(min%5!=0){
            calendar.setTime(addMinute(date,-(min%5)));
        }
        return calendar.getTime();
    }

	/**
	 * 根据当前时间获取相邻的十五分钟
	 * @param date date
	 * @return date
	 */
	public static Date get15MinuteByDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int min = calendar.get(Calendar.MINUTE);
		if(min%15!=0){
			calendar.setTime(addMinute(date,-(min%15)));
		}
		return calendar.getTime();
	}

	/**
	 * 根据当前时间获取相邻的三十分钟
	 * @param date date
	 * @return date
	 */
	public static Date get30MinuteByDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int min = calendar.get(Calendar.MINUTE);
		if(min%30!=0){
			calendar.setTime(addMinute(date,-(min%30)));
		}
		return calendar.getTime();
	}

	/**
	 * 获取日期所在月的天数
	 * @param date date
	 * @return 天数
	 */
	public static int getTotalDaysOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据日期判断所在年的天数
	 * @param year 年数
	 * @return 天数
	 */
	public static int getYearDays(int year)
	{
		if((year % 4 == 0 && year % 100 != 0) || year % 400 == 0){
			return 366;
		}
		return 365;
	}
	/**
	 * 获取日期所在天数(年)a
	 * 
	 * @param date
	 *            日期对象
	 * @return int 天数
	 */
	public static int getDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取日期所在天数(月)
	 * 
	 * @param date
	 *            日期对象
	 * @return int 天数
	 */
	public static int getDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期所在天数(周)
	 * 
	 * @param date
	 *            日期对象
	 * @return int 天数
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int week = calendar.get(Calendar.DAY_OF_WEEK);
		return week == 1 ? WEEK_END : week - 1;
	}

	/**
	 * 增加指定日期对象的天数
	 * 
	 * @param date
	 *            日期对象
	 * @param days
	 *            天数 可以为负数 这样的话就是减少天数
	 * @return java.util.Date 添加完天数后的日期对象
	 */
	public static Date addDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);

		return calendar.getTime();
	}

	/**
	 * 判断日期1是否早于日期2
	 * 
	 * @param d1
	 *            日期1
	 * @param d2
	 *            日期2
	 * @return boolean 比较结果
	 */
	public static boolean earlierThan(Date d1, Date d2) {
		return d1.getTime() < d2.getTime();
	}

	/**
	 * 测试日期1是否在日期2和日期3之间
	 * 
	 * @param d1
	 *            日期1
	 * @param d2
	 *            日期2
	 * @param d3
	 *            日期3
	 * @return boolean 比较结果
	 */
	public static boolean isBetween(Date d1, Date d2, Date d3) {
		return d1.getTime() > d2.getTime() && d1.getTime() < d3.getTime();
	}

	/**
	 * 判断两个日期是否在同一个月份中
	 * 
	 * @param d1
	 *            日期1
	 * @param d2
	 *            日期2
	 * @return boolean 比较结果
	 */
	public static boolean isSameMonth(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(d1);
		c2.setTime(d2);

		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
	}

	/**
	 * 判断两个日期是否在同一个天
	 * 
	 * @param d1
	 *            日期1
	 * @param d2
	 *            日期2
	 * @return boolean 比较结果
	 */
	public static boolean isSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(d1);
		c2.setTime(d2);

		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DATE) == c2.get(Calendar.DATE);
	}

	/**
	 * 测试日期1与日期2之间相差的天数
	 * 
	 * @param d1
	 *            日期1
	 * @param d2
	 *            日期2
	 * @return int 相差天数
	 */
	public static int between(Date d1, Date d2) {
		return daysBetween(d1,d2);
	}
	
	/**
	 * 计算两个日期间的天数
	 * @param d1 日期1
	 * @param d2 日期2
	 * @return 天数
	 */
	public static int daysBetween(Date d1, Date d2){
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		c2.setTime(d2);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		int count = 0;
		if (c1.after(c2)){
			Calendar tmp = c1;
			c1 = c2;
			c2 = tmp;
		}
		while (c1.before(c2)){
			c1.add(Calendar.DATE, 1);
			count ++;
		}
		return count;
	}
	
	/**
	 * 获取两个日期中间隔的月份集合
	 * 
	 * @param start
	 *            起始日期
	 * @param end
	 *            结束日期
	 * @return java.util.LinkedList&lt;String&gt; 月份集合 格式为yyyy-MM
	 */
	public static LinkedList<String> getBetweenMonths(Date start, Date end) {
		assert start.getTime() < end.getTime();

		LinkedList<String> result = new LinkedList<String>();
		Calendar index = Calendar.getInstance();

		index.setTime(start);
		index.setTime(DateFormatUnit.YEAR_AND_MONTH.getDateByStr(index.get(Calendar.YEAR) + "-" + (index.get(Calendar.MONTH) + 1)));

		while (true) {
			if (index.getTimeInMillis() > end.getTime()) {
				break;
			} else {
				result.add(DateFormatUnit.YEAR_AND_MONTH.getDateStr(index.getTime()));
				index.add(Calendar.MONTH, 1);
			}
		}
		return result;
	}
	
	/**
	 * 获取两个日期中间隔的周集合
	 * 
	 * @param start
	 *            起始日期
	 * @param end
	 *            结束日期
	 * @return java.util.LinkedList&lt;String&gt; 周集合 格式为yyyy-MM-dd,yyyy-MM-dd
	 */
	public static LinkedList<String> getBetweenWeeks(Date start, Date end) {
		assert start.getTime() < end.getTime();

		LinkedList<String> result = new LinkedList<String>();
		Date indexDate = start;
		while (true) {
			if (indexDate.getTime() > end.getTime()) {
				break;
			} else{
				String lastDay = getWkEndByDay(indexDate);
				if(DateFormatUnit.DATE.getDateByStr(lastDay).getTime() >= end.getTime()){
					result.add(DateFormatUnit.DATE.getDateStr(indexDate)+","+ DateFormatUnit.DATE.getDateStr(end));
				}else{
					result.add(DateFormatUnit.DATE.getDateStr(indexDate)+","+lastDay);
				}
				Date newDate = DateFormatUnit.DATE.getDateByStr(lastDay+" 00:00:00");
				indexDate = addDays(newDate, 1);
			}
		}
		return result;
	}

	/**
	 * 获取两个日期中间隔的日期集合
	 * 
	 * @param start
	 *            起始日期
	 * @param end
	 *            结束日期
	 * @return java.util.LinkedList&lt;String&gt; 日期集合 格式为yyyy-MM-dd
	 */
	public static LinkedList<String> getBetweenDays(Date start, Date end) {
		assert start.getTime() < end.getTime();
		LinkedList<String> result = new LinkedList<String>();
		Calendar index = Calendar.getInstance();

		index.setTime(start);
		index.setTime(DateFormatUnit.DATE.getDateByStr(concat(index.get(Calendar.YEAR), "-", (index.get(Calendar.MONTH) + 1), "-", index
				.get(Calendar.DAY_OF_MONTH))));

		while (true) {
			if (index.getTimeInMillis() > end.getTime()) {
				break;
			} else {
				result.add(DateFormatUnit.DATE.getDateStr(index.getTime()));
				index.add(Calendar.DATE, 1);
			}
		}

		return result;
	}

	/**
	 * 连接字符串
	 *
	 * @param strs
	 *            被连接的字符串
	 * @return java.lang.String 已经连接的字符串
	 */
	public static String concat(Object... strs) {
		assert strs != null;

		StringBuilder builder = new StringBuilder();

		for (Object str : strs) {
			builder.append(str == null ? "null" : str.toString());
		}

		return builder.toString();
	}

	/**
	 * 某一个月的最后一刻
	 *  @param currDate 当月时间
	 *  @return date
	 *
	 */
	public static Date getLastTimeOfMonth(Date currDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currDate);
		calendar.add(Calendar.MONTH, 0);

		// 当月最后一天
		calendar.add(Calendar.MONTH, 1); // 加一个月
		calendar.set(Calendar.DATE, 1); // 设置为该月第一天
		calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
		String dayLast = df.format(calendar.getTime());
		StringBuffer endStr = new StringBuffer().append(dayLast).append(" 23:59:59");
		dayLast = endStr.toString();
		return DateFormatUnit.DATE_TIME.getDateByStr(dayLast);
	}
	
	/**
	 * 某一个月第一天
	 *  @param currDate 当月时间
	 *  @return date
	 *
	 */
	public static Date getFiestTimeOfMonth(Date currDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currDate);
		calendar.add(Calendar.MONTH, 0);

		calendar.set(Calendar.DATE, 1); // 设置为该月第一天
		String dayLast = df.format(calendar.getTime());
		StringBuffer endStr = new StringBuffer().append(dayLast).append(" 00:00:00");
		dayLast = endStr.toString();
		return DateFormatUnit.DATE_TIME.getDateByStr(dayLast);
	}
	
	/**
	 * 获取周几
	 * @param date 日期
	 * @return int
	 */
	public static int getWkByDay(Date date) {
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 获取周末
	 * @param date 日期
	 * @return yyyy-MM-dd
	 */
	public static String getWkEndByDay(Date date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式 
		Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期  
        
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
        if(1 == dayWeek) {  
           cal.add(Calendar.DAY_OF_MONTH, -1);  
        }  
        
       cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一  
       
       int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
       cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
       System.out.println("所在周星期一的日期："+sdf.format(cal.getTime()));
       cal.add(Calendar.DATE, 6);
       System.out.println("所在周星期日的日期："+sdf.format(cal.getTime()));  
       return sdf.format(cal.getTime());
	}
	
	// 常量属性
	/** 日志对象 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

	/** 一周最后一天 */
	public static final int WEEK_END = 7;

	/** 一天单位(毫秒数) */
	public static final int A_DAY_OF_MILL = 1000 * 60 * 60 * 24;

	/**
	 *时间转换 枚举
	 * 
	 * @version 1.0
	 * @author zhaojp
	 */
	public enum DateFormatUnit {
		/** 枚举元素 */
		DAY("dd"),DATE("yyyy-MM-dd"), TIME("HH:mm:ss"),DATE_TIME("yyyy-MM-dd HH:mm:ss"), SHORT_DATE("yyyyMMdd"), SHORT_DATE_TIME("yyyyMMddHHmmss"), YEAR_AND_MONTH("yyyy-MM"), DATE_AND_HOUR(
				"yyyy-MM-dd HH"), HOUR_AND_MIN("HH:mm"), DAY_AND_MIN("yyyyMMddHHmm"),DATE_AND_MIN("yyyy-MM-dd HH:mm"), HOUR_AND_DAY("yyyyMMddHH"),YEAR("yyyy"),DATE_AND_MONTH("yyyyMM");

        /**
		 * 初始化转换格式
		 * 
		 * @param pattern
		 *            转换格式
		 */
		DateFormatUnit(String pattern) {
			this.pattern = pattern;
			//this.formatter = new SimpleDateFormat(pattern);
		}

		/**
		 * 获得一个新的格式化对象
		 * 
		 * @return java.text.SimpleDateFormat 日期格式化对象
		 */
		public SimpleDateFormat getNewFormatter() {
			return new SimpleDateFormat(pattern);
		}

		/**
		 * 将日期对象转换为字符串
		 * 
		 * @param date
		 *            时间对象
		 * @return java.lang.String 字符串对象
		 */
		public String getDateStr(Date date) {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(date);
		}

		/**
		 * 将毫秒转换为日期对象的字符串
		 * 
		 * @param mill
		 *            毫秒
		 * @return java.lang.String 字符串对象
		 */
		public String getDateStr(long mill) {
			return getDateStr(new Date(mill));
		}

		/**
		 * 将字符串转换为日期对象
		 * 
		 * @param dateStr
		 *            日期字符串
		 * @return java.util.Date 日期对象
		 */
		public Date getDateByStr(String dateStr) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(pattern);
				return formatter.parse(dateStr);
			} catch (ParseException e) {
				LOGGER.error("将字符串转换为日期对象失败", e);
				return null;
			}
		}

		/**
		 * 获得毫秒 获取失败则返回-1L
		 * 
		 * @param dateStr
		 *            日期字符串
		 * @return long 毫秒
		 */
		public long getTimeByStr(String dateStr) {
			return getDateByStr(dateStr).getTime();
		}

		// 对象属性
		/** 转换对象 */
		//formatter不保证线程安全所以是多例的
		//private SimpleDateFormat formatter;

		/** 格式化格式 */
		private String pattern;
	}
	/**
	 * 字符串转换java.sql.date
	 * @param     dateStr 日期字符串
	 * @return    java.sql.Date 日期对象 
	 */
	public java.sql.Date str2JavaSqlDate(String dateStr){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try{
		    d = format.parse(dateStr);	
		}catch(Exception e){
		    e.printStackTrace();	
		}
		java.sql.Date date = new java.sql.Date(d.getTime());
		
		return date;
	}
	/**
	 * 字符串转换long类型
	 * @param     dateStr 日期字符串
	 * @return    long类型
	 */
	public Long str2Long(String dateStr){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try{
			d = format.parse(dateStr);	
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		return d.getTime();
	}

	public static String getNowTime(DateFormatUnit unit) {
		return unit.getDateStr(new Date());
	}

	/**
	 * 判断当前时间是否在给定区间内, 支持跨天  时间格式(小时:分钟)
	 * @param startTime "00:00"
	 * @param endTime "06:00"
	 * @param startEquals true:包含开始时间 false: 不包含开始时间
	 * @param endEquals true:包含结束时间 false: 不包含结束时间
     * @return
     */
	public static boolean isNowTimeInRange(String startTime, String endTime, boolean startEquals, boolean endEquals) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date nowTime = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(startTime.split(":")[1]));
		Date start = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(endTime.split(":")[1]));
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		Date end = calendar.getTime();
		//结束时间是当天的零点或者跨天,切到次日对应时间
		if (currentHour == 0 || (end.before(start) && currentHour != 0)) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			end = calendar.getTime();
		}
		//[start,end]
		if (startEquals && endEquals) {
			return !start.after(nowTime) && !end.before(nowTime);
		}
		//(start,end)
		if (!startEquals && !endEquals) {
			return nowTime.after(start) && nowTime.before(end);
		}
		//(start,end]
		if (!startEquals && endEquals) {
			return nowTime.after(start) && !end.before(nowTime);
		}
		//[start,end)
		if (startEquals && !endEquals) {
			return !start.after(nowTime) && nowTime.before(end);
		}
		return false;
	}
}
