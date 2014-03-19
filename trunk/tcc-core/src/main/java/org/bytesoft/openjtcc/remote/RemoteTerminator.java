package org.bytesoft.openjtcc.remote;

import org.bytesoft.openjtcc.common.TerminalKey;

public interface RemoteTerminator extends Prepareable, Committable, Rollbackable, Cleanupable {

	public TerminalKey getTerminalKey();

	public int hashCode();

	public boolean equals(Object obj);

}
