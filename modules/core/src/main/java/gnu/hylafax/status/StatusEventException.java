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
 * Exception generated for problems with the StatusWatcher.
 * 
 * @version $Revision$
 * @author Steven Jardine <steve@mjnservices.com>
 * @see gnu.hylafax.status.StatusWatcher
 */
public class StatusEventException extends Exception {

    private static final String CAUSED_BY = "Caused By: ";

    private Throwable cause;

    /**
     * Default constructor
     */
    public StatusEventException() {
	super();
    }

    /**
     * Overriden constructor. See Exception(String message) method.
     * 
     * @param message
     */
    public StatusEventException(String message) {
	super(message);
    }

    /**
     * Overriden constructor. See Exception(String, Throwable) method.
     * 
     * @param message
     * @param cause
     */
    public StatusEventException(String message, Throwable cause) {
	super(message);
	this.cause = cause;
    }

    /**
     * Overriden constructor. See Exception(Throwable) method.
     * 
     * @param cause
     */
    public StatusEventException(Throwable cause) {
	this(cause == null ? "" : cause.getMessage() == null ? "" : cause
		.getMessage(), cause);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getCause()
     */
    public Throwable getCause() {
	return this.cause;
    }

    /**
     * Returns the cause message of the exception
     * 
     * @return String
     */
    public String getCauseMessage() {
	if (this.cause == null)
	    return null;
	return this.cause.getMessage();
    }

    /**
     * Loads the exception message in a StringBuffer
     * 
     * @param sb
     *                StringBuffer
     */
    public void getMessage(StringBuffer sb) {
	sb.append(super.getMessage());
	sb.append("\n");
	if (cause != null) {
	    sb.append(CAUSED_BY);
	    if (cause instanceof StatusEventException) {
		StatusEventException chainedCause = (StatusEventException) cause;
		chainedCause.getMessage(sb);
	    } else {
		sb.append(cause.getMessage());
	    }
	}
    }

    /**
     * Returns true if this exception is the exception that caused the exception
     * chain.
     */
    public boolean isCause() {
	return (this.cause != null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#printStackTrace()
     */
    public void printStackTrace() {
	super.printStackTrace();
	if (cause != null) {
	    System.err.println(CAUSED_BY);
	    cause.printStackTrace();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    public void printStackTrace(java.io.PrintStream ps) {
	super.printStackTrace(ps);
	if (cause != null) {
	    ps.println(CAUSED_BY);
	    cause.printStackTrace(ps);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    public void printStackTrace(java.io.PrintWriter pw) {
	super.printStackTrace(pw);
	if (cause != null) {
	    pw.println(CAUSED_BY);
	    cause.printStackTrace(pw);
	}
    }

}
