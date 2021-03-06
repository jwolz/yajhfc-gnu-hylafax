// PassivePutter.java
// $Id$
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
package gnu.inet.ftp;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements an FTP-style data connection server thread for PUTing
 * files/data passively to the server.
 * <P>
 * This class is used internally to the FtpClient class.
 */
public class PassivePutter extends Putter {

    private final static Log log = LogFactory.getLog(PassivePutter.class);

    private PassiveConnection connection;

    /**
     * Create a new PassivePutter thread given the input stream data source and
     * PssiveParameters to use to connect to the server.
     * 
     * @param in
     *                data source
     * @param connection
     *                the passive connection to the server
     */
    /**
     * @param in
     * @param connection
     */
    public PassivePutter(InputStream in, PassiveConnection connection) {
	super();

	this.istream = in;
	this.connection = connection;
    }

    //
    // public methods
    //

    /**
     * implements thread behavior. Put data to server using given parameters.
     */
    public void run() {
	boolean signalClosure = false;
	Socket sock = null;
	OutputStream ostream = null;
	long amount = 0;
	int buffer_size = 0;
	byte buffer[] = new byte[BUFFER_SIZE];
	// this.cancelled= false; // reset cancelled flag
	PassiveParameters parameters = connection.getPassiveParameters();

	try {
	    // make connection
	    sock = connection.getSocket();
	    if (cancelled)
		throw new InterruptedIOException("Transfer cancelled");
	    signalConnectionOpened(new ConnectionEvent(parameters
		    .getInetAddress(), parameters.getPort()));
	    signalClosure = true;
	    signalTransferStarted();

	    try {

		// handle different type settings
		switch (type) {
		case FtpClientProtocol.TYPE_ASCII:
		    ostream = new AsciiOutputStream(sock.getOutputStream());
		    break;
		default:
		    ostream = sock.getOutputStream();
		    break;
		}

		// handle different mode settings
		switch (mode) {
		case FtpClientProtocol.MODE_ZLIB:
		    ostream = new DeflaterOutputStream(ostream);
		    break;
		case FtpClientProtocol.MODE_STREAM:
		default:
		    break;
		}

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
	    } catch (InterruptedIOException iioe) {
		if (!cancelled) {
		    log.error(iioe.getMessage(), iioe);
		}
	    } catch (Exception e) {
		log.error(e.getMessage(), e);
	    } finally {
		log.debug("Closing inputstream");
		if (ostream != null) {
		    ostream.close();
		}
		if (!sock.isClosed()) {
		    try {
			log.debug("Setting socket to 0 lingering");
			sock.setSoLinger(true, 0);
			sock.close();
		    } catch (SocketException e) {
			// Don't care.
		    }
		}
		signalTransferCompleted();
	    }
	} catch (Exception ee) {
	    signalConnectionFailed(ee);
	    log.error(ee.getMessage(), ee);
	}

	if (signalClosure == true) {
	    signalConnectionClosed(new ConnectionEvent(parameters
		    .getInetAddress(), parameters.getPort()));
	}
    }
}

// PassivePutter.java
