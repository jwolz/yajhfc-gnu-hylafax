//$Id$
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

package gnu.hylafax;

import gnu.hylafax.pool.ClientPoolException;

/**
 * @author <a href="mailto:steve@mjnservices.com">Steven Jardine </a>
 */
public interface ClientPool {

    /**
     * Retrieve a client from the client pool. If the pool is empty or all
     * connections are being used this function will block for
     * <code>blockingTimeout</code> milli-seconds and then throw an exception.
     * 
     * @return A client from the client pool.
     */
    public Client getClient() throws ClientPoolException;

    /**
     * Get the username that has been designated for use in creating client
     * connections.
     * 
     * @return the username being used to connect to the client pool.
     */
    public String getUserName() throws ClientPoolException;

    /**
     * Sets the username to be used to connect clients to the server.
     * 
     * @param userName
     *            The usename to be used for client connections.
     */
    public void setUserName(String userName) throws ClientPoolException;

    /**
     * Sets the password to be used to connect clients to the server. If the
     * password is "" or <code>null</code> it will not be used to connect.
     * 
     * @param password
     */
    public void setPassword(String password) throws ClientPoolException;

    /**
     * @return the number of milli-seconds the pool will wait for an available
     *         connection prior to throwing an exception.
     */
    public long getBlockingTimeout() throws ClientPoolException;

    /**
     * Sets the number of milli-seconds to wait for an available connection.
     * 
     * @param blockingTimeout
     *            the number of milli-seconds the pool will wait for an
     *            available connection prior to throwing an exception.
     */
    public void setBlockingTimeout(long blockingTimeout)
            throws ClientPoolException;

    /**
     * @return the maximum number of clients allowed by the client pool.
     */
    public int getMaxPoolSize() throws ClientPoolException;

    /**
     * Sets the maximum number of clients allowed by the client pool.
     * 
     * @param maxPoolSize
     */
    public void setMaxPoolSize(int maxPoolSize) throws ClientPoolException;

    /**
     * @return the minimum number of clients in the pool.
     */
    public int getMinPoolSize() throws ClientPoolException;

    /**
     * Sets the minimum number of clients to initialize the pool with.
     * 
     * @param minPoolSize
     */
    public void setMinPoolSize(int minPoolSize) throws ClientPoolException;

    /**
     * @return the number of milli-seconds of client inactivity before sending a
     *         noop command.
     */
    public long getNoopInterval() throws ClientPoolException;

    /**
     * Sets the number of milli-seconds of client inactivity before sending a
     * noop command to the server.
     * 
     * @param noopInterval
     */
    public void setNoopInterval(long noopInterval) throws ClientPoolException;

}
