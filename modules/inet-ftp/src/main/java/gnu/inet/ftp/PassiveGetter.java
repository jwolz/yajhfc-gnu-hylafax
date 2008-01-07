// PassiveGetter.java
// $Id: PassiveGetter.java,v 1.6 2007/02/21 00:07:50 sjardine Exp $
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


import gnu.inet.logging.ConsoleLogger;
import gnu.inet.logging.Logger;
import gnu.inet.logging.LoggingFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.InflaterInputStream;

/**
 * This class implements an FTP-style data connection thread for GETing
 * files/data passively. This class is used internally to the FtpClient class.
 */
public class PassiveGetter extends Getter {
	// private data
	private PassiveConnection connection;

	private Socket sock = null;

	private final static Logger log = LoggingFactory
			.getLogger(PassiveGetter.class);

	
	//

	/**
	 * Create a new PassiveGetter instance with the given OutpuStream for data
	 * output and using the given PassiveParameters to connect to the server.
	 * 
	 * @param out
	 *            the OutputStream where retrieved data will be written
	 * @param connection
	 *            the PassiveConnection to the server
	 */
	public PassiveGetter(OutputStream out, PassiveConnection connection) {
		super();
		this.setDebug(false);

		this.ostream = out;
		this.connection = connection;
	}// end of default constructor

	//
	// public methods
	//

	/**
	 * Sets the ConsoleLogger's debug output. Does nothing for log4j. Log4j
	 * needs to be configured using log4j.properties
	 * 
	 * @param value
	 *            new debug flag value
	 */
	public void setDebug(boolean value) {
		if (log instanceof ConsoleLogger) {
			ConsoleLogger cl = (ConsoleLogger) log;
			cl.setDebugEnabled(value);
		}
	}// end of debug method

	/**
	 * cancel a running transfer sets a flag and calls interrupt() can only be
	 * called once
	 */
	public void cancel() {
		if (!cancelled) {
			cancelled = true;
			interrupt();
			if (sock != null)
				try {
					sock.close(); // Interrupt I/O
				} catch (IOException e) {
					// do nothing
				}
		}
	}// cancel

	/**
	 * get data from server using given parameters.
	 */
	public void run() {
		boolean signalClosure = false;
		// Socket sock= null;
		InputStream istream;
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
		sock = null;
	}// run

}// PassiveGetter

// PassiveGetter.java
