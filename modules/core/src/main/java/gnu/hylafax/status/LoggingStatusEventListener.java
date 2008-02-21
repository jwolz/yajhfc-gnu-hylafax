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
 *
 * For more information on the HylaFAX Fax Server please see
 * 	HylaFAX  - http://www.hylafax.org or 
 * 	Hylafax+ - http://hylafax.sourceforge.net
 * 
 * Contributors:
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An example listener that logs the status events to the logging system.
 * 
 * @version $Revision$
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class LoggingStatusEventListener implements StatusEventListener {

    private static final Log log = LogFactory
	    .getLog(LoggingStatusEventListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#jobEventReceived(gnu.hylafax.StatusEvent)
     */
    public void jobEventReceived(StatusEvent event) {
	log.debug(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#modemEventReceived(gnu.hylafax.StatusEvent)
     */
    public void modemEventReceived(StatusEvent event) {
	log.debug(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#receiveEventReceived(gnu.hylafax.StatusEvent)
     */
    public void receiveEventReceived(StatusEvent event) {
	log.debug(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#sendEventReceived(gnu.hylafax.StatusEvent)
     */
    public void sendEventReceived(StatusEvent event) {
	log.debug(event);
    }

}
