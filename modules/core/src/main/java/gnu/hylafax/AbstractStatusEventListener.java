// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: AbstractStatusEventListener.java 1 Feb 18, 2008 steve $
// ==============================================================================
package gnu.hylafax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Id: AbstractStatusEventListener.java 1 Feb 18, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 *
 */
abstract public class AbstractStatusEventListener implements StatusEventListener {

    private static final Log log = LogFactory.getLog(AbstractStatusEventListener.class);

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#eventReceived(gnu.hylafax.StatusEvent)
     */
    public void eventReceived(StatusEvent event) {
        log.debug(event);
    }

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#getEventMask()
     */
    abstract public int getEventMask();

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
     * @see gnu.hylafax.StatusEventListener#receivedEventReceived(gnu.hylafax.StatusEvent)
     */
    public void receivedEventReceived(StatusEvent event) {
        log.debug(event);
    }

    /* (non-Javadoc)
     * @see gnu.hylafax.StatusEventListener#sendEventReceived(gnu.hylafax.StatusEvent)
     */
    public void sendEventReceived(StatusEvent event) {
        log.debug(event);
    }

}
