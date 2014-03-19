package org.bytesoft.openjtcc.supports.schedule;

import org.bytesoft.openjtcc.TransactionImpl;

public interface CleanupProcesser {

	public void registerTransaction(TransactionImpl transaction);

}
