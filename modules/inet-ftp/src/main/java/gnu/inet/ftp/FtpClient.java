// FtpClient.java - a FTP client protocol implementation in Java
// $Id$
//
// Copyright 1999, 2000 Joe Phillips <jaiger@innovationsw.com>
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements convenience methods that wrapper the FtpClientProtocol
 * methods for common functionality.
 * <P>
 * Most developers will want to use this class rather than the lower-level
 * FtpClientProtocol methods directly.
 * 
 * @see FtpClientProtocol
 */
public class FtpClient extends FtpClientProtocol implements ConnectionEventSource, TransferEventSource {

    private boolean passive; // indicate whether passive transfers should

    private final static Log log = LogFactory.getLog(FtpClient.class);

    // be used
    private char mode; // the current mode setting

    private Vector connectionListeners;

    private Vector transferListeners;

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

    /**
     * default constructor. initialize class state.
     */
    public FtpClient() {
        passive = false; // disable passive transfers by default
        mode = MODE_STREAM; // default mode is stream mode
        transferListeners = new Vector();
        connectionListeners = new Vector();
    }// end of default constructor

    /**
     * enable or disable passive transfers
     * 
     * @param passive
     *            indicates whether passive transfers should be used
     */
    public synchronized void setPassive(boolean passive) {
        this.passive = passive;
    }// setPassive

    /**
     * check whether we're using passive transfers or not.
     * 
     * @return true if passive transfers are enabled, false otherwise
     */
    public synchronized boolean getPassive() {
        return passive;
    }// getPassive

    /**
     * set the transfer mode. valid mode values are MODE_* listed in the
     * FtpClientProtocol class.
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
    }// mode

    /**
     * Register a connection listener with the event source.
     * 
     * @param listener
     *            the listener to register with the event source
     */
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.addElement(listener);
    }// addConnectionListener

    /**
     * Register a set of connection listeners with the event source.
     * 
     * @param listeners
     *            the listeners to register with the event source
     */
    public void addConnectionListeners(Vector listeners) {
        Enumeration e = listeners.elements();
        while (e.hasMoreElements()) {
            ConnectionListener listener = (ConnectionListener) e.nextElement();
            connectionListeners.addElement(listener);
        }
    }// addConnectionListeners

    /**
     * De-register a connection listener with the event source.
     * 
     * @param listener
     *            the listener to de-register with the event source
     */
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.removeElement(listener);
    }// removeConnectionListener

    /**
     * Register a transfer listener with the event source.
     * 
     * @param listener
     *            the listener to register with the event source
     */
    public void addTransferListener(TransferListener listener) {
        transferListeners.addElement(listener);
    }// addTransferListener

    /**
     * Register a set of transfer listeners with the event source.
     * 
     * @param listeners
     *            the listeners to register with the event source
     */
    public void addTransferListeners(Vector listeners) {
        Enumeration e = listeners.elements();
        while (e.hasMoreElements()) {
            TransferListener listener = (TransferListener) e.nextElement();
            transferListeners.addElement(listener);
        }
    }// addTransferListeners

    /**
     * De-register a transfer listener with the event source.
     * 
     * @param listener
     *            the listener to de-register with the event source
     */
    public void removeTransferListener(TransferListener listener) {
        transferListeners.removeElement(listener);
    }// removeTransferListener

    /**
     * put a temp file, the data is stored in a uniquely named file on the
     * server. The remote temp file is deleted when the connection is closed.
     * NOTE: this calls stot() internally.
     * 
     * @exception IOException
     *                io error occurred talking to the server
     * @exception ServerResponseException
     *                server replied with error code
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
        put.setMode(mode);
        put.setType(fileType);
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
    }// putTemporary

    /**
     * put a file with a unique name. NOTE: this calls stou() internally.
     * 
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error code
     * @return the name of the file created
     */
    public synchronized String put(InputStream in) throws IOException, ServerResponseException {
        String filename;

        // prepare to put file
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
            port(getInetAddress(), aput.getPort()); // tell server where to send
            // data
        }
        put.setMode(mode);
        put.setType(fileType);
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
    }// put

    /**
     * store a file. NOTE: this calls stor() internally.
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

        // prepare for transfer
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
            port(getInetAddress(), aput.getPort()); // tell server where to send
            // data
        }
        put.setMode(mode);
        put.setType(fileType);
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

        // done
    }// put

    /**
     * get a long-style listing of files in the given directory. NOTE: this
     * calls the list() method internally.
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
        }// if passive mode

        // start transfer
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
    }// getList

    /**
     * get a long-style listing of files in the current directory. NOTE: this
     * calls the list() method internally with the "." path.
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
        return getList(null);
    }// getList

    /**
     * get name list of files in the given directory. Similar to getList() but
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
        }// if passive mode

        // start transfer
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
        } catch (Exception e) {
            sparky.cancel();
            throw new ServerResponseException(e.getMessage());
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
    }// getNameList

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
    }// getNameList

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
        get.setMode(mode);
        get.setType(fileType);
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

    }// get

    /**
     * Renames remote file from source to target. Uses rnfr and rnto internally.
     * 
     * @param source
     *            file to rename
     * @param target
     *            new filename
     * @exception IOException
     *                an IO error occurred
     * @exception ServerResponseException
     *                the server reported an error
     * @exception FileNotFoundException
     *                the source file does not exist
     */
    public synchronized void rename(String source, String target) throws IOException, ServerResponseException,
            FileNotFoundException {
        rnfr(source);
        rnto(target);
        return;
    }// rename

    /**
     * run some basic tests. eventually this method should be removed in favor
     * of a decent testing framework.
     * 
     * @param Arguments
     *            an array of command-line-argument Strings
     */
    public static void main(String Arguments[]) {
        FtpClient c = new FtpClient();
        try {
            c.open("localhost");
            c.noop();

            c.setPassive(true); // use passive transfers

            c.user("jaiger");

            c.type(TYPE_IMAGE); // should cause files to remain same size after
            // being transfered back and forth

            System.out.println("current directory is: " + c.pwd());

            c.cwd("docq");
            // c.cwd("bad-directory-name");
            System.out.println("current directory is: " + c.pwd());

            c.cdup();
            System.out.println("current directory is: " + c.pwd());

            // c.admin("MyPassword");
            System.out.println("idle timer set to " + c.idle() + " seconds.");
            c.idle(1800);
            System.out.println("idle timer set to " + c.idle() + " seconds.");

            // set file structure
            c.stru(STRU_FILE);
            // c.stru(STRU_RECORD);
            c.stru(STRU_TIFF);
            // c.stru(STRU_PAGE);
            c.stru(STRU_FILE);

            // send temp file (stot)
            {
                String filename = "test.ps";
                FileInputStream file = new FileInputStream(filename);

                String f = c.putTemporary(file);
                System.out.println("filename= " + f);

                // test size command
                long local_size, remote_size;
                local_size = (new RandomAccessFile(filename, "r").length());
                remote_size = c.size(f);
                System.out.println(filename + " local size is " + local_size);
                System.out.println(f + " remote size is " + remote_size);

                // retrieve the temp file now
                FileOutputStream out_file = new FileOutputStream(filename + ".retr");
                c.get(f, out_file);
                local_size = (new RandomAccessFile(filename + ".retr", "r").length());
                System.out.println(filename + ".retr size is " + local_size);

                // retrieve the temp file now (using ZLIB mode)
                FileOutputStream zip_file = new FileOutputStream(filename + ".gz");
                c.mode(MODE_ZLIB);
                c.get(f, zip_file);
                local_size = (new RandomAccessFile(filename + ".gz", "r").length());
                System.out.println(filename + ".gz size is " + local_size);
                c.mode(MODE_STREAM);

            }
            // end stot/retr test

            // test list command
            {
                Vector files;
                int counter;

                // list current directory
                files = c.getList();
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }

                // list /tmp directory
                files = c.getList("/tmp");
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }

                // list /tmp directory (with mode ZLIB)
                c.mode(MODE_ZLIB);
                files = c.getList("/tmp");
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }
                c.mode(MODE_STREAM);

                try {
                    // attempt to list file that doesn't exist
                    c.getList("/joey-joe-joe-jr.shabba-do"); // that's the
                    // worst name
                    // I've ever
                    // heard.
                    System.out.println("ERROR: file not found was expected");
                } catch (FileNotFoundException fnfe) {
                    // expected this, continue
                    System.out.println("GOOD: file not found, as expected");
                }

                // list current directory, should be the same as above
                files = c.getList();
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }
            }
            // end list test

            // test nlst command
            {
                Vector files;
                int counter;

                // list /tmp directory
                files = c.getNameList("/tmp");
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }
                // list /tmp directory
                files = c.getNameList("/tmp");
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }

                // list /tmp directory (using mode ZLIB)
                c.mode(MODE_ZLIB);
                files = c.getNameList("/tmp");
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }
                c.mode(MODE_STREAM);

                // list current directory
                files = c.getNameList();
                for (counter = 0; counter < files.size(); counter++) {
                    System.out.println((String) files.elementAt(counter));
                }

            }
            // end nlst test

            // get system type string
            String system = c.syst();
            System.out.println("system type: " + system + ".");

            c.noop();

            // stat tests
            {
                // test normal server status message
                Vector status = c.stat();
                int counter;
                for (counter = 0; counter < status.size(); counter++) {
                    System.out.println(status.elementAt(counter));
                }

                // test directory status
                status = c.stat("docq");
                for (counter = 0; counter < status.size(); counter++) {
                    System.out.println(status.elementAt(counter));
                }

                // test non-existing directory status
                try {
                    status = c.stat("joey-joe-joe-junior-shabba-do");
                    for (counter = 0; counter < status.size(); counter++) {
                        System.out.println(status.elementAt(counter));
                    }
                } catch (FileNotFoundException fnfe) {
                    System.out.println("GOOD: file not found.  this is what we expected");
                }
            }

            c.noop();

            c.quit();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("main: end");
    }// main
}// end of FtpClient

// end of file
