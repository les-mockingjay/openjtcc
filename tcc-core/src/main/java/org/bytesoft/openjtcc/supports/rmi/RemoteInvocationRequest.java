package org.bytesoft.openjtcc.supports.rmi;

public interface RemoteInvocationRequest {

	public Object getTransactionContext();

	public void setTransactionContext(Object transactionContext);

}
