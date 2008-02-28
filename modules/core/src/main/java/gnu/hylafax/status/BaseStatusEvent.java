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
 *
 * For more information on the HylaFAX Fax Server please see
 * 	HylaFAX  - http://www.hylafax.org or 
 * 	Hylafax+ - http://hylafax.sourceforge.net
 * 
 * Contributors:
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents the base functionality of an event sent by the fax server.
 * 
 * @version $Revision: 80 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class BaseStatusEvent implements StatusEvent {

    private static final Log log = LogFactory.getLog(BaseStatusEvent.class);

    protected Date clientTime;

    protected Event event = null;

    protected String serverStr = null;

    protected Date serverTime;

    protected String description = null;

    public BaseStatusEvent(Event event, String serverStr) {
	this.event = event;
	this.serverStr = serverStr;

	clientTime = new Date();

	// Set server time.
	String timeStr = serverStr.split(" |: ")[0];
	if (timeStr.length() == 6) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStr
		    .substring(0, 1)));
	    calendar.set(Calendar.MINUTE, Integer.parseInt(timeStr.substring(2,
		    3)));
	    calendar.set(Calendar.SECOND, Integer.parseInt(timeStr.substring(4,
		    5)));
	    calendar.set(Calendar.MILLISECOND, 0);
	    serverTime = calendar.getTime();
	} else {
	    log.warn("Cannot determine server time of event.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	String result = "CTIME: " + df.format(clientTime) + "; ";
	result += "STIME: " + df.format(serverTime) + "; ";
	result += "EVENT: " + event.getName() + "; ";
	result += "DESC: " + description + "; ";
	return result;
    }

}
