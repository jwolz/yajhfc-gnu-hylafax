// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: FaxWatch.java 1 Feb 17, 2008 steve $
// ==============================================================================
package gnu.hylafax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

/**
 * Implements a thread that performs the same functions as the faxwatch program distributed with hylafax server.
 * This thread allows for event listeners to be registered and sends connection, modem, send, receive, and job events.
 * 
 * The thead will attempt to maintain the client connection and will retry to connect upon a closed connection/
 *  
 * @version $Id: FaxWatch.java 1 Feb 17, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 */
public class FaxWatch extends HylaFAXClientProtocol {

    static final Log log = LogFactory.getLog(FaxWatch.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            Client client = new HylaFAXClient();
            client.addStatusEventListener(new AbstractStatusEventListener() {
                public int getEventMask() {
                    return StatusEventListener.MODEM;
                }
            });
            client.addStatusEventListener(new AbstractStatusEventListener() {
                public int getEventMask() {
                    return StatusEventListener.RECEIVE;
                }
            });
            client.addStatusEventListener(new AbstractStatusEventListener() {
                public int getEventMask() {
                    return StatusEventListener.SEND | StatusEventListener.JOB;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
