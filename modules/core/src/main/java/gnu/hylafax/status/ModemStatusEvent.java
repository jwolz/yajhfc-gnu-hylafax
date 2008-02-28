/*******************************************************************************
 * $Id: ModemStatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
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

/**
 * Represents a MODEM event sent by the fax server.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class ModemStatusEvent extends BaseStatusEvent {

    private String device = null;

    public ModemStatusEvent(Event event, String serverStr) {
	super(event, serverStr);
	if (serverStr != null) {
	    // Set the device name.
	    device = serverStr.split(" |: ")[3];
	    // Set the description.
	    description = (serverStr.split(": ")[1]).trim().toUpperCase();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.status.BaseStatusEvent#toString()
     */
    public String toString() {
	String result = "Event Type:\t" + event.getTypeStr() + "\n";
	result += "Event Name:\t" + event.getName() + "\n";
	result += "Modem Device:\t" + device + "\n";
	result += "Server Time:\t" + serverTime + "\n";
	result += "Client Time:\t" + clientTime + "\n";
	result += "Description:\t" + description + "\n";
	return result;
    }

}
