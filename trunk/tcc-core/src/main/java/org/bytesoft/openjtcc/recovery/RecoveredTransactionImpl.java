package org.bytesoft.openjtcc.recovery;

import org.bytesoft.openjtcc.TransactionImpl;

public class RecoveredTransactionImpl extends TransactionImpl {

	private boolean recoveryRollbackOnly;

	public boolean isRecoveryRollbackOnly() {
		return recoveryRollbackOnly;
	}

	public void setRecoveryRollbackOnly(boolean recoveryRollbackOnly) {
		this.recoveryRollbackOnly = recoveryRollbackOnly;
	}

}
