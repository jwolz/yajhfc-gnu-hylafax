/*******************************************************************************
 * $Id: ModemStatusEvent.java 80 2008-02-20 22:55:43Z sjardine $
 * 
 * Copyright 2000 Joe Phillips <jaiger@innovationsw.com>
 * Copyright 2001, 2002 Innovation Software Group, LLC - http://www.innovationsw.com
 * Copyright 2008 Steven Jardine, MJN Services, Inc. <steve@mjnservices.com>
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
 * 	Joe Phillips - Initial API and implementation
 * 	Steven Jardine 
 ******************************************************************************/
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
 * 
 * This class is used internally to the FtpClient class.
 * 
 * @version $Revision: 80 $
 * @author Joe Phillips <jaiger@innovationsw.com>
 * @author Steven Jardine <steve@mjnservices.com>
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
     *                 an IO error occurred with the ServerSocket
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
    }

    /**
     * get the local IP address that this ActiveGetter is listening on
     * 
     * @return server socket IP address
     */
    public InetAddress getInetAddress() {
	return address;
    }

    /**
     * get the local port this ActiveGetter is listening on
     * 
     * @return port number
     */
    public synchronized int getPort() {
	return port;
    }

    /**
     * get data from server using given parameters.
     */
    public void run() {
	boolean signalClosure = false;
	Socket sock = null;
	InputStream istream;
	long amount = 0;
	long buffer_size = 0;
	byte buffer[] = new byte[BUFFER_SIZE];

	try {
	    // Wait for connection until timeout
	    server.setSoTimeout(timeout);
	    if (cancelled)
		// Small race condition here
		throw new InterruptedIOException("Transfer cancelled");
	    sock = server.accept();
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
		}

		// handle different mode settings
		switch (mode) {
		case FtpClientProtocol.MODE_ZLIB:
		    istream = new InflaterInputStream(istream);
		    break;
		case FtpClientProtocol.MODE_STREAM:
		default:
		    break;
		}

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
		sock.close(); // make sure the socket is closed
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
	}
	if (signalClosure == true && sock != null) {
	    signalConnectionClosed(new ConnectionEvent(sock.getInetAddress(),
		    sock.getPort()));
	}
    }

    /**
     * Set the connection timeout in milliseconds. This method must be called
     * before start()/run() for the value to take affect.
     * 
     * @param milliseconds
     *                the socket timeout value in milliseconds
     */
    public void setTimeout(int milliseconds) {
	timeout = milliseconds;
    }

}