// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: LoggingStatusEventListener.java 1 Feb 20, 2008 steve $
// ==============================================================================
package gnu.hylafax.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Id: LoggingStatusEventListener.java 1 Feb 20, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 *
 */
public class LoggingStatusEventListener implements StatusEventListener {

    private static final Log log = LogFactory.getLog(LoggingStatusEventListener.class);

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#jobEventReceived(gnu.hylafax.StatusEvent)
     */
    public void jobEventReceived(StatusEvent event) {
        log.debug(event);
    }

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#modemEventReceived(gnu.hylafax.StatusEvent)
     */
    public void modemEventReceived(StatusEvent event) {
        log.debug(event);
    }

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#receiveEventReceived(gnu.hylafax.StatusEvent)
     */
    public void receiveEventReceived(StatusEvent event) {
        log.debug(event);
    }

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#sendEventReceived(gnu.hylafax.StatusEvent)
     */
    public void sendEventReceived(StatusEvent event) {
        log.debug(event);
    }

}
