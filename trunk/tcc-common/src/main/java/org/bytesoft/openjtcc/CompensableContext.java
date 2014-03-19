package org.bytesoft.openjtcc;

import java.io.Serializable;

/**
 * @author yangming.liu
 */
public interface CompensableContext<T extends Serializable> {
	public T getCompensableVariable();

	public void setCompensableVariable(T variable) throws IllegalStateException;

	public void setRollbackOnly() throws IllegalStateException;

	public boolean isRollbackOnly();

	public String getGlobalTransactionId();

	public boolean isGlobalCoordinator();
}
