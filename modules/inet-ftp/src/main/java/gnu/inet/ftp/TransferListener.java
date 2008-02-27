// TransferListener.java - 
// $Id: TransferListener.java,v 1.3 2006/02/20 04:52:11 sjardine Exp $
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

import java.util.EventListener;

/**
 * This describes the interface used to get transfer status events.
 */
public interface TransferListener extends EventListener {

    /**
     * Indicates the data transfer has begun.
     */
    void transferStarted();

    /**
     * Indicates the data transfer has completed.
     */
    void transferCompleted();

    /**
     * Indicates some data has been transfered.
     * 
     * @param amount
     *                the amount of data transfered since transferStarted() was
     *                called
     */
    void transfered(long amount);

    /**
     * Indicates some error occurred during transfer.
     */
    void transferFailed();

}
