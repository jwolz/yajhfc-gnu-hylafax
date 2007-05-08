// PassivePutter.java
// $Id: PassivePutter.java,v 1.7 2007/05/07 18:26:54 sjardine Exp $
//
// Copyright 2000, Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001, 2002 Innovation Software Group, LLC - http://www.innovationsw.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the Free
// Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// TODO:
// - do compressed streams
//
//
package gnu.inet.ftp;

// system includes
import java.io.*;
import java.net.*;
import java.util.zip.*;

// home-grown imports
import gnu.inet.logging.Logger;
import gnu.inet.logging.LoggingFactory;

/**
 * This class implements an FTP-style data connection server thread for PUTing files/data passively to the
 * server.
 * <P>
 * This class is used internally to the FtpClient class.
 */
public class PassivePutter extends Putter {
    // private data
    private PassiveConnection connection;

    private final static Logger log = LoggingFactory.getLogger(PassivePutter.class);

    // public constructors
    //

    /**
     * Create a new PassivePutter thread given the input stream data source and PssiveParameters to use to
     * connect to the server.
     * 
     * @param in data source
     * @param connection the passive connection to the server
     */
    public PassivePutter(InputStream in, PassiveConnection connection) {
        super();

        this.istream = in;
        this.connection = connection;

    }// end of default constructor

    //
    // public methods
    //

    /**
     * implements thread behavior. Put data to server using given parameters.
     */
    public void run() {
        boolean signalClosure = false;
        Socket sock = null;
        OutputStream ostream;
        long amount = 0;
        int buffer_size = 0;
        byte buffer[] = new byte[BUFFER_SIZE];
        // this.cancelled= false; // reset cancelled flag
        PassiveParameters parameters = connection.getPassiveParameters();

        try {
            // make connection
            sock = connection.getSocket();
            if (cancelled) throw new InterruptedIOException("Transfer cancelled");
            signalConnectionOpened(new ConnectionEvent(parameters.getInetAddress(), parameters.getPort()));
            signalClosure = true;
            signalTransferStarted();

            try {

                // handle different type settings
                switch (type) {
                case FtpClient.TYPE_ASCII:
                    ostream = new AsciiOutputStream(sock.getOutputStream());
                    break;
                default:
                    ostream = sock.getOutputStream();
                    break;
                }// switch

                // handle different mode settings
                switch (mode) {
                case FtpClient.MODE_ZLIB:
                    ostream = new DeflaterOutputStream(ostream);
                    break;
                case FtpClient.MODE_STREAM:
                default:
                    break;
                }// switch

                int len;
                while ((len = istream.read(buffer)) != -1) {
                    ostream.write(buffer, 0, len);
                    amount += len;
                    buffer_size += len;
                    if (buffer_size >= BUFFER_SIZE) {
                        buffer_size = buffer_size % BUFFER_SIZE;
                        signalTransfered(amount);
                    }
                    yield();
                }

                ostream.close();
                sock.close();
            } catch (InterruptedIOException iioe) {
                if (!cancelled) {
                    log.error(iioe.getMessage(), iioe);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                signalTransferCompleted();
            }
        } catch (Exception ee) {
            signalConnectionFailed(ee);
            log.error(ee.getMessage(), ee);
        }

        if (signalClosure == true) {
            signalConnectionClosed(new ConnectionEvent(parameters.getInetAddress(), parameters.getPort()));
        }
    }// run

}// PassivePutter

// PassivePutter.java
