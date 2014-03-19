package org.bytesoft.openjtcc.supports.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.supports.TransactionLogger;
import org.bytesoft.openjtcc.supports.TransactionRepository;
import org.bytesoft.openjtcc.xa.XidImpl;

public class TransactionRepositoryImpl implements TransactionRepository {
	private final Map<XidImpl, TransactionImpl> xidToTxMap = new ConcurrentHashMap<XidImpl, TransactionImpl>();
	private final Map<XidImpl, TransactionImpl> xidToErrTxMap = new ConcurrentHashMap<XidImpl, TransactionImpl>();
	private final TransactionLoggerImpl transactionLoggerWrapper = new TransactionLoggerImpl();
	private TransactionLogger transactionLogger;

	public void putTransaction(XidImpl globalXid, TransactionImpl transaction) {
		this.xidToTxMap.put(globalXid, transaction);
	}

	public TransactionImpl getTransaction(XidImpl globalXid) {
		return this.xidToTxMap.get(globalXid);
	}

	public TransactionImpl removeTransaction(XidImpl globalXid) {
		return this.xidToTxMap.remove(globalXid);
	}

	public TransactionLogger getTransactionLogger() {
		if (this.transactionLogger == null) {
			this.transactionLoggerWrapper.setDelegate(TransactionLogger.defaultTransactionLogger);
		} else {
			this.transactionLoggerWrapper.setDelegate(this.transactionLogger);
		}
		return this.transactionLoggerWrapper;
	}

	public void setTransactionLogger(TransactionLogger transactionLogger) {
		this.transactionLogger = transactionLogger;
	}

	public void putErrorTransaction(XidImpl globalXid, TransactionImpl transaction) {
		this.xidToErrTxMap.put(globalXid, transaction);
	}

	public TransactionImpl getErrorTransaction(XidImpl globalXid) {
		return this.xidToErrTxMap.get(globalXid);
	}

	public TransactionImpl removeErrorTransaction(XidImpl globalXid) {
		return this.xidToErrTxMap.remove(globalXid);
	}

	public Set<TransactionImpl> getErrorTransactionSet() {
		return new HashSet<TransactionImpl>(this.xidToErrTxMap.values());
	}

	public Set<TransactionImpl> getActiveTransactionSet() {
		return new HashSet<TransactionImpl>(this.xidToTxMap.values());
	}

}
