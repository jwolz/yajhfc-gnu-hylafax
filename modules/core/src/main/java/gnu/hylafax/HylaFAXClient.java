// HylaFAXClient.java - a HylaFAX client protocol implementation in Java
// $Id: HylaFAXClient.java,v 1.13 2007/05/07 18:26:54 sjardine Exp $
//
// Copyright 1999, 2000 Joe Phillips <jaiger@net-foundry.com>
// Copyright 2001 Innovation Software Group, LLC - http://www.innovationsw.com
// Copyright 2006 John Yeary <jyeary@javanetwork.net>
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
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
package gnu.hylafax;

import gnu.inet.ftp.ActiveGetter;
import gnu.inet.ftp.ActivePutter;
import gnu.inet.ftp.ConnectionListener;
import gnu.inet.ftp.Getter;
import gnu.inet.ftp.PassiveConnection;
import gnu.inet.ftp.PassiveGetter;
import gnu.inet.ftp.PassivePutter;
import gnu.inet.ftp.Putter;
import gnu.inet.ftp.ServerResponseException;
import gnu.inet.ftp.TransferListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * This class implements convenience methods that wrapper the ClientProtocol methods for common functionality.
 * <P>
 * Most developers will want to use this class rather than the lower-level ClientProtocol methods directly.
 * 
 * @see ClientProtocol
 */
public class HylaFAXClient extends HylaFAXClientProtocol implements Client {

    // indicate whether passive transfers should be used
    private boolean passive;

    private char mode; // the current mode setting

    private Vector connectionListeners;

    private Vector transferListeners;

    /**
     * This is a cached PassiveConnection instance. After some hair-pulling I came to realize that in order to
     * avoid an annoying bug, I needed to cache the last PassiveConnection instance in case an error was
     * encountered in the last passive transfer. In my test cases the error condition that triggered the wierd
     * bug could be duplicated by attempting to getList(some-non-exising-file). This caused an exception
     * during the LIST transfer. The next PASV command would return the same IP/Port values. Opening a second
     * socket to the same IP/Port would yield an exception or other errors. Caching the last PassiveConnection
     * (and by extension the Socket) I could reuse the last PassiveConnection values avoiding the
     * bug/error-condition. This is not very pretty but it works. Unfortunately, I'm worried that it may not
     * be very portable since it relies on the behavior of the HylaFAX server. I'll have to see if this
     * behavior is in the FTP RFC (RFC0959.)
     * <P>
     * Whenever a successful passive transfer occurrs, this variable should be set to null, thereby
     * invalidating the cached value.
     */
    protected PassiveConnection connection = null;

    /**
     * default constructor. initialize class state.
     */
    public HylaFAXClient() {
        passive = false; // disable passive transfers by default
        mode = MODE_STREAM; // default mode is stream mode
        connectionListeners = new Vector();
        transferListeners = new Vector();
    }

    /**
     * enable or disable passive transfers
     * 
     * @param passive indicates whether passive transfers should be used
     */
    public synchronized void setPassive(boolean passive) {
        this.passive = passive;
    }

    /**
     * Check whether passive transfers have been enabled
     * 
     * @return true if passive transfers are enabled, false otherwise
     */
    public synchronized boolean getPassive() {
        return passive;
    }

    /**
     * Set the transfer mode. Valid mode values are MODE_* listed in the ClientProtocol class.
     * 
     * @param mode the new mode setting
     * @exception IOException an io error occurred talking to the server
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void mode(char mode) throws IOException, ServerResponseException {
        super.mode(mode);
        this.mode = mode; // cache the mode for later use
    }

    /**
     * Put a temp file, the data is stored in a uniquely named file on the server. The remote temp file is
     * deleted when the connection is closed.
     * 
     * <b>NOTE:</b> this calls stot() internally.
     * 
     * @exception IOException io error occurred talking to the server
     * @exception ServerResponseException server replied with error code
     * @return the filename of the temp file
     */
    public synchronized String putTemporary(InputStream data) throws IOException, ServerResponseException {
        String filename;
        Putter put;

        if (passive == true) {
            // do a passive transfer
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }

            put = new PassivePutter(data, connection);

        } else {
            // do a non-passive (active) transfer
            ActivePutter aput = new ActivePutter(data);
            put = aput;
            port(getInetAddress(), aput.getPort());
        }

        put.setDebug(getDebug());
        put.setMode(mode);
        put.addConnectionListeners(connectionListeners);
        put.addTransferListeners(transferListeners);
        put.start();

        // start transmission
        try {
            filename = stot(data);
        } catch (IOException ioe) {
            put.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            put.cancel();
            throw sree;
        } finally {
            // wait for thread to end
            try {
                put.join();
            } catch (InterruptedException ie) { /* not really an error */
            }
        }

        connection = null;

        return filename;
    }

    /**
     * put a file with a unique name.
     * 
     * <b>NOTE:</b> this calls stou() internally.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error code
     * @return the name of the file created
     */
    public synchronized String put(InputStream in) throws IOException, ServerResponseException {
        String filename;
        Putter put;

        if (passive == true) {
            // passive mode transfers
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }
            put = new PassivePutter(in, connection);
        } else {
            // non-passive (active) transfers
            ActivePutter aput = new ActivePutter(in);
            put = aput;
            port(getInetAddress(), aput.getPort()); // tell server where to
            // send data
        }

        put.setDebug(getDebug());
        put.setMode(mode);
        put.addConnectionListeners(connectionListeners);
        put.addTransferListeners(transferListeners);
        put.start();

        // transfer file
        try {
            filename = stou(in);
        } catch (IOException ioe) {
            put.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            put.cancel();
            throw sree;
        } finally {
            // shut down data thread
            try {
                put.join();
            } catch (InterruptedException ie) { /* not really an error */
            }
        }

        connection = null;

        return filename;
    }

    /**
     * Store a file.
     * 
     * <b>NOTE:</b> this calls stor() internally.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error
     * @param pathname name of file to store on server (where to put the file on the server)
     */
    public synchronized void put(InputStream in, String pathname) throws IOException, ServerResponseException {

        Putter put;

        if (passive == true) {
            // passive mode transfers
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }

            put = new PassivePutter(in, connection);

        } else {
            // non-passive (active) transfers
            ActivePutter aput = new ActivePutter(in);
            put = aput;
            port(getInetAddress(), aput.getPort()); // tell server where to
            // send data
        }

        put.setDebug(getDebug());
        put.setMode(mode);
        put.addConnectionListeners(connectionListeners);
        put.addTransferListeners(transferListeners);
        put.start();

        // transfer file
        try {
            stor(in, pathname);
        } catch (IOException ioe) {
            put.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            put.cancel();
            throw sree;
        } finally {
            // stop putter thread
            try {
                put.join();
            } catch (InterruptedException ie) { /* not really an error */
            }
        }

        connection = null;
    }

    /**
     * Get a long-style listing of files in the given directory.
     * 
     * <b>NOTE:</b> this calls the list() method internally.
     * 
     * @param path the path that we're interested in finding the contents of
     * @exception IOException an IO error occurred
     * @exception FileNotFoundException the given path doesn't exist
     * @exception ServerResponseException the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public synchronized Vector getList(String path) throws IOException, FileNotFoundException, ServerResponseException {
        Vector filenames = new Vector();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Getter spot;

        if (passive == true) {
            // do passive style transfers
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }

            spot = new PassiveGetter(buffer, connection);

        } else {
            ActiveGetter aget = new ActiveGetter(buffer);
            // tell server which port we'll listen on
            port(getInetAddress(), aget.getPort());
            spot = aget;
        }

        // start transfer
        spot.setDebug(getDebug());
        spot.addConnectionListeners(connectionListeners);
        spot.addTransferListeners(transferListeners);
        spot.start();

        // start the listing ...
        try {
            list(path);
        } catch (FileNotFoundException fnfe) {
            spot.cancel();
            throw fnfe;
        } catch (IOException ioe) {
            spot.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            spot.cancel();
            throw sree;
        } finally {
            // wait for thread to complete
            try {
                spot.join();
            } catch (InterruptedException ie) { /* no error */
            }
        }

        connection = null;

        // make list of filenames
        BufferedReader data = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));

        boolean done = false;

        while (!done) {
            String line = data.readLine();

            if (line == null) {
                done = true;
            } else {
                filenames.addElement(line);
            }
        }

        return filenames;
    }

    /**
     * Get a long-style listing of files in the current directory.
     * 
     * <b>NOTE:</b> this calls the list() method internally with the "." path.
     * 
     * @exception IOException an IO error occurred
     * @exception FileNotFoundException the "." path doesn't exist
     * @exception ServerResponseException the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public synchronized Vector getList() throws IOException, FileNotFoundException, ServerResponseException {
        return getList(null);
    }

    /**
     * Get name list of files in the given directory. Similar to getList() but returns filenames only where
     * getList() returns other, system dependant information.
     * 
     * @param path the path of the directory that we want the name list of
     * @exception IOException an IO error occurred
     * @exception ServerResponseException the server reported an error
     * @exception FileNotFoundException the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public synchronized Vector getNameList(String path) throws IOException, ServerResponseException,
            FileNotFoundException {
        Vector filenames = new Vector();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Getter sparky;

        if (passive == true) {
            // do passive style transfers
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }

            sparky = new PassiveGetter(buffer, connection);
        } else {
            ActiveGetter aget = new ActiveGetter(buffer);
            // tell server which port we'll listen on
            port(getInetAddress(), aget.getPort());
            sparky = aget;
        }

        // start transfer
        sparky.setDebug(getDebug());
        sparky.addConnectionListeners(connectionListeners);
        sparky.addTransferListeners(transferListeners);
        sparky.start();

        // initiate the nlst command...
        try {
            nlst(path);
        } catch (FileNotFoundException fnfe) {
            sparky.cancel();
            throw fnfe;
        } catch (IOException ioe) {
            sparky.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            sparky.cancel();
            throw sree;
        } finally {
            // transfer complete
            try {
                sparky.join();
            } catch (InterruptedException ie) { /* it's ok */
            }
        }

        connection = null;

        // build file list
        BufferedReader data = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));

        boolean done = false;

        while (!done) {
            String line = data.readLine();
            if (line == null) {
                done = true;
            } else {
                filenames.addElement(line);
            }
        }

        return filenames;
    }

    /**
     * get name list of files in the current directory. Similar to getList() but returns filenames only where
     * getList() returns other, system dependant information.
     * 
     * @exception IOException an IO error occurred
     * @exception ServerResponseException the server reported an error
     * @exception FileNotFoundException the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public synchronized Vector getNameList() throws IOException, ServerResponseException, FileNotFoundException {
        return getNameList(null);
    }

    /**
     * GET the named file, FTP style.
     * 
     * @param path the name of the file to GET. This can be a full or partial path.
     * @param out the OutputStream to write the file data to
     * @exception IOException an IO error occurred
     * @exception ServerResponseException the server reported an error
     * @exception FileNotFoundException the given path does not exist
     */
    public synchronized void get(String path, OutputStream out) throws IOException, FileNotFoundException,
            ServerResponseException {
        Getter get;

        if (passive == true) {
            // do a passive transfer
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }
            get = new PassiveGetter(out, connection);

        } else {
            // do a non-passive (active) transfer
            ActiveGetter aget = new ActiveGetter(out);
            get = aget;
            port(getInetAddress(), aget.getPort());
        }

        get.setDebug(getDebug());
        get.setMode(mode);
        get.addConnectionListeners(connectionListeners);
        get.addTransferListeners(transferListeners);
        get.start();

        // start transmission
        try {
            retr(path);
        } catch (FileNotFoundException fnfe) {
            get.cancel();
            throw fnfe;
        } catch (IOException ioe) {
            get.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            get.cancel();
            throw sree;
        } finally {
            // wait for thread to end
            try {
                get.join();
            } catch (InterruptedException ie) { /* not really an error */
            }
        }

        connection = null;

    }

    /**
     * Create a new job in the server
     * 
     * @return a new Job instance on the server
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public Job createJob() throws ServerResponseException, IOException {
        return new gnu.hylafax.job.Job(this);
    }

    /**
     * Get a Job instance for the given job id
     * 
     * @param id the id of the job to get
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public Job getJob(long id) throws ServerResponseException, IOException {
        return new gnu.hylafax.job.Job(this, id);
    }

    /**
     * Submit the given job to the scheduler.
     * 
     * @param job the Job to submit
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void submit(Job job) throws ServerResponseException, IOException {
        jsubm(job.getId());
    }

    /**
     * Retry a given job with a default killtime of "now + 3 hours".
     * 
     * @param id the job id to retry.
     * @return the job id associated with the new job.
     * @throws ServerResponseException
     * @throws IOException
     */
    public long retry(long id) throws ServerResponseException, IOException {
        return retry(id, "000259");
    }

    /**
     * Retry a given job.
     * 
     * @param id the job id to retry.
     * @param killTime the new killTime for the job.
     * @return the job id associated with the new job.
     * @throws ServerResponseException
     * @throws IOException
     */
    public long retry(long id, String killTime) throws ServerResponseException, IOException {
        job(id);
        // parse the document names.
        List documents = new ArrayList();
        String[] docs = jparm("document").split("\n");
        for (int count = 0; count < docs.length; count++) {
            String document = docs[count];
            if (document.equals("End of documents.")) break;
            documents.add(document.split(" ")[1]);
        }
        jnew();
        for (int index = 0; index < documents.size(); index++) {
            jparm("document", documents.get(index));
        }
        jparm("lasttime", killTime);
        return jsubm();
    }

    /**
     * Delete the given done or suspended job.
     * 
     * @param job the (done or suspended) job to delete
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void delete(Job job) throws ServerResponseException, IOException {
        jdele(job.getId());
    }

    /**
     * Suspend the given job from the scheduler.
     * 
     * @param job the Job to suspend
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void suspend(Job job) throws ServerResponseException, IOException {
        jsusp(job.getId());
    }

    /**
     * wait for the given job to complete
     * 
     * @param job the job to wait for
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void wait(Job job) throws ServerResponseException, IOException {
        jwait(job.getId());
    }

    /**
     * Kill the given job
     * 
     * @param job the job to kill
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void kill(Job job) throws ServerResponseException, IOException {
        jkill(job.getId());
    }

    /**
     * Interrupt the given job.
     * 
     * @param job the job to interrupt
     * @exception ServerResponseException
     * @exception IOException an IO error occurred while communicating with the server
     */
    public void interrupt(Job job) throws ServerResponseException, IOException {
        jintr(job.getId());
    }

    /**
     * Register a connection listener with the event source.
     * 
     * @param listener the listener to register with the event source
     */
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.addElement(listener);
    }

    /**
     * Register a set of connection listeners with the event source.
     * 
     * @param listeners the listeners to register with the event source
     */
    public void addConnectionListeners(Vector listeners) {
        Enumeration enumeration = listeners.elements();

        while (enumeration.hasMoreElements()) {
            ConnectionListener listener = (ConnectionListener) enumeration.nextElement();
            connectionListeners.addElement(listener);
        }
    }

    /**
     * De-register a connection listener with the event source.
     * 
     * @param listener the listener to de-register with the event source
     */
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.removeElement(listener);
    }

    /**
     * Register a transfer listener with the event source.
     * 
     * @param listener the listener to register with the event source
     */
    public void addTransferListener(TransferListener listener) {
        transferListeners.addElement(listener);
    }

    /**
     * Register a set of transfer listeners with the event source.
     * 
     * @param listeners the listeners to register with the event source
     */
    public void addTransferListeners(Vector listeners) {
        Enumeration enumeration = listeners.elements();

        while (enumeration.hasMoreElements()) {
            TransferListener listener = (TransferListener) enumeration.nextElement();
            transferListeners.addElement(listener);
        }
    }

    /**
     * De-register a transfer listener with the event source.
     * 
     * @param listener the listener to de-register with the event source
     */
    public void removeTransferListener(TransferListener listener) {
        transferListeners.removeElement(listener);
    }
}