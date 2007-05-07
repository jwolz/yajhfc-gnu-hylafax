//$Id: Logger.java,v 1.1 2007/02/21 00:07:50 sjardine Exp $
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
public interface Logger {

    /**
     * @return true is debug level is enabled.
     */
    public boolean isDebugEnabled();

    /**
     * Log a debug message.
     * 
     * @param message
     */
    public void debug(Object message);

    /**
     * Log a debug message with an exception.
     * 
     * @param message
     * @param t
     */
    public void debug(Object message, Throwable t);

    /**
     * @return true is info level is enabled.
     */
    public boolean isInfoEnabled();

    /**
     * Log an info message.
     * 
     * @param message
     */
    public void info(Object message);

    /**
     * Log an info message with an exception.
     * 
     * @param message
     * @param t
     */
    public void info(Object message, Throwable t);

    /**
     * @return true is warn level is enabled.
     */
    public boolean isWarnEnabled();

    /**
     * Log a warn message.
     * 
     * @param message
     */
    public void warn(Object message);

    /**
     * Log a warn message with an exception.
     * 
     * @param message
     * @param t
     */
    public void warn(Object message, Throwable t);

    /**
     * @return true is error level is enabled.
     */
    public boolean isErrorEnabled();

    /**
     * Log an error message.
     * 
     * @param message
     */
    public void error(Object message);

    /**
     * Log an error message with an exception.
     * 
     * @param message
     * @param t
     */
    public void error(Object message, Throwable t);

    /**
     * @return true is fatal level is enabled.
     */
    public boolean isFatalEnabled();

    /**
     * Log a fatal message.
     * 
     * @param message
     */
    public void fatal(Object message);

    /**
     * Log a fatal message with an exception.
     * 
     * @param message
     * @param t
     */
    public void fatal(Object message, Throwable t);
}
