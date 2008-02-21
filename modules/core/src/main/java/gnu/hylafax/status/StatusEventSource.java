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
 * Adds and removes StatusEventListeners.
 * 
 * @version $Revision$
 * @author Steven Jardine <steve@mjnservices.com>
 */
public interface StatusEventSource {

    public void addStatusEventListener(StatusEventListener listener)
	    throws StatusEventException;

    public void addStatusEventListener(StatusEventListener listener, int type)
	    throws StatusEventException;

    public void addStatusEventListener(StatusEventListener listener, int type,
	    int events) throws StatusEventException;

    public void addStatusEventListener(StatusEventListener listener, int type,
	    int events, String id) throws StatusEventException;

    public void removeStatusEventListener(StatusEventListener listener)
	    throws StatusEventException;

}
