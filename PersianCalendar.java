package com.arashafsharpour.musicplatform.lib.util;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import com.nahantech.musicplatform.lib.BaseApplication;

public class PersianCalendar implements Serializable {

    //****************************************************************//
    // public Static Variables
    //****************************************************************//
    public static int g_days_in_month[] = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    public static int j_days_in_month[] = {31, 31, 31, 31, 31, 31, 30, 30, 30,
            30, 30, 29};

    public static final int SATARDAY = 1;
    public static final int SUNDAY = 2;
    public static final int MONDAY = 3;
    public static final int TUESDAY = 4;
    public static final int WENDSDAY = 5;
    public static final int THURSDAY = 6;
    public static final int FRIDAY = 7;

    //****************************************************************//
    // private static variables
    //****************************************************************//
    private static double len = 365.24219879;
    private static double base = 2346;

    private static double greg_len = 365.2425;
    private static double greg_origin_from_jalali_base = 629964;
    private static String greg_month_names[] = {"", "Jan", "Feb", "Mar",
            "Apr", "May", "June", "Jul", "A ug", "Sep", "Oct", "Nov", "Dec"};

    private int _year;
    private int _month;
    private int _dayOfMonth;
    private int _hour;
    private int _minute;
    private int _second;

    private Calendar _gregorianCalendar;

    private boolean _leap;

    //****************************************************************//
    // Static functions
    //****************************************************************//

    /**
     * Converts specified gregorian date to persian date in form of (yyyy/mm/dd)
     */
    public static String getPersianDate(int gregYear, int gregMonth, int gregDay) {
        // passed days from Greg orig
        double d = Math.ceil((gregYear - 1) * greg_len);
        // passed days from jalali base
        double d_j = d + greg_origin_from_jalali_base
                + getGregDayOfYear(gregYear, gregMonth, gregDay);

        // first result! jalali year
        double j_y = Math.ceil(d_j / len) - 2346;
        // day of the year
        double j_days_of_year = Math
                .floor(((d_j / len) - Math.floor(d_j / len)) * 365) + 1;

        // System.out.println(j_days_of_year);
        StringBuffer result = new StringBuffer();

        result.append((int) j_y + "/" + (int) month(j_days_of_year) + "/"
                + (int) dayOfMonth(j_days_of_year));
        return result.toString();
    }

    /**
     * Converts specified gregorian date to persian date in form of (yyyy/mm/dd)
     */
    public static String getPersianDate(Date d) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        return getPersianDate(year, (gc.get(Calendar.MONTH)) + 1,
                gc.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Returns persian year according to specified gregorian date.
     */
    public static int getPersianYear(Date dt) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dt);
        int gregYear = gc.get(Calendar.YEAR);
        int gregMonth = gc.get(Calendar.MONTH) + 1;
        int gregDay = gc.get(Calendar.DAY_OF_MONTH);

        double d = Math.ceil((gregYear - 1) * greg_len);
        double d_j = d + greg_origin_from_jalali_base
                + getGregDayOfYear(gregYear, gregMonth, gregDay);
        double j_y = Math.ceil(d_j / len) - 2346;
        double j_days_of_year = Math
                .floor(((d_j / len) - Math.floor(d_j / len)) * 365);
        return (int) j_y;
    }

    /**
     * returns the persian month number according to specified gregorian date
     * (Months:1..12)
     */
    public static int getPersianMonth(Date dt) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dt);
        int gregYear = gc.get(Calendar.YEAR);
        int gregMonth = gc.get(Calendar.MONTH) + 1;
        int gregDay = gc.get(Calendar.DAY_OF_MONTH);

        double d = Math.ceil((gregYear - 1) * greg_len);
        double d_j = d + greg_origin_from_jalali_base
                + getGregDayOfYear(gregYear, gregMonth, gregDay);
        double j_y = Math.ceil(d_j / len) - 2346;
        double j_days_of_year = Math
                .floor(((d_j / len) - Math.floor(d_j / len)) * 365);

        int month = (int) month(j_days_of_year);

        //
        // Modified by "Hosein Bitaraf" At 5/6/2012
        //
        int day = (int) getPersianDayOfMonth(dt);
        if (day == 1) {
            month++;
        }

        return month;
    }

    /**
     * Returns day number (1..31)
     */
    public static int getPersianDayOfMonth(Date dt) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dt);
        int gregYear = gc.get(Calendar.YEAR);
        int gregMonth = gc.get(Calendar.MONTH) + 1;
        int gregDay = gc.get(Calendar.DAY_OF_MONTH);

        double d = Math.ceil((gregYear - 1) * greg_len);
        double d_j = d + greg_origin_from_jalali_base
                + getGregDayOfYear(gregYear, gregMonth, gregDay);
        double j_y = Math.ceil(d_j / len) - 2346;
        double j_days_of_year = Math
                .floor(((d_j / len) - Math.floor(d_j / len)) * 365 + 1);
        return (int) dayOfMonth(j_days_of_year);
    }

    public static int getdifference(Date start, Date current) {
        Date[] datesofcperiod;
        double startdayofyear, currentdatofyear;
        int def = 0, periodnm = 0;
        GregorianCalendar gc = new GregorianCalendar();
        GregorianCalendar gc1 = new GregorianCalendar();
        gc.setTime(start);
        gc1.setTime(current);
        return def = (int) ((gc1.get(Calendar.YEAR) - gc.get(Calendar.YEAR)) * 365.2425)
                + gc1.get(Calendar.DAY_OF_YEAR) - gc.get(Calendar.DAY_OF_YEAR);
    }


    /**
     * We use "Calendar" java class for show our persian calendar. Subtract one
     * to set calendar with correct format. it is because "Calendar" class works
     * with subtract one from real month. eq: January = 0 or in our case eq:
     * Farvardin = 0
     */
    public static Calendar getPersianCalendar(Calendar gre) {
        Calendar persianCalendar = gre.getInstance();
        persianCalendar.set(getPersianYear(gre.getTime()),
                getPersianMonth(gre.getTime()) - 1,
                getPersianDayOfMonth(gre.getTime()));
        return persianCalendar;
    }

    /**
     * Converts Persian date to gregorian date
     */
    public static Calendar getGregorianCalendar(int year, int month, int day) {

        int gy, gm, gd;
        int jy, jm, jd;
        long g_day_no, j_day_no;
        boolean leap;

        int i;

        jy = year - 979;
        jm = month - 1;
        jd = day - 1;

        j_day_no = 365 * jy + (jy / 33) * 8 + (jy % 33 + 3) / 4;
        for (i = 0; i < jm; ++i)
            j_day_no += j_days_in_month[i];

        j_day_no += jd;

        g_day_no = j_day_no + 79;

        gy = (int) (1600 + 400 * (g_day_no / 146097)); /*
                                                         * 146097 = 365*400 +
														 * 400/4 - 400/100 +
														 * 400/400
														 */
        g_day_no = g_day_no % 146097;

        leap = true;
        if (g_day_no >= 36525) /* 36525 = 365*100 + 100/4 */ {
            g_day_no--;
            gy += 100 * (g_day_no / 36524); /* 36524 = 365*100 + 100/4 - 100/100 */
            g_day_no = g_day_no % 36524;

            if (g_day_no >= 365)
                g_day_no++;
            else
                leap = false;
        }

        gy += 4 * (g_day_no / 1461); /* 1461 = 365*4 + 4/4 */
        g_day_no %= 1461;

        if (g_day_no >= 366) {
            leap = false;

            g_day_no--;
            gy += g_day_no / 365;
            g_day_no = g_day_no % 365;
        }

        for (i = 0; g_day_no >= g_days_in_month[i]
                + parsBooleanToInt(i == 1 && leap); i++)
            g_day_no -= g_days_in_month[i] + parsBooleanToInt(i == 1 && leap);

        gm = i + 1;
        gd = (int) (g_day_no + 1);

        GregorianCalendar gregorian = new GregorianCalendar(gy, gm - 1, gd);
        return gregorian;
    }

    public static Calendar getGregorainCalendar(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar gregorian = (GregorianCalendar) getGregorianCalendar(year, month, day);

        gregorian.set(Calendar.HOUR_OF_DAY, hour);
        gregorian.set(Calendar.MINUTE, minute);
        gregorian.set(Calendar.SECOND, second);

        return gregorian;

    }

    public static Calendar getGregorianCalendar(PersianCalendar jalali) {
        GregorianCalendar gregorian = (GregorianCalendar) getGregorainCalendar(jalali.getYear(), jalali.getMonth(), jalali.getDay(),
                jalali.getHour(), jalali.getMinute(), jalali.getSecond());

        return gregorian;
    }

    public static String getDayOfWeek(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "یکشنبه";
            case 2:
                return "دوشنبه";
            case 3:
                return "سه شنبه";
            case 4:
                return "چهارشنبه";
            case 5:
                return "پنج شنبه";
            case 6:
                return "جمعه";
            case 7:
                return "شنبه";
        }
        return null;
    }

    public static int getDayOfWeek(int gregorianDayOfWeek) {
        int persianDayOfWeek;
        switch (gregorianDayOfWeek) {
            case 7:
                persianDayOfWeek = SATARDAY;
                break;

            case 1:
                persianDayOfWeek = SUNDAY;
                break;

            case 2:
                persianDayOfWeek = MONDAY;
                break;

            case 3:
                persianDayOfWeek = TUESDAY;
                break;

            case 4:
                persianDayOfWeek = WENDSDAY;
                break;

            case 5:
                persianDayOfWeek = THURSDAY;
                break;

            case 6:
                persianDayOfWeek = FRIDAY;
                break;

            default:
                persianDayOfWeek = -1;
                break;
        }
        return persianDayOfWeek;

    }

    //****************************************************************//
    // Private functions
    //****************************************************************//
    private static int parsBooleanToInt(Boolean sample) {
        if (sample)
            return 1;
        else
            return 0;
    }

    private static double month(double day) {

        if (day < 6 * 31)
            return Math.ceil(day / 31);
        else
            return Math.ceil((day - 6 * 31) / 30) + 6;
    }

    private static double dayOfMonth(double day) {

        double m = month(day);
        if (m <= 6)
            return day - 31 * (m - 1);
        else
            return day - (6 * 31) - (m - 7) * 30;
    }

    private static double getGregDayOfYear(double year, double month, double day) {
        int greg_moneths_len[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
                30, 31};
        boolean leap = false;
        if (((year % 4) == 0) && (((year % 400) != 0)))
            leap = true;
        if (leap)
            greg_moneths_len[2] = 29;
        int sum = 0;
        for (int i = 0; i < month; i++)
            sum += greg_moneths_len[i];
        return sum + day - 2;
    }

    private void syncPersianCalendarWithGregorian() {
        _year = getPersianYear(_gregorianCalendar.getTime());

        _month = getPersianMonth(_gregorianCalendar.getTime());

        _dayOfMonth = getPersianDayOfMonth(_gregorianCalendar.getTime());
    }

    public void syncGregorianWithPersianCalendar() {
        _gregorianCalendar = getGregorianCalendar(_year, _month, _dayOfMonth);
        _gregorianCalendar.set(Calendar.HOUR_OF_DAY, _hour);
        _gregorianCalendar.set(Calendar.MINUTE, _minute);
        _gregorianCalendar.set(Calendar.SECOND, _second);
    }


    //****************************************************************//
    // Public functions
    //****************************************************************//

    /**
     * Constructor
     */
    public PersianCalendar(Calendar gregorian) {
        _gregorianCalendar = gregorian.getInstance();

        _year = getPersianYear(gregorian.getTime());

        _month = getPersianMonth(gregorian.getTime());

        _dayOfMonth = getPersianDayOfMonth(gregorian.getTime());

        _hour = gregorian.get(Calendar.HOUR_OF_DAY);

        _minute = gregorian.get(Calendar.MINUTE);

        _second = gregorian.get(Calendar.SECOND);

    }

    public PersianCalendar() {
        setPersianCalendar(Calendar.getInstance());
    }

    /**
     * arg should be without hour and minute and second. eq: 14-6-1391
     */
    public PersianCalendar(String jalaliString) {
        String[] part = jalaliString.split("-");
        setPersainCalendarWithJalali(Integer.parseInt(part[2]), Integer.parseInt(part[1]),
                Integer.parseInt(part[0]));
    }

    public PersianCalendar(PersianCalendar jalali) {
        setPersainCalendarWithJalali(jalali.getYear(), jalali.getMonth(), jalali.getDay(),
                jalali.getHour(), jalali.getMinute(), jalali.getSecond());
    }

    public PersianCalendar(int jalaliyear, int jalalimonth, int jalaliday) {
        setPersainCalendarWithJalali(jalaliyear, jalalimonth, jalaliday);
    }

    public PersianCalendar(int jalaliyear, int jalalimonth, int jalaliday, int jalalihour,
                           int jalalimintute, int jalalisecond) {
        setPersainCalendarWithJalali(jalaliyear, jalalimonth, jalaliday, jalalihour,
                jalalimintute, jalalisecond);
    }

    public void setPersainCalendarWithJalali(int year, int month, int dayOfMonth, int hour,
                                             int minute, int second) {
        _year = year;
        _month = month;
        _dayOfMonth = dayOfMonth;
        _hour = hour;
        _minute = minute;
        _second = second;

        _gregorianCalendar = getGregorianCalendar(year, month, dayOfMonth);
        _gregorianCalendar.set(_gregorianCalendar.get(Calendar.YEAR),
                _gregorianCalendar.get(Calendar.MONTH), _gregorianCalendar.get(Calendar.DAY_OF_MONTH),
                hour, minute, second);
    }

    public void setPersainCalendarWithJalali(int year, int month, int dayOfMonth) {
        _year = year;
        _month = month;
        _dayOfMonth = dayOfMonth;
        _hour = 0;
        _minute = 0;
        _second = 0;

        _gregorianCalendar = getGregorianCalendar(year, month, dayOfMonth);
    }

    public void setPersianCalendar(Calendar gregorian) {
        _gregorianCalendar = gregorian;

        _year = getPersianYear(gregorian.getTime());

        _month = getPersianMonth(gregorian.getTime());

        _dayOfMonth = getPersianDayOfMonth(gregorian.getTime());

        _hour = gregorian.get(Calendar.HOUR_OF_DAY);

        _minute = gregorian.get(Calendar.MONTH);

        _second = gregorian.get(Calendar.SECOND);
    }

    public void setHour(int hour) {

        _hour = hour;
    }

    public void setMinute(int minute) {

        _minute = minute;
    }

    public void setSecond(int second) {
        _second = second;
    }

    public Calendar getGregorianCalendar() {
        syncGregorianWithPersianCalendar();
        return _gregorianCalendar;
    }

    public int getYear() {
        return _year;
    }

    public int getMonth() {
        return _month;
    }

    public int getDay() {
        return _dayOfMonth;
    }

    public int getHour() {
        return _hour;
    }

    public int getMinute() {
        return _minute;
    }

    public int getSecond() {
        return _second;
    }


    /**
     * Returns how much days are blank at start
     */
    public int getBlankDayAtMonthStarting() {

        Calendar firstDayOfMonth = getGregorianCalendar(_year, _month, 1);

        int dayofWeek;
        dayofWeek = getDayOfWeek(firstDayOfMonth.get(Calendar.DAY_OF_WEEK));
        int blankDay = dayofWeek - 1;

        return blankDay;
    }

    public int getDayOfWeek() {
        int dayOfWeek;
        switch (_gregorianCalendar.get(Calendar.DAY_OF_WEEK)) {
            case 7:
                dayOfWeek = SATARDAY;
                break;

            case 1:
                dayOfWeek = SUNDAY;
                break;

            case 2:
                dayOfWeek = MONDAY;
                break;

            case 3:
                dayOfWeek = TUESDAY;
                break;

            case 4:
                dayOfWeek = WENDSDAY;
                break;

            case 5:
                dayOfWeek = THURSDAY;
                break;

            case 6:
                dayOfWeek = FRIDAY;
                break;

            default:
                dayOfWeek = -1;
                break;
        }
        return dayOfWeek;
    }

    public void add(int field, int value) {
        _gregorianCalendar.add(field, value);

        syncPersianCalendarWithGregorian();
    }

    @SuppressLint("ParserError")
    public boolean isEqual(PersianCalendar jalali) {
        if (_second == jalali.getSecond() && _minute == jalali.getMinute() && _hour == jalali.getHour() && _dayOfMonth == jalali.getDay()
                && _month == jalali.getMonth() && _year == jalali.getYear())
            return true;

        else
            return false;
    }

    public int compareTo(PersianCalendar jalali) {
        Calendar anotherCalendar = getGregorianCalendar(jalali);
        Calendar calendar = getGregorianCalendar(this);

        return calendar.compareTo(anotherCalendar);
    }

    /**
     * this function is correct for years beetween  1343 and 1472
     * from: www.fa.wikipedia.org
     */
    public boolean isLeapYear() {

        switch (_year % 33) {
            case 1:
                _leap = true;
                break;
            case 5:
                _leap = true;
                break;
            case 9:
                _leap = true;
                break;
            case 13:
                _leap = true;
                break;
            case 17:
                _leap = true;
                break;
            case 22:
                _leap = true;
                break;
            case 26:
                _leap = true;
                break;
            case 30:
                _leap = true;
                break;

            default:
                _leap = false;
                break;
        }
        return _leap;

    }

    @Override
    public String toString() {
        return getYear() + "/" + getMonth() + "/" + getDay() + "   (" + getHour() + ":" + getMinute() + ")";
    }

    public static String getDayOfWeekString(int dayOfWeek) {
        String strWeekDay = null;
        switch (dayOfWeek) {
            case 1:
                strWeekDay = "شنبه";
                break;
            case 2:
                strWeekDay = "يکشنبه";
                break;
            case 3:
                strWeekDay = "دوشنبه";
                break;
            case 4:
                strWeekDay = "سه شنبه";
                break;
            case 5:
                strWeekDay = "چهارشنبه";
                break;
            case 6:
                strWeekDay = "پنج شنبه";
                break;
            case 7:
                strWeekDay = "جمعه";
                break;
        }
        return strWeekDay;
    }

    public String getMonthString() {
        return getMonthString(getMonth());
    }

    public static String getDayChar(int i) {
        switch (i) {
            case 0:
                return "ش";
            case 1:
                return "ی";
            case 2:
                return "د";
            case 3:
                return "س";
            case 4:
                return "چ";
            case 5:
                return "پ";
            default:
                return "ج";
        }
    }

    public static String getEnDayChar(int i) {
        switch (i) {
            case 0:
                return "Sun";
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            default:
                return "Sat";
        }
    }

    public static String getMonthString(int month) {
        String strMonth = null;
        switch (month) {
            case 1:
                strMonth = "فروردين";
                break;
            case 2:
                strMonth = "ارديبهشت";
                break;
            case 3:
                strMonth = "خرداد";
                break;
            case 4:
                strMonth = "تير";
                break;
            case 5:
                strMonth = "مرداد";
                break;
            case 6:
                strMonth = "شهريور";
                break;
            case 7:
                strMonth = "مهر";
                break;
            case 8:
                strMonth = "آبان";
                break;
            case 9:
                strMonth = "آذر";
                break;
            case 10:
                strMonth = "دي";
                break;
            case 11:
                strMonth = "بهمن";
                break;
            case 12:
                strMonth = "اسفند";
                break;
        }
        return strMonth;
    }

    public static String getMonthStringEn(int month) {
        String strMonth = null;
        switch (month) {
            case 1:
                strMonth = "January";
                break;
            case 2:
                strMonth = "February";
                break;
            case 3:
                strMonth = "March";
                break;
            case 4:
                strMonth = "April";
                break;
            case 5:
                strMonth = "May";
                break;
            case 6:
                strMonth = "June";
                break;
            case 7:
                strMonth = "July";
                break;
            case 8:
                strMonth = "August";
                break;
            case 9:
                strMonth = "September";
                break;
            case 10:
                strMonth = "October";
                break;
            case 11:
                strMonth = "November";
                break;
            case 12:
                strMonth = "December";
                break;
        }
        return strMonth;
    }

    public static String getDayOfWeek(int year, int month, int day) {
        PersianCalendar persianCalendar = new PersianCalendar(year, month, day);
        return getDayOfWeekString(persianCalendar.getDayOfWeek());
    }

    public static String getDayOfWeekEn(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return getDayOfWeekStringEn(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static String getDayOfWeekStringEn(int i) {
        switch (i) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            default:
                return "Saturday";
        }
    }

    public static int getMonthDays(int[] dates, BaseApplication.Language language) {
        if (language == BaseApplication.Language.EN) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(dates[0], dates[1] - 1, 1);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            if (dates[1] >= 1 && dates[1] <= 6) {
                return 31;
            } else {
                if (dates[1] == 12) {
                    PersianCalendar calendar = new PersianCalendar(dates[0], dates[1], 1);
                    if (calendar.isLeapYear()) {
                        return 30;
                    } else {
                        return 29;
                    }
                } else {
                    return 30;
                }
            }
        }
    }

    public static int getFirstDay(int[] dates, BaseApplication.Language language) {
        if (language == BaseApplication.Language.FA) {
            PersianCalendar calendar = new PersianCalendar(dates[0], dates[1], 1);
            return calendar.getDayOfWeek();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(dates[0], dates[1] - 1, 1);
            return calendar.get(Calendar.DAY_OF_WEEK);
        }
    }

    public static int[] getDates(int position, BaseApplication.Language language) {
        int year, month, day;
        if (language == BaseApplication.Language.EN) {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH) + position + 1;
            year = calendar.get(Calendar.YEAR);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            if (month > 12) {
                year++;
                month = month - 12;
            }
        } else {
            PersianCalendar calendar = new PersianCalendar(Calendar.getInstance());
            month = calendar.getMonth() + position;
            year = calendar.getYear();
            day = calendar.getDay();
            if (month > 12) {
                year++;
                month = month - 12;
            }
        }
        return new int[]{year, month, day};
    }

    public static long calculateTimestamp(int y, int m, int d, BaseApplication.Language language) {
        if (language == BaseApplication.Language.EN) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(y, m - 1, d, 0, 0, 0);
            return calendar.getTimeInMillis();
        } else {
            PersianCalendar persianCalendar = new PersianCalendar(y, m, d);
            Calendar calendar = persianCalendar.getGregorianCalendar();
            return calendar.getTimeInMillis();
        }
    }

    public static int[] toGeorgian(int[] persian) {
        PersianCalendar persianCalendar = new PersianCalendar(persian[0], persian[1], persian[2]);
        Calendar calendar = persianCalendar.getGregorianCalendar();
        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }

    public static int[] toPersian(int[] georgian) {
        Calendar georgianCalendar = Calendar.getInstance();
        georgianCalendar.set(georgian[0], georgian[1] - 1, georgian[2]);
        PersianCalendar calendar = new PersianCalendar(georgianCalendar);
        return new int[]{calendar.getYear(), calendar.getMonth(), calendar.getDay()};
    }

    public static boolean isSame(int[] subject, int[] other) {
        return subject[0] == other[0] && subject[1] == other[1] && subject[2] == other[2];
    }

    public static boolean isLower(int[] subject, int[] other) {
        if (subject[0] < other[0]) {
            return true;
        } else if (subject[0] == other[0]
                && subject[1] < other[1]) {
            return true;
        } else if (subject[0] == other[0]
                && subject[1] == other[1]
                && subject[2] < other[2]) {
            return true;
        }
        return false;
    }

    public static String toString(BaseApplication.Language lang, int[] date) {
        if (lang == BaseApplication.Language.FA) {
            PersianCalendar calendar = new PersianCalendar(date[0], date[1], date[2]);
            return PersianCalendar.getDayOfWeekString(calendar.getDayOfWeek())
                    + ", " + calendar.getDay() + " " + calendar.getMonthString();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(date[0], date[1] - 1, date[2]);
            return PersianCalendar.getDayOfWeekStringEn(calendar.get(Calendar.DAY_OF_WEEK))
                    + ", " + calendar.get(Calendar.DAY_OF_MONTH) + " "
                    + PersianCalendar.getMonthStringEn(calendar.get(Calendar.MONTH) + 1);
        }
    }

    public static int[] previousDay(int[] date, BaseApplication.Language language) {
        if (language == BaseApplication.Language.FA) {
            PersianCalendar calendar = new PersianCalendar(date[0], date[1], date[2]);
            Calendar geo = calendar.getGregorianCalendar();
            geo.setTimeInMillis(geo.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
            calendar = new PersianCalendar(geo);
            return new int[]{calendar.getYear(), calendar.getMonth(), calendar.getDay()};
        } else {
            Calendar geo = Calendar.getInstance();
            geo.set(date[0], date[1] - 1, date[2]);
            geo.setTimeInMillis(geo.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
            return new int[]{geo.get(Calendar.YEAR), geo.get(Calendar.MONTH) + 1, geo.get(Calendar.DAY_OF_MONTH)};
        }
    }

    public static int[] nextDay(int[] date, BaseApplication.Language language) {
        if (language == BaseApplication.Language.FA) {
            PersianCalendar calendar = new PersianCalendar(date[0], date[1], date[2]);
            Calendar geo = calendar.getGregorianCalendar();
            geo.setTimeInMillis(geo.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
            calendar = new PersianCalendar(geo);
            return new int[]{calendar.getYear(), calendar.getMonth(), calendar.getDay()};
        } else {
            Calendar geo = Calendar.getInstance();
            geo.set(date[0], date[1] - 1, date[2]);
            geo.setTimeInMillis(geo.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
            return new int[]{geo.get(Calendar.YEAR), geo.get(Calendar.MONTH) + 1, geo.get(Calendar.DAY_OF_MONTH)};
        }
    }

    public static int[] convertToEn(int[] persian) {
        PersianCalendar calendar = new PersianCalendar(persian[0], persian[1], persian[2]);
        Calendar georgian = calendar.getGregorianCalendar();
        return new int[]{georgian.get(Calendar.YEAR), georgian.get(Calendar.MONTH) + 1
                , georgian.get(Calendar.DAY_OF_MONTH)};
    }

    public static int[] convertToFa(int[] georgian) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(georgian[0], georgian[1] - 1, georgian[2]);
        PersianCalendar persianCalendar = new PersianCalendar(calendar);
        return new int[]{persianCalendar.getYear(), persianCalendar.getMonth(), persianCalendar.getDay()};
    }

    public static String parse(com.nahantech.musicplatform.lib.model.Date object) {
        if (object != null && object.getGeorgian() != null && object.getGeorgian().length >= 3) {
            return object.getGeorgian()[0] + "-" + object.getGeorgian()[1]
                    + "-" + object.getGeorgian()[2];
        }
        return null;
    }

    public static com.nahantech.musicplatform.lib.model.Date parse(String date) {
        String[] dateValue;
        if (date != null && (dateValue = date.split("-")) != null && dateValue.length >= 3) {
            com.nahantech.musicplatform.lib.model.Date object = new com.nahantech.musicplatform.lib.model.Date();
            object.setLanguageOrdinal(BaseApplication.getLanguage().ordinal());
            int day, month, year;
            year = Integer.parseInt(parseNumber(dateValue[0]));
            month = Integer.parseInt(parseNumber(dateValue[1]));
            day = Integer.parseInt(parseNumber(dateValue[2]));
            object.setGeorgian(new int[]{year, month, day});
            object.setPersian(convertToFa(object.getGeorgian()));
            return object;
        }
        return null;
    }

    private static String parseNumber(String s) {
        if (s != null && s.length() == 2 && s.startsWith("0")) {
            return s.substring(1, s.length());
        }
        return s;
    }
}
