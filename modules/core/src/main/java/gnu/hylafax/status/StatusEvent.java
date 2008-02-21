/*******************************************************************************
 * $Id$
 * 
 * Copyright 2008, Steven Jardine <steve@mjnservices.com>
 * Copyright 2008, MJN Services, Inc. - http://www.mjnservices.com
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v2.1 which 
 * accompanies this distribution, and is available at
 * 	http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

 * For more information on the HylaFAX Fax Server please see
 * 	HylaFAX  - http://www.hylafax.org or 
 * 	Hylafax+ - http://hylafax.sourceforge.net
 * 
 * Contributors:
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

/**
 * Represents an event received from the hylafax server.
 * 
 * @version $Revision$
 * @author Steven Jardine
 */
public interface StatusEvent {

    public static final int ALL = Integer.MAX_VALUE;

    /**
     * Job Created
     */
    public static final int JOB_CREATE = 0x0001;

    /**
     * Job Suspended
     */
    public static final int JOB_SUSPEND = 0x0002;

    /**
     * Job Ready To Send
     */
    public static final int JOB_READY = 0x0004;

    /**
     * Job Sleeping, Awaiting Time-To-Send
     */
    public static final int JOB_SLEEP = 0x0008;

    /**
     * Job Marked Dead.
     */
    public static final int JOB_DEAD = 0x0010;

    /**
     * Job Processed By Scheduler
     */
    public static final int JOB_PROCESS = 0x0020;

    /**
     * Job Corpus Reaped
     */
    public static final int JOB_REAP = 0x0040;

    /**
     * Job Activated
     */
    public static final int JOB_ACTIVE = 0x0080;

    /**
     * Job Rejected
     */
    public static final int JOB_REJECT = 0x0100;

    /**
     * Job Killed
     */
    public static final int JOB_KILL = 0x0200;

    /**
     * Job Blocked By Other Job
     */
    public static final int JOB_BLOCKED = 0x0400;

    /**
     * Job Delayed By TOD Restriction or Similiar
     */
    public static final int JOB_DELAYED = 0x0800;

    /**
     * Job Parameters Altered
     * 
     * @deprecated
     */
    public static final int JOB_ALTERED = 0x1000;

    /**
     * Job Kill Timer Expired
     */
    public static final int JOB_TIMEDOUT = 0x2000;

    /**
     * Job Preparation Started
     */
    public static final int JOB_PREP_BEGIN = 0x4000;

    /**
     * Job Preparation Finished
     */
    public static final int JOB_PREP_END = 0x8000;

    /**
     * Fax, Send Attempt Started
     */
    public static final int SEND_BEGIN = 0x0001;

    /**
     * Fax, Call Placed
     */
    public static final int SEND_CALL = 0x0002;

    /**
     * Fax, Call Answered By Remote Fax
     */
    public static final int SEND_CONNECTED = 0x0004;

    /**
     * Fax, Page Transmit Done
     */
    public static final int SEND_PAGE = 0x0008;

    /**
     * Fax, Document Transmit Done
     */
    public static final int SEND_DOC = 0x0010;

    /**
     * Fax, Document Retrieved by Poll Operation
     */
    public static final int SEND_POLLRCVD = 0x0020;

    /**
     * Fax, Poll Operation Completed
     */
    public static final int SEND_POLLDONE = 0x0040;

    /**
     * Fax, Send Attempt Finished
     */
    public static final int SEND_END = 0x0080;

    /**
     * Fax, Job Being Reformatted
     */
    public static final int SEND_REFORMAT = 0x0100;

    /**
     * Fax, Job Requeued
     */
    public static final int SEND_REQUEUE = 0x0200;

    /**
     * Fax, Send Job Done
     */
    public static final int SEND_DONE = 0x0400;

    /**
     * Fax, Inbound Call Started
     */
    public static final int RECV_BEGIN = 0x0001;

    /**
     * Fax, Session Started
     */
    public static final int RECV_START = 0x0002;

    /**
     * Fax, Page Receive Done
     */
    public static final int RECV_PAGE = 0x0004;

    /**
     * Fax, Document Receive Done
     */
    public static final int RECV_DOC = 0x0008;

    /**
     * Fax, Inbound Call Finished
     */
    public static final int RECV_END = 0x0010;

    /**
     * Modem Assigned To Job
     */
    public static final int MODEM_ASSIGN = 0x0001;

    /**
     * Modem Released By Job
     */
    public static final int MODEM_RELEASE = 0x0002;

    /**
     * Modem Marked Down
     */
    public static final int MODEM_DOWN = 0x0004;

    /**
     * Modem Marked Ready
     */
    public static final int MODEM_READY = 0x0008;

    /**
     * Modem Marked Busy
     */
    public static final int MODEM_BUSY = 0x0010;

    /**
     * Modem Considered Wedged
     */
    public static final int MODEM_WEDGED = 0x0020;

    /**
     * Modem In-Use for Outbound Work
     */
    public static final int MODEM_INUSE = 0x0040;

    /**
     * Inbound Data Call Begun
     */
    public static final int MODEM_DATA_BEGIN = 0x0080;

    /**
     * Inbound Data Call Finished
     */
    public static final int MODEM_DATA_END = 0x0100;

    /**
     * Inbound Voice Call Begun
     */
    public static final int MODEM_VOICE_BEGIN = 0x0200;

    /**
     * Inbound Voice Call Finished
     */
    public static final int MODEM_VOICE_END = 0x0400;

    /**
     * Inbound Caller-ID Information
     */
    public static final int MODEM_CID = 0x0800;

}
