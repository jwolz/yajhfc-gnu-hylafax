//$Id: ConsoleLogger.java,v 1.1 2007/02/21 00:07:50 sjardine Exp $
//
//Copyright 2005 Steven Jardine <steve@mjnservices.com>
//Copyright 2005 MJN Services, Inc - http://www.mjnservices.com
//
//for information on the HylaFAX FAX server see
//http://www.hylafax.org/
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Library General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Library General Public License for more details.
//
//You should have received a copy of the GNU Library General Public
//License along with this library; if not, write to the Free
//Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//

package gnu.inet.logging;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Implements the console logger for gnu.inet.logging.LoggingFactory. All
 * logging for this class is sent to System.out by default.
 * 
 * It is possible to change the PrintStream / OutputStream of the logger by
 * calling <code>setOutputStream(OutputStream out)</code>. This feature has
 * not been tested and is not even recommended because if an
 * <code>OutputStream</code> is needed other than System.out, log4j should be
 * used.
 * 
 * log4j can be found at http://logging.apache.org
 * 
 * @author <a href="mailto:steve@mjnservices.com">Steven Jardine </a>
 */
public class ConsoleLogger implements Logger {

    /**
     * Specify the <code>DEBUG_TYPE</code> for logging.
     */
    public final static String DEBUG_TYPE = "DEBUG";

    /**
     * Specify the <code>ERROR_TYPE</code> for logging.
     */
    public final static String ERROR_TYPE = "ERROR";

    /**
     * Specify the <code>FATAL_TYPE</code> for logging.
     */
    public final static String FATAL_TYPE = "FATAL";

    /**
     * Specify the <code>INFO_TYPE</code> for logging.
     */
    public final static String INFO_TYPE = "INFO";

    private static PrintStream out = System.out;

    /**
     * Specify the <code>WARN_TYPE</code> for logging.
     */
    public final static String WARN_TYPE = "WARN";

    private String className;

    private boolean debugEnabled = false;

    private boolean errorEnabled = true;

    private boolean fatalEnabled = true;

    private boolean infoEnabled = true;

    private boolean warnEnabled = true;

    /**
     * Sets the class name for the logger.
     * 
     * @param logClass
     *            The class to use for logging.
     */
    public ConsoleLogger(Class logClass) {
        this.className = logClass.getName();
    }

    /**
     * Sets the class name for the logger.
     * 
     * @param className
     *            The name of the class to use for logging.
     */
    public ConsoleLogger(String className) {
        this.className = className;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#debug(java.lang.Object)
     */
    public void debug(Object message) {
        debug(message, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#debug(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void debug(Object message, Throwable t) {
        if (isDebugEnabled())
            log(DEBUG_TYPE, message, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#error(java.lang.Object)
     */
    public void error(Object message) {
        error(message, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#error(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void error(Object message, Throwable t) {
        log(ERROR_TYPE, message, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#fatal(java.lang.Object)
     */
    public void fatal(Object message) {
        fatal(message, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#fatal(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void fatal(Object message, Throwable t) {
        log(FATAL_TYPE, message, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#info(java.lang.Object)
     */
    public void info(Object message) {
        info(message, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#info(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void info(Object message, Throwable t) {
        log(INFO_TYPE, message, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return errorEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isFatalEnabled()
     */
    public boolean isFatalEnabled() {
        return fatalEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return infoEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return warnEnabled;
    }

    /**
     * Method used for convenience. Logs all types of messages.
     * 
     * @param type
     *            The type of message to be logged (DEBUG, INFO, WARN, ERROR,
     *            FATAL)
     * @param message
     *            The message to be logged.
     * @param t
     *            The exception thrown. If no exception was thrown
     *            <code>null</code> is acceptable.
     */
    private void log(String type, Object message, Throwable t) {
        synchronized (out) {
            out.println(type + " " + className + " - " + message);
            if (t != null) {
                t.printStackTrace(out);
            }
        }
    }

    /**
     * Sets the ConsoleLogger to show debug messages.
     * 
     * @param debugEnabled
     */
    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    /**
     * Sets the ConsoleLogger to show error messages.
     * 
     * @param errorEnabled
     */
    public void setErrorEnabled(boolean errorEnabled) {
        this.errorEnabled = errorEnabled;
    }

    /**
     * Sets the ConsoleLogger to show fatal messages.
     * 
     * @param fatalEnabled
     */
    public void setFatalEnabled(boolean fatalEnabled) {
        this.fatalEnabled = fatalEnabled;
    }

    /**
     * Sets the ConsoleLogger to show info messages.
     * 
     * @param infoEnabled
     */
    public void setInfoEnabled(boolean infoEnabled) {
        this.infoEnabled = infoEnabled;
    }

    /**
     * Sets the <code>java.io.OutputStream</code> for the console logger.
     * 
     * @param out
     */
    public void setOutputStream(OutputStream out) {
        ConsoleLogger.out = new PrintStream(out);
    }

    /**
     * Sets the ConsoleLogger to show warn messages.
     * 
     * @param warnEnabled
     */
    public void setWarnEnabled(boolean warnEnabled) {
        this.warnEnabled = warnEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#warn(java.lang.Object)
     */
    public void warn(Object message) {
        warn(message, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#warn(java.lang.Object,
     *      java.lang.Throwable)
     */
    public void warn(Object message, Throwable t) {
        log(WARN_TYPE, message, t);
    }
}
