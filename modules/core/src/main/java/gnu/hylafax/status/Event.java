/*******************************************************************************
 * $Id: StatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
 * 
 * Copyright 2008, Steven Jardine <steve@mjnservices.com>
 * Copyright 2008, MJN Services, Inc. - http://www.mjnservices.com
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
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents an event bit for a bitmask of the desired events to receive from
 * the hylafax server.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine
 */
public class Event {

    public static final int ALL = 0xffff;

    public static final int MODEM = 0x0001;

    public static final int SEND = 0x0002;

    public static final int RECEIVE = 0x0004;

    public static final int JOB = 0x0008;

    private static final Log log = LogFactory.getLog(Event.class);

    private int code;
    private int mask;
    private String name;
    private int type;

    private Event(String name, int type, final int code, final int mask) {
	this.name = name;
	this.type = type;
	this.code = code;
	this.mask = mask;
    }

    public StatusEvent createStatusEvent(String serverStr) {
	switch (type) {
	case JOB:
	    return new JobStatusEvent(this, serverStr);
	case SEND:
	    return new SendStatusEvent(this, serverStr);
	case RECEIVE:
	    return new ReceiveStatusEvent(this, serverStr);
	case MODEM:
	    return new ModemStatusEvent(this, serverStr);
	default:
	    return null;
	}
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final Event other = (Event) obj;
	if (code != other.code)
	    return false;
	if (mask != other.mask)
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (type != other.type)
	    return false;
	return true;
    }

    public int getCode() {
	return code;
    }

    public static Event getEvent(int eventId) {
	try {
	    Field[] fields = Event.class.getDeclaredFields();
	    for (int index = 0; index < fields.length; index++) {
		Field field = fields[index];
		if (field.getType() == Event.class) {
		    Event event = (Event) field.get(null);
		    if (event.getCode() == eventId) {
			return event;
		    }
		}
	    }
	} catch (Exception e) {
	    // This shouldn't happen.
	    log.fatal(e.getMessage(), e);
	}
	return null;
    }

    public int getMask() {
	return mask;
    }

    public String getName() {
	return name;
    }

    public int getType() {
	return type;
    }

    public String getTypeStr() {
	if (type == JOB)
	    return "JOB";
	if (type == SEND)
	    return "SEND";
	if (type == RECEIVE)
	    return "RECEIVE";
	if (type == MODEM)
	    return "MODEM";
	return null;
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + code;
	result = prime * result + mask;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + type;
	return result;
    }

    static final int JOB_BASE = 100;

    static final int SEND_BASE = JOB_BASE + 16;

    static final int RECV_BASE = JOB_BASE + 32;

    static final int MODEM_BASE = JOB_BASE + 48;

    /**
     * Job Created
     */
    public static final Event JOB_CREATE = new Event("JOB_CREATE", JOB,
	    JOB_BASE + 0, 0x0001);

    /**
     * Job Suspended
     */
    public static final Event JOB_SUSPEND = new Event("JOB_SUSPEND", JOB,
	    JOB_BASE + 1, 0x0002);

    /**
     * Job Ready To Send
     */
    public static final Event JOB_READY = new Event("JOB_READY", JOB,
	    JOB_BASE + 2, 0x0004);

    /**
     * Job Sleeping, Awaiting Time-To-Send
     */
    public static final Event JOB_SLEEP = new Event("JOB_SLEEP", JOB,
	    JOB_BASE + 3, 0x0008);

    /**
     * Job Marked Dead.
     */
    public static final Event JOB_DEAD = new Event("JOB_DEAD", JOB,
	    JOB_BASE + 4, 0x0010);

    /**
     * Job Processed By Scheduler
     */
    public static final Event JOB_PROCESS = new Event("JOB_PROCESS", JOB,
	    JOB_BASE + 5, 0x0020);

    /**
     * Job Corpus Reaped
     */
    public static final Event JOB_REAP = new Event("JOB_REAP", JOB,
	    JOB_BASE + 6, 0x0040);

    /**
     * Job Activated
     */
    public static final Event JOB_ACTIVE = new Event("JOB_ACTIVE", JOB,
	    JOB_BASE + 7, 0x0080);

    /**
     * Job Rejected
     */
    public static final Event JOB_REJECT = new Event("JOB_REJECT", JOB,
	    JOB_BASE + 8, 0x0100);

    /**
     * Job Killed
     */
    public static final Event JOB_KILL = new Event("JOB_KILL", JOB,
	    JOB_BASE + 9, 0x0200);

    /**
     * Job Blocked By Other Job
     */
    public static final Event JOB_BLOCKED = new Event("JOB_BLOCKED", JOB,
	    JOB_BASE + 10, 0x0400);

    /**
     * Job Delayed By TOD Restriction or Similiar
     */
    public static final Event JOB_DELAYED = new Event("JOB_DELAYED", JOB,
	    JOB_BASE + 11, 0x0800);

    /**
     * Job Parameters Altered
     */
    public static final Event JOB_ALTERED = new Event("JOB_ALTERED", JOB,
	    JOB_BASE + 12, 0x1000);

    /**
     * Job Kill Timer Expired
     */
    public static final Event JOB_TIMEDOUT = new Event("JOB_TIMEDOUT", JOB,
	    JOB_BASE + 13, 0x2000);

    /**
     * Job Preparation Started
     */
    public static final Event JOB_PREP_BEGIN = new Event("JOB_PREP_BEGIN", JOB,
	    JOB_BASE + 14, 0x4000);

    /**
     * Job Preparation Finished
     */
    public static final Event JOB_PREP_END = new Event("JOB_PREP_END", JOB,
	    JOB_BASE + 15, 0x8000);

    /**
     * Fax, Send Attempt Started
     */
    public static final Event SEND_BEGIN = new Event("SEND_BEGIN", SEND,
	    SEND_BASE + 0, 0x0001);

    /**
     * Fax, Call Placed
     */
    public static final Event SEND_CALL = new Event("SEND_CALL", SEND,
	    SEND_BASE + 1, 0x0002);

    /**
     * Fax, Call Answered By Remote Fax
     */
    public static final Event SEND_CONNECTED = new Event("SEND_CONNECTED",
	    SEND, SEND_BASE + 2, 0x0004);

    /**
     * Fax, Page Transmit Done
     */
    public static final Event SEND_PAGE = new Event("SEND_PAGE", SEND,
	    SEND_BASE + 3, 0x0008);

    /**
     * Fax, Document Transmit Done
     */
    public static final Event SEND_DOC = new Event("SEND_DOC", SEND,
	    SEND_BASE + 4, 0x0010);

    /**
     * Fax, Document Retrieved by Poll Operation
     */
    public static final Event SEND_POLLRCVD = new Event("SEND_POLLRCVD", SEND,
	    SEND_BASE + 5, 0x0020);

    /**
     * Fax, Poll Operation Completed
     */
    public static final Event SEND_POLLDONE = new Event("SEND_POLLDONE", SEND,
	    SEND_BASE + 6, 0x0040);

    /**
     * Fax, Send Attempt Finished
     */
    public static final Event SEND_END = new Event("SEND_END", SEND,
	    SEND_BASE + 7, 0x0080);

    /**
     * Fax, Job Being Reformatted
     */
    public static final Event SEND_REFORMAT = new Event("SEND_REFORMAT", SEND,
	    SEND_BASE + 8, 0x0100);

    /**
     * Fax, Job Requeued
     */
    public static final Event SEND_REQUEUE = new Event("SEND_REQUEUE", SEND,
	    SEND_BASE + 9, 0x0200);

    /**
     * Fax, Send Job Done
     */
    public static final Event SEND_DONE = new Event("SEND_DONE", SEND,
	    SEND_BASE + 10, 0x0400);

    /**
     * Fax, Inbound Call Started
     */
    public static final Event RECV_BEGIN = new Event("RECV_BEGIN", RECEIVE,
	    RECV_BASE + 0, 0x0001);

    /**
     * Fax, Session Started
     */
    public static final Event RECV_START = new Event("RECV_START", RECEIVE,
	    RECV_BASE + 1, 0x0002);

    /**
     * Fax, Page Receive Done
     */
    public static final Event RECV_PAGE = new Event("RECV_PAGE", RECEIVE,
	    RECV_BASE + 2, 0x0004);

    /**
     * Fax, Document Receive Done
     */
    public static final Event RECV_DOC = new Event("RECV_DOC", RECEIVE,
	    RECV_BASE + 3, 0x0008);

    /**
     * Fax, Inbound Call Finished
     */
    public static final Event RECV_END = new Event("RECV_END", RECEIVE,
	    RECV_BASE + 4, 0x0010);

    /**
     * Modem Assigned To Job
     */
    public static final Event MODEM_ASSIGN = new Event("MODEM_ASSIGN", MODEM,
	    MODEM_BASE + 0, 0x0001);

    /**
     * Modem Released By Job
     */
    public static final Event MODEM_RELEASE = new Event("MODEM_RELEASE", MODEM,
	    MODEM_BASE + 1, 0x0002);

    /**
     * Modem Marked Down
     */
    public static final Event MODEM_DOWN = new Event("MODEM_DOWN", MODEM,
	    MODEM_BASE + 2, 0x0004);

    /**
     * Modem Marked Ready
     */
    public static final Event MODEM_READY = new Event("MODEM_READY", MODEM,
	    MODEM_BASE + 3, 0x0008);

    /**
     * Modem Marked Busy
     */
    public static final Event MODEM_BUSY = new Event("MODEM_BUSY", MODEM,
	    MODEM_BASE + 4, 0x0010);

    /**
     * Modem Considered Wedged
     */
    public static final Event MODEM_WEDGED = new Event("MODEM_WEDGED", MODEM,
	    MODEM_BASE + 5, 0x0020);

    /**
     * Modem In-Use for Outbound Work
     */
    public static final Event MODEM_INUSE = new Event("MODEM_INUSE", MODEM,
	    MODEM_BASE + 6, 0x0040);

    /**
     * Inbound Data Call Begun
     */
    public static final Event MODEM_DATA_BEGIN = new Event("MODEM_DATA_BEGIN",
	    MODEM, MODEM_BASE + 7, 0x0080);

    /**
     * Inbound Data Call Finished
     */
    public static final Event MODEM_DATA_END = new Event("MODEM_DATA_END",
	    MODEM, MODEM_BASE + 8, 0x0100);

    /**
     * Inbound Voice Call Begun
     */
    public static final Event MODEM_VOICE_BEGIN = new Event(
	    "MODEM_VOICE_BEGIN", MODEM, MODEM_BASE + 9, 0x0200);

    /**
     * Inbound Voice Call Finished
     */
    public static final Event MODEM_VOICE_END = new Event("MODEM_VOICE_END",
	    MODEM, MODEM_BASE + 10, 0x0400);

    /**
     * Inbound Caller-ID Information
     */
    public static final Event MODEM_CID = new Event("MODEM_CID", MODEM,
	    MODEM_BASE + 11, 0x0800);

}
