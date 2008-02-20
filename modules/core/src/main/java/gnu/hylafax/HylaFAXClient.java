// HylaFAXClient.java - a HylaFAX client protocol implementation in Java
// $Id: HylaFAXClient.java,v 1.13 2007/05/07 18:26:54 sjardine Exp $
//
// Copyright 1999, 2000 Joe Phillips <jaiger@net-foundry.com>
// Copyright 2001 Innovation Software Group, LLC - http://www.innovationsw.com
// Copyright 2006 John Yeary <jyeary@javanetwork.net>
// Copyright 2007 Steven Jardine, MJN Services, Inc. <sjardine@users.sourceforge.net>
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements convenience methods that wrapper the ClientProtocol
 * methods for common functionality.
 * <P>
 * Most developers will want to use this class rather than the lower-level
 * ClientProtocol methods directly.
 * 
 * @see ClientProtocol
 */
public class HylaFAXClient extends HylaFAXClientProtocol implements Client {

    private static final int GET = 0;

    private static final int LIST = 1;

    private static final int NAMELIST = 2;

    private static final Log log = LogFactory.getLog(HylaFAXClient.class);

    private List statusEventListeners = Collections.synchronizedList(new ArrayList());

    public void addStatusEventListener(StatusEventListener listener) {
        try {
            FaxWatch.getInstance().addStatusEventListener(hylafaxServerHost, hylafaxServerPort, hylafaxServerUsername,
                hylafaxServerTimeZone, listener);
            statusEventListeners.add(listener);
        } catch (FaxWatchException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void addStatusEventListeners(List listeners) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            addStatusEventListener((StatusEventListener) iterator.next());
        }
        statusEventListeners.addAll(listeners);
    }

    public void removeStatusEventListener(StatusEventListener listener) {
        try {
            FaxWatch.getInstance().removeStatusEventListener(hylafaxServerHost, listener);
            statusEventListeners.remove(listener);
        } catch (FaxWatchException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * This is a cached PassiveConnection instance. After some hair-pulling I
     * came to realize that in order to avoid an annoying bug, I needed to cache
     * the last PassiveConnection instance in case an error was encountered in
     * the last passive transfer. In my test cases the error condition that
     * triggered the wierd bug could be duplicated by attempting to
     * getList(some-non-exising-file). This caused an exception during the LIST
     * transfer. The next PASV command would return the same IP/Port values.
     * Opening a second socket to the same IP/Port would yield an exception or
     * other errors. Caching the last PassiveConnection (and by extension the
     * Socket) I could reuse the last PassiveConnection values avoiding the
     * bug/error-condition. This is not very pretty but it works. Unfortunately,
     * I'm worried that it may not be very portable since it relies on the
     * behavior of the HylaFAX server. I'll have to see if this behavior is in
     * the FTP RFC (RFC0959.)
     * <P>
     * Whenever a successful passive transfer occurrs, this variable should be
     * set to null, thereby invalidating the cached value.
     */
    protected PassiveConnection connection = null;

    private Vector connectionListeners;

    private char mode; // the current mode setting

    // indicate whether passive transfers should be used
    private boolean passive;

    private Vector transferListeners;

    /**
     * default constructor. initialize class state.
     */
    public HylaFAXClient() {
        passive = false; // disable passive transfers by default
        mode = MODE_STREAM; // default mode is stream mode
        connectionListeners = new Vector();
        transferListeners = new Vector();
    }

    /* (non-Javadoc)
     * @see gnu.inet.ftp.FtpClientProtocol#quit()
     */
    public void quit() throws IOException, ServerResponseException {
        //Remove all status event listeners.  This will shutdown the thread if all listeners are removed.
        ArrayList listeners = new ArrayList();
        Iterator iterator = statusEventListeners.iterator();
        while (iterator.hasNext()) {
            listeners.add(iterator.next());
        }
        iterator = listeners.iterator();
        while (iterator.hasNext()) {
            removeStatusEventListener((StatusEventListener) iterator.next());
        }

        super.quit();
    }

    /**
     * Register a connection listener with the event source.
     * 
     * @param listener
     *            the listener to register with the event source
     */
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.addElement(listener);
    }

    /**
     * Register a set of connection listeners with the event source.
     * 
     * @param listeners
     *            the listeners to register with the event source
     */
    public void addConnectionListeners(Vector listeners) {
        Enumeration enumeration = listeners.elements();

        while (enumeration.hasMoreElements()) {
            ConnectionListener listener = (ConnectionListener) enumeration.nextElement();
            connectionListeners.addElement(listener);
        }
    }

    /**
     * Register a transfer listener with the event source.
     * 
     * @param listener
     *            the listener to register with the event source
     */
    public void addTransferListener(TransferListener listener) {
        transferListeners.addElement(listener);
    }

    /**
     * Register a set of transfer listeners with the event source.
     * 
     * @param listeners
     *            the listeners to register with the event source
     */
    public void addTransferListeners(Vector listeners) {
        Enumeration enumeration = listeners.elements();

        while (enumeration.hasMoreElements()) {
            TransferListener listener = (TransferListener) enumeration.nextElement();
            transferListeners.addElement(listener);
        }
    }

    /**
     * Create a new job in the server
     * 
     * @return a new Job instance on the server
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public Job createJob() throws ServerResponseException, IOException {
        return new gnu.hylafax.job.Job(this);
    }

    /**
     * Delete the given done or suspended job.
     * 
     * @param job
     *            the (done or suspended) job to delete
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void delete(Job job) throws ServerResponseException, IOException {
        jdele(job.getId());
    }

    /**
     * GET the named file, FTP style.
     * 
     * @param path
     *            the name of the file to GET. This can be a full or partial
     *            path.
     * @param out
     *            the OutputStream to write the file data to
     * @exception IOException
     *                an IO error occurred
     * @exception ServerResponseException
     *                the server reported an error
     * @exception FileNotFoundException
     *                the given path does not exist
     */
    public synchronized void get(String path, OutputStream out) throws IOException, FileNotFoundException,
            ServerResponseException {
        get(path, out, GET);
    }

    private synchronized void get(String path, OutputStream out, int type) throws IOException, FileNotFoundException,
            ServerResponseException {

        Getter getter;
        if (passive == true) {
            // do a passive transfer
            if (connection == null) {
                connection = new PassiveConnection(pasv());
            }
            getter = new PassiveGetter(out, connection);
        } else {
            getter = new ActiveGetter(out);
            // do a non-passive (active) transfer
            port(getInetAddress(), ((ActiveGetter) getter).getPort());
        }

        // start transfer
        getter.addConnectionListeners(connectionListeners);
        getter.addTransferListeners(transferListeners);
        getter.start();

        // start transmission
        try {
            switch (type) {
            case GET:
                retr(path);
                break;
            case LIST:
                list(path);
                break;
            case NAMELIST:
                nlst(path);
                break;
            }
        } catch (FileNotFoundException fnfe) {
            getter.cancel();
            throw fnfe;
        } catch (IOException ioe) {
            getter.cancel();
            throw ioe;
        } catch (ServerResponseException sree) {
            getter.cancel();
            throw sree;
        } finally {
            // wait for thread to end
            try {
                getter.join();
            } catch (InterruptedException ie) {
                // not really an error
            }
        }
        connection = null;
    }

    /**
     * Get a Job instance for the given job id
     * 
     * @param id
     *            the id of the job to get
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public Job getJob(long id) throws ServerResponseException, IOException {
        return new gnu.hylafax.job.Job(this, id);
    }

    /**
     * Get a long-style listing of files in the current directory.
     * 
     * <b>NOTE:</b> this calls the list() method internally with the "." path.
     * 
     * @exception IOException
     *                an IO error occurred
     * @exception FileNotFoundException
     *                the "." path doesn't exist
     * @exception ServerResponseException
     *                the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public synchronized Vector getList() throws IOException, FileNotFoundException, ServerResponseException {
        return getList(null, false);
    }

    /**
     * Get a long-style listing of files in the given directory.
     * 
     * <b>NOTE:</b> this calls the list() method internally.
     * 
     * @param path
     *            the path that we're interested in finding the contents of
     * @exception IOException
     *                an IO error occurred
     * @exception FileNotFoundException
     *                the given path doesn't exist
     * @exception ServerResponseException
     *                the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public synchronized Vector getList(String path) throws IOException, FileNotFoundException, ServerResponseException {
        return getList(path, false);
    }

    private synchronized Vector getList(String path, boolean namelist) throws IOException, FileNotFoundException,
            ServerResponseException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        get(path, buffer, namelist ? NAMELIST : LIST);

        Vector result = new Vector();
        BufferedReader data = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
        String line = null;
        String next = null;
        while ((next = data.readLine()) != null) {
            next = next.trim();
            if (next.endsWith("\\")) {
                next = next.substring(0, next.lastIndexOf("\\"));
                line = (line == null ? next : line + " " + next).trim();
                continue;
            }
            line = (line == null ? next : line + " " + next).trim();
            result.add(line);
            line = null;
        }
        return result;

    }

    /**
     * get name list of files in the current directory. Similar to getList() but
     * returns filenames only where getList() returns other, system dependant
     * information.
     * 
     * @exception IOException
     *                an IO error occurred
     * @exception ServerResponseException
     *                the server reported an error
     * @exception FileNotFoundException
     *                the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public synchronized Vector getNameList() throws IOException, ServerResponseException, FileNotFoundException {
        return getNameList(null);
    }

    /**
     * Get name list of files in the given directory. Similar to getList() but
     * returns filenames only where getList() returns other, system dependant
     * information.
     * 
     * @param path
     *            the path of the directory that we want the name list of
     * @exception IOException
     *                an IO error occurred
     * @exception ServerResponseException
     *                the server reported an error
     * @exception FileNotFoundException
     *                the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public synchronized Vector getNameList(String path) throws IOException, ServerResponseException,
            FileNotFoundException {
        return getList(path, true);
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
     * Interrupt the given job.
     * 
     * @param job
     *            the job to interrupt
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void interrupt(Job job) throws ServerResponseException, IOException {
        jintr(job.getId());
    }

    /**
     * Kill the given job
     * 
     * @param job
     *            the job to kill
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void kill(Job job) throws ServerResponseException, IOException {
        jkill(job.getId());
    }

    /**
     * Set the transfer mode. Valid mode values are MODE_* listed in the
     * ClientProtocol class.
     * 
     * @param mode
     *            the new mode setting
     * @exception IOException
     *                an io error occurred talking to the server
     * @exception ServerResponseException
     *                the server replied with an error code
     */
    public synchronized void mode(char newMode) throws IOException, ServerResponseException {
        super.mode(newMode);
        this.mode = newMode; // cache the mode for later use
    }

    /**
     * put a file with a unique name.
     * 
     * <b>NOTE:</b> this calls stou() internally.
     * 
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error code
     * @return the name of the file created
     */
    public synchronized String put(InputStream data) throws IOException, ServerResponseException {
        return put(data, null, false);
    }

    /**
     * Store a file.
     * 
     * <b>NOTE:</b> this calls stor() internally.
     * 
     * @param pathname
     *            name of file to store on server (where to put the file on the
     *            server)
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error
     */
    public synchronized void put(InputStream in, String pathname) throws IOException, ServerResponseException {
        put(in, pathname, false);
    }

    /**
     * Puts a file on the server. If temporary is set the file is a temporary
     * file and will be deleted when the connection is closed.
     * 
     * <b>NOTE:</b> this calls stot(), stou() or stor() internally.
     * 
     * @param data
     * @param pathname
     *            the pathname of the file. Should be set to null if file is a
     *            temporary file or no specific pathname is desired.
     * @param temporary
     *            is the file a temporary file?
     * @return the filename of the file. Will be NULL when pathname is not null.
     * @throws IOException
     *             io error occurred talking to the server
     * @throws ServerResponseException
     *             server replied with error code
     */
    private synchronized String put(InputStream data, String pathname, boolean temporary) throws IOException,
            ServerResponseException {
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
            put = new ActivePutter(data);
            port(getInetAddress(), ((ActivePutter) put).getPort());
        }

        put.setMode(mode);
        put.addConnectionListeners(connectionListeners);
        put.addTransferListeners(transferListeners);
        put.start();

        // start transmission
        try {
            if (pathname != null) {
                stor(data, pathname);
                return null;
            }
            filename = temporary ? stot(data) : stou(data);
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
            } catch (InterruptedException ie) {
                // not really an error
            }
        }
        connection = null;
        return filename;
    }

    /**
     * Put a temp file, the data is stored in a uniquely named file on the
     * server. The remote temp file is deleted when the connection is closed.
     * 
     * <b>NOTE:</b> this calls stot() internally.
     * 
     * @exception IOException
     *                io error occurred talking to the server
     * @exception ServerResponseException
     *                server replied with error code
     * @return the filename of the temp file
     */
    public synchronized String putTemporary(InputStream data) throws IOException, ServerResponseException {
        return put(data, null, true);
    }

    /**
     * De-register a connection listener with the event source.
     * 
     * @param listener
     *            the listener to de-register with the event source
     */
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.removeElement(listener);
    }

    /**
     * De-register a transfer listener with the event source.
     * 
     * @param listener
     *            the listener to de-register with the event source
     */
    public void removeTransferListener(TransferListener listener) {
        transferListeners.removeElement(listener);
    }

    /**
     * Retry a given job with a default killtime of "now + 3 hours".
     * 
     * @param id
     *            the job id to retry.
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
     * @param id
     *            the job id to retry.
     * @param killTime
     *            the new killTime for the job.
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
     * enable or disable passive transfers
     * 
     * @param passive
     *            indicates whether passive transfers should be used
     */
    public synchronized void setPassive(boolean passive) {
        this.passive = passive;
    }

    /**
     * Submit the given job to the scheduler.
     * 
     * @param job
     *            the Job to submit
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void submit(Job job) throws ServerResponseException, IOException {
        jsubm(job.getId());
    }

    /**
     * Suspend the given job from the scheduler.
     * 
     * @param job
     *            the Job to suspend
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void suspend(Job job) throws ServerResponseException, IOException {
        jsusp(job.getId());
    }

    /**
     * wait for the given job to complete
     * 
     * @param job
     *            the job to wait for
     * @exception ServerResponseException
     * @exception IOException
     *                an IO error occurred while communicating with the server
     */
    public void wait(Job job) throws ServerResponseException, IOException {
        jwait(job.getId());
    }

}