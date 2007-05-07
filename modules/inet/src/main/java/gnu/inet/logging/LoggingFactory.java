//$Id: LoggingFactory.java,v 1.2 2007/05/07 18:26:53 sjardine Exp $
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
public class LoggingFactory {

    private static boolean log4jFound = false;

    private static boolean loggerChecked = false;

    private static boolean forceConsole = false; // For testing purposes

    /**
     * Returns the a logger for the class passed via logClass.
     * 
     * @param logClass
     *            the class to be used for logging.
     * @return a object that implements the Logger interface for the specified
     *         class.
     */
    public static Logger getLogger(Class logClass) {
        if (!loggerChecked) {
            try {
                Class.forName("org.apache.log4j.Logger");
                log4jFound = true;
            } catch (ClassNotFoundException e) {
                log4jFound = false;
            }
            loggerChecked = true;
        }

        if (log4jFound && !forceConsole)
            return new Log4jLogger(logClass);

        return new ConsoleLogger(logClass);
    }

    /**
     * Checks to see if the class is defined and returns a Logger for it. If the
     * class is not defined <code>null</code> is returned.
     * 
     * @param logClass
     *            the name of the class to be used for logging.
     * @return a object that implements the Logger interface for the specified
     *         class.
     */
    public static Logger getLogger(String logClass) {
        try {
            return getLogger(Class.forName(logClass));
        } catch (Exception e) {
            return null;
        }
    }
}
