// ThreadSafeJob.java - a HylaFAX Job representation
// $Id$
//
// Copyright 2003 Innovation Software Group, LLC - http://www.innovationsw.com
//                Joe Phillips <jaiger@innovationsw.com>
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
package gnu.hylafax.job;

import java.io.IOException;
import gnu.inet.ftp.ServerResponseException;
import gnu.hylafax.Client;

/**
 * This is a thread-safe implementation of the gnu.hylafax.Job interface.
 * @see gnu.hylafax.ClientProtocol
 * @see gnu.hylafax.Client
 **/
public class ThreadSafeJob extends Job
{

   public ThreadSafeJob(Client c)
      throws ServerResponseException, IOException
   {
	   super(c);
   }// constructor

   public ThreadSafeJob(Client c, long id)
      throws ServerResponseException, IOException
   {
	   super(c,id);
   }// constructor

   /**
    * Get the value for an arbitrary property for this job.
    * Developers using this method should be familiar with the HylaFAX client protocol in order to provide the correct key values and how to interpret the values returned.
    * This method is thread-safe.
    * @exception ServerResponseException the server responded with an error.  This is likely due to a protocol error.
    * @exception IOException an i/o error occured
    * @return a String value for the given property key
    */
   public String getProperty(String key)
      throws ServerResponseException, IOException
   {
      synchronized(client){
         long j= client.job();
         client.job(getId());
         String tmp= super.getProperty(key);
         client.job(j);
         return tmp;
      }
   }// getProperty

   /**
    * Set any arbitrary property on this job.
    * In order to use this method, developers should be familiar with the HylaFAX client protocol.
    * This method is thread-safe.
    * @exception ServerResponseException the server responded with an error code.  This is likely a protocol violation.
    * @exception IOException an i/o error occured
    */
   public void setProperty(String parameter, String value)
      throws ServerResponseException, IOException
   {
      synchronized(client){
         long j= client.job();
         client.job(getId());
         super.setProperty(parameter, value);
         client.job(j);
      }
   }// setProperty


}// ThreadSafeJob class
// ThreadSafeJob.java
