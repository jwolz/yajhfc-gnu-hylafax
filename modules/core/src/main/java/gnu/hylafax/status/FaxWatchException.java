// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: FaxWatchException.java 1 Feb 19, 2008 steve $
// ==============================================================================
package gnu.hylafax.status;

/**
 * @version $Id: FaxWatchException.java 1 Feb 19, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 */
public class FaxWatchException extends Exception {

    private static final String CAUSED_BY = "Caused By: ";

    private Throwable cause;

    /**
     * Default constructor
     */
    public FaxWatchException() {
        super();
    }

    /**
     * Overriden constructor. See Exception(String message) method.
     * 
     * @param message
     */
    public FaxWatchException(String message) {
        super(message);
    }

    /**
     * Overriden constructor. See Exception(String, Throwable) method.
     * 
     * @param message
     * @param cause
     */
    public FaxWatchException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Overriden constructor. See Exception(Throwable) method.
     * 
     * @param cause
     */
    public FaxWatchException(Throwable cause) {
        this(cause == null ? "" : cause.getMessage() == null ? "" : cause.getMessage(), cause);
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
        if (this.cause == null) return null;
        return this.cause.getMessage();
    }

    /**
     * Loads the exception message in a StringBuffer
     * 
     * @param sb StringBuffer
     */
    public void getMessage(StringBuffer sb) {
        sb.append(super.getMessage());
        sb.append("\n");
        if (cause != null) {
            sb.append(CAUSED_BY);
            if (cause instanceof FaxWatchException) {
                FaxWatchException chainedCause = (FaxWatchException) cause;
                chainedCause.getMessage(sb);
            } else {
                sb.append(cause.getMessage());
            }
        }
    }

    /**
     * Returns true if this exception is the exception that caused the exception chain.
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
