package org.bytesoft.openjtcc.resource;

import java.rmi.RemoteException;

public interface Cleanupable {
	public void cleanup() throws RemoteException;
}
