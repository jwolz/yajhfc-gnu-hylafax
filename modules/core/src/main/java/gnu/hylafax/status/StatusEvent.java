// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: StatusEvent.java 1 Feb 18, 2008 steve $
// ==============================================================================
package gnu.hylafax.status;

/**
 * Represents an event received from the hylafax server.
 *  
 * @version $Id: StatusEvent.java 1 Feb 18, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 */
public interface StatusEvent {

    // job events

    public static final int JOB_CREATE = 0x0001; // jobcreated

    public static final int JOB_SUSPEND = 0x0002; // jobsuspended

    public static final int JOB_READY = 0x0004; // jobready to send

    public static final int JOB_SLEEP = 0x0008; // jobsleeping awaiting time-to-send

    public static final int JOB_DEAD = 0x0010; // jobmarked dead

    public static final int JOB_PROCESS = 0x0020; // jobprocessed by scheduler

    public static final int JOB_REAP = 0x0040; // jobcorpus reaped

    public static final int JOB_ACTIVE = 0x0080; // jobactivated

    public static final int JOB_REJECT = 0x0100; // jobrejected

    public static final int JOB_KILL = 0x0200; // jobkilled

    public static final int JOB_BLOCKED = 0x0400; // jobblocked by other job

    public static final int JOB_DELAYED = 0x0800; // jobdelayed by tod restriction or similar

    /**
     * @deprecated
     */
    public static final int JOB_ALTERED = 0x1000; // jobparameters altered

    public static final int JOB_TIMEDOUT = 0x2000; // jobkill timer expired

    public static final int JOB_PREP_BEGIN = 0x4000; // jobpreparation started

    public static final int JOB_PREP_END = 0x8000; // jobpreparation finished

    // send events

    public static final int SEND_BEGIN = 0x0001; // fax, send attempt started

    public static final int SEND_CALL = 0x0002; // fax, call placed

    public static final int SEND_CONNECTED = 0x0004; // fax, call answered by fax

    public static final int SEND_PAGE = 0x0008; // fax, page transmit done

    public static final int SEND_DOC = 0x0010; // fax, document transmit done

    public static final int SEND_POLLRCVD = 0x0020; // fax, document retrieved by poll operation

    public static final int SEND_POLLDONE = 0x0040; // fax, poll operation completed

    public static final int SEND_END = 0x0080; // fax, send attempt finished

    public static final int SEND_REFORMAT = 0x0100; // fax, job being reformatted

    public static final int SEND_REQUEUE = 0x0200; // fax, job requeued

    public static final int SEND_DONE = 0x0400; // fax, send job done

    // receive events

    public static final int RECV_BEGIN = 0x0001; // fax, inbound call started

    public static final int RECV_START = 0x0002; // fax, session started

    public static final int RECV_PAGE = 0x0004; // fax, page receive done

    public static final int RECV_DOC = 0x0008; // fax, document receive done

    public static final int RECV_END = 0x0010; // fax, inbound call finished

    // modem events

    public static final int MODEM_ASSIGN = 0x0001; // modem assigned to job

    public static final int MODEM_RELEASE = 0x0002; // modem released by job

    public static final int MODEM_DOWN = 0x0004; // modem marked down

    public static final int MODEM_READY = 0x0008; // modem marked ready

    public static final int MODEM_BUSY = 0x0010; // modem marked busy

    public static final int MODEM_WEDGED = 0x0020; // modem considered wedged

    public static final int MODEM_INUSE = 0x0040; // modem inuse for outbound work

    public static final int MODEM_DATA_BEGIN = 0x0080; // inbound data call begun

    public static final int MODEM_DATA_END = 0x0100; // inbound data call finished

    public static final int MODEM_VOICE_BEGIN = 0x0200; // inbound voice call begun

    public static final int MODEM_VOICE_END = 0x0400; // inbound voice call finished

    public static final int MODEM_CID = 0x0800; // inbound caller-ID information

}
