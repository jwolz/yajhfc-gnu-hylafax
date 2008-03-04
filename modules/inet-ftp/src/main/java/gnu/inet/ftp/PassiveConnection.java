// PassiveConnection.java
// $Id: PassiveConnection.java,v 1.3 2006/02/20 04:52:11 sjardine Exp $
//
// Copyright 2000, Joe Phillips <jaiger@innovationsw.com>
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

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class encapsulates the parameters of a passive data connection.
 */
public class PassiveConnection extends Object {

    // private data members
    private PassiveParameters parameters;
    private Socket sock;

    //
    // public methods
    //

    /**
     * create a new instance of PassiveParameters
     * 
     * @param parameters
     *                passive connection parameters
     */
    public PassiveConnection(PassiveParameters parameters)
	    throws UnknownHostException, IOException {
	this.parameters = parameters;
	this.sock = new Socket(parameters.getInetAddress(), parameters
		.getPort());
    }

    /**
     * get the passive parameters
     * 
     * @return passive parameter data
     */
    public PassiveParameters getPassiveParameters() {
	return parameters;
    }

    /**
     * get the socket for this passive connection
     * 
     * @return the socket for this passive connection
     */
    public Socket getSocket() {
	return this.sock;
    }

    /**
     * compare another PassiveConnection instance to this one.
     * 
     * @param other
     *                the other instance to compare this one with
     * @return true if the other instance equals this one, false if they are not
     *         equal
     */
    public boolean equals(PassiveConnection other) {
	if (this.parameters.equals(other.parameters)) {
	    return true;
	}
	return false;
    }

}

// PassiveConnection.java
