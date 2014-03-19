package org.bytesoft.openjtcc.supports.internal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.TransactionManagerImpl;
import org.bytesoft.openjtcc.common.TerminalKey;
import org.bytesoft.openjtcc.common.TransactionContext;
import org.bytesoft.openjtcc.remote.RemoteTerminator;
import org.bytesoft.openjtcc.supports.rmi.RemoteInvocationInterceptor;
import org.bytesoft.openjtcc.supports.rmi.RemoteInvocationRequest;
import org.bytesoft.openjtcc.supports.rmi.RemoteInvocationResponse;
import org.bytesoft.openjtcc.supports.serialize.TerminatorInfo;
import org.bytesoft.openjtcc.supports.serialize.TerminatorMarshaller;
import org.bytesoft.openjtcc.xa.XidFactory;
import org.bytesoft.openjtcc.xa.XidImpl;
import org.bytesoft.utils.CommonUtils;

public class RemoteInvocationInterceptorImpl implements RemoteInvocationInterceptor {
	private static final Logger logger = Logger.getLogger("openjtcc");

	private XidFactory xidFactory;
	private TerminatorMarshaller terminatorMarshaller;
	private TransactionManagerImpl transactionManager;
	private Map<AssociateKey, XidImpl> branchXidMap = new ConcurrentHashMap<AssociateKey, XidImpl>();

	public void beforeSendRequest(RemoteInvocationRequest request) throws IllegalStateException {
		TransactionImpl transaction = this.getCurrentTransaction();
		if (transaction != null) {
			TransactionContext transactionContext = transaction.getTransactionContext();
			XidImpl branchXid = this.xidFactory.createBranchXid(transactionContext.getGlobalXid());
			TransactionContext propagationContext = transactionContext.clone();
			propagationContext.setBranchXid(branchXid);
			propagationContext.setInstanceKey(this.transactionManager.getInstanceKey());

			request.setTransactionContext(propagationContext);

			logger.info(String.format("[%15s] method: %s", "before-send-req", request));
		}
	}

	public void beforeSendResponse(RemoteInvocationResponse response) throws IllegalStateException {
		TransactionImpl transaction = this.getCurrentTransaction();
		if (transaction != null) {
			TransactionContext transactionContext = transaction.getTransactionContext();
			TransactionContext propagationContext = transactionContext.clone();
			response.setTransactionContext(propagationContext);

			AssociateKey key = new AssociateKey();
			key.global = transactionContext.getGlobalXid();
			key.thread = Thread.currentThread();

			XidImpl branchXid = this.branchXidMap.remove(key);
			if (branchXid != null) {
				transactionContext.revertTransactionContext(branchXid);
			}

			logger.info(String.format("[%15s] method: %s", "before-send-res", response));
		}
	}

	public void afterReceiveRequest(RemoteInvocationRequest request) throws IllegalStateException {
		TransactionContext propagationContext = (TransactionContext) request.getTransactionContext();
		if (propagationContext != null) {
			logger.info(String.format("[%15s] method: %s", "after-recv-req", request));

			AssociateKey key = new AssociateKey();
			key.global = propagationContext.getGlobalXid();
			key.thread = Thread.currentThread();

			TransactionImpl transaction = this.transactionManager.getCurrentTransaction();
			if (transaction != null) {
				TransactionContext transactionContext = transaction.getTransactionContext();
				this.branchXidMap.put(key, transactionContext.getBranchXid());
			}

			try {
				transaction = this.transactionManager.begin(propagationContext);
				TransactionContext transactionContext = transaction.getTransactionContext();
				transactionContext.propagateTransactionContext(propagationContext);
			} catch (NotSupportedException ex) {
				throw new IllegalStateException(ex);
			} catch (SystemException ex) {
				throw new IllegalStateException(ex);
			} catch (RuntimeException ex) {
				throw new IllegalStateException(ex);
			}

		}
	}

	public void afterReceiveResponse(RemoteInvocationResponse response) throws IllegalStateException {
		TransactionContext propagationContext = (TransactionContext) response.getTransactionContext();
		if (propagationContext != null && propagationContext.isCompensable()) {
			logger.info(String.format("[%15s] method: %s", "after-recv-res", response));
			TransactionImpl transaction = this.getCurrentTransaction();
			TerminalKey instanceKey = this.transactionManager.getInstanceKey();
			TerminalKey terminalKey = propagationContext.getInstanceKey();
			if (instanceKey.equals(terminalKey)) {
				RemoteTerminator terminator;
				try {
					XidImpl branchXid = propagationContext.getBranchXid();

					TerminatorInfo terminatorInfo = new TerminatorInfo();
					terminatorInfo.setApplication(terminalKey.getApplication());
					terminatorInfo.setEndpoint(terminalKey.getEndpoint());
					terminatorInfo.setBranchXid(branchXid);

					terminator = this.terminatorMarshaller.unmarshallTerminator(terminatorInfo);
					transaction.registerTerminator(terminator);
				} catch (IOException ex) {
					throw new IllegalStateException(ex);
				} catch (SystemException ex) {
					throw new IllegalStateException(ex);
				} catch (RuntimeException ex) {
					throw new IllegalStateException(ex);
				}

			}
		}
	}

	public TransactionImpl getCurrentTransaction() {
		if (this.transactionManager == null) {
			return null;
		} else {
			return this.transactionManager.getCurrentTransaction();
		}
	}

	public void setTransactionManager(TransactionManagerImpl transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setTerminatorMarshaller(TerminatorMarshaller terminatorMarshaller) {
		this.terminatorMarshaller = terminatorMarshaller;
	}

	public void setXidFactory(XidFactory xidFactory) {
		this.xidFactory = xidFactory;
	}

	public static class AssociateKey {
		public XidImpl global;
		public Thread thread;

		@Override
		public int hashCode() {
			int hash = 7;
			hash += 11 * (this.global == null ? 0 : this.global.hashCode());
			hash += 13 * (this.thread == null ? 0 : this.thread.hashCode());
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else if (!AssociateKey.class.isInstance(obj)) {
				return false;
			}
			AssociateKey that = (AssociateKey) obj;
			boolean globalEquals = CommonUtils.equals(this.global, that.global);
			boolean threadEquals = CommonUtils.equals(this.thread, that.thread);
			return globalEquals && threadEquals;
		}

	}
}
