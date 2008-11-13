/*******************************************************************************
 * $Id$
 * 
 * Copyright 1999, 2000 Joe Phillips <jaiger@innovationsw.com>
 * Copyright 2001 Innovation Software Group, LLC - http://www.innovationsw.com
 * Copyright 2005-2008 Steven Jardine <steve@mjnservices.com>
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
 * 	Joe Phillips - Initial API and implementation
 * 	Steven Jardine
 * 	Jonas Wolz  
 ******************************************************************************/
package gnu.hylafax;

import gnu.inet.ftp.FtpClientProtocol;
import gnu.inet.ftp.ServerResponseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the core implementation of the HylaFAX client protocol.
 * 
 * The purpose of this class is to implement the HylaFAX client protocol as
 * simply and straight-forward as possible.
 * 
 * Method names are not my choosing for the most part. They have been largely
 * pulled straight from the protocol and HylaFAX man pages. I expect that
 * convenience classes and methods, with more developer friendly names will be
 * built on top of this raw protocol implementation as time passes.
 * 
 * Most developers should use the higher-level Client to perform some actions
 * rather than this class directly.
 * 
 * @version $Revision$
 * @author Joe Phillips <jaiger@net-foundry.com>
 * @author Steven Jardine <steve@mjnservices.com>
 * @author Jonas Wolz
 */
public class HylaFAXClientProtocol extends FtpClientProtocol implements
	ClientProtocol {

    /**
     * default HylaFAX server port. currently 4559
     */
    public static int DEFAULT_PORT = 4559;

    // public static stuff
    private final static Log log = LogFactory.getLog(ClientProtocol.class);

    /**
     * use the GMT timezone for date fields.
     */
    public static final String TZONE_GMT = "GMT";

    // public methods

    /**
     * use the local timezone for date fields.
     */
    public static final String TZONE_LOCAL = "LOCAL";

    protected String hylafaxServerTimeZone = null;

    protected String version = null;

    /**
     * default constructor. sets up the initial class state.
     */
    public HylaFAXClientProtocol() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#admin(java.lang.String)
     */
    public synchronized void admin(String password) throws IOException,
	    ServerResponseException {
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
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#config(java.lang.String, int)
     */
    public synchronized void config(String parm, int value) throws IOException,
	    ServerResponseException {
	config(parm, Integer.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#config(java.lang.String, long)
     */
    public void config(String parm, long value) throws IOException,
	    ServerResponseException {
	config(parm, Long.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#config(java.lang.String,
     * java.lang.Object)
     */
    public synchronized void config(String parm, Object value)
	    throws IOException, ServerResponseException {
	config(parm, value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#config(java.lang.String,
     * java.lang.String)
     */
    public synchronized void config(String parm, String value)
	    throws IOException, ServerResponseException {
	String response;
	StringTokenizer st;

	String cmd = "site config " + parm + " " + value + "\r\n";

	ostream.write(cmd);
	ostream.flush();

	log.debug("-> " + cmd);

	response = istream.readLine();
	log.debug(response);

	st = new StringTokenizer(response);

	String return_code = st.nextToken();
	if ((!return_code.equals("213")) && (!return_code.equals("200"))) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#filefmt()
     */
    public synchronized String filefmt() throws IOException,
	    ServerResponseException {
	ostream.write("filefmt\r\n");
	ostream.flush();
	log.debug("-> filefmt");

	String response = istream.readLine();
	log.debug(response);

	if (!response.substring(0, 3).equals("200")) {
	    throw (new ServerResponseException(response));
	}
	return response.substring(3);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#filefmt(java.lang.String)
     */
    public synchronized void filefmt(String value) throws IOException,
	    ServerResponseException {
	ostream.write("filefmt \"" + value + "\"\r\n");
	ostream.flush();
	log.debug("-> filefmt \"" + value + "\"");

	String response = istream.readLine();
	log.debug(response);

	if (!response.substring(0, 3).equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#tzone(java.lang.String)
     */
    public synchronized void form(String value) throws IOException,
	    ServerResponseException {
	ostream.write("form \"" + value + "\"\r\n");
	ostream.flush();
	log.debug("-> form " + value);

	String response = istream.readLine();
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    // problem
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * @return the hylafax server version.
     */
    public String getServerVersion() {
	if (version == null) {
	    try {
		String tmp = getGreeting();
		if (tmp == null || tmp.equals(""))
		    version = null;
		else if (tmp.startsWith("220")) {
		    version = tmp.substring(tmp.indexOf("(") + 1, tmp
			    .lastIndexOf(")"));
		}
	    } catch (Exception e) {
		version = null;
		log.error("Cannot parse version from greeting", e);
	    }
	}
	return version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#idle()
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#idle(long)
     */
    public synchronized void idle(long timeout) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jdele(long)
     */
    public synchronized void jdele(long jobid) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jintr(long)
     */
    public synchronized void jintr(long jobid) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jkill(long)
     */
    public synchronized void jkill(long jobid) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jnew()
     */
    public synchronized void jnew() throws IOException, ServerResponseException {
	jnew(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jnew(boolean)
     */
    public synchronized void jnew(boolean inheritDefault) throws IOException,
	    ServerResponseException {
	if (inheritDefault) {
	    job(0);
	}
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#job()
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#job(long)
     */
    public synchronized void job(long val) throws IOException,
	    ServerResponseException {
	// send job command to the server
	String value = val <= 0 ? "default" : String.valueOf(val);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jobfmt()
     */
    public synchronized String jobfmt() throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jobfmt(java.lang.String)
     */
    public synchronized void jobfmt(String value) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String)
     */
    public synchronized String jparm(String parm) throws IOException,
	    ServerResponseException {
	String response;

	if (log.isDebugEnabled())
	    log.debug("jparam " + parm);
	ostream.write("jparm " + parm + "\r\n");
	ostream.flush();
	response = istream.readLine();
	if (response.startsWith("213")) {
	    // FIXME check response.length()
	    String res = response.substring(4);
	    while (response.charAt(3) == '-') {
		response = istream.readLine();
		if (!response.startsWith("213"))
		    throw (new ServerResponseException(response));
		// FIXME check response.length()
		res += "\n" + response.substring(4);
	    }
	    return res;
	}
	throw (new ServerResponseException(response));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, int)
     */
    public synchronized void jparm(String parm, int value) throws IOException,
	    ServerResponseException {
	jparm(parm, Integer.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, long)
     */
    public synchronized void jparm(String parm, long value) throws IOException,
	    ServerResponseException {
	jparm(parm, Long.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, java.lang.Object)
     */
    public synchronized void jparm(String parm, Object value)
	    throws IOException, ServerResponseException {
	jparm(parm, value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, java.lang.String)
     */
    public synchronized void jparm(String parm, String value)
	    throws IOException, ServerResponseException {
	String response;
	StringTokenizer st;

	ostream.write("jparm " + parm + " " + value + "\r\n");
	ostream.flush();

	log.debug("-> jparm " + parm + " " + value);

	response = istream.readLine();
	log.debug(response);

	st = new StringTokenizer(response);

	String return_code = st.nextToken();
	if ((!return_code.equals("213")) && (!return_code.equals("200"))) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jrest()
     */
    public synchronized void jrest() throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsubm()
     */
    public synchronized long jsubm() throws IOException,
	    ServerResponseException {
	// send command
	ostream.write("jsubm\r\n");
	ostream.flush();
	log.debug("-> jsubm");

	// get results
	String response = new String(istream.readLine());
	log.debug(response);

	// check response
	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}

	st.nextToken();
	return (Long.parseLong(st.nextToken()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsubm(long)
     */
    public synchronized int jsubm(long jobid) throws IOException,
	    ServerResponseException {
	// send command
	ostream.write("jsubm " + jobid + "\r\n");
	ostream.flush();
	log.debug("-> jsubm " + jobid);

	// get result
	String response = new String(istream.readLine());
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	int jobID = 0;

	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsusp(long)
     */
    public synchronized void jsusp(long jobid) throws IOException,
	    ServerResponseException {
	ostream.write("jsusp " + jobid + "\r\n");
	ostream.flush();
	log.debug("-> jsusp " + jobid);

	String response = new String(istream.readLine());
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jwait(long)
     */
    public synchronized void jwait(long jobid) throws IOException,
	    ServerResponseException {
	ostream.write("jwait " + jobid + "\r\n");
	ostream.flush();
	log.debug("-> jwait " + jobid);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response, " -");
	if (!st.nextToken().equals("216")) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#mdmfmt()
     */
    public synchronized String mdmfmt() throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#mdmfmt(java.lang.String)
     */
    public synchronized void mdmfmt(String value) throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open()
     */
    public synchronized void open() throws UnknownHostException, IOException,
	    ServerResponseException {
	open("localhost");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open(java.lang.String)
     */
    public synchronized void open(String host) throws UnknownHostException,
	    IOException, ServerResponseException {
	open(host, DEFAULT_PORT); // connect to default port
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open(java.lang.String, int)
     */
    public synchronized void open(String host, int p)
	    throws UnknownHostException, IOException, ServerResponseException {
	connect(host, p); // connect to default port
	log.debug("Connected to: " + getServerVersion());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#rcvfmt()
     */
    public synchronized String rcvfmt() throws IOException,
	    ServerResponseException {
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#rcvfmt(java.lang.String)
     */
    public synchronized void rcvfmt(String value) throws IOException,
	    ServerResponseException {
	ostream.write("rcvfmt \"" + value + "\"\r\n");
	ostream.flush();
	log.debug("-> rcvfmt \"" + value + "\"\n");

	String response = istream.readLine();
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#site(java.lang.String, int)
     */
    public synchronized void site(String parm, int value) throws IOException,
	    ServerResponseException {
	site(parm, Integer.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#site(java.lang.String, long)
     */
    public synchronized void site(String parm, long value) throws IOException,
	    ServerResponseException {
	site(parm, Long.toString(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#site(java.lang.String, java.lang.Object)
     */
    public synchronized void site(String parm, Object value)
	    throws IOException, ServerResponseException {
	site(parm, value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#site(java.lang.String, java.lang.String)
     */
    public synchronized void site(String parm, String value)
	    throws IOException, ServerResponseException {
	String response;
	StringTokenizer st;

	ostream.write("site " + parm + " " + value + "\r\n");
	ostream.flush();

	log.debug("-> site " + parm + " " + value);

	response = istream.readLine();
	log.debug(response);

	st = new StringTokenizer(response);

	String return_code = st.nextToken();
	if ((!return_code.equals("213")) && (!return_code.equals("200"))
		&& (!return_code.equals("150"))) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#size(java.lang.String)
     */
    public synchronized long size(String pathname) throws IOException,
	    FileNotFoundException, ServerResponseException {
	ostream.write("size " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> size " + pathname);

	String response = istream.readLine();
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	String return_code = st.nextToken();
	if (!return_code.equals("213")) {
	    if (return_code.equals("550")) {
		throw (new FileNotFoundException(response));
	    }
	    throw (new ServerResponseException(response));
	}

	// get file size from response
	return Long.parseLong(st.nextToken());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#stot(java.io.InputStream)
     */
    public synchronized String stot(InputStream data) throws IOException,
	    ServerResponseException {
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
	if (!st.nextToken().equals("150")) {
	    throw (new ServerResponseException(response));
	}
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#tzone(java.lang.String)
     */
    public synchronized void tzone(String value) throws IOException,
	    ServerResponseException {
	hylafaxServerTimeZone = value;
	ostream.write("tzone " + value + "\r\n");
	ostream.flush();
	log.debug("-> tzone " + value);

	String response = new String(istream.readLine());
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#vrfy(java.lang.String)
     */
    public synchronized InetAddress vrfy(String dialstring) throws IOException,
	    ServerResponseException {
	ostream.write("vrfy " + dialstring + "\r\n");
	ostream.flush();
	log.debug("-> vrfy " + dialstring);

	String response = istream.readLine();
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
	return InetAddress.getByName(st.nextToken());
    }

}