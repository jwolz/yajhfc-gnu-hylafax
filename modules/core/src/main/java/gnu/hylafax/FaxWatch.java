// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: FaxWatch.java 1 Feb 17, 2008 steve $
// ==============================================================================
package gnu.hylafax;

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
 * Implements a thread that performs the same functions as the faxwatch program distributed with hylafax server.
 * This thread allows for event listeners to be registered and sends connection, modem, send, receive, and job events.
 * 
 * The thead will attempt to maintain the client connection and will retry to connect upon a closed connection/
 *  
 * @version $Id: FaxWatch.java 1 Feb 17, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 */
public class FaxWatch implements Runnable {

    static int watcherNum = 0;

    private class Watcher extends HylaFAXClientProtocol implements Runnable, StatusEventSource {

        private String host = null;

        private int port = -1;

        private String timeZone = null;

        private String user = null;

        public void load() throws FaxWatchException {
            try {
                open(host, port);
                if (user != null && !user.equals("")) user(user);

                if (timeZone == null || timeZone.equals("")) {
                    tzone("LOCAL");
                } else tzone(timeZone);

                port(getInetAddress(), server.getLocalPort());
                site("trigger", getMask());

            } catch (Exception e) {
                //Log the error.
                log.error(e.getMessage(), e);

                //Remove and stop the watcher
                watchers.remove(this);
                stop();

                throw new FaxWatchException(e);
            }
        }

        private int options = 0;

        private Socket socket;

        private List statusEventListeners = Collections.synchronizedList(new ArrayList());

        public List getListeners() {
            return statusEventListeners;
        }

        private Thread watcher;

        public Watcher(String host, int port, String user, String timeZone) {
            watcher = new Thread(this, "FaxWatcher-" + watcherNum++);
            this.host = host;
            this.port = port;
            this.user = user;
            this.timeZone = timeZone;
        }

        public void addStatusEventListener(StatusEventListener listener) {
            statusEventListeners.add(listener);
        }

        public void addStatusEventListeners(List listeners) {
            statusEventListeners.addAll(listeners);
        }

        /**
         * @return the actual string representation of the events to receive from 
         * the hylafax server.
         */
        public String getMask() {
            String mask = "";
            if ((options & StatusEventListener.MODEM) == StatusEventListener.MODEM) mask += "M*";
            if ((options & StatusEventListener.SEND) == StatusEventListener.SEND) mask += "S*";
            if ((options & StatusEventListener.RECEIVE) == StatusEventListener.RECEIVE) mask += "R*";
            if ((options & StatusEventListener.JOB) == StatusEventListener.JOB) mask += "J*";
            return mask;
        }

        public void setOptions(int options) {
            this.options = options;
        }

        public int getOptions() {
            return options;
        }

        public void notify(String line) {
            log.debug(line);
        }

        public void removeStatusEventListener(StatusEventListener listener) {
            statusEventListeners.remove(listener);
            if (statusEventListeners.size() <= 0) {
                //This thread is no longer necessary.
                stop();
            }
        }

        public boolean started = false;

        public void run() {
            try {
                try {
                    String line = null;
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        while ((line = in.readLine()) != null) {
                            notify(line);
                        }
                    } finally {
                        in.close();
                    }
                } finally {
                    if (!socket.isClosed()) socket.close();
                }
            } catch (Exception e) {
                if (terminated && e instanceof SocketException)
                    log.debug(e.getMessage());
                else log.error(e.getMessage(), e);
            }
            System.err.println(watcher.getName() + ": stopped");
        }

        public void setSocket(Socket socket) throws FaxWatchException {
            if (!started) {
                this.socket = socket;
                watcher.start();
                started = true;
            } else throw new FaxWatchException("Thread already started.");
        }

        private boolean terminated = false;

        public void stop() {
            if (!terminated) {
                terminated = true;
                try {
                    quit();
                    watchers.remove(this);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    private static FaxWatch faxWatch = new FaxWatch();

    static final Log log = LogFactory.getLog(FaxWatch.class);

    static Map watchers = Collections.synchronizedMap(new HashMap());

    public static FaxWatch getInstance() {
        return faxWatch;
    }

    public static boolean isValidMask(int mask) {
        if (mask == 0) return true;
        if ((mask & StatusEventListener.MODEM) == StatusEventListener.MODEM) return true;
        if ((mask & StatusEventListener.SEND) == StatusEventListener.SEND) return true;
        if ((mask & StatusEventListener.RECEIVE) == StatusEventListener.RECEIVE) return true;
        if ((mask & StatusEventListener.JOB) == StatusEventListener.JOB) return true;
        return false;
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread("Hylafax1") {
            public void run() {
                try {
                    //                    HylaFAXClient client = new HylaFAXClient();
                    //
                    //                    client.open("10.0.0.205");
                    //                    client.user("autofax");
                    //
                    //                    client.addStatusEventListener(new AbstractStatusEventListener() {
                    //                        public int getEventMask() {
                    //                            return StatusEventListener.MODEM;
                    //                        }
                    //                    });
                    //                    Thread.sleep(10000);
                    //                    client.addStatusEventListener(new AbstractStatusEventListener() {
                    //                        public int getEventMask() {
                    //                            return StatusEventListener.RECEIVE;
                    //                        }
                    //                    });
                    //                    Thread.sleep(10000);
                    //                    client.addStatusEventListener(new AbstractStatusEventListener() {
                    //                        public int getEventMask() {
                    //                            return StatusEventListener.SEND | StatusEventListener.JOB;
                    //                        }
                    //                    });
                    //                    Thread.sleep(10000);
                    //                    client.addStatusEventListener(new AbstractStatusEventListener() {
                    //                        public int getEventMask() {
                    //                            return StatusEventListener.MODEM;
                    //                        }
                    //                    });
                    //                    client.quit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread2 = new Thread("Hylafax2") {
            public void run() {
                try {
                    HylaFAXClient client = new HylaFAXClient();

                    client.open("10.0.0.222");
                    client.user("autofax");

                    client.addStatusEventListener(new AbstractStatusEventListener() {
                        public int getEventMask() {
                            return StatusEventListener.MODEM;
                        }
                    });
                    Thread.sleep(5000);
                    client.addStatusEventListener(new AbstractStatusEventListener() {
                        public int getEventMask() {
                            return StatusEventListener.RECEIVE;
                        }
                    });
                    Thread.sleep(5000);
                    client.addStatusEventListener(new AbstractStatusEventListener() {
                        public int getEventMask() {
                            return StatusEventListener.SEND | StatusEventListener.JOB;
                        }
                    });
                    Thread.sleep(5000);
                    client.addStatusEventListener(new AbstractStatusEventListener() {
                        public int getEventMask() {
                            return StatusEventListener.MODEM;
                        }
                    });

                    Thread.sleep(100000);
                    client.quit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        thread2.start();
    }

    private boolean started = false;

    ServerSocket server;

    private Thread thread = new Thread(this, "FaxWatch");

    private FaxWatch() {
        // Nothing necessary here.
    }

    private String lastHost = null;

    private Object lastHostMutex = new Object();

    public synchronized void addStatusEventListener(String host, int port, String user, String timeZone,
            StatusEventListener listener) throws FaxWatchException {
        try {
            if (!started) {
                started = true;
                start();
            }

            int options = listener.getEventMask();
            if (!isValidMask(options)) throw new FaxWatchException("Invalid Options for StatusEventListener");

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
                watcher = new Watcher(host, port, user, timeZone);
                watcher.addStatusEventListeners(listeners);
                watcher.setOptions(opts | listener.getEventMask());
                synchronized (lastHostMutex) {
                    lastHost = host;
                }
                watcher.load();
            }

            watcher.addStatusEventListener(listener);
            watchers.put(host, watcher);

            log.debug("Number of Watchers: " + watchers.size());

        } catch (Exception e) {
            throw new FaxWatchException(e);
        }
    }

    /**
     * Remove listener.
     * 
     * @param listener the StatusEventListener to remove.
     */
    public void removeStatusEventListener(String host, StatusEventListener listener) throws FaxWatchException {
        try {
            Watcher watcher = (Watcher) watchers.get(host);
            if (watcher != null) watcher.removeStatusEventListener(listener);
        } catch (Exception e) {
            throw new FaxWatchException(e);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            server = new ServerSocket(0);
            try {
                while (!terminated && !server.isClosed()) {
                    Socket socket = server.accept();
                    Watcher watcher = (Watcher) watchers.get(lastHost);
                    if (watcher != null) {
                        synchronized (lastHostMutex) {
                            lastHost = null;
                        }
                        watcher.setSocket(socket);
                    } else {
                        if (!socket.isClosed()) socket.close();
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
            else log.error(e);
        }
    }

    /**
     * Start the server or reload the FaxWatcher client.  At any time there 
     * should be a maximum of 2 FaxWatcher threads running at a time.
     */
    private synchronized void start() {
        thread.start();
    }

    private boolean terminated = false;

    /**
     * Stops the FaxWatchers and FaxWatch thread.
     */
    public synchronized void stop() {
        if (!terminated) {
            terminated = true;

            // Stop the watchers.
            log.debug("Stopping " + watchers.size() + " watchers");

            Iterator iterator = watchers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Watcher watcher = ((Watcher) watchers.remove(key));
                if (watcher != null) watcher.stop();
            }

            if (server != null && !server.isClosed()) {
                try {
                    server.close();
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }
            server = null;
            started = false;
            faxWatch = new FaxWatch();
        }
    }
}
