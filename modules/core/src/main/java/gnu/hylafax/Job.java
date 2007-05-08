// Job.java - a HylaFAX Job representation
// $Id: Job.java,v 1.5 2006/02/20 04:52:11 sjardine Exp $
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
package gnu.hylafax;

// system includes
import java.awt.Dimension;
import java.io.*;
import java.util.*;

// home-grown includes
import gnu.inet.ftp.ServerResponseException;

/**
 * Defines a FAX Job.
 * @see gnu.hylafax.ClientProtocol
 * @see gnu.hylafax.Client
 */
public interface Job {
    
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

    public String getFromUser() throws ServerResponseException, IOException;

    public String getKilltime() throws ServerResponseException, IOException;

    public int getMaximumDials() throws ServerResponseException, IOException;

    public int getMaximumTries() throws ServerResponseException, IOException;

    public int getPriority() throws ServerResponseException, IOException;

    public String getDialstring() throws ServerResponseException, IOException;

    public String getNotifyAddress() throws ServerResponseException, IOException;

    public int getVerticalResolution() throws ServerResponseException, IOException;

    public Dimension getPageDimension() throws ServerResponseException, IOException;

    public int getPageWidth() throws ServerResponseException, IOException;

    public int getPageLength() throws ServerResponseException, IOException;

    public String getNotifyType() throws ServerResponseException, IOException;

    public String getPageChop() throws ServerResponseException, IOException;

    public int getChopThreshold() throws ServerResponseException, IOException;

    public String getDocumentName() throws ServerResponseException, IOException;

    public String getRetrytime() throws ServerResponseException, IOException;

    /**
     * Get JobInfo attribute. JobInfo is an identifying string associated with the job.
     */
    public String getJobInfo() throws ServerResponseException, IOException;

    /**
     * Get TagLine format attribute. The TagLine
     */
    public String getTagline() throws ServerResponseException, IOException;

    /**
     * Get the UseTagLine attribute. The TagLine
     */
    public boolean getUseTagline() throws ServerResponseException, IOException;

    /**
     * Get the value for an arbitrary property for this job. Developers using this method should be familiar
     * with the HylaFAX client protocol in order to provide the correct key values and how to interpret the
     * values returned. This method is thread-safe.
     * @exception ServerResponseException the server responded with an error. This is likely due to a protocol
     *                error.
     * @exception IOException an i/o error occured
     * @return a String value for the given property key
     */
    public String getProperty(String key) throws ServerResponseException, IOException;

    /**
     * get the job-id of this Job instance.
     * @return job id
     */
    public long getId();

    public void setFromUser(String value) throws ServerResponseException, IOException;

    public void setKilltime(String value) throws ServerResponseException, IOException;

    public void setMaximumDials(int value) throws ServerResponseException, IOException;

    public void setMaximumTries(int value) throws ServerResponseException, IOException;

    public void setPriority(int value) throws ServerResponseException, IOException;

    public void setDialstring(String value) throws ServerResponseException, IOException;

    public void setNotifyAddress(String value) throws ServerResponseException, IOException;

    public void setVerticalResolution(int value) throws ServerResponseException, IOException;

    /**
     * Set the job's pagesize.
     * @see Pagesize for common pagesizes
     */
    public void setPageDimension(Dimension value) throws ServerResponseException, IOException;

    public void setPageWidth(int width) throws ServerResponseException, IOException;

    public void setPageLength(int length) throws ServerResponseException, IOException;

    /**
     * set the notification type. For possible values, see the NOTIFY_* members of this class.
     * @param value the new notification type
     * @exception ServerResponseException the server responded with an error. This is likely a protocol
     *                violation.
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void setNotifyType(String value) throws ServerResponseException, IOException;

    public void setPageChop(String value) throws ServerResponseException, IOException;

    public void setChopThreshold(int value) throws ServerResponseException, IOException;

    public void addDocument(String value) throws ServerResponseException, IOException;

    public void setRetrytime(String value) throws ServerResponseException, IOException;

    /**
     * Set the JobInfo attribute. This is an identifying string associated with each job.
     */
    public void setJobInfo(String value) throws ServerResponseException, IOException;

    /**
     * Set the TagLine format attribute. This property specifies the format of the tagline rendered at the top
     * of each page of the transmitted FAX. Tagline format strings are documented in config(5F). If you use
     * this, you will probably want to use setUseTagline()
     */
    public void setTagline(String value) throws ServerResponseException, IOException;

    /**
     * Set the UseTagLine format attribute. This is an
     */
    public void setUseTagline(boolean f) throws ServerResponseException, IOException;

    /**
     * Set any arbitrary property on this job. In order to use this method, developers should be familiar with
     * the HylaFAX client protocol. This method is thread-safe.
     * @exception ServerResponseException the server responded with an error code. This is likely a protocol
     *                violation.
     * @exception IOException an i/o error occured
     */
    public void setProperty(String parameter, String value) throws ServerResponseException, IOException;

    /**
     * Set any arbitrary property on this job to an integer value. In order to use this method, developers
     * should be familiar with the HylaFAX client protocol. This method is thread-safe.
     * @exception ServerResponseException the server responded with an error code. This is likely a protocol
     *                violation.
     * @exception IOException an i/o error occured
     */
    public void setProperty(String property, int value) throws ServerResponseException, IOException;

    /**
     * Set the SENDTIME attribute. This allows for queuing up faxes and sending them at a specific time.
     * @param sendTime
     * @throws ServerResponseException the server responded with an error code. This is likely a protocol
     *             violation.
     * @throws IOException an i/o error occured
     */
    public void setSendTime(Date sendTime) throws ServerResponseException, IOException;

    /**
     * Set the SENDTIME attribute. Must be in the format yyyyMMddHHmm and in GMT time. This allows for queuing
     * up faxes and sending them at a specific time.
     * @param sendTime
     * @throws ServerResponseException the server responded with an error code. This is likely a protocol
     *             violation.
     * @throws IOException an i/o error occured
     */
    public void setSendTime(String sendTime) throws ServerResponseException, IOException;

}// Job interface
// Job.java
