package org.bytesoft.openjtcc.supports;

import java.util.Set;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.xa.XidImpl;

public interface TransactionRepository {

	public void putTransaction(XidImpl globalXid, TransactionImpl transaction);

	public TransactionImpl getTransaction(XidImpl globalXid);

	public TransactionImpl removeTransaction(XidImpl globalXid);

	public void putErrorTransaction(XidImpl globalXid, TransactionImpl transaction);

	public TransactionImpl getErrorTransaction(XidImpl globalXid);

	public TransactionImpl removeErrorTransaction(XidImpl globalXid);

	public TransactionLogger getTransactionLogger();

	public Set<TransactionImpl> getActiveTransactionSet();

	public Set<TransactionImpl> getErrorTransactionSet();

}
