/*******************************************************************************
 * $Id$
 * 
 * Copyright 2001-2003 Innovation Software Group, LLC - http://www.innovationsw.com
 * Copyright 2001-2003 Joe Phillips <jaiger@innovationsw.com>
 * Copyright 2008 Steven Jardine, MJN Services, Inc. <steve@mjnservices.com>
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v2.1 which 
 * accompanies this distribution, and is available at
 * 	http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * For more information on the HylaFAX Fax Server please see
 * 	HylaFAX  - http://www.hylafax.org or 
 * 	Hylafax+ - http://hylafax.sourceforge.net
 * 
 * Contributors:
 * 	Joe Phillips - Initial API and implementation
 * 	Steven Jardine - Code formatting, rework of license header, javadoc 
 ******************************************************************************/
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
 * 
 * @version $Revision$
 * @author Joe Phillips <jaiger@innovationsw.com>
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class Job implements gnu.hylafax.Job {

    public static String CHOP_DEFAULT = "default";

    public static String NOTIFY_ALL = ClientProtocol.NOTIFY_ALL;

    public static String NOTIFY_DONE = ClientProtocol.NOTIFY_DONE;

    public static String NOTIFY_NONE = ClientProtocol.NOTIFY_NONE;

    public static String NOTIFY_REQUEUE = ClientProtocol.NOTIFY_REQUEUE;

    public static int PRIORITY_BULK = 207;

    public static int PRIORITY_HIGH = 63;

    public static int PRIORITY_NORMAL = 127;

    public static int RESOLUTION_LOW = 98;

    public static int RESOLUTION_MEDIUM = 196;

    protected Client client;

    private long Id;

    /**
     * Creates a new job. All job parameters are inherited from the default job.
     * 
     * @param c
     *            the client to use to create the new job.
     * @throws ServerResponseException
     * @throws IOException
     */
    public Job(Client c) throws ServerResponseException, IOException {
	this(c, true);
    }

    /**
     * Creates a new job.
     * 
     * @param c
     *            the client to use to create the new job.
     * @param inheritDefault
     *            inherit job parameters from the default job on the hylafax
     *            server.
     * @throws ServerResponseException
     * @throws IOException
     */
    public Job(Client c, boolean inheritDefault)
	    throws ServerResponseException, IOException {
	synchronized (c) {
	    client = c;
	    client.jnew(inheritDefault);
	    Id = client.job();
	}
    }

    /**
     * Gets a job from the hylafax server.
     * 
     * @param c
     *            the client to use to create the new job.
     * @param id
     *            the job id to retrieve from the hylafax server.
     * @throws ServerResponseException
     * @throws IOException
     */
    public Job(Client c, long id) throws ServerResponseException, IOException {
	synchronized (c) {
	    client = c;
	    client.job(id);
	    Id = client.job();
	}
    }

    public void addDocument(String value) throws ServerResponseException,
	    IOException {
	setProperty("DOCUMENT", value);
    }

    public int getChopThreshold() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("CHOPTHRESHOLD"));
    }

    public String getDialstring() throws ServerResponseException, IOException {
	return getProperty("DIALSTRING");
    }

    public String getDocumentName() throws ServerResponseException, IOException {
	return getProperty("DOCUMENT");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Job#getFromUser()
     */
    public String getFromUser() throws ServerResponseException, IOException {
	return getProperty("FROMUSER");
    }

    /**
     * get the job-id of this Job instance.
     * 
     * @return job id
     */
    public long getId() {
	return Id;
    }

    /**
     * Get JobInfo attribute. JobInfo is an identifying string associated with
     * the job.
     */
    public String getJobInfo() throws ServerResponseException, IOException {
	return getProperty("JOBINFO");
    }

    public String getKilltime() throws ServerResponseException, IOException {
	return getProperty("LASTTIME");
    }

    public int getMaximumDials() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("MAXDIALS"));
    }

    public int getMaximumTries() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("MAXTRIES"));
    }

    public String getNotifyAddress() throws ServerResponseException,
	    IOException {
	return getProperty("NOTIFYADDR");
    }

    public String getNotifyType() throws ServerResponseException, IOException {
	return getProperty("NOTIFY");
    }

    public String getPageChop() throws ServerResponseException, IOException {
	return getProperty("PAGECHOP");
    }

    public Dimension getPageDimension() throws ServerResponseException,
	    IOException {
	return new Dimension(getPageWidth(), getPageLength());
    }

    public int getPageLength() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("PAGELENGTH"));
    }

    public int getPageWidth() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("PAGEWIDTH"));
    }

    public int getPriority() throws ServerResponseException, IOException {
	return Integer.parseInt(getProperty("SCHEDPRI"));
    }

    /**
     * Get the value for an arbitrary property for this job. Developers using
     * this method should be familiar with the HylaFAX client protocol in order
     * to provide the correct key values and how to interpret the values
     * returned.
     * 
     * @exception ServerResponseException
     *                the server responded with an error. This is likely due to
     *                a protocol error.
     * @exception IOException
     *                an i/o error occured
     * @return a String value for the given property key
     */
    public String getProperty(String key) throws ServerResponseException,
	    IOException {
	String tmp = client.jparm(key);
	return tmp;
    }

    public String getRetrytime() throws ServerResponseException, IOException {
	return getProperty("RETRYTIME");
    }

    /**
     * Get TagLine format attribute. The TagLine
     */
    public String getTagline() throws ServerResponseException, IOException {
	return getProperty("TAGLINE");
    }

    /**
     * Get the UseTagLine attribute. The TagLine
     */
    public boolean getUseTagline() throws ServerResponseException, IOException {
	return ("YES".equalsIgnoreCase(getProperty("USETAGLINE")) ? true
		: false);
    }

    public int getVerticalResolution() throws ServerResponseException,
	    IOException {
	return Integer.parseInt(getProperty("VRES"));
    }

    public void setChopThreshold(int value) throws ServerResponseException,
	    IOException {
	setProperty("CHOPTHRESHOLD", value);
    }

    public void setDialstring(String value) throws ServerResponseException,
	    IOException {
	setProperty("DIALSTRING", value);
    }

    public void setFromUser(String value) throws ServerResponseException,
	    IOException {
	setProperty("FROMUSER", value);
    }

    /**
     * Set the JobInfo attribute. This is an identifying string associated with
     * each job.
     */
    public void setJobInfo(String value) throws ServerResponseException,
	    IOException {
	setProperty("JOBINFO", value);
    }

    public void setKilltime(String value) throws ServerResponseException,
	    IOException {
	String time;
	try {
	    time = new TimeParser().getKillTime(value);
	} catch (ParseException e) {
	    time = value;
	}
	setProperty("LASTTIME", time);
    }

    public void setMaximumDials(int value) throws ServerResponseException,
	    IOException {
	setProperty("MAXDIALS", value);
    }

    public void setMaximumTries(int value) throws ServerResponseException,
	    IOException {
	setProperty("MAXTRIES", value);
    }

    public void setNotifyAddress(String value) throws ServerResponseException,
	    IOException {
	setProperty("NOTIFYADDR", value);
    }

    /**
     * set the notification type. For possible values, see the NOTIFY_* members
     * of this class.
     * 
     * @param value
     *            the new notification type
     * @exception ServerResponseException
     *                the server responded with an error. This is likely a
     *                protocol violation.
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void setNotifyType(String value) throws ServerResponseException,
	    IOException {
	setProperty("NOTIFY", value);
    }

    public void setPageChop(String value) throws ServerResponseException,
	    IOException {
	setProperty("PAGECHOP", value);
    }

    public void setPageDimension(Dimension value)
	    throws ServerResponseException, IOException {
	setPageWidth((int) value.getWidth());
	setPageLength((int) value.getHeight());
    }

    public void setPageLength(int length) throws ServerResponseException,
	    IOException {
	setProperty("PAGELENGTH", length);
    }

    public void setPageWidth(int width) throws ServerResponseException,
	    IOException {
	setProperty("PAGEWIDTH", width);
    }

    public void setPriority(int value) throws ServerResponseException,
	    IOException {
	setProperty("SCHEDPRI", value);
    }

    /**
     * Set any arbitrary property on this job to an integer value. In order to
     * use this method, developers should be familiar with the HylaFAX client
     * protocol.
     * 
     * @exception ServerResponseException
     *                the server responded with an error code. This is likely a
     *                protocol violation.
     * @exception IOException
     *                an i/o error occured
     */
    public void setProperty(String property, int value)
	    throws ServerResponseException, IOException {
	setProperty(property, (new Integer(value)).toString());
    }

    /**
     * Set any arbitrary property on this job. In order to use this method,
     * developers should be familiar with the HylaFAX client protocol.
     * 
     * @exception ServerResponseException
     *                the server responded with an error code. This is likely a
     *                protocol violation.
     * @exception IOException
     *                an i/o error occured
     */
    public void setProperty(String parameter, String value)
	    throws ServerResponseException, IOException {
	client.jparm(parameter, value);
    }

    public void setRetrytime(String value) throws ServerResponseException,
	    IOException {
	setProperty("RETRYTIME", value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Job#setSendTime(java.util.Date)
     */
    public void setSendTime(Date sendTime) throws ServerResponseException,
	    IOException {
	// Format the date using the GMT timezone and Hylafax date format for
	// SENDTIME.
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	df.setTimeZone(TimeZone.getTimeZone("GMT"));
	setProperty("SENDTIME", df.format(sendTime));
    }

    /*
     * (non-Javadoc)
     * 
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

    /**
     * Set the TagLine format attribute. This property specifies the format of
     * the tagline rendered at the top of each page of the transmitted FAX.
     * Tagline format strings are documented in config(5F). If you use this, you
     * will probably want to use setUseTagline()
     */
    public void setTagline(String value) throws ServerResponseException,
	    IOException {
	setProperty("TAGLINE", value);
    }

    public void setUseTagline(boolean value) throws ServerResponseException,
	    IOException {
	setProperty("USETAGLINE", (value ? "YES" : "NO"));
    }

    public void setVerticalResolution(int value)
	    throws ServerResponseException, IOException {
	setProperty("VRES", value);
    }

}
