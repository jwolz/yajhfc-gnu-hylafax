// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id$
// ==============================================================================
package gnu.hylafax.status;

/**
 * @version $Id$
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 *
 */
public interface StatusEventSource {

    public void addStatusEventListener(StatusEventListener listener);

    public void addStatusEventListener(StatusEventListener listener, int type);

    public void addStatusEventListener(StatusEventListener listener, int type, int events);

    public void addStatusEventListener(StatusEventListener listener, int type, int events, String id);

    public void removeStatusEventListener(StatusEventListener listener);

}
