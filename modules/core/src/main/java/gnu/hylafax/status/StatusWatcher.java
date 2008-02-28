/*******************************************************************************
 * $Id: StatusWatcher.java 84 2008-02-21 23:08:12Z sjardine $
 * 
 * Copyright 2008, Steven Jardine <steve@mjnservices.com>
 * Copyright 2008, MJN Services, Inc. - http://www.mjnservices.com
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
 * 	Steven Jardine - Initial API and implementation
 ******************************************************************************/
package gnu.hylafax.status;

import gnu.hylafax.HylaFAXClientProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements a daemon that performs the same functions as the faxwatch program
 * distributed with hylafax server. This thread allows for event listeners to be
 * registered and sends connection, modem, send, receive, and job events.
 * 
 * @version $Revision: 84 $
 * @author Steven Jardine <steve@mjnservices.com>
 */
public class StatusWatcher implements Runnable {

    private class ListenerWrapper {

	private int eventMask = -1;

	private String id = null;

	private StatusEventListener listener = null;

	private int typeMask = -1;

	public ListenerWrapper(StatusEventListener listener, int typeMask,
		int eventMask, String id) {
	    this.listener = listener;
	    this.typeMask = typeMask;
	    this.eventMask = eventMask;
	    this.id = id;
	}

	public int getEventMask() {
	    return eventMask;
	}

	public String getId() {
	    return id;
	}

	public StatusEventListener getListener() {
	    return listener;
	}

	public int getTypeMask() {
	    return typeMask;
	}

	public void setEventMask(int eventMask) {
	    this.eventMask = eventMask;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public void setListener(StatusEventListener listener) {
	    this.listener = listener;
	}

	public void setTypeMask(int typeMask) {
	    this.typeMask = typeMask;
	}

    }

    private class Watcher extends HylaFAXClientProtocol implements Runnable {

	private String host = null;

	final Log log = LogFactory.getLog(Watcher.class);

	private int options = 0;

	private int port = -1;

	private Socket socket;

	public boolean started = false;

	private List statusEventListeners = Collections
		.synchronizedList(new ArrayList());

	private boolean terminated = false;

	private String timeZone = null;

	private String user = null;

	private Thread watcher;

	public Watcher(String host, int port, String user, String timeZone) {
	    watcher = new Thread(this, "FaxWatcher-" + host);
	    this.host = host;
	    this.port = port;
	    this.user = user;
	    this.timeZone = timeZone;
	}

	public void addStatusEventListener(ListenerWrapper listener) {
	    statusEventListeners.add(listener);
	}

	public void addStatusEventListeners(List listeners) {
	    statusEventListeners.addAll(listeners);
	}

	public List getListeners() {
	    return statusEventListeners;
	}

	/**
	 * @return the actual string representation of the events to receive
	 *         from the hylafax server.
	 */
	public String getMask() {
	    return StatusWatcher.getMask(options);
	}

	public int getOptions() {
	    return options;
	}

	public void load() throws StatusEventException {
	    try {
		open(host, port);
		if (user != null && !user.equals(""))
		    user(user);

		if (timeZone == null || timeZone.equals("")) {
		    tzone("LOCAL");
		} else
		    tzone(timeZone);

		port(getInetAddress(), server.getLocalPort());
		site("trigger", getMask());

	    } catch (Exception e) {
		// Log the error.
		log.error(e.getMessage(), e);

		// Stop the watcher.
		stop();

		throw new StatusEventException(e);
	    }
	}

	public synchronized void notify(final String line) {

	    if (line == null || line.equals(""))
		return;

	    int eventId = -1;
	    String eventStr = line.split(" ")[1];
	    try {
		eventId = Integer.parseInt(line.split(" ")[1]);
	    } catch (NumberFormatException e) {
		// This shouldn't happen but if it does we don't know how to
		// handle it.
		log.error("Unknown Event: " + eventStr + " ... Discarding.");
		return;
	    }

	    final Event event = Event.getEvent(eventId);
	    final int eventType = event.getType();

	    final String id = line.split(" ")[3];

	    log.debug(event.getTypeStr() + " event received");

	    // Notify the listeners.
	    ArrayList threads = new ArrayList();
	    Iterator iterator = statusEventListeners.iterator();
	    while (iterator.hasNext()) {
		final ListenerWrapper wrapper = (ListenerWrapper) iterator
			.next();

		final int typeMask = wrapper.getTypeMask();
		if ((typeMask & eventType) == 0) {
		    continue;
		}

		final int eventMask = wrapper.getEventMask();
		if ((eventMask & event.getMask()) == 0) {
		    continue;
		}

		final String listenerId = wrapper.getId();
		if (listenerId != null && !listenerId.equals(id)) {
		    continue;
		}

		Thread t = new Thread() {
		    public void run() {
			// Notify the listeners.
			StatusEvent statusEvent = event.createStatusEvent(line);
			if ((typeMask & Event.MODEM) != 0
				&& statusEvent instanceof ModemStatusEvent) {
			    wrapper.getListener().modemEventReceived(
				    statusEvent);
			}
			if ((typeMask & Event.SEND) != 0
				&& statusEvent instanceof SendStatusEvent) {
			    wrapper.getListener()
				    .sendEventReceived(statusEvent);
			}
			if ((typeMask & Event.RECEIVE) != 0
				&& statusEvent instanceof ReceiveStatusEvent) {
			    wrapper.getListener().receiveEventReceived(
				    statusEvent);
			}
			if ((typeMask & Event.JOB) != 0
				&& statusEvent instanceof JobStatusEvent) {
			    wrapper.getListener().jobEventReceived(statusEvent);
			}
		    }
		};
		t.start();
		threads.add(t);
	    }
	}

	public void removeStatusEventListener(StatusEventListener listener) {
	    statusEventListeners.remove(listener);
	    if (statusEventListeners.size() <= 0) {
		// This thread is no longer necessary.
		stop();
	    }
	}

	public void run() {
	    log.debug("starting watcher thread: " + watcher.getName());
	    try {
		try {
		    String line = null;
		    BufferedReader in = new BufferedReader(
			    new InputStreamReader(socket.getInputStream()));
		    try {
			log.debug("reading input stream");
			while ((line = in.readLine()) != null) {
			    log.debug("message received: " + line);
			    notify(line);
			}
		    } finally {
			in.close();
		    }
		} finally {
		    if (!socket.isClosed())
			socket.close();
		}
	    } catch (Exception e) {
		if (terminated && e instanceof SocketException)
		    log.debug(e.getMessage());
		else
		    log.error(e.getMessage(), e);
	    }
	    log.debug("thread stopped: " + watcher.getName());
	}

	public void setOptions(int options) {
	    this.options = options;
	}

	public void setSocket(Socket socket) throws StatusEventException {
	    if (!started) {
		this.socket = socket;
		watcher.start();
		started = true;
	    } else
		throw new StatusEventException("Thread already started.");
	}

	public void stop() {
	    if (!terminated) {
		terminated = true;
		try {
		    quit();

		    log.debug("removing watcher: " + watcher.getName());
		    watchers.remove(socket.getInetAddress().getHostAddress());
		} catch (Exception e) {
		    log.warn(e.getMessage(), e);
		}
	    }
	}
    }

    static final Log log = LogFactory.getLog(StatusWatcher.class);

    private static StatusWatcher statusWatcher = new StatusWatcher();

    static Map watchers = Collections.synchronizedMap(new HashMap());

    public static StatusWatcher getInstance() {
	return statusWatcher;
    }

    /**
     * @return the actual string representation of the events to receive from
     *         the hylafax server.
     */
    public static String getMask(int options) {
	String mask = "";
	if ((options & Event.MODEM) != 0)
	    mask += "M*";
	if ((options & Event.SEND) != 0)
	    mask += "S*";
	if ((options & Event.RECEIVE) != 0)
	    mask += "R*";
	if ((options & Event.JOB) != 0)
	    mask += "J*";
	return mask;
    }

    public static boolean isValidMask(int mask) {
	if (mask == 0)
	    return true;
	if ((mask & Event.MODEM) != 0)
	    return true;
	if ((mask & Event.SEND) != 0)
	    return true;
	if ((mask & Event.RECEIVE) != 0)
	    return true;
	if ((mask & Event.JOB) != 0)
	    return true;
	return false;
    }

    public static void main(String[] args) {
	org.apache.log4j.BasicConfigurator.configure();
	final String host1 = "10.0.0.205";
	try {
	    StatusWatcher.getInstance().addStatusEventListener(
		    host1,
		    4559,
		    "autofax",
		    null,
		    new FileStatusEventListener(
			    "/home/steve/Desktop/modem-messages.txt"),
		    Event.MODEM, Event.ALL, null);

	    StatusWatcher.getInstance().addStatusEventListener(
		    host1,
		    4559,
		    "autofax",
		    null,
		    new FileStatusEventListener(
			    "/home/steve/Desktop/send-messages.txt"),
		    Event.SEND, Event.ALL, null);

	    StatusWatcher.getInstance().addStatusEventListener(
		    "10.0.0.222",
		    4559,
		    "autofax",
		    null,
		    new FileStatusEventListener(
			    "/home/steve/Desktop/receive-messages.txt"),
		    Event.RECEIVE, Event.ALL, null);

	    StatusWatcher.getInstance().addStatusEventListener(
		    host1,
		    4559,
		    "autofax",
		    null,
		    new FileStatusEventListener(
			    "/home/steve/Desktop/job-messages.txt"), Event.JOB,
		    Event.ALL, null);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    ServerSocket server = null;

    private boolean started = false;

    private boolean terminated = false;

    private Thread thread = new Thread(this, "FaxWatch");

    private StatusWatcher() {
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    public void run() {
		log.debug("starting shutdown hook");
		StatusWatcher.getInstance().stop();
	    }
	});
    }

    public synchronized void addStatusEventListener(String host, int port,
	    String user, String timeZone, StatusEventListener listener,
	    int typeMask, int eventMask, String id) throws StatusEventException {
	try {
	    ListenerWrapper wrapper = new ListenerWrapper(listener, typeMask,
		    eventMask, id);

	    if (!started) {
		started = true;
		start();
	    }

	    int options = wrapper.getTypeMask();
	    if (!isValidMask(options))
		throw new StatusEventException(
			"Invalid Options for StatusEventListener");

	    boolean load = false;
	    List listeners = new ArrayList();
	    int opts = 0;
	    Watcher watcher = (Watcher) watchers.remove(host);
	    if (watcher != null) {
		if (watcher.getOptions() != options) {
		    listeners = watcher.getListeners();
		    watcher.stop();
		    opts = watcher.getOptions();
		    watcher = null;
		}
	    }

	    if (watcher == null) {
		load = true;
		watcher = new Watcher(host, port, user, timeZone);
		watcher.addStatusEventListeners(listeners);
		watcher.setOptions(opts | options);
	    }

	    watchers.put(host, watcher);
	    watcher.addStatusEventListener(wrapper);
	    if (load)
		watcher.load();

	    log.debug("Number of Watchers: " + watchers.size());

	} catch (Exception e) {
	    throw new StatusEventException(e);
	}
    }

    /**
     * Remove listener.
     * 
     * @param listener
     *                the StatusEventListener to remove.
     */
    public void removeStatusEventListener(String host,
	    StatusEventListener listener) throws StatusEventException {
	try {
	    Watcher watcher = (Watcher) watchers.get(host);
	    if (watcher != null)
		watcher.removeStatusEventListener(listener);
	} catch (Exception e) {
	    throw new StatusEventException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
	log.debug("starting server socket");
	try {
	    server = new ServerSocket(0);
	    try {
		while (!terminated && !server.isClosed()) {
		    log.debug("waiting for client connection");
		    Socket socket = server.accept();
		    log.debug("client connected");

		    Watcher watcher = (Watcher) watchers.get(socket
			    .getInetAddress().getHostAddress());
		    if (watcher != null) {
			watcher.setSocket(socket);
		    } else {
			log.warn("Cannot find watcher for: "
				+ socket.getInetAddress().getHostAddress());
			if (!socket.isClosed())
			    socket.close();
		    }
		}
	    } finally {
		if (server != null && !server.isClosed()) {
		    server.close();
		}
		server = null;
	    }
	} catch (Exception e) {
	    if (terminated && e instanceof SocketException)
		log.debug(e.getMessage());
	    else
		log.error(e);
	}
    }

    private synchronized void start() {
	thread.start();
    }

    /**
     * Stops the FaxWatchers and FaxWatch thread.
     */
    public synchronized void stop() {
	if (!terminated) {
	    terminated = true;

	    // Stop the watchers.
	    log.debug("stopping " + watchers.size() + " watchers");

	    ArrayList keys = new ArrayList();
	    Iterator iterator = watchers.keySet().iterator();
	    while (iterator.hasNext()) {
		keys.add(iterator.next());
	    }
	    iterator = keys.iterator();
	    while (iterator.hasNext()) {
		Watcher watcher = ((Watcher) watchers.remove(iterator.next()));
		if (watcher != null)
		    watcher.stop();
	    }

	    log.debug("stopping FaxWatch server");
	    if (server != null && !server.isClosed()) {
		try {
		    server.close();
		    server = null;
		} catch (IOException e) {
		    log.warn(e.getMessage(), e);
		}
	    }
	    started = false;
	}
    }
}
