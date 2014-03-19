package org.bytesoft.openjtcc;

import java.io.Serializable;

public interface CompensableContext<T extends Serializable> {

	public T getCompensableVariable() throws IllegalStateException;

	public void setCompensableVariable(T variable) throws IllegalStateException;

	public void setRollbackOnly() throws IllegalStateException;

	public boolean isRollbackOnly() throws IllegalStateException;

	public String getGlobalTransactionId() throws IllegalStateException;

	public boolean isCoordinator() throws IllegalStateException;

}
