package org.bytesoft.openjtcc.supports.internal;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.supports.TransactionStatistic;

public class TransactionStatisticImpl implements TransactionStatistic {

	public void fireBeginTransaction(TransactionImpl transaction) {
	}

	public void firePreparingTransaction(TransactionImpl transaction) {
	}

	public void firePreparedTransaction(TransactionImpl transaction) {
	}

	public void fireCommittingTransaction(TransactionImpl transaction) {
	}

	public void fireCommittedTransaction(TransactionImpl transaction) {
	}

	public void fireRollingBackTransaction(TransactionImpl transaction) {
	}

	public void fireRolledbackTransaction(TransactionImpl transaction) {
	}

	public void fireCompleteFailure(TransactionImpl transaction) {
	}

	public void fireCleanupTransaction(TransactionImpl transaction) {
	}

	public void fireRecoverTransaction(TransactionImpl transaction) {
	}

}
