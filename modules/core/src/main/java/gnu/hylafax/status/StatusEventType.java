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

 * For more information on the HylaFAX Fax Server please see
 * 	HylaFAX  - http://www.hylafax.org or 
 * 	Hylafax+ - http://hylafax.sourceforge.net
 * 
 * Contributors:
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

/**
 * Represents the valid status event types. Event types can be combined in a bit
 * mask to represent multiple events.
 * 
 * This is accomplished by using logical operators such as | or &.
 * 
 * Example: <code>(StatusEventType.MODEM | StatuEventType.SEND)</code> creates
 * a mask that has the MODEM and SEND bits set.
 * 
 * @version $Id:StatusEventType.java 77 2008-02-20 21:54:51Z sjardine $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class StatusEventType {

    public static final int ALL = Integer.MAX_VALUE;

    public static final int MODEM = 0x0001;

    public static final int SEND = 0x0002;

    public static final int RECEIVE = 0x0004;

    public static final int JOB = 0x0008;

}
