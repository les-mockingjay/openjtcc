package org.bytesoft.openjtcc.resource;

import java.rmi.RemoteException;

import javax.transaction.SystemException;

import org.bytesoft.openjtcc.NoSuchTransactionException;

public interface Prepareable {

	public void prepare() throws SystemException, RemoteException, NoSuchTransactionException;

}
