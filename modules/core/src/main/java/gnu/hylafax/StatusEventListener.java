// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: StatusEventListener.java 1 Feb 18, 2008 steve $
// ==============================================================================
package gnu.hylafax;

import java.util.EventListener;

/**
 * @version $Id: StatusEventListener.java 1 Feb 18, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 *
 */
public interface StatusEventListener extends EventListener {

    public static final int MODEM = 0x0001;

    public static final int SEND = 0x0002;

    public static final int RECEIVE = 0x0004;

    public static final int JOB = 0x0008;

    /**
     * Gets the bit mask options for the events desired.  If the function 
     * returns 0 or an invalid mask the listener will be discarded.
     * 
     * @return a bit mask made up of MODEM | SEND | RECEIVE | JOB options. 
     */
    public int getEventMask();

    /**
     * Receives all events from the hylafax server.
     * @param event
     */
    public void eventReceived(String eventTxt);

    /**
     * Receives only job events from the hylafax server.
     * @param event
     */
    public void jobEventReceived(StatusEvent event);

    /**
     * Receives only modem events from the hylafax server.
     * @param event
     */
    public void modemEventReceived(StatusEvent event);

    /**
     * Receives only receive events from the hylafax server.
     * @param event
     */
    public void receivedEventReceived(StatusEvent event);

    /**
     * Receives only send events from the hylafax server.
     * @param event
     */
    public void sendEventReceived(StatusEvent event);

}
