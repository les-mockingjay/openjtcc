package org.bytesoft.openjtcc.internal;

import java.io.Serializable;

import org.bytesoft.openjtcc.CompensableContext;
import org.bytesoft.openjtcc.common.TransactionContext;
import org.bytesoft.openjtcc.common.TransactionStatus;
import org.bytesoft.openjtcc.xa.XidImpl;
import org.bytesoft.utils.ByteUtils;

public class CompensableContextImpl<T extends Serializable> implements CompensableContext<T> {
	private TransactionContext transactionContext;
	private TransactionStatus transactionStatus;
	private XidImpl branchXid;
	private T compensableVariable;

	public String getGlobalTransactionId() {
		return ByteUtils.byteArrayToString(this.branchXid.getGlobalTransactionId());
	}

	public T getCompensableVariable() {
		return this.compensableVariable;
	}

	public boolean isGlobalCoordinator() {
		if (transactionContext.isCoordinator()) {
			XidImpl globalXid = this.transactionContext.getGlobalXid();
			XidImpl branchXid = this.transactionContext.getBranchXid();
			if (globalXid.equals(branchXid)) {
				// TODO
				return true;
			}
		}
		return false;
	}

	public boolean isRollbackOnly() {
		return transactionStatus.isMarkedRollbackOnly();
	}

	public void setRollbackOnly() throws IllegalStateException {
		this.transactionStatus.markStatusRollback();
	}

	public void setCompensableVariable(T variable) throws IllegalStateException {
		this.compensableVariable = variable;
	}

	public TransactionContext getTransactionContext() {
		return transactionContext;
	}

	public void setTransactionContext(TransactionContext transactionContext) {
		this.transactionContext = transactionContext;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public XidImpl getBranchXid() {
		return branchXid;
	}

	public void setBranchXid(XidImpl branchXid) {
		this.branchXid = branchXid;
	}

}
