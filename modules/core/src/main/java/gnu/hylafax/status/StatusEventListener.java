// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id$
// ==============================================================================
package gnu.hylafax.status;

import java.util.EventListener;

/**
 * Interface for the status events sent from the FaxWatch daemon.  ALL implementations should be <strong>thread safe</strong>.
 * 
 * @version $Id$
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 */
public interface StatusEventListener extends EventListener {

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
    public void receiveEventReceived(StatusEvent event);

    /**
     * Receives only send events from the hylafax server.
     * @param event
     */
    public void sendEventReceived(StatusEvent event);

}
