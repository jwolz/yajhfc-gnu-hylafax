/*******************************************************************************
 * $Id: StatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
 * 
 * Copyright 2008, Steven Jardine <steve@mjnservices.com>
 * Copyright 2008, MJN Services, Inc. - http://www.mjnservices.com
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser  License v2.1 which 
 * accompanies this distribution, and is available at
 * 	http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
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
 * @version $Revision: 84 $
 * @author Steven Jardine
 */
class StatusEventId {

    static final int JOB_BASE = 0;
    static final int SEND_BASE = 16;
    static final int RECV_BASE = 32;
    static final int MODEM_BASE = 48;

    /**
     * Job Created
     */
    static final int JOB_CREATE = JOB_BASE + 0;

    /**
     * Job Suspended
     */
    static final int JOB_SUSPEND = JOB_BASE + 1;

    /**
     * Job Ready To Send
     */
    static final int JOB_READY = JOB_BASE + 2;

    /**
     * Job Sleeping, Awaiting Time-To-Send
     */
    static final int JOB_SLEEP = JOB_BASE + 3;

    /**
     * Job Marked Dead.
     */
    static final int JOB_DEAD = JOB_BASE + 4;

    /**
     * Job Processed By Scheduler
     */
    static final int JOB_PROCESS = JOB_BASE + 5;

    /**
     * Job Corpus Reaped
     */
    static final int JOB_REAP = JOB_BASE + 6;

    /**
     * Job Activated
     */
    static final int JOB_ACTIVE = JOB_BASE + 7;
    /**
     * Job Rejected
     */
    static final int JOB_REJECT = JOB_BASE + 8;

    /**
     * Job Killed
     */
    static final int JOB_KILL = JOB_BASE + 9;

    /**
     * Job Blocked By Other Job
     */
    static final int JOB_BLOCKED = JOB_BASE + 10;

    /**
     * Job Delayed By TOD Restriction or Similiar
     */
    static final int JOB_DELAYED = JOB_BASE + 11;

    /**
     * Job Parameters Altered
     */
    static final int JOB_ALTERED = JOB_BASE + 12;

    /**
     * Job Kill Timer Expired
     */
    static final int JOB_TIMEDOUT = JOB_BASE + 13;

    /**
     * Job Preparation Started
     */
    static final int JOB_PREP_BEGIN = JOB_BASE + 14;

    /**
     * Job Preparation Finished
     */
    static final int JOB_PREP_END = JOB_BASE + 15;

    /**
     * Fax, Send Attempt Started
     */
    static final int SEND_BEGIN = SEND_BASE + 0;

    /**
     * Fax, Call Placed
     */
    static final int SEND_CALL = SEND_BASE + 1;

    /**
     * Fax, Call Answered By Remote Fax
     */
    static final int SEND_CONNECTED = SEND_BASE + 2;

    /**
     * Fax, Page Transmit Done
     */
    static final int SEND_PAGE = SEND_BASE + 3;

    /**
     * Fax, Document Transmit Done
     */
    static final int SEND_DOC = SEND_BASE + 4;

    /**
     * Fax, Document Retrieved by Poll Operation
     */
    static final int SEND_POLLRCVD = SEND_BASE + 5;

    /**
     * Fax, Poll Operation Completed
     */
    static final int SEND_POLLDONE = SEND_BASE + 6;

    /**
     * Fax, Send Attempt Finished
     */
    static final int SEND_END = SEND_BASE + 7;

    /**
     * Fax, Job Being Reformatted
     */
    static final int SEND_REFORMAT = SEND_BASE + 8;

    /**
     * Fax, Job Requeued
     */
    static final int SEND_REQUEUE = SEND_BASE + 9;

    /**
     * Fax, Send Job Done
     */
    static final int SEND_DONE = SEND_BASE + 10;

    /**
     * Fax, Inbound Call Started
     */
    static final int RECV_BEGIN = RECV_BASE + 0;

    /**
     * Fax, Session Started
     */
    static final int RECV_START = RECV_BASE + 1;

    /**
     * Fax, Page Receive Done
     */
    static final int RECV_PAGE = RECV_BASE + 2;

    /**
     * Fax, Document Receive Done
     */
    static final int RECV_DOC = RECV_BASE + 3;

    /**
     * Fax, Inbound Call Finished
     */
    static final int RECV_END = RECV_BASE + 4;

    /**
     * Modem Assigned To Job
     */
    static final int MODEM_ASSIGN = MODEM_BASE + 0;

    /**
     * Modem Released By Job
     */
    static final int MODEM_RELEASE = MODEM_BASE + 1;

    /**
     * Modem Marked Down
     */
    static final int MODEM_DOWN = MODEM_BASE + 2;

    /**
     * Modem Marked Ready
     */
    static final int MODEM_READY = MODEM_BASE + 3;

    /**
     * Modem Marked Busy
     */
    static final int MODEM_BUSY = MODEM_BASE + 4;

    /**
     * Modem Considered Wedged
     */
    static final int MODEM_WEDGED = MODEM_BASE + 5;

    /**
     * Modem In-Use for Outbound Work
     */
    static final int MODEM_INUSE = MODEM_BASE + 6;

    /**
     * Inbound Data Call Begun
     */
    static final int MODEM_DATA_BEGIN = MODEM_BASE + 7;

    /**
     * Inbound Data Call Finished
     */
    static final int MODEM_DATA_END = MODEM_BASE + 7;

    /**
     * Inbound Voice Call Begun
     */
    static final int MODEM_VOICE_BEGIN = MODEM_BASE + 9;

    /**
     * Inbound Voice Call Finished
     */
    static final int MODEM_VOICE_END = MODEM_BASE + 10;

    /**
     * Inbound Caller-ID Information
     */
    static final int MODEM_CID = MODEM_BASE + 11;

}
