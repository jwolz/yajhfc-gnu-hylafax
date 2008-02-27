/*******************************************************************************
 * $Id$
 * 
 * Copyright 2005-2008 Steven Jardine, MJN Services, Inc. <steve@mjnservices.com>
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
package gnu.hylafax;

import gnu.hylafax.status.StatusEventSource;
import gnu.inet.ftp.ConnectionEventSource;
import gnu.inet.ftp.ServerResponseException;
import gnu.inet.ftp.TransferEventSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Interface for HylaFAX clients.
 * 
 * @author Steven Jardine <steve@mjnservices.com>
 */
public interface Client extends ClientProtocol, TransferEventSource,
	ConnectionEventSource, StatusEventSource {

    /**
     * Create a new job in the server
     * 
     * @return a new Job instance on the server
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public Job createJob() throws ServerResponseException, IOException;

    /**
     * Delete the given done or suspended job @
     * 
     * param job the (done or suspended) job to delete
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void delete(Job job) throws ServerResponseException, IOException;

    /**
     * GET the named file, FTP style.
     * 
     * @param path
     *                the name of the file to GET. This can be a full or partial
     *                path.
     * @param out
     *                the OutputStream to write the file data to
     * @exception IOException
     *                    an IO error occurred
     * @exception ServerResponseException
     *                    the server reported an error
     * @exception FileNotFoundException
     *                    the given path does not exist
     */
    public void get(String path, OutputStream out) throws IOException,
	    FileNotFoundException, ServerResponseException;

    /**
     * get a Job instance for the given job id
     * 
     * @param id
     *                the id of the job to get
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public Job getJob(long id) throws ServerResponseException, IOException;

    /**
     * get a long-style listing of files in the current directory. NOTE: this
     * calls the list() method internally with the "." path.
     * 
     * @exception IOException
     *                    an IO error occurred
     * @exception FileNotFoundException
     *                    the "." path doesn't exist
     * @exception ServerResponseException
     *                    the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public Vector getList() throws IOException, FileNotFoundException,
	    ServerResponseException;

    /**
     * get a long-style listing of files in the given directory. NOTE: this
     * calls the list() method internally.
     * 
     * @param path
     *                the path that we're interested in finding the contents of
     * @exception IOException
     *                    an IO error occurred
     * @exception FileNotFoundException
     *                    the given path doesn't exist
     * @exception ServerResponseException
     *                    the server reported an error
     * @return a Vector of Strings containing the list information
     */
    public Vector getList(String path) throws IOException,
	    FileNotFoundException, ServerResponseException;

    /**
     * get name list of files in the current directory. Similar to getList() but
     * returns filenames only where getList() returns other, system dependant
     * information.
     * 
     * @exception IOException
     *                    an IO error occurred
     * @exception ServerResponseException
     *                    the server reported an error
     * @exception FileNotFoundException
     *                    the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public Vector getNameList() throws IOException, ServerResponseException,
	    FileNotFoundException;

    /**
     * get name list of files in the given directory. Similar to getList() but
     * returns filenames only where getList() returns other, system dependant
     * information.
     * 
     * @param path
     *                the path of the directory that we want the name list of
     * @exception IOException
     *                    an IO error occurred
     * @exception ServerResponseException
     *                    the server reported an error
     * @exception FileNotFoundException
     *                    the requested path does not exist
     * @return Vector of Strings containing filenames
     */
    public Vector getNameList(String path) throws IOException,
	    ServerResponseException, FileNotFoundException;

    /**
     * check whether passive transfers have been enabled
     * 
     * @return true if passive transfers are enabled, false otherwise
     */
    public boolean getPassive();

    /**
     * interrupt the given job
     * 
     * @param job
     *                the job to interrupt
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void interrupt(Job job) throws ServerResponseException, IOException;

    /**
     * kill the given job
     * 
     * @param job
     *                the job to kill
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void kill(Job job) throws ServerResponseException, IOException;

    /**
     * set the transfer mode. valid mode values are MODE_* listed in the
     * ClientProtocol class.
     * 
     * @param mode
     *                the new mode setting
     * @exception IOException
     *                    an io error occurred talking to the server
     * @exception ServerResponseException
     *                    the server replied with an error code
     */
    public void mode(char mode) throws IOException, ServerResponseException;

    /**
     * put a file with a unique name. NOTE: this calls stou() internally.
     * 
     * @exception IOException
     *                    a socket IO error occurred
     * @exception ServerResponseException
     *                    the server responded with an error code
     * @return the name of the file created
     */
    public String put(InputStream in) throws IOException,
	    ServerResponseException;

    /**
     * store a file. NOTE: this calls stor() internally.
     * 
     * @param pathname
     *                name of file to store on server (where to put the file on
     *                the server)
     * @exception IOException
     *                    a socket IO error occurred
     * @exception ServerResponseException
     *                    the server responded with an error
     */
    public void put(InputStream in, String pathname) throws IOException,
	    ServerResponseException;

    /**
     * put a temp file, the data is stored in a uniquely named file on the
     * server. The remote temp file is deleted when the connection is closed.
     * NOTE: this calls stot() internally.
     * 
     * @exception IOException
     *                    io error occurred talking to the server
     * @exception ServerResponseException
     *                    server replied with error code
     * @return the filename of the temp file
     */
    public String putTemporary(InputStream data) throws IOException,
	    ServerResponseException;

    /**
     * enable or disable passive transfers
     * 
     * @param passive
     *                indicates whether passive transfers should be used
     */
    public void setPassive(boolean passive);

    /**
     * submit the given job to the scheduler
     * 
     * @param job
     *                the Job to submit
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void submit(Job job) throws ServerResponseException, IOException;

    /**
     * suspend the given job from the scheduler
     * 
     * @param job
     *                the Job to suspend
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void suspend(Job job) throws ServerResponseException, IOException;

    /**
     * Specify the type of file being sent.
     * 
     * @param type
     *                the type of file being sent.
     * @throws ServerResponseException
     * @throws IOException
     */
    public void type(char type) throws ServerResponseException, IOException;

    /**
     * wait for the given job to complete
     * 
     * @param job
     *                the job to wait for
     * @exception ServerResponseException
     * @exception IOException
     *                    an IO error occurred while communicating with the
     *                    server
     */
    public void wait(Job job) throws ServerResponseException, IOException;

}
