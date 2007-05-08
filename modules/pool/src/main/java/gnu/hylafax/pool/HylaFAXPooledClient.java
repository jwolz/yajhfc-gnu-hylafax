//$Id: HylaFAXPooledClient.java,v 1.8 2007/05/07 18:26:55 sjardine Exp $
//
//Copyright 2005 Steven Jardine <steve@mjnservices.com>
//Copyright 2005 MJN Services, Inc - http://www.mjnservices.com
//
//for information on the HylaFAX FAX server see
//http://www.hylafax.org/
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Library General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Library General Public License for more details.
//
//You should have received a copy of the GNU Library General Public
//License along with this library; if not, write to the Free
//Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
package gnu.hylafax.pool;

import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.Job;
import gnu.inet.ftp.ServerResponseException;
import gnu.inet.logging.Logger;
import gnu.inet.logging.LoggingFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * This is an extension of the <code>gnu.hylafax.HylaFAXClient</code> specifically designed to work with the
 * <code>gnu.hylafax.pool.ClientPool</code> pooling functionality.
 * 
 * @author <a href="mailto:sjardine@users.sourceforge.net">Steven Jardine </a>
 */
public class HylaFAXPooledClient extends HylaFAXClient implements PooledClient, Runnable {

    private static final Logger log = LoggingFactory.getLogger(HylaFAXPooledClient.class);

    private ClientPool clientPool;

    private boolean terminated = false;

    private Thread thread = new Thread(this);

    private boolean valid = true;

    private boolean working = true;

    /**
     * Default constructor.
     * 
     * @param clientPool
     */
    public HylaFAXPooledClient(ClientPool clientPool) {

        this.clientPool = clientPool;
        start();

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#admin(java.lang.String)
     */
    public void admin(String password) throws IOException, ServerResponseException {

        log.warn("Method ignored for pooled clients.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#createJob()
     */
    public Job createJob() throws ServerResponseException, IOException {

        if (valid && working) return super.createJob();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#delete(gnu.hylafax.Job)
     */
    public void delete(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.delete(job);

    }

    private boolean forceReopen = false;

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.pool.PooledClient#destroy()
     */
    public void destroy() throws ClientPoolException {

        try {
            super.quit();
            stop();

        } catch (Exception e) {

            throw new ClientPoolException(e.getMessage());

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#filefmt()
     */
    public String filefmt() throws IOException, ServerResponseException {

        if (valid && working) return super.filefmt();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#filefmt(java.lang.String)
     */
    public void filefmt(String value) throws IOException, ServerResponseException {

        if (valid && working) super.filefmt(value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#get(java.lang.String, java.io.OutputStream)
     */
    public void get(String path, OutputStream out) throws IOException, FileNotFoundException, ServerResponseException {

        if (valid && working) super.get(path, out);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getJob(long)
     */
    public Job getJob(long id) throws ServerResponseException, IOException {

        if (valid && working) return super.getJob(id);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getList()
     */
    public Vector getList() throws IOException, FileNotFoundException, ServerResponseException {

        if (valid && working) return super.getList();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getList(java.lang.String)
     */
    public Vector getList(String path) throws IOException, FileNotFoundException, ServerResponseException {

        if (valid && working) return super.getList(path);
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getNameList()
     */
    public Vector getNameList() throws IOException, ServerResponseException, FileNotFoundException {

        if (valid && working) return super.getNameList();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getNameList(java.lang.String)
     */
    public Vector getNameList(String path) throws IOException, ServerResponseException, FileNotFoundException {

        if (valid && working) return super.getNameList(path);
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#getPassive()
     */
    public boolean getPassive() {

        if (valid && working) return super.getPassive();
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#idle()
     */
    public long idle() throws IOException, ServerResponseException {

        if (valid && working) return super.idle();
        return -1;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#idle(long)
     */
    public void idle(long timeout) throws IOException, ServerResponseException {

        if (valid && working) super.idle(timeout);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#interrupt(gnu.hylafax.Job)
     */
    public void interrupt(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.interrupt(job);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.pool.PooledClient#isValid()
     */
    public boolean isValid() {

        return valid;

    }

    /**
     * @return true if the client is given out for work.
     */
    public boolean isWorking() {
        return working;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jdele(long)
     */
    public void jdele(long jobid) throws IOException, ServerResponseException {

        if (valid && working) super.jdele(jobid);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jintr(long)
     */
    public void jintr(long jobid) throws IOException, ServerResponseException {

        if (valid && working) super.jintr(jobid);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jkill(long)
     */
    public void jkill(long jobid) throws IOException, ServerResponseException {

        if (valid && working) super.jkill(jobid);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jnew()
     */
    public void jnew() throws IOException, ServerResponseException {

        if (valid && working) super.jnew();

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#job()
     */
    public long job() throws IOException, ServerResponseException {

        if (valid && working) return super.job();
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#job(long)
     */
    public void job(long value) throws IOException, ServerResponseException {

        if (valid && working) super.job(value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jobfmt()
     */
    public String jobfmt() throws IOException, ServerResponseException {

        if (valid && working) return super.jobfmt();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jobfmt(java.lang.String)
     */
    public void jobfmt(String value) throws IOException, ServerResponseException {

        if (valid && working) super.jobfmt(value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String)
     */
    public String jparm(String parm) throws IOException, ServerResponseException {

        if (valid && working) return super.jparm(parm);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, int)
     */
    public void jparm(String parm, int value) throws IOException, ServerResponseException {

        if (valid && working) super.jparm(parm, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, long)
     */
    public void jparm(String parm, long value) throws IOException, ServerResponseException {

        if (valid && working) super.jparm(parm, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, java.lang.Object)
     */
    public void jparm(String parm, Object value) throws IOException, ServerResponseException {

        if (valid && working) super.jparm(parm, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jparm(java.lang.String, java.lang.String)
     */
    public void jparm(String parm, String value) throws IOException, ServerResponseException {

        if (valid && working) super.jparm(parm, value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jrest()
     */
    public void jrest() throws IOException, ServerResponseException {

        if (valid && working) super.jrest();

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsubm()
     */
    public long jsubm() throws IOException, ServerResponseException {

        if (valid && working) return super.jsubm();
        return -1;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsubm(long)
     */
    public int jsubm(long jobid) throws IOException, ServerResponseException {

        if (valid && working) return super.jsubm(jobid);
        return -1;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jsusp(long)
     */
    public void jsusp(long jobid) throws IOException, ServerResponseException {

        if (valid && working) super.jsusp(jobid);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#jwait(long)
     */
    public void jwait(long jobid) throws IOException, ServerResponseException {

        if (valid && working) super.jwait(jobid);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#kill(gnu.hylafax.Job)
     */
    public void kill(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.kill(job);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#mdmfmt()
     */
    public String mdmfmt() throws IOException, ServerResponseException {

        if (valid && working) return super.mdmfmt();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#mdmfmt(java.lang.String)
     */
    public void mdmfmt(String value) throws IOException, ServerResponseException {

        if (valid && working) super.mdmfmt(value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#mode(char)
     */
    public void mode(char mode) throws IOException, ServerResponseException {

        if (valid && working) super.mode(mode);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#noop()
     */
    public void noop() throws IOException, ServerResponseException {

        if (valid && working) super.noop();

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open()
     */
    public void open() throws UnknownHostException, IOException, ServerResponseException {
        log.warn("Method open() ignored for pooled clients.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open(java.lang.String)
     */
    public void open(String host) throws UnknownHostException, IOException, ServerResponseException {
        log.warn("Method open(String host) ignored for pooled clients.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#open(java.lang.String, int)
     */
    public void open(String host, int port) throws UnknownHostException, IOException, ServerResponseException {
        log.warn("Method open(String host, int port) ignored for pooled clients.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#pass(java.lang.String)
     */
    public void pass(String password) throws IOException, ServerResponseException {
        log.warn("Method pass(String password) ignored for pooled clients.");
    }

    /**
     * Set the admin password for pooled clients.
     * 
     * @param password
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolAdmin(String password) throws IOException, ServerResponseException {
        super.admin(password);
    }

    /**
     * Open method for pooled clients.
     * 
     * @throws UnknownHostException
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolOpen() throws UnknownHostException, IOException, ServerResponseException {
        super.open();
    }

    /**
     * Open method for pooled clients.
     * 
     * @param host
     * @throws UnknownHostException
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolOpen(String host) throws UnknownHostException, IOException, ServerResponseException {
        super.open(host);
    }

    /**
     * Open method for pooled clients.
     * 
     * @param host
     * @param port
     * @throws UnknownHostException
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolOpen(String host, int port) throws UnknownHostException, IOException, ServerResponseException {
        super.open(host, port);
    }

    /**
     * Password method for pooled clients.
     * 
     * @param password
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolPass(String password) throws IOException, ServerResponseException {
        super.pass(password);
    }

    /**
     * Timezone method for pooled clients.
     * 
     * @param value
     * @throws IOException
     * @throws ServerResponseException
     */
    void poolTzone(String value) throws IOException, ServerResponseException {
        super.tzone(value);
    }

    /**
     * User method for pooled clients.
     * 
     * @param username
     * @return the user associated with the username.
     * @throws IOException
     * @throws ServerResponseException
     */
    boolean poolUser(String username) throws IOException, ServerResponseException {
        return super.user(username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#put(java.io.InputStream)
     */
    public String put(InputStream in) throws IOException, ServerResponseException {

        if (valid && working) return super.put(in);
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#put(java.io.InputStream, java.lang.String)
     */
    public void put(InputStream in, String pathname) throws IOException, ServerResponseException {

        if (valid && working) super.put(in, pathname);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#putTemporary(java.io.InputStream)
     */
    public String putTemporary(InputStream data) throws IOException, ServerResponseException {

        if (valid && working) return super.putTemporary(data);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#quit()
     */
    public void quit() throws IOException, ServerResponseException {

        try {

            forceReopen = true;
            clientPool.put(this);

        } catch (ClientPoolException e) {

            log.error(e);

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#rcvfmt()
     */
    public String rcvfmt() throws IOException, ServerResponseException {

        if (valid && working) return super.rcvfmt();
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#rcvfmt(java.lang.String)
     */
    public void rcvfmt(String value) throws IOException, ServerResponseException {

        if (valid && working) super.rcvfmt(value);

    }

    /**
     * Attempts to reopen the client connection.
     * 
     * @throws ClientPoolException
     */
    private void reopen() throws ClientPoolException {
        try {

            log.debug("Attempting to reopen client.");

            forceReopen = false;
            super.quit();
            clientPool.openClient(this);

            log.debug("Reopen successful.");

        } catch (Exception e) {

            throw new ClientPoolException(e.getMessage());

        }
    }

    private long lastNoop = -1;

    private long lastReopen = -1;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        Thread.currentThread().setName("Pooled Client");

        while (!terminated) {

            try {

                // Only send noop on idle connections every 10 seconds.
                if (!working) {

                    long time = System.currentTimeMillis();

                    if (forceReopen || (time - lastReopen) >= clientPool.getConfiguration().getMaxIdleTime()) {

                        reopen();
                        lastReopen = System.currentTimeMillis();

                    } else if ((time - lastNoop) >= clientPool.getConfiguration().getMaxNoopTime()) {

                        super.noop();
                        lastNoop = System.currentTimeMillis();

                    }
                }

                Thread.sleep(1000);
                if (clientPool.isStopped()) terminated = true;

            } catch (InterruptedException e) {

                // Do nothing.

            } catch (Exception e) {

                // All other exceptions should initiate a reopen of the client.
                valid = false;
                forceReopen = true;

                // Try and reopen the connection.
                try {
                    Thread.sleep(5000); // Wait before attempting to reopen
                    // connection.
                } catch (Exception e2) {
                    // Do Nothing.
                }
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#setDebug(boolean)
     */
    public void setDebug(boolean value) {

        if (valid && working) super.setDebug(value);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#setPassive(boolean)
     */
    public void setPassive(boolean passive) {

        if (valid && working) super.setPassive(passive);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.pool.PooledClient#setValid(boolean)
     */
    public void setValid(boolean valid) {

        this.valid = valid;

    }

    /**
     * Set to true when the client has been given out of the pool. false when the client is idle.
     * 
     * @param working
     */
    void setWorking(boolean working) {

        this.working = working;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#size(java.lang.String)
     */
    public long size(String pathname) throws IOException, FileNotFoundException, ServerResponseException {

        if (valid && working) return super.size(pathname);
        return -1;

    }

    /**
     * Start the maintaince thread for this client.
     */
    public void start() {

        terminated = false;
        lastNoop = lastReopen = System.currentTimeMillis();
        thread.start();

    }

    /**
     * Stops the maintaince thread for this client.
     */
    public void stop() {

        terminated = true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#stot(java.io.InputStream)
     */
    public String stot(InputStream data) throws IOException, ServerResponseException {

        if (valid && working) return super.stot(data);
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#submit(gnu.hylafax.Job)
     */
    public void submit(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.submit(job);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.Client#suspend(gnu.hylafax.Job)
     */
    public void suspend(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.suspend(job);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#tzone(java.lang.String)
     */
    public void tzone(String value) throws IOException, ServerResponseException {

        log.warn("Method tzone(String value) ignored for pooled clients.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.inet.ftp.FtpClientProtocol#user(java.lang.String)
     */
    public boolean user(String username) throws IOException, ServerResponseException {

        log.warn("Method user(String username) ignored for pooled clients.");
        return false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gnu.hylafax.ClientProtocol#vrfy(java.lang.String)
     */
    public InetAddress vrfy(String dialstring) throws IOException, ServerResponseException {

        if (valid && working) return super.vrfy(dialstring);
        return null;

    }

    /*
     * (non-Javadoc)s
     * 
     * @see gnu.hylafax.Client#wait(gnu.hylafax.Job)
     */
    public void wait(Job job) throws ServerResponseException, IOException {

        if (valid && working) super.wait(job);

    }

}
