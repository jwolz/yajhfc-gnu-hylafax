/*******************************************************************************
 * $Id: SendStatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
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

import org.apache.log4j.BasicConfigurator;

/**
 * Represents a SEND event sent by the fax server.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class SendStatusEvent extends JobStatusEvent {

    public static void main(String[] args) {
	try {
	    BasicConfigurator.configure();
	    String line = null;
	    BufferedReader in = new BufferedReader(new FileReader(
		    "/home/steve/Desktop/send-messages.txt"));
	    while ((line = in.readLine()) != null) {
		int eventId = Integer.parseInt(line.split(" ")[1]);
		Event event = Event.getEvent(eventId);

		StatusEvent statusEvent = new SendStatusEvent(event, line);
		// System.out.println(line);
		System.out.println(statusEvent.toString());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public SendStatusEvent(Event event, String serverStr) {
	super(event, serverStr);

	if (event == Event.SEND_PAGE || event == Event.SEND_DOC) {
	    String info = prepStr(description);
	    description = null;
	    if (info != null) {
		if (event == Event.SEND_PAGE) {
		    parsePageInfo(info);
		} else if (event == Event.SEND_DOC) {
		    parseDocInfo(info);
		}
	    }
	}
    }

    private void parseDocInfo(String info) {
	// TODO Finish parser.
    }

    private void parsePageInfo(String info) {
	// TODO Finish parser.
    }
}
