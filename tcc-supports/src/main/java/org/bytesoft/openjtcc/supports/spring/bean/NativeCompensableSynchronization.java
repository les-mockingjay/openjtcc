package org.bytesoft.openjtcc.supports.spring.bean;

import java.io.Serializable;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.Compensable;
import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.TransactionManagerImpl;
import org.bytesoft.openjtcc.supports.AbstractSynchronization;
import org.bytesoft.openjtcc.xa.XidImpl;

public class NativeCompensableSynchronization<T extends Serializable> extends AbstractSynchronization {
	private TransactionManager transactionManager;
	private Compensable<T> compensable;

	public NativeCompensableSynchronization(Compensable<T> service) {
		this.compensable = service;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterCreation(XidImpl xid) {

		try {
			TransactionManagerImpl txManager = (TransactionManagerImpl) this.transactionManager;
			TransactionImpl transaction = txManager.getCurrentTransaction();

			transaction.enlistService((Compensable<Serializable>) this.compensable);
			transaction.registerSynchronization(this);
		} catch (IllegalStateException ex) {
		} catch (SystemException ex) {
		} catch (RollbackException ex) {
		} catch (RuntimeException ex) {
		}
	}

	@Override
	public void beforeCompletion(XidImpl xid) {
	}

	@Override
	public void afterCompletion(XidImpl xid, int status) {
		// if (created) {
		// this.compensable.setServiceContext(null);
		// }
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
