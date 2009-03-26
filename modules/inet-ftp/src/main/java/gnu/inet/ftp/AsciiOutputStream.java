// AsciiOutputStream.java
// $Id$
//
// Copyright 2002 Innovation Software Group, LLC - http://www.innovationsw.com
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

import java.io.*;

/**
 * The <code>AsciiOutputStream</code> class acts as a filter to convert outgoing
 * ASCII FTP streams from the system's local ASCII format by filtering and
 * converting line termination strings. Note that the class as currently
 * written will only handle <code>\r\n</code> or single character line
 * termination strings; a more technically correct implementation would be
 * preferable.
 */
public class AsciiOutputStream extends FilterOutputStream {

   protected boolean active;
   protected int eol;

   /**
    * Creates an AsciiOutputStream by passing <code>out</code> to its
    * superclass' constructor and determining the system-specific line
    * termination string.
    *
    * @param  out  the underlying output stream.
    * 
    * @throws  Exception  if system line separator is longer than 1 char and is
    *                     not <code>\r\n</code>
    */
   public AsciiOutputStream(OutputStream out) throws Exception {
      super(out);

      String lineSeparator = System.getProperty("line.separator");
      if (lineSeparator.equals("\r\n")) {
         active = false;
      } else if (lineSeparator.length() > 1) {
         throw new Exception("System line separator longer than 1 char");
      } else {
         active = true;
         eol = lineSeparator.charAt(0);
      }
   }
  
   /**
    * Writes the specified byte to this output stream.
    *
    * @param  b  the byte.
    *
    * @throws  IOException  if an I/O error occurs.
    */
   public void write(int b) throws IOException {
      if (active && b == eol) {
         out.write('\r');
	 out.write('\n');
      } else {
         out.write(b);
      }
   }

   /**
    * Writes b.length bytes to this output stream.
    *
    * @param  b  the data to be written.
    *
    * @throws  IOException  if an I/O error occurs.
    */
   public void write(byte[] b) throws IOException {
      write(b, 0, b.length);
   }

   /**
    * Writes <code>len</code> bytes from the specified byte array starting at
    * offset <code>off</code> to this output stream.
    * 
    * @param  b    the data.
    * @param  off  the start offset in the data.
    * @param  len  the number of bytes to write.
    *
    * @throws  IOException  if an I/O error occurs.
    */
   public void write(byte[] b, int off, int len) throws IOException {
      for (int i=off; i<off+len; i++)
         write(b[i]);
   }

}
