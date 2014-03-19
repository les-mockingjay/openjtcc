package org.bytesoft.openjtcc.remote;

import java.rmi.RemoteException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.SystemException;

public interface Committable {
	public void commit() throws HeuristicMixedException, HeuristicRollbackException, SystemException, RemoteException;
}
