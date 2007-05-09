// ==============================================================================
// Copyright (c) 2007 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: GZIPOutputStream.java 1 May 8, 2007 steve $
// ==============================================================================
package gnu.inet.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

/**
 * @version $Id: GZIPOutputStream.java 1 May 8, 2007 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2007, All Rights
 *         Reserved
 * 
 */
public class GZIPOutputStream extends java.util.zip.GZIPOutputStream {

	public GZIPOutputStream(OutputStream out) throws IOException {
		this(out, 512);
	}

	public GZIPOutputStream(OutputStream out, int bufferSize)
			throws IOException {
		super(out, bufferSize);
		this.def.setLevel(Deflater.BEST_COMPRESSION);
	}

}
