package org.bytesoft.openjtcc.remote;

import java.rmi.RemoteException;

public interface Cleanupable {
	public void cleanup() throws RemoteException;
}
