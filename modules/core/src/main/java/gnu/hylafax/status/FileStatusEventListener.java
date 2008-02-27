/*******************************************************************************
 * $Id: LoggingStatusEventListener.java 80 2008-02-20 22:55:43Z sjardine $
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An example listener that logs the status events to the logging system.
 * 
 * @version $Revision: 80 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class FileStatusEventListener implements StatusEventListener {

    private static final Log log = LogFactory
	    .getLog(FileStatusEventListener.class);

    private File file = null;

    private Object mutex = new Object();

    public FileStatusEventListener(String path) throws IOException {
	this(new File(path));
    }

    public FileStatusEventListener(File file) throws IOException {
	this.file = file;
	if (!this.file.exists())
	    this.file.createNewFile();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#jobEventReceived(gnu.hylafax.StatusEvent)
     */
    public void jobEventReceived(StatusEvent event) {
	synchronized (mutex) {
	    try {
		FileWriter out = new FileWriter(file, true);
		try {
		    out.write(event.toString() + "\n");
		    out.flush();
		} finally {
		    out.close();
		}
	    } catch (Exception e) {
		log.error(e.getMessage(), e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#modemEventReceived(gnu.hylafax.StatusEvent)
     */
    public void modemEventReceived(StatusEvent event) {
	jobEventReceived(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#receiveEventReceived(gnu.hylafax.StatusEvent)
     */
    public void receiveEventReceived(StatusEvent event) {
	jobEventReceived(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.StatusEventListener#sendEventReceived(gnu.hylafax.StatusEvent)
     */
    public void sendEventReceived(StatusEvent event) {
	jobEventReceived(event);
    }

}
