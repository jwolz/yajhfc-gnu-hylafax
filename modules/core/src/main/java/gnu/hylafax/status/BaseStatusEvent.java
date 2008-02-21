/*******************************************************************************
 * $Id: ModemStatusEvent.java 80 2008-02-20 22:55:43Z sjardine $
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

/**
 * Represents the base functionality of an event sent by the fax server.
 * 
 * @version $Revision: 80 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class BaseStatusEvent implements StatusEvent {

    protected String event = null;

    public BaseStatusEvent(String event) {
	this.event = event;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return event;
    }

}
