/*******************************************************************************
 * $Id$
 * 
 * Copyright 2003 Innovation Software Group, LLC - http://www.innovationsw.com
 * Copyright 2003 Joe Phillips <jaiger@innovationsw.com>
 * Copyright 2008 Steven Jardine, MJN Services, Inc. <steve@mjnservices.com>
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
 * 	Joe Phillips - Initial API and implementation
 * 	Steven Jardine - Code formatting, rework of license header, javadoc 
 ******************************************************************************/
package gnu.hylafax.job;

import gnu.hylafax.Client;
import gnu.inet.ftp.ServerResponseException;

import java.io.IOException;

/**
 * This is a thread-safe implementation of the gnu.hylafax.Job interface.
 * 
 * @see gnu.hylafax.ClientProtocol
 * @see gnu.hylafax.Client
 */
public class ThreadSafeJob extends Job {

    /**
     * Default constructor.
     * 
     * @param c
     *                HylaFAX client to use for the job.
     * @throws ServerResponseException
     * @throws IOException
     */
    public ThreadSafeJob(Client c) throws ServerResponseException, IOException {
	super(c);
    }

    /**
     * Default constructor.
     * 
     * @param c
     *                HylaFAX client to use for the job.
     * @param id
     *                Job id
     * @throws ServerResponseException
     * @throws IOException
     */
    public ThreadSafeJob(Client c, long id) throws ServerResponseException,
	    IOException {
	super(c, id);
    }

    /**
     * Get the value for an arbitrary property for this job. Developers using
     * this method should be familiar with the HylaFAX client protocol in order
     * to provide the correct key values and how to interpret the values
     * returned. This method is thread-safe.
     * 
     * @exception ServerResponseException
     *                    the server responded with an error. This is likely due
     *                    to a protocol error.
     * @exception IOException
     *                    an i/o error occured
     * @return a String value for the given property key
     */
    public String getProperty(String key) throws ServerResponseException,
	    IOException {
	synchronized (client) {
	    long j = client.job();
	    client.job(getId());
	    String tmp = super.getProperty(key);
	    client.job(j);
	    return tmp;
	}
    }

    /**
     * Set any arbitrary property on this job. In order to use this method,
     * developers should be familiar with the HylaFAX client protocol. This
     * method is thread-safe.
     * 
     * @exception ServerResponseException
     *                    the server responded with an error code. This is
     *                    likely a protocol violation.
     * @exception IOException
     *                    an i/o error occured
     */
    public void setProperty(String parameter, String value)
	    throws ServerResponseException, IOException {
	synchronized (client) {
	    long j = client.job();
	    client.job(getId());
	    super.setProperty(parameter, value);
	    client.job(j);
	}
    }

}