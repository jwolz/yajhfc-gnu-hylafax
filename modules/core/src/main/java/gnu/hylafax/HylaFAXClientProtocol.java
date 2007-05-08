// ClientProtocol.java - a HylaFAX client protocol implementation in Java
// $Id: HylaFAXClientProtocol.java,v 1.11 2007/05/07 18:26:54 sjardine Exp $
//
// Copyright 1999, 2000 Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001 Innovation Software Group, LLC - http://www.innovationsw.com
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

// system includes
import gnu.inet.ftp.FtpClientProtocol;
import gnu.inet.ftp.ServerResponseException;
import gnu.inet.logging.Logger;
import gnu.inet.logging.LoggingFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This is the core implementation of the HylaFAX client protocol.
 * <P>
 * The purpose of this class is to implement the HylaFAX client protocol as simply and straight-forward as
 * possible.
 * <P>
 * Method names are not my choosing for the most part. They have been largely pulled straight from the
 * protocol and HylaFAX man pages. I expect that convenience classes and methods, with more developer friendly
 * names will be built on top of this raw protocol implementation as time passes.
 * <P>
 * Most developers should use the higher-level Client to perform some actions rather than this class directly.
 */
public class HylaFAXClientProtocol extends FtpClientProtocol implements ClientProtocol {

    // public static stuff
    private final static Logger log = LoggingFactory.getLogger(ClientProtocol.class);

    /**
     * default HylaFAX server port. currently 4559
     */
    public static int DEFAULT_PORT = 4559;

    /**
     * default constructor. sets up the initial class state.
     */
    public HylaFAXClientProtocol() {
        super();
    }// end of default constructor

    // public methods

    /**
     * establish administrator privileges given password
     * 
     * @param password administrator password
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void admin(String password) throws IOException, ServerResponseException {
        // send admin command to the server
        ostream.write("admin " + password + "\r\n");
        ostream.flush();
        log.debug("-> admin " + password);

        // get reply string
        String response = new String(istream.readLine());
        log.debug(response);

        // check response code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("230")) {
            // command failed
            throw (new ServerResponseException(response));
        }// fi
    }// end of admin method

    /**
     * get the current idle timeout in seconds
     * 
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     * @return server's idle timeout in seconds
     */
    public synchronized long idle() throws IOException, ServerResponseException {
        // send idle command to the server
        ostream.write("idle\r\n");
        ostream.flush();
        log.debug("-> idle");

        // get response string
        String response = new String(istream.readLine());
        log.debug(response);

        // check response code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("213")) {
            // command failed for some reason
            throw (new ServerResponseException(response));
        }

        // get the data to return
        Long l = new Long(st.nextToken());
        return l.longValue();

    }// end of idle method

    /**
     * set the idle timeout value to the given number of seconds
     * 
     * @param timeout new timeout value in seconds (MAX = 7200)
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void idle(long timeout) throws IOException, ServerResponseException {
        // send idle command to the server
        ostream.write("idle " + timeout + "\r\n");
        ostream.flush();
        log.debug("-> idle " + timeout);

        // get reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("213")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// end of idle method

    /**
     * get the current job id 0 indicates the current job id is "default" value
     * 
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized long job() throws IOException, ServerResponseException {
        // send job command to server
        ostream.write("job\r\n");
        ostream.flush();
        log.debug("-> job");

        // get reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }

        // command succeeded
        // response should contain current job id
        st.nextToken(); // skip "Current"
        st.nextToken(); // skip "job:"
        st.nextToken(); // skip ...

        // now, next token contains the job id or does not exist at all
        try {
            Long l = new Long(st.nextToken());
            return l.longValue();
        } catch (Exception e) {
            // default job selected
            return 0;
        }

    }// job (get current job id)

    /**
     * set the current job id
     * 
     * @param value new current job id
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void job(long value) throws IOException, ServerResponseException {
        // send job command to the server
        ostream.write("job " + value + "\r\n");
        ostream.flush();
        log.debug("-> job " + value);

        // get server reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// job

    /**
     * get the job format string. read the HylaFAX man pages, hfaxd(8c), for format codes.
     * 
     * @exception IOException a socket IO error occurred.
     * @exception ServerResponseException the server responded with an error code
     */
    public synchronized String jobfmt() throws IOException, ServerResponseException {
        // send command to server
        ostream.write("jobfmt\r\n");
        ostream.flush();
        log.debug("-> jobfmt");

        // get server reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        String temp = new String(st.nextToken());
        if (!temp.equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }

        // get format string from response
        return response.substring(temp.length());

    }// jobfmt

    /**
     * set the job format string. read the HylaFAX man pages, hfaxd(8c), for format codes.
     * 
     * @param value new job format string
     * @exception IOException a socket IO error occurred.
     * @exception ServerResponseException the server responded with an error
     */
    public synchronized void jobfmt(String value) throws IOException, ServerResponseException {
        // send command
        String command = new String("jobfmt \"" + value + "\"\r\n");
        ostream.write(command);
        ostream.flush();
        log.debug("-> jobfmt " + value);

        // get server reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed - why?
            throw (new ServerResponseException(response));
        }
    }// jobfmt

    /**
     * set the modem format string. the modem format string is used to format the modem status information.
     * Refer to the HylaFAX man pages, hfaxd(8c), for formatting codes.
     * 
     * @param value the new modem format string to use
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error code
     */
    public synchronized void mdmfmt(String value) throws IOException, ServerResponseException {
        ostream.write("mdmfmt \"" + value + "\"\r\n");
        ostream.flush();
        log.debug("-> mdmfmt " + value);

        String response = istream.readLine();
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // problem
            throw (new ServerResponseException(response));
        }
    }// mdmfmt

    /**
     * get the modem format string value. the modem format string specifies how modem status information
     * should be displayed. refer to the HylaFAX man pages, hfaxd(8c), for the format string codes.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error code
     */
    public synchronized String mdmfmt() throws IOException, ServerResponseException {
        ostream.write("mdmfmt\r\n");
        ostream.flush();
        log.debug("-> mdmfmt");

        String response = istream.readLine();
        log.debug(response);

        if (!response.substring(0, 3).equals("200")) {
            // error
            throw (new ServerResponseException(response));
        }

        return response.substring(3);

    }// mdmfmt

    /**
     * set the receive file output format string. The rcvfmt string specifies how received faxes (files in the
     * rcvq directory) are displayed. refer to the HylaFAX man pages, hfaxd(8c), for the format string codes.
     * 
     * @param value the new format string
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error code
     */
    public synchronized void rcvfmt(String value) throws IOException, ServerResponseException {
        ostream.write("rcvfmt \"" + value + "\"\r\n");
        ostream.flush();
        log.debug("-> rcvfmt \"" + value + "\"\n");

        String response = istream.readLine();
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }
    }// rcvfmt

    /**
     * get the received file output format string. The rcvfmt string specifies how received faxes (files in
     * the rcvq directory) are displayed. Refer to the HylaFAX man pages, hfaxd(8c), for the format string
     * codes.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server responded with an error code
     */
    public synchronized String rcvfmt() throws IOException, ServerResponseException {
        ostream.write("rcvfmt\r\n");
        ostream.flush();
        log.debug("-> rcvfmt");

        String response = istream.readLine();
        log.debug(response);

        if (!response.substring(0, 3).equals("200")) {
            // error setting rcvfmt
            throw (new ServerResponseException(response));
        }

        return response.substring(3);

    }// rcvfmt

    /**
     * set the FILEFMT string value. the FILEFMT string specifies how file status information is returned when
     * the LIST and STAT commands are used. Refer to the HylaFAX man pages, hfaxd(8c), for the formatting
     * codes.
     * 
     * @param value the new value of the FILEFMT string
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void filefmt(String value) throws IOException, ServerResponseException {
        ostream.write("filefmt \"" + value + "\"\r\n");
        ostream.flush();
        log.debug("-> filefmt \"" + value + "\"");

        String response = istream.readLine();
        log.debug(response);

        if (!response.substring(0, 3).equals("200")) { throw (new ServerResponseException(response)); }
    }// filefmt

    /**
     * get the FILEFMT string value. The FILEFMT string specifies how file status information is formatted
     * when returned by the LIST and STAT commands. Refer to the HylaFAX man pages, hfaxd(8c), for information
     * on the formatting codes that can be used in this string.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     * @return the current FILEFMT value
     */
    public synchronized String filefmt() throws IOException, ServerResponseException {
        ostream.write("filefmt\r\n");
        ostream.flush();
        log.debug("-> filefmt");

        String response = istream.readLine();
        log.debug(response);

        if (!response.substring(0, 3).equals("200")) { throw (new ServerResponseException(response)); }
        return response.substring(3);

    }// filefmt

    /**
     * delete the given job this can be called on a suspended or done job.
     * 
     * @param jobid id of the job to delete
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException server replied with error code
     */
    public synchronized void jdele(long jobid) throws IOException, ServerResponseException {
        // send command to server
        ostream.write("jdele " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jdele " + jobid);

        // get server reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result value
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// jdele

    /**
     * interrupt the given job id
     * 
     * @param jobid id of the job to interrupt
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void jintr(long jobid) throws IOException, ServerResponseException {
        // send command to server
        ostream.write("jintr " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jintr " + jobid);

        // get reply string
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// jintr

    /**
     * kill the job with the given job id
     * 
     * @param jobid the id of the job to kill
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void jkill(long jobid) throws IOException, ServerResponseException {
        // send command
        ostream.write("jkill " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jkill " + jobid);

        // get reply
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // job failed
            throw (new ServerResponseException(response));
        }
    }// jkill

    /**
     * create a new job. get the new job id using the job() method. The new job is the current job.
     * 
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void jnew() throws IOException, ServerResponseException {
        // send command string
        ostream.write("jnew\r\n"); // no options
        ostream.flush();
        log.debug("-> jnew");

        // get results
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// jnew

    /**
     * reset the state of the current job. get/set the current job id via the 'job' method
     * 
     * @exception IOException an IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void jrest() throws IOException, ServerResponseException {
        // send command
        ostream.write("jrest\r\n");
        ostream.flush();
        log.debug("-> jrest");

        // get result
        String response = new String(istream.readLine());
        log.debug(response);

        // check result code
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) {
            // command failed
            throw (new ServerResponseException(response));
        }
    }// jrest

    /**
     * submit the given job to the scheduler
     * 
     * @param jobid the id of the job to submit
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     * @return the submitted job id, should match jobid passed in
     */
    public synchronized int jsubm(long jobid) throws IOException, ServerResponseException {
        // send command
        ostream.write("jsubm " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jsubm " + jobid);

        // get result
        String response = new String(istream.readLine());
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        int jobID = 0;

        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }

        // third token is the stringified job id. return this.
        // catch those messed up responses.

        try {
            st.nextToken(); // waste this.
            String jobStr = st.nextToken();
            // only this matters. jobStr should be an int.
            jobID = Integer.parseInt(jobStr); // return that job id.
        } catch (NumberFormatException nfe) {
            throw new ServerResponseException("Bad number format for job id");
        } catch (NoSuchElementException nsee) {
            new ServerResponseException("Mangled server response to job submit");
        }

        return jobID;
    }// jsubm

    /**
     * submit the current job to the scheduler
     * 
     * @return the job id
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized long jsubm() throws IOException, ServerResponseException {
        // send command
        ostream.write("jsubm\r\n");
        ostream.flush();
        log.debug("-> jsubm");

        // get results
        String response = new String(istream.readLine());
        log.debug(response);

        // check response
        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }

        st.nextToken();
        return (Long.parseLong(st.nextToken()));

    }// jsubm

    /**
     * Suspend the job with the given job id.
     * 
     * @param jobid id of the job to suspend
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jsusp(long jobid) throws IOException, ServerResponseException {
        ostream.write("jsusp " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jsusp " + jobid);

        String response = new String(istream.readLine());
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }
    }// jsusp

    /**
     * Wait for the job with the given job id to complete.
     * 
     * @param jobid id of the job to wait for
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jwait(long jobid) throws IOException, ServerResponseException {
        ostream.write("jwait " + jobid + "\r\n");
        ostream.flush();
        log.debug("-> jwait " + jobid);

        String response = readResponse(istream);
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response, " -");
        if (!st.nextToken().equals("216")) { throw (new ServerResponseException(response)); }// end of if
    }// jwait

    // types

    // tzone
    // set the timezone display format.
    //

    /**
     * use the GMT timezone for date fields.
     */
    public static final String TZONE_GMT = "GMT";

    /**
     * use the local timezone for date fields.
     */
    public static final String TZONE_LOCAL = "LOCAL";

    /**
     * set the timezone display format valid tzone values are TZONE_GMT and TZONE_LOCAL
     * 
     * @param value new timezone display setting
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void tzone(String value) throws IOException, ServerResponseException {
        ostream.write("tzone " + value + "\r\n");
        ostream.flush();
        log.debug("-> tzone " + value);

        String response = new String(istream.readLine());
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }
    }// tzone

    /**
     * set job parameters on the current job
     * 
     * @param parm the name of the job parameter to change
     * @param value the value of the given parameter
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jparm(String parm, String value) throws IOException, ServerResponseException {
        String response;
        StringTokenizer st;

        ostream.write("jparm " + parm + " " + value + "\r\n");
        ostream.flush();

        log.debug("-> jparm " + parm + " " + value);

        response = istream.readLine();
        log.debug(response);

        st = new StringTokenizer(response);

        String return_code = st.nextToken();
        if ((!return_code.equals("213")) && (!return_code.equals("200"))) { throw (new ServerResponseException(response)); }
    }// jparm

    /**
     * set job parameters on the current job
     * 
     * @param parm the name of the job parameter to change
     * @param value the value of the given parameter as an Object
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jparm(String parm, Object value) throws IOException, ServerResponseException {
        jparm(parm, value.toString());
    }// jparm

    /**
     * set job parameters on the current job
     * 
     * @param parm the name of the job parameter to change
     * @param value the value of the given parameter
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jparm(String parm, int value) throws IOException, ServerResponseException {
        jparm(parm, Integer.toString(value));
    }// jparm

    /**
     * set job parameters on the current job
     * 
     * @param parm the name of the job parameter to change
     * @param value the value of the given parameter
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     */
    public synchronized void jparm(String parm, long value) throws IOException, ServerResponseException {
        jparm(parm, Long.toString(value));
    }// jparm

    /**
     * get job parameters of the current job
     * 
     * @param parm the name of the job parameter to change
     * @exception IOException io error occurred
     * @exception ServerResponseException server replied with an error code
     * @return value of the named job parameter
     */
    public synchronized String jparm(String parm) throws IOException, ServerResponseException {
        String response;

        if (log.isDebugEnabled()) log.debug("jparam " + parm);
        ostream.write("jparm " + parm + "\r\n");
        ostream.flush();
        response = istream.readLine();
        if (response.startsWith("213")) {
            // FIXME check response.length()
            String res = response.substring(4);
            while (response.charAt(3) == '-') {
                response = istream.readLine();
                if (!response.startsWith("213")) throw (new ServerResponseException(response));
                // FIXME check response.length()
                res += "\n" + response.substring(4);
            }
            return res;
        }
        throw (new ServerResponseException(response));
    }// jparm

    /**
     * store temp file, the file is stored in a uniquely named file on the server. The remote temp file is
     * deleted when the connection is closed.
     * 
     * @exception IOException io error occurred talking to the server
     * @exception ServerResponseException server replied with error code
     * @return the filename of the temp file
     */
    public synchronized String stot(InputStream data) throws IOException, ServerResponseException {
        String filename;
        String response;
        StringTokenizer st;

        // send stot command to server
        ostream.write("stot\r\n");
        ostream.flush();

        log.debug("-> stot");

        // get results
        response = istream.readLine();
        log.debug(response);

        st = new StringTokenizer(response);
        if (!st.nextToken().equals("150")) { throw (new ServerResponseException(response)); }
        st.nextToken(); // ignore 'FILE:' string
        filename = new String(st.nextToken()); // get filename value

        // transfering ...

        // next line tells us transfer completed
        response = istream.readLine();
        log.debug(response);

        st = new StringTokenizer(response);
        if (!st.nextToken().equals("226")) {
            // some sort of error
            throw (new ServerResponseException(response));
        }

        return filename;
    }// stot

    /**
     * verify dialstring handling and/or least-cost routing.
     * 
     * @param dialstring the dialstring to verify
     * @exception IOException a socket IO error occurred
     * @exception ServerResponseException the server replied with an error code
     * @return the InetAddress of the server that will handle the given dialstring
     */
    public synchronized InetAddress vrfy(String dialstring) throws IOException, ServerResponseException {
        ostream.write("vrfy " + dialstring + "\r\n");
        ostream.flush();
        log.debug("-> vrfy " + dialstring);

        String response = istream.readLine();
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        if (!st.nextToken().equals("200")) { throw (new ServerResponseException(response)); }
        return InetAddress.getByName(st.nextToken());
    }// vrfy

    /**
     * open a connection to a given server at default port this is an alias for connect()
     * 
     * @param host the hostname of the HylaFAX server
     * @exception UnknownHostException cannot resolve the given hostname
     * @exception IOException IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void open(String host) throws UnknownHostException, IOException, ServerResponseException {
        connect(host, DEFAULT_PORT); // connect to default port
    }// end of open method

    /**
     * open a connection to the localhost on the default port
     * 
     * @exception UnknownHostException cannot resolve the given hostname
     * @exception IOException IO error occurred
     * @exception ServerResponseException the server replied with an error code
     */
    public synchronized void open() throws UnknownHostException, IOException, ServerResponseException {
        connect("localhost", DEFAULT_PORT);
    }// end of open method

    /**
     * Returns the size (in bytes) of the given regular file. This is the size on the server and may not
     * accurately represent the file size once the file has been transferred (particularly via ASCII mode)
     * 
     * @param pathname the name of the file to get the size for
     * @exception IOException caused by a socket IO error
     * @exception ServerResponseException caused by a server response indicating an error
     * @exception FileNotFoundException the given path does not exist
     */
    public synchronized long size(String pathname) throws IOException, FileNotFoundException, ServerResponseException {
        ostream.write("size " + pathname + "\r\n");
        ostream.flush();
        log.debug("-> size " + pathname);

        String response = istream.readLine();
        log.debug(response);

        StringTokenizer st = new StringTokenizer(response);
        String return_code = st.nextToken();
        if (!return_code.equals("213")) {
            if (return_code.equals("550")) { throw (new FileNotFoundException(response)); }
            throw (new ServerResponseException(response));
        }

        // get file size from response
        return Long.parseLong(st.nextToken());

    }// size

    // ***** data manipulation functions *****

    // ***** protected methods *****

    // ***** private methods *****

}// ClientProtocol

// ClientProtocol.java
