/*******************************************************************************
 * $Id: ReceiveStatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
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

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

/**
 * Represents a RECEIVE event sent by the fax server.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class ReceiveStatusEvent extends BaseStatusEvent {

    private static final Log log = LogFactory.getLog(ReceiveStatusEvent.class);

    private String sender = null;
    private String commId = null;

    public ReceiveStatusEvent(Event event, String serverStr) {
	super(event, serverStr);
	if (serverStr != null) {
	    String details = (serverStr.split("RECV FAX: ")[1]).trim()
		    .toUpperCase();

	    // Parse the details.
	    if (details.startsWith("CALL")) {
		description = details;
		return;
	    }

	    if (details.startsWith("SESSION STARTED")) {
		String tmp = details.substring(details.indexOf(" (COM ") + 6,
			details.indexOf("), ")).trim();
		if (!tmp.equals(""))
		    commId = tmp;
		return;
	    }

	    if (details.startsWith("FROM")) {
		String tmp = details.substring(details.indexOf("FROM ") + 5,
			details.indexOf(" (COM ")).trim();
		if (!tmp.equals(""))
		    sender = tmp;

		tmp = details.substring(details.indexOf(" (COM ") + 6,
			details.indexOf("), ")).trim();
		if (!tmp.equals(""))
		    commId = tmp;

		System.err.println(sender + " | " + commId);
	    }
	    // log.debug("Receive Event Details: " + details);

	}
    }

    public static void main(String[] args) {
	try {
	    BasicConfigurator.configure();
	    String line = null;
	    BufferedReader in = new BufferedReader(new FileReader(
		    "/home/steve/Desktop/receive-messages.txt"));
	    while ((line = in.readLine()) != null) {
		int eventId = Integer.parseInt(line.split(" ")[1]);
		Event event = Event.getEvent(eventId);

		StatusEvent statusEvent = new ReceiveStatusEvent(event, line);
		// System.out.println(statusEvent.toString());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
