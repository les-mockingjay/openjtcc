package org.bytesoft.openjtcc.resource;

public interface RemoteTerminator extends Prepareable, Committable, Rollbackable, Cleanupable {

	public String getApplication();

	public String getEndpoint();

	public int hashCode();

	public boolean equals(Object obj);
}
