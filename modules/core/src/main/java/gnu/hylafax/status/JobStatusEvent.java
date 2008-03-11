/*******************************************************************************
 * $Id: JobStatusEvent.java 84 2008-02-21 23:08:12Z sjardine $
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
 * Represents a JOB event sent by the fax server.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class JobStatusEvent extends BaseStatusEvent {

    private static final Log log = LogFactory.getLog(JobStatusEvent.class);

    protected String destination = null;

    protected Integer jobId = null;

    private String pri = null;

    public JobStatusEvent(Event event, String serverStr) {
	super(event, serverStr);
	if (serverStr != null) {
	    String details = null;
	    try {
		details = prepStr(serverStr.split(" JOB ")[1]).toUpperCase();
	    } catch (Exception e) {
		log.warn(errorMsg("Cannot Parse Job Details", details, e));
	    }

	    if (details != null) {
		jobId = parseJobId(details);
		destination = parseDestination(details);
		pri = parsePri(details);
		commId = parseCommId(details);
		description = parseDescription(details);
	    }
	}
    }

    public String getDestination() {
	return destination;
    }

    public Integer getJobId() {
	return jobId;
    }

    public String getPri() {
	return pri;
    }

    protected String parseDescription(String details) {
	try {
	    return prepStr(details.substring(details.indexOf("): ") + 3));
	} catch (Exception e) {
	    log.warn(errorMsg("Cannot Parse Description", details, e));
	}
	return null;
    }

    private String parseDestination(String details) {
	try {
	    return prepStr(details.substring(details.indexOf(" (DEST ") + 7,
		    details.indexOf(" PRI ")));
	} catch (Exception e) {
	    log.warn(errorMsg("Cannot Parse Destination", details, e));
	}
	return null;
    }

    private Integer parseJobId(String details) {
	try {
	    return Integer.valueOf(details.substring(0, details.indexOf(" ")));
	} catch (Exception e) {
	    log.warn(errorMsg("Cannot Parse Job Id", details, e));
	}
	return null;
    }

    protected String parsePri(String details) {
	try {
	    return prepStr(details.substring(details.indexOf(" PRI ") + 5,
		    details.indexOf(" COM ")));
	} catch (Exception e) {
	    log.warn(errorMsg("Cannot Parse PRI", details, e));
	}
	return null;
    }

    public String toString() {
	String result = super.toString();
	result += "JOB ID: " + jobId + "; ";
	result += "DEST: " + destination + "; ";
	result += "PRI: " + pri + "; ";
	return result;
    }
}
