package org.bytesoft.openjtcc.supports.spring.bean;

import java.io.Serializable;

import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.CompensableContext;
import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.TransactionManagerImpl;

public class CompensableContextProxy<T extends Serializable> implements CompensableContext<T> {
	private TransactionManager transactionManager;

	public String getGlobalTransactionId() {
		CompensableContext<T> context = this.getCompensableContext();
		return context.getGlobalTransactionId();
	}

	public T getCompensableVariable() {
		CompensableContext<T> context = this.getCompensableContext();
		return context.getCompensableVariable();
	}

	public boolean isCoordinator() {
		CompensableContext<T> context = this.getCompensableContext();
		return context.isCoordinator();
	}

	public boolean isRollbackOnly() {
		CompensableContext<T> context = this.getCompensableContext();
		return context.isRollbackOnly();
	}

	public void setRollbackOnly() throws IllegalStateException {
		CompensableContext<T> context = this.getCompensableContext();
		context.setRollbackOnly();
	}

	public void setCompensableVariable(T arg0) throws IllegalStateException {
		TransactionImpl transaction = this.getTransaction();
		if (transaction == null) {
			throw new IllegalStateException();
		}

		CompensableContext<T> context = this.getCompensableContext();
		context.setCompensableVariable(arg0);
	}

	@SuppressWarnings("unchecked")
	public CompensableContext<T> getCompensableContext() throws IllegalStateException {
		TransactionManagerImpl txManager = (TransactionManagerImpl) this.transactionManager;
		TransactionImpl transaction = txManager.getCurrentTransaction();
		if (transaction == null) {
			throw new IllegalStateException();
		} else {
			return (CompensableContext<T>) transaction.getCompensableContext();
		}
	}

	protected TransactionImpl getTransaction() {
		try {
			TransactionManagerImpl txManager = (TransactionManagerImpl) this.transactionManager;
			TransactionImpl transaction = txManager.getCurrentTransaction();
			return transaction;
		} catch (RuntimeException ex) {
			return null;
		}
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
