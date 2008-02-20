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

import java.util.EventListener;

/**
 * Interface for the status events sent from the FaxWatch daemon. ALL
 * implementations should be <strong>thread safe</strong>.
 * 
 * @version $Revision$
 * @author Steven Jardine <steve@mjnservices.com>
 */
public interface StatusEventListener extends EventListener {

    /**
     * Receives only job events from the hylafax server.
     * 
     * @param event
     */
    public void jobEventReceived(StatusEvent event);

    /**
     * Receives only modem events from the hylafax server.
     * 
     * @param event
     */
    public void modemEventReceived(StatusEvent event);

    /**
     * Receives only receive events from the hylafax server.
     * 
     * @param event
     */
    public void receiveEventReceived(StatusEvent event);

    /**
     * Receives only send events from the hylafax server.
     * 
     * @param event
     */
    public void sendEventReceived(StatusEvent event);

}
