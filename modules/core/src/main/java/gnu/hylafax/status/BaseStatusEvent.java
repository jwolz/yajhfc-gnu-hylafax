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

    protected String commId = null;

    protected String description = null;

    protected Event event = null;

    protected String serverStr = null;

    protected Date serverTime;

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

    protected String errorMsg(String message, String info, Exception e) {
	String result = "";
	String tmpMessage = prepStr(message);
	if (tmpMessage != null)
	    result += tmpMessage;

	String tmpInfo = prepStr(info);
	if (tmpMessage != null && tmpInfo != null)
	    result += ": ";
	if (tmpInfo != null)
	    result += tmpInfo;

	if (e != null) {
	    String tmpExcept = prepStr(e.getMessage());
	    if (tmpExcept != null && !result.equals(""))
		result += "; Exception: ";
	    if (tmpExcept != null)
		result += tmpExcept;
	}
	return prepStr(result);
    }

    public Date getClientTime() {
	return clientTime;
    }

    public String getCommId() {
	return commId;
    }

    public String getDescription() {
	return description;
    }

    public Event getEvent() {
	return event;
    }

    public String getServerStr() {
	return serverStr;
    }

    public Date getServerTime() {
	return serverTime;
    }

    protected String parseCommId(String details) {
	try {
	    String tmp = prepStr(details.substring(details.indexOf("COM ") + 4,
		    details.indexOf(")")));
	    if (tmp != null)
		return tmp;
	} catch (Exception e) {
	    log.warn(errorMsg("Cannot Parse CommId", details, e));
	}
	return null;
    }

    protected String prepStr(String str) {
	if (str != null) {
	    String tmp = str.trim();
	    if (!tmp.equals("")) {
		return tmp;
	    }
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	String result = "CTIME: " + df.format(clientTime) + "; ";
	result += "STIME: " + df.format(serverTime) + "; ";
	result += "EVENT: " + event.getName() + "; ";
	result += "COMM ID: " + commId + "; ";
	result += "DESC: " + description + "; ";
	return result;
    }

}
