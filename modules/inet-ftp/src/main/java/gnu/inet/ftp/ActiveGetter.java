// ActiveGetter.java
// $Id: ActiveGetter.java,v 1.6 2007/02/21 00:07:50 sjardine Exp $
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
// TODO implement compressed streams

package gnu.inet.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.InflaterInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements an FTP-style data connection server thread for GETing
 * files/data non-passively.
 * <P>
 * This class is used internally to the FtpClient class.
 */
public class ActiveGetter extends Getter {

    private final static Log log = LogFactory.getLog(ActiveGetter.class);

    private InetAddress address;

    private int port;

    private ServerSocket server;

    private int timeout;

    /**
     * Create a new ActiveGetter with the given OutputStream for data output.
     * 
     * @throws IOException
     *             an IO error occurred with the ServerSocket
     */
    public ActiveGetter(OutputStream out) throws IOException {
	super();

	// create server socket
	this.server = new ServerSocket(0);
	this.timeout = 30 * 1000; // 30s timeout
	// store the port that the server is listening on
	this.port = server.getLocalPort();
	this.address = this.server.getInetAddress();

	this.ostream = out;
    }// end of default constructor

    //
    // public methods
    //

    /**
     * get the local port this ActiveGetter is listening on
     * 
     * @return port number
     */
    public synchronized int getPort() {
	return port;
    }// getPort

    /**
     * get the local IP address that this ActiveGetter is listening on
     * 
     * @return server socket IP address
     */
    public InetAddress getInetAddress() {
	return address;
    }// getInetAddress

    /**
     * Set the connection timeout in milliseconds. This method must be called
     * before start()/run() for the value to take affect.
     * 
     * @param milliseconds
     *            the socket timeout value in milliseconds
     */
    public void setTimeout(int milliseconds) {
	timeout = milliseconds;
    }// setTimeout

    /**
     * get data from server using given parameters.
     */
    public void run() {
	boolean signalClosure = false;
	Socket sock = null;
	InputStream istream = null;
	long amount = 0;
	long buffer_size = 0;
	byte buffer[] = new byte[BUFFER_SIZE];
	// this.cancelled= false; // reset cancelled flag

	try {
	    // wait for connection
	    server.setSoTimeout(timeout); // can only wait so long
	    if (cancelled)
		throw new InterruptedIOException("Transfer cancelled"); // small
	    // race
	    // condition
	    // here
	    sock = server.accept();
	    sock.setSoLinger(true, 250);
	    signalConnectionOpened(new ConnectionEvent(sock.getInetAddress(),
		    sock.getPort()));
	    signalClosure = true;
	    signalTransferStarted();

	    try {

		// handle different type settings
		switch (type) {
		case FtpClientProtocol.TYPE_ASCII:
		    istream = new AsciiInputStream(sock.getInputStream());
		    break;
		default:
		    istream = sock.getInputStream();
		    break;
		}// switch

		// handle different mode settings
		switch (mode) {
		case FtpClientProtocol.MODE_ZLIB:
		    istream = new InflaterInputStream(istream);
		    break;
		case FtpClientProtocol.MODE_STREAM:
		default:
		    break;
		}// switch

		int len;
		while (!cancelled && ((len = istream.read(buffer)) > 0)) {
		    ostream.write(buffer, 0, len);
		    amount += len;
		    buffer_size += len;
		    if (buffer_size >= BUFFER_SIZE) {
			buffer_size = buffer_size % BUFFER_SIZE;
			signalTransfered(amount);
		    }
		    yield();
		}

		ostream.flush();
	    } catch (InterruptedIOException iioe) {
		if (!cancelled) {
		    log.error(iioe.getMessage(), iioe);
		}
	    } catch (Exception e) {
		log.error(e.getMessage(), e);
	    } finally {
		log.debug("Closing inputstream");
		if (istream != null) {
		    istream.close();
		}
		if (!sock.isClosed()) {
		    sock.close();
		}
		signalTransferCompleted();
	    }
	} catch (InterruptedIOException eiioe) {
	    signalConnectionFailed(eiioe);
	    if (!cancelled) {
		log.error(eiioe.getMessage(), eiioe);
	    }
	} catch (Exception ee) {
	    signalConnectionFailed(ee);
	    log.error(ee.getMessage(), ee);
	} finally {
	    try {
		log.debug("Closing server socket");
		server.close();
	    } catch (IOException ex) {
		// don't care
	    }
	}

	if (signalClosure == true && sock != null) {
	    signalConnectionClosed(new ConnectionEvent(sock.getInetAddress(),
		    sock.getPort()));
	}
    }// run

}// ActiveGetter

// ActiveGetter.java
