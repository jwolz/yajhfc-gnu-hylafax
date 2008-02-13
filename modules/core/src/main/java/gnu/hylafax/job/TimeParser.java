package gnu.hylafax.job;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * This class parses hylafax time Strings as used in hfaxd.conf for example to Strings in the format awaited by server
 * using the hylafax client protocol.
 * <p/>
 * The time is specified in the formats:
 * <p/>
 * <b>HH:MM [AM|PM] [ month DD | dayofweek ]</b> or <b>now + N period</b>
 * <p/>
 * Valid values for <b>month</b> are Jan(uary), Feb(ruary), Mar(ch), Apr(il), May, Jun(e), Jul(y), Aug(ust),
 * Sep(tember), Oct(ober), Nov(ember) and Dec(ember).
 * <p/>
 * Valid values for <b>dayofweek</b> are Mon(day), Tue(sday), Wed(nesday), Thu(rsday), Fri(day), Sat(urday), Sun(day)
 * <p/>
 * Valid values for <b>period<b> are second(s), minute(s), hour(s), day(s), week(s), month(s)
 * <p/>
 * Examples: now + 5 minutes 10:15 PM May 5 23:20 Monday
 *
 * @author Stefan Unterhofer
 */
public class TimeParser
{

    private static final Logger logger = Logger.getLogger(TimeParser.class);

    private TimeZone timeZone;
    private Locale locale;

    /**
     * HashMap containing valid period values and linking to the corresponding Calendar fields
     */
    private static Map periods = new HashMap();

    static
    {
        periods.put("second", new Integer(Calendar.SECOND));
        periods.put("seconds", new Integer(Calendar.SECOND));
        periods.put("minute", new Integer(Calendar.MINUTE));
        periods.put("minutes", new Integer(Calendar.MINUTE));
        periods.put("hour", new Integer(Calendar.HOUR));
        periods.put("hours", new Integer(Calendar.HOUR));
        periods.put("day", new Integer(Calendar.DAY_OF_YEAR));
        periods.put("days", new Integer(Calendar.DAY_OF_YEAR));
        periods.put("week", new Integer(Calendar.WEEK_OF_YEAR));
        periods.put("weeks", new Integer(Calendar.WEEK_OF_YEAR));
        periods.put("month", new Integer(Calendar.MONTH));
        periods.put("months", new Integer(Calendar.MONTH));
    }

    /**
     * HashMap containing valid month values and the corresponding Calendar fields
     */
    private static Map months = new HashMap();

    static
    {
        months.put("jan", new Integer(Calendar.JANUARY));
        months.put("january", new Integer(Calendar.JANUARY));
        months.put("feb", new Integer(Calendar.FEBRUARY));
        months.put("february", new Integer(Calendar.FEBRUARY));
        months.put("mar", new Integer(Calendar.MARCH));
        months.put("march", new Integer(Calendar.MARCH));
        months.put("apr", new Integer(Calendar.APRIL));
        months.put("april", new Integer(Calendar.APRIL));
        months.put("may", new Integer(Calendar.MAY));
        months.put("jun", new Integer(Calendar.JUNE));
        months.put("june", new Integer(Calendar.JUNE));
        months.put("jul", new Integer(Calendar.JULY));
        months.put("july", new Integer(Calendar.JULY));
        months.put("aug", new Integer(Calendar.AUGUST));
        months.put("august", new Integer(Calendar.AUGUST));
        months.put("sep", new Integer(Calendar.SEPTEMBER));
        months.put("september", new Integer(Calendar.SEPTEMBER));
        months.put("oct", new Integer(Calendar.OCTOBER));
        months.put("october", new Integer(Calendar.OCTOBER));
        months.put("nov", new Integer(Calendar.NOVEMBER));
        months.put("november", new Integer(Calendar.NOVEMBER));
        months.put("dec", new Integer(Calendar.DECEMBER));
        months.put("december", new Integer(Calendar.DECEMBER));
    }

    /**
     * HashMap containing valid dayofweek values linking to the corresponding Calendar fields
     */
    private static Map daysOfWeek = new HashMap();

    static
    {
        daysOfWeek.put("mon", new Integer(Calendar.MONDAY));
        daysOfWeek.put("monday", new Integer(Calendar.MONDAY));
        daysOfWeek.put("tue", new Integer(Calendar.TUESDAY));
        daysOfWeek.put("tuesday", new Integer(Calendar.TUESDAY));
        daysOfWeek.put("wed", new Integer(Calendar.WEDNESDAY));
        daysOfWeek.put("wednesday", new Integer(Calendar.WEDNESDAY));
        daysOfWeek.put("thu", new Integer(Calendar.THURSDAY));
        daysOfWeek.put("thursday", new Integer(Calendar.THURSDAY));
        daysOfWeek.put("fri", new Integer(Calendar.FRIDAY));
        daysOfWeek.put("friday", new Integer(Calendar.FRIDAY));
        daysOfWeek.put("sat", new Integer(Calendar.SATURDAY));
        daysOfWeek.put("saturday", new Integer(Calendar.SATURDAY));
        daysOfWeek.put("sun", new Integer(Calendar.SUNDAY));
        daysOfWeek.put("sunday", new Integer(Calendar.SUNDAY));
    }

    /**
     * Initializes the parser with a custom timezone and locale
     *
     * @param locale Custom locale
     * @param timeZone Custom timezone
     */
    public TimeParser(Locale locale, TimeZone timeZone)
    {
        this.locale = locale;
        this.timeZone = timeZone;
    }

    /**
     * Initializes the Parser with the current timezone/locale of the program.
     */
    public TimeParser()
    {
        this.locale = Locale.getDefault();
        this.timeZone = TimeZone.getDefault();
    }

    /**
     * This method parses a given datetime String into a java.util.Date using a Calendar
     *
     * @param string Hylafax datetime String (e.g. now + 10 minutes)
     * @return java.util.Date object containing the parsed date
     * @throws ParseException if the entered String is not valid
     */
    private Date parse(String string)
        throws ParseException
    {
        try
        {

            string = string.toLowerCase().trim();

            Calendar cal = Calendar.getInstance(timeZone, locale);
            cal.setTimeInMillis(System.currentTimeMillis());

            StringTokenizer tokenizer = new StringTokenizer(string);

            if (logger.isDebugEnabled())
            {
                logger.debug("Parsing String " + string);
                logger.debug("Current Time is " + cal.getTime());
            }

            String time = tokenizer.nextToken();
            // String has form now + N period
            if (time.equals("now"))
            {
                // check if + is present
                if (tokenizer.nextToken().equals("+"))
                {
                    try
                    {
                        // parse N
                        String number = tokenizer.nextToken();
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("N = " + number);
                        }
                        int n = Integer.parseInt(number);

                        if (n < 0)
                        {
                            throw new NumberFormatException();
                        }

                        // parse period
                        String period = tokenizer.nextToken();

                        if (periods.get(period) != null)
                        {
                            cal.setLenient(true);
                            cal.add(((Integer) periods.get(period)).intValue(), n);
                        }
                        else
                        {
                            throw new ParseException(
                                "period has to be minute(s), hour(s), day(s), week(s) or month(s)");
                        }

                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Parsing finished, date is " + cal.getTime());
                        }

                        return cal.getTime();

                    }
                    catch (NumberFormatException e)
                    {
                        throw new ParseException("N must be a positive numeric value");
                    }
                }
                else
                {
                    throw new ParseException("Parse error now must be followed by '+'");
                }
            }
            else
            {
                // string has form HH:MM [AM|PM] [month DD | dayofweek]
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                boolean isAmPm = false;

                if (tokenizer.hasMoreTokens())
                {

                    // check if the time is given in AM/PM format or 24 hours
                    String amPm = tokenizer.nextToken();

                    if (amPm.equals("am") || amPm.equals("pm"))
                    {
                        format = new SimpleDateFormat("KK:mm");
                        isAmPm = true;
                    }

                    try
                    {
                        Date d = format.parse(time);
                        cal.set(Calendar.HOUR_OF_DAY, d.getHours());
                        cal.set(Calendar.MINUTE, d.getMinutes());
                    }
                    catch (java.text.ParseException e)
                    {
                        throw new ParseException("Time has to be hh:mm [AM|PM]");
                    }

                    if (isAmPm)
                    {
                        if (amPm.equals("pm"))
                        {
                            cal.add(Calendar.HOUR, 12);
                        }

                        if (tokenizer.hasMoreTokens())
                        {
                            amPm = tokenizer.nextToken();
                        }
                        else
                        {
                            return cal.getTime();
                        }
                    }

                    if (daysOfWeek.get(amPm) != null)
                    {
                        cal.set(Calendar.DAY_OF_WEEK, ((Integer) daysOfWeek.get(amPm)).intValue());
                    }
                    else if (months.get(amPm) != null)
                    {
                        cal.set(Calendar.MONTH, ((Integer) months.get(amPm)).intValue());

                        try
                        {
                            cal.setLenient(false);
                            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tokenizer.nextToken()));
                        }
                        catch (NumberFormatException e)
                        {
                            throw new ParseException("day of month has to be a valid numeric value");
                        }
                        catch (ArrayIndexOutOfBoundsException e)
                        {
                            throw new ParseException("day value has to be within mont boundaries");
                        }

                    }
                    else
                    {
                        throw new ParseException("Value has to be a valid month or day of week");
                    }

                }
                else
                {
                    try
                    {
                        Date d = format.parse(time);
                        cal.set(Calendar.HOUR_OF_DAY, d.getHours());
                        cal.set(Calendar.MINUTE, d.getMinutes());
                    }
                    catch (java.text.ParseException e)
                    {
                        throw new ParseException("Time has to be hh:mm [AM|PM]");
                    }

                }
                if (logger.isDebugEnabled())
                {
                    logger.debug("Returning Date " + cal.getTime());
                }

                return cal.getTime();
            }
        }
        catch (NoSuchElementException e)
        {
            throw new ParseException("String to parse not complete");
        }
    }

    /**
     * Gets the given Time in the Format awaited by the Hylafax client protocol for the SENDTIME parameter, which is
     * YYYYMMDDhhmmss
     *
     * @param time Time String in the hylafax config file format
     * @return Time String used by the hylafax client protocol
     * @throws ParseException if the supplied time string isn't valid
     */
    public String getSendTime(String time)
        throws ParseException
    {
        if (time.trim().toLowerCase().equals("now"))
        {
            return "now";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(parse(time));
    }

    /**
     * Gets the given Time in the Format awaited by the hylafax client protocol for the KILLTIME parameter, which is now
     * + ddhhmm, days, hours and minutes from now.
     *
     * @param time Time String in the hylafax config file format
     * @return Time String used by the hylafax client protocol
     * @throws ParseException if the supplied time string isn't valid
     */
    public String getKillTime(String time)
        throws ParseException
    {
        if (time.toLowerCase().trim().equals("now"))
        {
            return "000000";
        }

        long killTime = parse(time).getTime() - System.currentTimeMillis();

        // convert to seconds since we don't need ms
        killTime /= 1000;

        long days = killTime / (60 * 60 * 24);
        killTime %= 60 * 60 * 24;

        long hours = killTime / (60 * 60);
        killTime %= 60 * 60;

        long minutes = killTime / 60;

        String dayS, hourS, minS;
        if (days < 10)
        {
            dayS = "0" + days;
        }
        else
        {
            dayS = "" + days;
        }

        if (hours < 10)
        {
            hourS = "0" + hours;
        }
        else
        {
            hourS = "" + hours;
        }

        if (minutes < 10)
        {
            minS = "0" + minutes;
        }
        else
        {
            minS = "" + minutes;
        }

        return dayS + hourS + minS;
    }

    /**
     * Exception class for parse errors
     */
    class ParseException extends Exception
    {
        ParseException()
        {
        }

        ParseException(Throwable cause)
        {
            super(cause);
        }

        ParseException(String message)
        {
            super(message);
        }

        ParseException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }

}
