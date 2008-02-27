// Putter.java
// $Id: Putter.java,v 1.7 2007/02/21 00:07:50 sjardine Exp $
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
package gnu.inet.ftp;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * implements a FTP-style data connection server thread for PUTing files/data.
 * <P>
 * This class mainly serves as a superclass to ActivePutter and PassivePutter.
 */
public class Putter extends Thread implements ConnectionEventSource,
		TransferEventSource {

	public static final int BUFFER_SIZE = 1024;

	protected InputStream istream;

	protected boolean cancelled = false;

	protected Vector connectionListeners;

	protected Vector transferListeners;

	protected char mode;

	protected char type;

	/**
	 * default constructor
	 */
	public Putter() {
		super();
		connectionListeners = new Vector();
		transferListeners = new Vector();
		this.mode = FtpClientProtocol.MODE_STREAM;
		this.type = FtpClientProtocol.TYPE_IMAGE;
	}

	//
	// public methods
	//

	public synchronized void start() {
		this.cancelled = false; // Reset cancelled flag
		super.start();
	}

	/**
	 * set the InputStream to use for data input
	 * 
	 * @param istream
	 *            the InputStream to read data from
	 */
	public synchronized void setInputStream(InputStream istream) {
		this.istream = istream;
	}

	/**
	 * set the mode value. valid mode settings (MODE_*) can be found in the
	 * FtpClientProtocol class.
	 * 
	 * @param mode
	 *            the new mode setting
	 */
	public synchronized void setMode(char mode) {
		this.mode = mode;
	}

	/**
	 * set the type value. valid type settings (TYPE_*) can be found in the
	 * FtpClientProtocol class.
	 * 
	 * @param type
	 *            the new type setting
	 */
	public synchronized void setType(char type) {
		this.type = type;
	}

	/**
	 * cancel a running transfer sets a flag and calls interrupt() can only be
	 * called once
	 */
	public void cancel() {
		if (!cancelled) {
			cancelled = true;
			interrupt();
		}
	}

	/**
	 * add a ConnectionListener to the list of connectionListeners
	 * 
	 * @param listener
	 *            the ConnectionListener to add to the list
	 */
	public void addConnectionListener(ConnectionListener listener) {
		connectionListeners.addElement(listener);
	}

	/**
	 * add a set of ConnectionListeners to the list of connectionListeners
	 * 
	 * @param listeners
	 *            the ConnectionListeners to add to the list
	 */
	public void addConnectionListeners(Vector listeners) {
		Enumeration e = listeners.elements();
		while (e.hasMoreElements()) {
			ConnectionListener listener = (ConnectionListener) e.nextElement();
			connectionListeners.addElement(listener);
		}
	}

	/**
	 * De-register a ConnectionListener with the event source
	 * 
	 * @param listener
	 *            the ConnectionListener to remove from the list of listeners
	 */
	public void removeConnectionListener(ConnectionListener listener) {
		connectionListeners.removeElement(listener);
	}

	/**
	 * register a TransferListener to the list of transfer listeners. Each
	 * transfer listener registered with the event source will be notified when
	 * a transfer event occurs.
	 * 
	 * @param listener
	 *            the TransferListener to register with the event source
	 */
	public void addTransferListener(TransferListener listener) {
		transferListeners.addElement(listener);
	}

	/**
	 * add a set of TransferListeners to the list of transfer listeners
	 * 
	 * @param listeners
	 *            the TransferListeners to add to the list
	 */
	public void addTransferListeners(Vector listeners) {
		Enumeration e = listeners.elements();
		while (e.hasMoreElements()) {
			TransferListener listener = (TransferListener) e.nextElement();
			transferListeners.addElement(listener);
		}
	}

	/**
	 * De-register a TransferListener with the event source.
	 * 
	 * @param listener
	 *            the TransferListener to de-register with the event source
	 */
	public void removeTransferListener(TransferListener listener) {
		transferListeners.removeElement(listener);
	}

	/**
	 * signal that a connection has been opened
	 * 
	 * @param event
	 *            the event to distribute to each ConnectionListener
	 */
	protected void signalConnectionOpened(ConnectionEvent event) {
		Enumeration listeners = connectionListeners.elements();
		while (listeners.hasMoreElements()) {
			ConnectionListener listener = (ConnectionListener) listeners
					.nextElement();
			listener.connectionOpened(event);
		}
	}

	/**
	 * signal that a connection has been closed
	 * 
	 * @param event
	 *            the event to distribute to each ConnectionListener
	 */
	protected void signalConnectionClosed(ConnectionEvent event) {
		Enumeration listeners = connectionListeners.elements();
		while (listeners.hasMoreElements()) {
			ConnectionListener listener = (ConnectionListener) listeners
					.nextElement();
			listener.connectionClosed(event);
		}
	}

	/**
	 * signal that a connection has encountered an error
	 * 
	 * @param exception
	 *            the exception that was thrown
	 */
	protected void signalConnectionFailed(Exception exception) {
		Enumeration listeners = connectionListeners.elements();
		while (listeners.hasMoreElements()) {
			ConnectionListener listener = (ConnectionListener) listeners
					.nextElement();
			listener.connectionFailed(exception);
		}
	}

	/**
	 * signal that a transfer has started
	 */
	protected void signalTransferStarted() {
		Enumeration listeners = transferListeners.elements();
		while (listeners.hasMoreElements()) {
			TransferListener listener = (TransferListener) listeners
					.nextElement();
			listener.transferStarted();
		}
	}

	/**
	 * signal that a transfer has completed
	 */
	protected void signalTransferCompleted() {
		Enumeration listeners = transferListeners.elements();
		while (listeners.hasMoreElements()) {
			TransferListener listener = (TransferListener) listeners
					.nextElement();
			listener.transferCompleted();
		}
	}

	/**
	 * signal that a transfer has completed
	 * 
	 * @param amount
	 *            the amount of data (in octets) that has been transfered
	 */
	protected void signalTransfered(long amount) {
		Enumeration listeners = transferListeners.elements();
		while (listeners.hasMoreElements()) {
			TransferListener listener = (TransferListener) listeners
					.nextElement();
			listener.transfered(amount);
		}
	}

}

// Putter.java
