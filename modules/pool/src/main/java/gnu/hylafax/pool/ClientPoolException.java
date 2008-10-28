//$Id: ClientPoolException.java,v 1.3 2006/02/20 05:00:25 sjardine Exp $
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

/**
 * @author <a href="mailto:steve@mjnservices.com">Steven Jardine </a>
 */
public class ClientPoolException extends Exception {

    public ClientPoolException() {
	super();
    }

    public ClientPoolException(String message) {
	super(message);
    }

}
