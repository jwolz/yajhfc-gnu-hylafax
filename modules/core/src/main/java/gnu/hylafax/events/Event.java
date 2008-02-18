package gnu.hylafax.events;

public class Event {

    // JOB EVENTS

    public static final int JOB_ACTIVE = 0x0080; // job activated

    public static final int JOB_BLOCKED = 0x0400; // job blocked by other job

    public static final int JOB_CREATE = 0x0001; // job created

    public static final int JOB_DEAD = 0x0010; // job marked dead

    public static final int JOB_DELAYED = 0x0800; // job delayed by tod restriction or similar

    public static final int JOB_KILL = 0x0200; // job killed

    public static final int JOB_PREP_BEGIN = 0x4000; // job preparation started

    public static final int JOB_PREP_END = 0x8000; // job preparation finished

    public static final int JOB_PROCESS = 0x0020; // job processed by scheduler

    public static final int JOB_READY = 0x0004; // job ready to send

    public static final int JOB_REAP = 0x0040; // job corpus reaped

    public static final int JOB_REJECT = 0x0100; // job rejected

    public static final int JOB_SLEEP = 0x0008; // job sleeping awaiting time-to-send

    public static final int JOB_SUSPEND = 0x0002; // job suspended

    public static final int JOB_TIMEDOUT = 0x2000; // job kill timer expired

    // MODEM EVENTS

    public static final int MODEM_ASSIGN = 0x0001; // modem assigned to job

    public static final int MODEM_BUSY = 0x0010; // modem marked busy

    public static final int MODEM_CID = 0x0800; // inbound caller-ID information

    public static final int MODEM_DATA_BEGIN = 0x0080; // inbound data call begun

    public static final int MODEM_DATA_END = 0x0100; // inbound data call finished

    public static final int MODEM_DOWN = 0x0004; // modem marked down

    public static final int MODEM_INUSE = 0x0040; // modem inuse for outbound work

    public static final int MODEM_READY = 0x0008; // modem marked ready

    public static final int MODEM_RELEASE = 0x0002; // modem released by job

    public static final int MODEM_VOICE_BEGIN = 0x0200; // inbound voice call begun

    public static final int MODEM_VOICE_END = 0x0400; // inbound voice call finished

    public static final int MODEM_WEDGED = 0x0020; // modem considered wedged

    // RECEIVE EVENTS.

    public static final int RECV_BEGIN = 0x0001; // fax, inbound call started

    public static final int RECV_DOC = 0x0008; // fax, document receive done

    public static final int RECV_END = 0x0010; // fax, inbound call finished

    public static final int RECV_PAGE = 0x0004; // fax, page receive done

    public static final int RECV_START = 0x0002; // fax, session started

    // SEND EVENTS.

    public static final int SEND_BEGIN = 0x0001; // fax, send attempt started

    public static final int SEND_CALL = 0x0002; // fax, call placed

    public static final int SEND_CONNECTED = 0x0004; // fax, call answered by fax

    public static final int SEND_DOC = 0x0010; // fax, document transmit done

    public static final int SEND_DONE = 0x0400; // fax, send job done

    public static final int SEND_END = 0x0080; // fax, send attempt finished

    public static final int SEND_PAGE = 0x0008; // fax, page transmit done

    public static final int SEND_POLLDONE = 0x0040; // fax, poll operation completed

    public static final int SEND_POLLRCVD = 0x0020; // fax, document retrieved by poll operation

    public static final int SEND_REFORMAT = 0x0100; // fax, job being reformatted

    public static final int SEND_REQUEUE = 0x0200; // fax, job requeued

}
