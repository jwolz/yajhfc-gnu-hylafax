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
package gnu.hylafax.pool;

import gnu.hylafax.Client;
import gnu.inet.ftp.ConnectionEventSource;
import gnu.inet.ftp.TransferEventSource;

public interface PooledClient extends Client, TransferEventSource, ConnectionEventSource {

    /**
     * Destroy the client. This makes the client unusable.
     * @throws ClientPoolException
     */
    public void destroy() throws ClientPoolException;

    /**
     * Checks to see if the client is currently valid. If the client is not valid it should be reopened or
     * destroyed.
     */
    public boolean isValid();

}
