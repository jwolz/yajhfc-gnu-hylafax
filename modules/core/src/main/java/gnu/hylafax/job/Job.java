// Job.java - a HylaFAX Job representation
// $Id: Job.java,v 1.3 2006/02/20 04:52:10 sjardine Exp $
//
// Copyright 2001-2003 Innovation Software Group, LLC - http://www.innovationsw.com
//                Joe Phillips <jaiger@innovationsw.com>
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the Free
// Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
package gnu.hylafax.job;

import gnu.hylafax.Client;
import gnu.hylafax.ClientProtocol;
import gnu.hylafax.job.TimeParser.ParseException;
import gnu.inet.ftp.ServerResponseException;

import java.awt.Dimension;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class is a light, unsynchronized implementation of gnu.hylafax.Job.
 * @author $Author: sjardine $
 * @version $Id: Job.java,v 1.3 2006/02/20 04:52:10 sjardine Exp $
 * @see gnu.hylafax.ClientProtocol
 * @see gnu.hylafax.Client
 * @see gnu.hylafax.Job
 **/
public class Job implements gnu.hylafax.Job {

	protected Client client;

	private long Id;

	public static int PRIORITY_NORMAL = 127;

	public static int PRIORITY_BULK = 207;

	public static int PRIORITY_HIGH = 63;

	public static int RESOLUTION_LOW = 98;

	public static int RESOLUTION_MEDIUM = 196;

	public static String NOTIFY_NONE = ClientProtocol.NOTIFY_NONE;

	public static String NOTIFY_DONE = ClientProtocol.NOTIFY_DONE;

	public static String NOTIFY_REQUEUE = ClientProtocol.NOTIFY_REQUEUE;

	public static String NOTIFY_ALL = ClientProtocol.NOTIFY_ALL;

	public static String CHOP_DEFAULT = "default";

	public Job(Client c) throws ServerResponseException, IOException {
		synchronized (c) {
			client = c;
			client.jnew();
			Id = client.job();
		}
	}// constructor

	public Job(Client c, long id) throws ServerResponseException, IOException {
		synchronized (c) {
			client = c;
			client.job(id);
			Id = client.job();
		}
	}// constructor

	/* (non-Javadoc)
	* @see gnu.hylafax.Job#getFromUser()
	*/
	public String getFromUser() throws ServerResponseException, IOException {
		return getProperty("FROMUSER");
	}// getFromUser

	public String getKilltime() throws ServerResponseException, IOException {
		return getProperty("LASTTIME");
	}// getKilltime

	public int getMaximumDials() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("MAXDIALS"));
	}// getMaximumDials

	public int getMaximumTries() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("MAXTRIES"));
	}// getMaximumTries

	public int getPriority() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("SCHEDPRI"));
	}// getPriority

	public String getDialstring() throws ServerResponseException, IOException {
		return getProperty("DIALSTRING");
	}// getDialstring

	public String getNotifyAddress() throws ServerResponseException,
			IOException {
		return getProperty("NOTIFYADDR");
	}// getNotifyAddress

	public int getVerticalResolution() throws ServerResponseException,
			IOException {
		return Integer.parseInt(getProperty("VRES"));
	}// getVerticalResolution

	public Dimension getPageDimension() throws ServerResponseException,
			IOException {
		return new Dimension(getPageWidth(), getPageLength());
	}// getPageDimension

	public int getPageWidth() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("PAGEWIDTH"));
	}// getPageWidth

	public int getPageLength() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("PAGELENGTH"));
	}// getPageLength

	public String getNotifyType() throws ServerResponseException, IOException {
		return getProperty("NOTIFY");
	}// getNotifyType

	public String getPageChop() throws ServerResponseException, IOException {
		return getProperty("PAGECHOP");
	}// getPageChop

	public int getChopThreshold() throws ServerResponseException, IOException {
		return Integer.parseInt(getProperty("CHOPTHRESHOLD"));
	}// getChopThreshold

	public String getDocumentName() throws ServerResponseException, IOException {
		return getProperty("DOCUMENT");
	}// getDocumentName

	public String getRetrytime() throws ServerResponseException, IOException {
		return getProperty("RETRYTIME");
	}// getRetrytime

	/**
	 * Get the value for an arbitrary property for this job.
	 * Developers using this method should be familiar with the HylaFAX client protocol in order to provide the correct key values and how to interpret the values returned.
	 * @exception ServerResponseException the server responded with an error.  This is likely due to a protocol error.
	 * @exception IOException an i/o error occured
	 * @return a String value for the given property key
	 */
	public String getProperty(String key) throws ServerResponseException,
			IOException {
		String tmp = client.jparm(key);
		return tmp;
	}// getProperty

	/**
	 * get the job-id of this Job instance. 
	 * @return job id
	 */
	public long getId() {
		return Id;
	}// getId

	public void setFromUser(String value) throws ServerResponseException,
			IOException {
		setProperty("FROMUSER", value);
	}// setFromUser

	public void setKilltime(String value) throws ServerResponseException,
			IOException {
		String time;
		try {
			time = new TimeParser().getKillTime(value);
		} catch (ParseException e) {
			time = value;
		}
		setProperty("LASTTIME", time);
	}// setKilltime

	public void setMaximumDials(int value) throws ServerResponseException,
			IOException {
		setProperty("MAXDIALS", value);
	}// setMaximumDials

	public void setMaximumTries(int value) throws ServerResponseException,
			IOException {
		setProperty("MAXTRIES", value);
	}// setMaximumTries

	public void setPriority(int value) throws ServerResponseException,
			IOException {
		setProperty("SCHEDPRI", value);
	}// setPriority

	public void setDialstring(String value) throws ServerResponseException,
			IOException {
		setProperty("DIALSTRING", value);
	}// setDialstring

	public void setNotifyAddress(String value) throws ServerResponseException,
			IOException {
		setProperty("NOTIFYADDR", value);
	}// setNotifyAddress

	public void setVerticalResolution(int value)
			throws ServerResponseException, IOException {
		setProperty("VRES", value);
	}// setVerticalResolution

	public void setPageDimension(Dimension value)
			throws ServerResponseException, IOException {
		setPageWidth((int) value.getWidth());
		setPageLength((int) value.getHeight());
	}// setPageDimension

	public void setPageWidth(int width) throws ServerResponseException,
			IOException {
		setProperty("PAGEWIDTH", width);
	}// setPageWidth

	public void setPageLength(int length) throws ServerResponseException,
			IOException {
		setProperty("PAGELENGTH", length);
	}// setPageLength

	/**
	 * Get JobInfo attribute.
	 * JobInfo is an identifying string associated with the job.
	 */
	public String getJobInfo() throws ServerResponseException, IOException {
		return getProperty("JOBINFO");
	}// getJobInfo

	/**
	 * Get TagLine format attribute.
	 * The TagLine
	 */
	public String getTagline() throws ServerResponseException, IOException {
		return getProperty("TAGLINE");
	}// getTagline

	/**
	 * Get the UseTagLine attribute.
	 * The TagLine
	 */
	public boolean getUseTagline() throws ServerResponseException, IOException {
		return ("YES".equalsIgnoreCase(getProperty("USETAGLINE")) ? true
				: false);
	}// getUseTagline

	/**
	 * set the notification type.  For possible values, see the NOTIFY_*
	 * members of this class.
	 * @param value the new notification type
	 * @exception ServerResponseException the server responded with an error.  This is likely a protocol violation.
	 * @exception IOException an IO error occurred while communicating with the server
	 */
	public void setNotifyType(String value) throws ServerResponseException,
			IOException {
		setProperty("NOTIFY", value);
	}// setNotifyType

	public void setPageChop(String value) throws ServerResponseException,
			IOException {
		setProperty("PAGECHOP", value);
	}// setPageChop

	public void setChopThreshold(int value) throws ServerResponseException,
			IOException {
		setProperty("CHOPTHRESHOLD", value);
	}// setChopThreshold

	public void addDocument(String value) throws ServerResponseException,
			IOException {
		setProperty("DOCUMENT", value);
	}// addDocument

	public void setUseTagline(boolean value) throws ServerResponseException,
			IOException {
		setProperty("USETAGLINE", (value ? "YES" : "NO"));
	}// setUseTagline

	public void setRetrytime(String value) throws ServerResponseException,
			IOException {
		setProperty("RETRYTIME", value);
	}// setRetrytime

	/**
	 * Set the JobInfo attribute.
	 * This is an identifying string associated with each job.
	 */
	public void setJobInfo(String value) throws ServerResponseException,
			IOException {
		setProperty("JOBINFO", value);
	}// setJobInfo

	/**
	 * Set the TagLine format attribute.
	 * This property specifies the format of the tagline rendered at
	 * the top of each page of the transmitted FAX.
	 * Tagline format strings are documented in config(5F).
	 * If you use this, you will probably want to use setUseTagline()
	 */
	public void setTagline(String value) throws ServerResponseException,
			IOException {
		setProperty("TAGLINE", value);
	}// setTagline

	/**
	 * Set any arbitrary property on this job.
	 * In order to use this method, developers should be familiar with the HylaFAX client protocol.
	 * @exception ServerResponseException the server responded with an error code.  This is likely a protocol violation.
	 * @exception IOException an i/o error occured
	 */
	public void setProperty(String parameter, String value)
			throws ServerResponseException, IOException {
		client.jparm(parameter, value);
	}// setProperty

	/**
	 * Set any arbitrary property on this job to an integer value.
	 * In order to use this method, developers should be familiar with the HylaFAX client protocol.
	 * @exception ServerResponseException the server responded with an error code.  This is likely a protocol violation.
	 * @exception IOException an i/o error occured
	 */
	public void setProperty(String property, int value)
			throws ServerResponseException, IOException {
		setProperty(property, (new Integer(value)).toString());
	}// setProperty

	/* (non-Javadoc)
	 * @see gnu.hylafax.Job#setSendTime(java.util.Date)
	 */
	public void setSendTime(Date sendTime) throws ServerResponseException,
			IOException {
		// Format the date using the GMT timezone and Hylafax date format for SENDTIME.
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		setProperty("SENDTIME", df.format(sendTime));
	}

	/* (non-Javadoc)
	* @see gnu.hylafax.Job#setSendTime(java.lang.String)
	*/
	public void setSendTime(String sendTime) throws ServerResponseException,
			IOException {
		setSendTime(sendTime, Locale.getDefault(), TimeZone.getDefault());
	}

	public void setSendTime(String sendTime, Locale locale, TimeZone timeZone)
			throws ServerResponseException, IOException {
		String time;
		try {
			time = new TimeParser(locale, timeZone).getSendTime(sendTime);
		} catch (ParseException e) {
			time = sendTime;
		}
		setProperty("SENDTIME", time);
	}

}// Job class

// Job.java
