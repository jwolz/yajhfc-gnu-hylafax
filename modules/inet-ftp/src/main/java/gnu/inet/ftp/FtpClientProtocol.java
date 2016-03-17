// FtpClientProtocol.java - a FTP client protocol implementation in Java
// $Id:FtpClientProtocol.java 77 2008-02-20 21:54:51Z sjardine $
//
// Copyright 1999, 2000 Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001, 2002 Innovation Software Group, LLC - http://www.innovationsw.com
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
// Todo:
//  - implement useful extensions (RFCs 2228, 2640, 2773 and STD0009)
//    - RFC2228 - security extensions
//    - RFC2640 - i18n
//    - RFC2773 - encryption with kea and skipjack
//
package gnu.inet.ftp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the core implementation of the FTP client protocol, RFC0959. You
 * should be able to find the document via searches on the World Wide Web. At
 * the time of this writing, RFCs could be found at <A
 * HREF="http://www.faqs.org">www.faqs.org</A>.
 * <P>
 * The purpose of this class is to implement the FTP client protocol as simply
 * and straight-forward as possible.
 * <P>
 * This package was started as an implementation of the HylaFAX client protocol
 * so the features that overlap with the HylaFAX protocol will be the first
 * implemented and most tested.
 * <P>
 * Method names are not my choosing for the most part. They have been largely
 * pulled straight from the protocol and man pages. I expect that convenience
 * classes and methods, with more developer friendly names will be built on top
 * of this raw protocol implementation as time passes.
 * <P>
 * Most developers should use the higher-level FtpClient to perform some actions
 * rather than this class directly.
 * 
 * @author <a href="mailto:jaiger@net-foundry.com">Joe Phillips</a>
 * @author <a href="mailto:steve@mjnservices.com">Steven Jardine</a>
 * @author <a href="mailto:dorpsidioot@gmail.com">Lieven Poelman</a>
 * 
 * @version $Id:FtpClientProtocol.java 77 2008-02-20 21:54:51Z sjardine $
 */
public class FtpClientProtocol extends Object {

    private final static Log log = LogFactory.getLog(FtpClientProtocol.class);

    protected Socket sock; // socket for communications

    protected int port; // port to use

    protected BufferedReader istream; // buffered input stream

    protected OutputStreamWriter ostream; // output stream

    protected String greeting; // greeting string from server

    protected char fileType; // transfer file type

    protected int socketTimeout = 10000; // Socket timeout in Milliseconds. 0 =

    protected String hylafaxServerHost = null;

    protected int hylafaxServerPort = -1;

    protected String hylafaxServerUsername = null;

    protected String characterEncoding = System.getProperty("file.encoding");
    
    // infinite.

    /**
     * @param socketTimeout
     *            timout in milliseconds.
     */
    public void setSocketTimeout(int socketTimeout) {
	this.socketTimeout = socketTimeout;
    }

    /**
     * @return the timout in milliseconds.
     */
    public int getSocketTimeout() {
	return socketTimeout;
    }

    // public static stuff

    /**
     * default FTP server port.
     */
    public static int DEFAULT_PORT = 21;

    /**
     * default constructor. sets up the initial class state.
     */
    public FtpClientProtocol() {
	fileType = TYPE_ASCII; // default as per RFC 959
    }

    // public methods

    /**
     * get the local ip address of the control connection socket.
     * 
     * @return control connection ip address
     */
    public synchronized InetAddress getInetAddress() {
	return sock.getLocalAddress();
    }

    /**
     * open a connection to a given server and port this is an alias for
     * connect()
     * 
     * @param host
     *            the hostname of the HylaFAX server
     * @param portnumber
     *            the port the server is listening on
     * @exception UnknownHostException
     *                cannot resolve the given hostname
     * @exception IOException
     *                IO error occurred
     * @exception ServerResponseException
     *                the server replied with an error code
     */
    public synchronized void open(String host, int portnumber)
	    throws UnknownHostException, IOException, ServerResponseException {
	connect(host, portnumber);
    }

    /**
     * open a connection to a given server at default port this is an alias for
     * connect()
     * 
     * @param host
     *            the hostname of the HylaFAX server
     * @exception UnknownHostException
     *                cannot resolve the given hostname
     * @exception IOException
     *                IO error occurred
     * @exception ServerResponseException
     *                the server replied with an error code
     */
    public synchronized void open(String host) throws UnknownHostException,
	    IOException, ServerResponseException {
	connect(host, DEFAULT_PORT); // connect to default port
    }

    /**
     * open a connection to the localhost on the default port
     * 
     * @exception UnknownHostException
     *                cannot resolve the given hostname
     * @exception IOException
     *                IO error occurred
     * @exception ServerResponseException
     *                the server replied with an error code
     */
    public synchronized void open() throws UnknownHostException, IOException,
	    ServerResponseException {
	connect("localhost", DEFAULT_PORT);
    }

    /**
     * send the user name for this session
     * 
     * @param username
     *            name of the user to login as
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @return true if a password is required, false if no password is required
     */
    public synchronized boolean user(String username) throws IOException,
	    ServerResponseException {

	hylafaxServerUsername = username;

	// send user command to server
	ostream.write("user " + username + "\r\n");
	ostream.flush();
	log.debug("-> user " + username);

	// make sure command is accepted
	String response = readResponse(istream); // read a multi-line
	// response
	log.debug(response);
	String temp = new String(response.substring(0, 3));
	if (temp.equals("230")) {
	    return false;
	} else if (temp.equals("331")) {
	    return true; // password required, see pass()
	} else {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * send the password for this username and session
     * 
     * @param password
     *            the password to login with
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void pass(String password) throws IOException,
	    ServerResponseException {
	// send pass command to server
	ostream.write("pass " + password + "\r\n");
	ostream.flush();

	log.debug("-> pass");

	// make sure command is accepted
	String response = readResponse(istream);

	log.debug(response);

	StringTokenizer st = new StringTokenizer(response, " -");
	if (!st.nextToken().equals("230")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * perform server No Operation could be used as a keep-alive
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void noop() throws IOException, ServerResponseException {
	// send noop command to server
	ostream.write("noop\r\n");
	ostream.flush();
	log.debug("-> noop");

	// make sure command is accepted
	String response = readResponse(istream);
	log.debug(response);

	String temp = new String(response.substring(0, 3));
	if (!temp.equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * return current directory
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized String pwd() throws IOException,
	    ServerResponseException {
	// send pwd command to server
	ostream.write("pwd\r\n");
	ostream.flush();
	log.debug("-> pwd");

	// check reply string
	String response = readResponse(istream);
	log.debug(response);

	// Grab response code
	String temp = new String(response.substring(0, 3));
	if (!temp.equals("257")) {
	    // server didn't like command
	    throw (new ServerResponseException(response));
	}
	// get value of current directory
	StringTokenizer st = new StringTokenizer(response, "\"");
	st.nextToken();
	return st.nextToken();
    }

    /**
     * change current working directory
     * 
     * @param value
     *            directory to set to current working directory
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void cwd(String value) throws IOException,
	    ServerResponseException {
	// send cwd command to server
	ostream.write("cwd " + value + "\r\n");
	ostream.flush();
	log.debug("-> cwd " + value);

	// get reply
	String response = readResponse(istream);
	log.debug(response);

	// get response code
	String temp = response.substring(0, 3);
	if (!"250".equals(temp)) {
	    // failed to change directories (probably doesn't exist)
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * change to parent of current working directory
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with error code
     */
    public synchronized void cdup() throws IOException, ServerResponseException {
	// send cdup command to the server
	ostream.write("cdup\r\n");
	ostream.flush();
	log.debug("-> cdup");

	// get reply
	String response = readResponse(istream);
	log.debug(response);

	// get response code
	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("250")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * get the current idle timeout in seconds
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @return server's idle timeout in seconds
     */
    public synchronized long idle() throws IOException, ServerResponseException {
	// send idle command to the server
	ostream.write("idle\r\n");
	ostream.flush();
	log.debug("-> idle");

	// get response string
	String response = readResponse(istream);
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

    /**
     * set the idle timeout value to the given number of seconds
     * 
     * @param timeout
     *            new timeout value in seconds (MAX = 7200)
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void idle(long timeout) throws IOException,
	    ServerResponseException {
	// send idle command to the server
	ostream.write("idle " + timeout + "\r\n");
	ostream.flush();
	log.debug("-> idle " + timeout);

	// get reply
	String response = readResponse(istream);
	log.debug(response);

	// check result code
	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("213")) {
	    // command failed
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * delete the given file.
     * 
     * @param pathname
     *            the name of the file to delete
     * @exception IOException
     *                a socket IO error happened
     * @exception ServerResponseException
     *                the server replied with an error code
     */
    public synchronized void dele(String pathname) throws IOException,
	    ServerResponseException {
	ostream.write("dele " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> dele " + pathname);

	String response = readResponse(istream);
	log.debug(response);

	if (!response.substring(0, 3).equals("250")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * the file type is ASCII
     */
    public static final char TYPE_ASCII = 'A';

    /**
     * the file type is EBCDIC
     */
    public static final char TYPE_EBCDIC = 'E';

    /**
     * the file type is an 'image'. the file is binary data.
     */
    public static final char TYPE_IMAGE = 'I';

    /**
     * the file type is a local type
     */
    public static final char TYPE_LOCAL = 'L';

    /**
     * Set the file type for transfer. File types can be TYPE_ASCII,
     * TYPE_EBCDIC, TYPE_IMAGE or TYPE_LOCAL. This may affect how the client and
     * server interpret file data during transfers. The default type is
     * TYPE_ASCII per RFC 959.
     * 
     * @param value
     *            new type
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with error code
     */
    public synchronized void type(char value) throws IOException,
	    ServerResponseException {
	ostream.write("type " + value + "\r\n");
	ostream.flush();
	log.debug("-> type " + value);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
	fileType = value;
    }

    /**
     * Return the file type for transfer. File types can be TYPE_ASCII,
     * TYPE_EBCDIC, TYPE_IMAGE, or TYPE_LOCAL.
     * 
     * @return current file transfer type
     */
    public char getType() {
	return fileType;
    }

    // mode values

    /**
     * data transfer mode is STREAM
     */
    public static final char MODE_STREAM = 'S';

    /**
     * data transfer mode is BLOCK mode
     */
    public static final char MODE_BLOCK = 'B';

    /**
     * data transfer mode is COMPRESSED, as in UNIX compress.
     */
    public static final char MODE_COMPRESSED = 'C';

    /**
     * data transfer mode is ZLIB, as in zlib compression.
     */
    public static final char MODE_ZLIB = 'Z';

    /**
     * set the data transfer mode. Valid modes are MODE_STREAM, MODE_BLOCK,
     * MODE_COMPRESSED and MODE_ZLIB. NOTE: only MODE_STREAM (default) has been
     * used to date.
     * 
     * @param value
     *            new data transfer mode
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void mode(char value) throws IOException,
	    ServerResponseException {
	ostream.write("mode " + value + "\r\n");
	ostream.flush();
	log.debug("-> mode " + value);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * abort the last command
     * 
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error code
     */
    public synchronized void abor() throws IOException, ServerResponseException {
	ostream.write("abor\r\n");
	ostream.flush();
	log.debug("-> abor");

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("225")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * abort the last command on the given modem. experience shows that this
     * requires ADMIN priviledges.
     * 
     * @param modem
     *            the modem to abort the command on
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error
     */
    public synchronized void abor(String modem) throws IOException,
	    ServerResponseException {
	ostream.write("abor " + modem + "\r\n");
	ostream.flush();
	log.debug("-> abor " + modem);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("225")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * tell the server which address/port we will listen on
     * 
     * @param address
     *            address that we'll be listening on
     * @param port
     *            port on given address we'll be listening on
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void port(InetAddress address, int newPort)
	    throws IOException, ServerResponseException {
	// The PORT command sends a comma-delimited list of positive
	// decimal integers, each of which is one octet of the IP
	// address and port on which this client will accept a
	// data connection (for transfering data.) The bytes are
	// listed in network byte order (msb first.) An example
	// port command would be:
	// PORT a0,a1,a2,a3,p0,p1
	// where a0 and p0 are the msb's of the IP address and port
	// respectively.

	// convert IP address into a form usable by PORT command
	String addr = address.getHostAddress();

	// now turn all '.' characters into ',' characters
	addr = addr.replace('.', ',');

	String str = new String("port " + addr + ","
		+ ((newPort & 0xff00) >> 8) + "," + (newPort & 0x00ff));

	ostream.write(str + "\r\n");
	ostream.flush();
	log.debug("-> " + str + " (" + addr.replace(',', '.') + ":" + newPort
		+ ")");

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * store temp file, the file is stored in a uniquely named file on the
     * server. The remote temp file is deleted when the connection is closed.
     * 
     * @exception IOException
     *                io error occurred talking to the server
     * @exception ServerResponseException
     *                server replied with error code
     * @return the filename of the temp file
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
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	if (!st.nextToken().equals("150")) {
	    throw (new ServerResponseException(response));
	}
	st.nextToken(); // ignore 'FILE:' string
	filename = new String(st.nextToken()); // get filename value

	// transfering ...

	// next line tells us transfer completed
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	if (!st.nextToken().equals("226")) {
	    // some sort of error
	    throw (new ServerResponseException(response));
	}

	return filename;
    }

    /**
     * store a file with a unique name.
     * 
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error code
     * @return the name of the file created
     */
    public synchronized String stou(InputStream in) throws IOException,
	    ServerResponseException {
	String filename;
	String response;
	StringTokenizer st;

	// send stou command to server
	ostream.write("stou\r\n");
	ostream.flush();

	log.debug("-> stou");

	// get results
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	if (!st.nextToken().equals("150")) {
	    throw (new ServerResponseException(response));
	}
	st.nextToken(); // ignore 'FILE:' string
	filename = new String(st.nextToken()); // get filename value

	// transfering ...

	// next line tell us transfer completed
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	if (!st.nextToken().equals("226")) {
	    // some sort of error
	    throw (new ServerResponseException(response));
	}

	return filename;
    }

    /**
     * store a file.
     * 
     * @param pathname
     *            name of file to store on server (where to put the file on the
     *            server)
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error
     */
    public synchronized void stor(InputStream in, String pathname)
	    throws IOException, ServerResponseException {

	// tell server to start transfer
	ostream.write("stor " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> stor " + pathname);

	String response = readResponse(istream);
	log.debug(response);

	if (!response.substring(0, 3).equals("150")) {
	    throw (new ServerResponseException(response));
	}

	// transfer is happening

	// next line should indicate transfer is complete
	response = readResponse(istream);
	log.debug(response);

	if (!response.substring(0, 3).equals("226")) {
	    throw (new ServerResponseException(response));
	}

	// done
    }

    /**
     * get system type returns the string that the server sends (not sure how to
     * handle it yet) on a Debian GNU/Linux 2.1 (slink) system, the result
     * string is: "UNIX Type: L8 Version: SVR4"
     * 
     * @exception IOException
     *                a socket IO error occurred
     * @exception ServerResponseException
     *                the server replied with an error code
     * @return the system type string
     */
    public synchronized String syst() throws IOException,
	    ServerResponseException {
	ostream.write("syst\r\n");
	ostream.flush();
	log.debug("-> syst");

	// get response
	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("215")) {
	    throw (new ServerResponseException(response));
	}

	// get string to return
	return response.substring(4);
    }

    // stru values

    /**
     * the file structure is FILE.
     */
    public static final char STRU_FILE = 'F'; // file structure

    /**
     * the file structure is RECORD based.
     */
    public static final char STRU_RECORD = 'R'; // record structure

    /**
     * the file structure is PAGE based.
     */
    public static final char STRU_PAGE = 'P'; // page structure

    /**
     * the file structure is TIFF.
     */
    public static final char STRU_TIFF = 'T'; // TIFF structure

    /**
     * set the file structure. valid file structure settings are STRU_FILE,
     * STRU_RECORD, STRU_PAGE and STRU_TIFF. NOTE: only STRU_FILE has been
     * tested to date. I have no idea when you would use the other settings.
     * 
     * @param value
     *            file structure setting
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void stru(char value) throws IOException,
	    ServerResponseException {
	ostream.write("stru " + value + "\r\n");
	ostream.flush();
	log.debug("-> stru " + value);

	// check response
	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("200")) {
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * get a list of files in a directory. Like nlst() but with more information
     * per line. The difference is more like ls vs. ls -l. With FTP servers, I
     * know the output is very system dependant but since there aren't too many
     * different implementations of the HylaFAX server, it's probably not much
     * of an issue here. Still, beware.
     * <P>
     * This method relies on a data port (passive or active) to receive the list
     * data. it is recommended that you use the getList() wrapper method rather
     * than this method directly.
     * 
     * @param path
     *            path of file or directory to get listing. A <i>null</i> path
     *            will get the listing of the current directory.
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @exception FileNotFoundException
     *                the given path does not exist
     */
    public synchronized void list(String path) throws IOException,
	    FileNotFoundException, ServerResponseException {

	String command;
	if (path == null) {
	    command = "list";
	} else {
	    command = "list " + path;
	}
	ostream.write(command + "\r\n");
	ostream.flush();
	log.debug("-> " + command);

	// get response
	String response = readResponse(istream);
	log.debug(response);

	// check response
	StringTokenizer st = new StringTokenizer(response);
	String result_code;
	try {
	    result_code = st.nextToken();
	    if (!result_code.equals("150")) {
		if (result_code.equals("550")) {
		    // "No such file or directory"
		    throw (new FileNotFoundException(response));
		}
		throw (new ServerResponseException(response));
	    }

	    // transferring ...

	    // next line of response
	    response = readResponse(istream);
	    log.debug(response);

	    st = new StringTokenizer(response);
	    if (!st.nextToken().equals("226")) {
		throw (new ServerResponseException(response));
	    }
	} catch (NoSuchElementException e) {
	    throw new ServerResponseException("No code found in response: \"" + response + "\"", e);
	}
    }

    /**
     * get list of files in current directory.
     * <P>
     * NOTE: uses list() with the <i>null</i> path.
     * <P>
     * This method relies on a data socket of some sort to receive the list
     * data. It is recommended that you use the getList() convenience method
     * rather than this one directly.
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @exception FileNotFoundException
     *                server could not find the specified file
     */
    public synchronized void list() throws IOException, FileNotFoundException,
	    ServerResponseException {
	list(null);
    }

    /**
     * get name list of files in directory. Similar to list() but names only.
     * <P>
     * This method requires a data socket to receive the name list data. It is
     * recommended that you use the getNameList() method instead of this method
     * directly.
     * 
     * @param path
     *            path of the file or directory to list, passing in <i>null</i>
     *            will result in listing the current directory
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @exception FileNotFoundException
     *                server could not find the specified file
     */
    public synchronized void nlst(String path) throws IOException,
	    ServerResponseException, FileNotFoundException {

	// initiate the nlst command...
	String command;
	if (path == null) {
	    command = "nlst";
	} else {
	    command = "nlst " + path;
	}
	ostream.write(command + "\r\n");
	ostream.flush();
	log.debug("-> " + command);

	// check response
	String response = readResponse(istream);
	log.debug(response);

	// check response
	String result_code = response.substring(0, 3);
	if (!"150".equals(result_code)) {
	    if ("550".equals(result_code)) {
		// file not found - "no such file or directory"
		throw (new FileNotFoundException(response));
	    }
	    throw (new ServerResponseException(response));
	}

	// transferring...

	// check next line of response
	response = readResponse(istream);
	log.debug(response);

	result_code = response.substring(0, 3);
	if (!"226".equals(result_code)) {
	    throw (new ServerResponseException(response));
	}

	// transfer complete

    }

    /**
     * get name list of files in directory. Similar to list() but names only.
     * NOTE: uses the <i>null</i> path as the argument to nlst(String)
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @exception FileNotFoundException
     *                server could not find the specified file
     */
    public synchronized void nlst() throws IOException, FileNotFoundException,
	    ServerResponseException {
	nlst(null);
    }

    /**
     * retrieve the given file
     * 
     * @param path
     *            the relative or absolute path to the file to retrieve
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @exception FileNotFoundException
     *                the given path does not exist
     */
    public synchronized void retr(String path) throws IOException,
	    FileNotFoundException, ServerResponseException {
	String response;
	StringTokenizer st;

	// send retr command to server
	ostream.write("retr " + path + "\r\n");
	ostream.flush();

	log.debug("-> retr " + path);

	// get results
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	String code = st.nextToken();
	if (!code.equals("150")) {
	    if (code.equals("550")) {
		// path not found
		throw (new FileNotFoundException(response));
	    }
	    throw (new ServerResponseException(response));
	}

	// transfering ...

	// next line tells us transfer completed
	response = readResponse(istream);
	log.debug(response);

	st = new StringTokenizer(response);
	if (!st.nextToken().equals("226")) {
	    // some sort of error
	    throw (new ServerResponseException(response));
	}
    }

    /**
     * Returns the size (in bytes) of the given regular file. This is the size
     * on the server and may not accurately represent the file size once the
     * file has been transferred (particularly via ASCII mode)
     * 
     * @param pathname
     *            the name of the file to get the size for
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @exception FileNotFoundException
     *                the given path does not exist
     */
    public synchronized long size(String pathname) throws IOException,
	    FileNotFoundException, ServerResponseException {
	ostream.write("size " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> size " + pathname);

	String response = readResponse(istream);
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

    public final static String MDTM_TIME_FORMAT1 = "yyyyMMddHHmmss.SSS";

    public final static String MDTM_TIME_FORMAT2 = "yyyyMMddHHmmss";

    /**
     * Returns the last modified time of the given file. This command is
     * specified in the FTPEXT Working Group draft currently available at <code>
     * http://www.ietf.org/internet-drafts/draft-ietf-ftpext-mlst-15.txt</code>
     * .
     * <p>
     * The date is returned in GMT in a string of the following format:
     * <code>YYYYMMDDhhmmss.ddd</code> where <code>.ddd</code> is an optional
     * suffix reporting milliseconds. This method attempts to parse the string
     * returned by the server and present the caller with a java.util.Date
     * instance.
     * 
     * @param pathname
     *            the name of the file to get the last-modified time for
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @exception FileNotFoundException
     *                the given path does not exist
     * @exception ParseException
     *                the server returned an unrecognized date format
     */
    public synchronized Date mdtm(String pathname) throws IOException,
	    FileNotFoundException, ServerResponseException, ParseException {
	ostream.write("mdtm " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> mdtm " + pathname);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	String return_code = st.nextToken();
	if (!return_code.equals("213")) {
	    if (return_code.equals("550")) {
		throw new FileNotFoundException(response);
	    }
	    throw new ServerResponseException(response);
	}

	String time = st.nextToken();
	SimpleDateFormat sdf;
	if (time.indexOf('.') == -1) {
	    // no '.', using format2
	    sdf = new SimpleDateFormat(MDTM_TIME_FORMAT2);
	} else {
	    // using format1
	    sdf = new SimpleDateFormat(MDTM_TIME_FORMAT1);
	}
	TimeZone tz = TimeZone.getTimeZone("GMT");
	Calendar c = sdf.getCalendar();
	c.setTimeZone(tz);
	sdf.setCalendar(c);
	Date d = sdf.parse(time);
	return d;
    }

    /**
     * Specifies a file to be renamed. This command must be immediately followed
     * by a RNTO command. It is recommended that you use the rename()
     * convenience method rather than this one directly.
     * 
     * @param pathname
     *            the name of the file to be renamed
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @exception FileNotFoundException
     *                the given path does not exist
     */
    public synchronized void rnfr(String pathname) throws IOException,
	    FileNotFoundException, ServerResponseException {
	ostream.write("rnfr " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> rnfr " + pathname);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	String return_code = st.nextToken();
	if (!return_code.equals("350")) {
	    if (return_code.equals("550")) {
		throw new FileNotFoundException(response);
	    }
	    throw new ServerResponseException(response);
	}
	return;
    }

    /**
     * Renames a previously specified file to the given name. This command must
     * be immediately preceded by a RNFR command. It is recommended that you use
     * the rename() convenience method rather than this one directly.
     * 
     * @param pathname
     *            the name of the file to be renamed
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     */
    public synchronized void rnto(String pathname) throws IOException,
	    ServerResponseException {
	ostream.write("rnto " + pathname + "\r\n");
	ostream.flush();
	log.debug("-> rnto " + pathname);

	String response = readResponse(istream);
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response);
	String return_code = st.nextToken();
	if (!return_code.equals("250")) {
	    throw new ServerResponseException(response);
	}
	return;
    }

    static final Pattern responsePattern = Pattern.compile("(\\d{3})(.)\\s*(.*)");
    
    /**
     * Returns the status of the named file or directory.
     * 
     * @param path
     *            the directory or file to get the status of, using a null path
     *            will cause this method to return the server status information
     * @exception IOException
     *                caused by a socket IO error
     * @exception FileNotFoundException
     *                the given path or file does not exist
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @return a Vector of String objects, each String being a single line of
     *         the status message from the server
     */
    public synchronized Vector stat(String path) throws IOException,
	    FileNotFoundException, ServerResponseException {
	String command;
	if (path == null) {
	    command = "stat\r\n";
	} else {
	    command = "stat " + path + "\r\n";
	}
	ostream.write(command);
	ostream.flush();
	log.debug("-> " + command);

	String response = istream.readLine();
	log.debug(response);

	StringTokenizer st = new StringTokenizer(response, "- ");
	String return_code = st.nextToken();
	String remainder = response.substring(4);
	if (return_code.equals("550")) {
	    throw new FileNotFoundException(remainder);
	}
	if (!return_code.equals("211")) {
	    if (response.charAt(3) == '-') {
		// it's a multi-line response, read the whole response
		String first_token;
		boolean done = false;
		while (!done) {
		    response = istream.readLine();
		    if (response == null) {
			// end of input, bail
			done = true;
		    } else {
			log.debug(response);
			remainder = remainder + "\n" + response;
			Matcher m = responsePattern.matcher(response);
			if (m.matches()) {
			    first_token = m.group(1);
			    if (return_code.equals(first_token)) {
				// last line, we're done
				done = true;
			    }
			}
		    }
		}
	    }
	    // now indicate a protocol error
	    throw new ServerResponseException(remainder);
	}
	// read and return the server reply
	Vector status = new Vector();
	if (response.charAt(3) == '-') {
	    // it's a multi-line response, read the whole response
	    String first_token;
	    boolean done = false;
	    while (!done) {
		response = istream.readLine();
		if (response == null) {
		    // end of input, bail
		    done = true;
		} else {
		    log.debug(response);

		    Matcher m = responsePattern.matcher(response);
		    if (m.matches()) {
			first_token = m.group(1);
			if (return_code.equals(first_token)) {
			    // last line, we're done
			    done = true;
			} else {
			    status.addElement(response);
			}
		    } else {
			status.addElement(response);
		    } 
		}
	    }
	}
	return status;
    }

    /**
     * Returns the server status message. This is equivalent to using
     * stat(null).
     * 
     * @exception IOException
     *                caused by a socket IO error
     * @exception ServerResponseException
     *                caused by a server response indicating an error
     * @return a Vector of String objects, each String being a single line of
     *         the status message from the server
     */
    public synchronized Vector stat() throws IOException,
	    ServerResponseException {
	return stat(null);
    }

    /**
     * prepare for server-to-server transfer (passive mode)
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     * @return tuple containing the server IP address and port number
     */
    public synchronized PassiveParameters pasv() throws IOException,
	    ServerResponseException {
	ostream.write("pasv\r\n");
	ostream.flush();
	log.debug("-> pasv");

	// get reply
	String response = readResponse(istream);
	log.debug(response);

	// check response
	StringTokenizer st = new StringTokenizer(response);
	if (!st.nextToken().equals("227")) {
	    throw (new ServerResponseException(response));
	}

	// get ip-address & port returned
	st.nextToken("(,)");
	String addr = new String(st.nextToken());
	addr = addr + "." + st.nextToken();
	addr = addr + "." + st.nextToken();
	addr = addr + "." + st.nextToken();

	int p = (((Integer.parseInt(st.nextToken()) << 8) & 0xff00) | (Integer
		.parseInt(st.nextToken()) & 0x00ff));
	return new PassiveParameters(addr, p);

    }

    /**
     * end session
     * 
     * @exception IOException
     *                io error occurred
     * @exception ServerResponseException
     *                server replied with an error code
     */
    public synchronized void quit() throws IOException, ServerResponseException {
	try {
	    // send quit command to server
	    ostream.write("quit\r\n");
	    ostream.flush();
	    log.debug("-> quit");

	    // make sure command is accepted
	    String response = readResponse(istream);
	    log.debug(response);

	    String temp = new String(response.substring(0, 3));
	    if (!temp.equals("221")) { // looking for 'Goodbye' message
		throw (new ServerResponseException(response));
	    }
	} finally {
	    if (sock != null && !sock.isClosed()) {
		try {
		    log.debug("Setting socket to 0 lingering");
		    sock.setSoLinger(true, 0);
		    sock.close();
		} catch (SocketException e) {
		    // Don't care.
		}
	    }
	}
    }

    // ***** data manipulation functions *****

    // return the greeting value
    public String getGreeting() {
	return greeting;
    }

    // ***** protected methods *****

    /**
     * read a (multi-line) response
     * 
     * @param input
     *            the BufferedReader to read data from
     * @exception IOException
     *                an IO error occurred
     * @return the response in a Vector of Strings
     */
    protected synchronized String readResponse(BufferedReader input)
	    throws IOException {
	String rc = "";
	String response = "";

	boolean done = false;
	boolean first = true;

	String tmp = null;
	while (!done && (tmp = input.readLine()) != null) {
	    if (tmp.length() > 0) { // Ignore empty lines
		response += tmp + '\n';
		if (tmp.length() >= 4) {
		    if (first == true) {
			// first time through
			rc = response.substring(0, 3);
			first = false;
		    }
		    // from rfc0959, a multiline response has '-' characters
		    // in the third position for all response lines other than
		    // the last which has a ' ' character after the response
		    // code number.
		    if ((rc.equals(tmp.substring(0, 3))) && (tmp.charAt(3) == ' ')) {
			done = true;
		    }
		} else {
		    // length is < 4
		    // not sure if this is a valid condition
		    // I assume this is ok and continue...
		}
	    }
	}
	return response;
    }

    /**
     * Returns the the character encoding to use to send and receive Strings from the server
     * @return the currently used character encoding
     */
    public String getCharacterEncoding() {
	return characterEncoding;
    }
    
    /**
     * Sets the character encoding to use to send and receive Strings from the server.
     * 
     * NOTE: If you wish to change the character encoding, this should be done before connecting to the server (i.e. before calling open()).
     * @param characterEncoding the name of the encoding to use
     */
    public void setCharacterEncoding(String characterEncoding) {
	this.characterEncoding = characterEncoding;
    }
    
    // ***** private methods *****

    /**
     * The number of parts in an IPv4 ip address.
     */
    private static final int IPV4_LENGTH = 4;

    /**
     * The maximum value for an IPv4 address part.
     */
    private static final int IPV4_MAX = 255;

    /**
     * Attempts to take a host name and parse it into an InetAddress.
     * 
     * @param address
     *            an IPv4 address in the form of xxx.xxx.xxx.xxx
     * @return the InetAddress result.
     */
    public static final InetAddress parseIPv4Address(final String address) {
	try {
	    String[] parts = address.split("\\.");
	    if (parts.length == IPV4_LENGTH) {
		try {
		    byte[] bytes = new byte[IPV4_LENGTH];
		    for (int i = 0; i < IPV4_LENGTH; i++) {
			int part = Integer.parseInt(parts[i]);
			if (part < 0 || part > IPV4_MAX) {
			    throw new NumberFormatException("Invalid part: "
				    + part);
			}
			bytes[i] = (byte) part;
		    }
		    return InetAddress.getByAddress(bytes);
		} catch (NumberFormatException e) {
		    log.debug("Not an IPv4 address: " + address);
		}
	    }

	} catch (Exception e) {
	    log.debug("Not an IPv4 address: " + address);
	}
	return null;
    }

    /**
     * connect to the given host at the given port number.
     * 
     * @param host
     *            the hostname of the server to connect to
     * @param portnumber
     *            the port that the server is listening on
     * @exception UnknownHostException
     *                the given host name cannot be resolved
     * @exception IOException
     *                an IO error occurred
     * @exception ServerResponseException
     *                the server responded with an error
     */
    protected void connect(String host, int portnumber)
	    throws UnknownHostException, IOException, ServerResponseException {

	hylafaxServerHost = host;
	hylafaxServerPort = portnumber;

	// try and bypass a potential dns bug.
	InetAddress inetAddress = parseIPv4Address(host);
	if (inetAddress == null) {
	    inetAddress = InetAddress.getByName(host);
	}
	// create and configure the socket.
	sock = new Socket(inetAddress, portnumber);
	sock.setSoTimeout(socketTimeout);

	// open streams for input and output to server
	istream = new BufferedReader(new InputStreamReader(sock
		.getInputStream(), getCharacterEncoding()));

	// for international users, the following MAY need to be changed
	// to specify the "US-ASCII" encoding as the second parameter.
	// someone will need to test this, hint hint
	ostream = new OutputStreamWriter(sock.getOutputStream(), getCharacterEncoding());

	// get greeting line(s) of text from server
	greeting = readResponse(istream);

	// make sure response starts with "220" before continuing
	String temp = new String(greeting.substring(0, 3));

	// Log any connect warnings from hfaxd.
	// There are two specifically we know are possibilities.
	// 1. 130 Warning, no inverse address mapping for client host name
	// 2. 130 Warning, client address is not listed for host name
	while (temp.equals("130")) {
	    log.warn(greeting);
	    greeting = readResponse(istream);
	    temp = new String(greeting.substring(0, 3));
	}

	if (!temp.equals("220")) {
	    throw (new ServerResponseException(greeting));
	}

	port = portnumber;
    }

}

// FtpClientProtocol.java
