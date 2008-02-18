// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: FaxWatch.java 1 Feb 17, 2008 steve $
// ==============================================================================
package gnu.hylafax.events;

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

    private Thread thread = new Thread(this);

    private FaxWatch() {
        thread.start();
    }

    public void run() {

    }

    /**
     * Manages the connection to the hylafax server.
     */
    private class Connection implements Runnable {

        private Thread thread = new Thread(this);

        public void run() {

        }

    }
}
