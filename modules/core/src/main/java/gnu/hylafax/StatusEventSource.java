// ==============================================================================
// Copyright (c) 2008 Steven Jardine, MJN Services, Inc., All Rights Reserved.
// $Id: StatusEventSource.java 1 Feb 18, 2008 steve $
// ==============================================================================
package gnu.hylafax;

import java.util.List;

/**
 * @version $Id: StatusEventSource.java 1 Feb 18, 2008 steve $
 * @author Steven Jardine, MJN Services, Inc., Copyright(c) 2008, All Rights Reserved
 *
 */
public interface StatusEventSource {

    public void addStatusEventListener(StatusEventListener listener);

    public void addStatusEventListeners(List listeners);

    public void removeStatusEventListener(StatusEventListener listener);

}
