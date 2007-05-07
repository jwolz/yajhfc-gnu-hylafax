//$Id: Log4jLogger.java,v 1.1 2007/02/21 00:07:50 sjardine Exp $
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

import org.apache.log4j.Level;

/**
 * Implements a logging factory that uses log4j if it is available. If log4 is
 * not available then output is logged to the console via System.out.
 * 
 * It is possible to change the PrintStream / OutputStream of the console logger
 * 
 * Future enhancements could include adding support for java.util.logging.
 * However, there would have to be some mechanism for determining which method
 * to use. The current implementation only uses the console logger if log4j is
 * not available.
 * 
 * log4j can be found at http://logging.apache.org
 * 
 * @author <a href="mailto:steve@mjnservices.com">Steven Jardine </a>
 */
public class Log4jLogger implements Logger {

    private org.apache.log4j.Logger logger = null;

    /**
     * Creates a logger using the class requesting logging.
     * 
     * @param logClass
     *            The class that is requesting logging.
     */
    protected Log4jLogger(Class logClass) {
        if (logger == null)
            init(logClass);
    }

    /**
     * Creates a logger using the class name requesting logging.
     * 
     * @param logClassName
     *            The name of the class requesting logging.
     * @throws ClassNotFoundException
     */
    protected Log4jLogger(String logClassName) throws ClassNotFoundException {
        if (logger == null) {
            init(Class.forName(logClassName));
        }
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
        if (isDebugEnabled()) {
            if (t != null)
                logger.debug(message, t);
            else
                logger.debug(message);
        }
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
        if (isErrorEnabled()) {
            if (t != null)
                logger.error(message, t);
            else
                logger.error(message);
        }
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
        if (isFatalEnabled()) {
            if (t != null)
                logger.fatal(message, t);
            else
                logger.fatal(message);
        }
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
        if (isInfoEnabled()) {
            if (t != null)
                logger.info(message, t);
            else
                logger.info(message);
        }
    }

    /**
     * Initializes the class with the specified logClass. If any exception is
     * thrown, the logger is set to <code>null</code>.
     * 
     * @param logClass
     */
    private void init(Class logClass) {
        try {
            logger = org.apache.log4j.Logger.getLogger(logClass);
        } catch (Throwable t) {
            logger = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return isEnabledFor(Level.DEBUG);
    }

    /**
     * Checks to see if log4j logging is enabled for the specified level.
     * 
     * @param level
     *            the level to check.
     * @return true if logging is enabled for the specified level, false
     *         otherwise.
     */
    private boolean isEnabledFor(Level level) {
        return logger != null && logger.isEnabledFor(level);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return isEnabledFor(Level.ERROR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isFatalEnabled()
     */
    public boolean isFatalEnabled() {
        return isEnabledFor(Level.FATAL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return isEnabledFor(Level.INFO);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.logging.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return isEnabledFor(Level.WARN);
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
        if (isWarnEnabled()) {
            if (t != null)
                logger.warn(message, t);
            else
                logger.warn(message);
        }
    }
}
