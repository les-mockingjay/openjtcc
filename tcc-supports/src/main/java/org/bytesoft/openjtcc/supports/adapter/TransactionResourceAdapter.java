package org.bytesoft.openjtcc.supports.adapter;

import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.bytesoft.openjtcc.recovery.RecoveryManager;
import org.bytesoft.openjtcc.supports.adapter.work.TransactionCleanupWork;
import org.bytesoft.openjtcc.supports.adapter.work.TransactionRecoveryWork;
import org.bytesoft.openjtcc.supports.adapter.work.TransactionTimingWork;

public class TransactionResourceAdapter implements ResourceAdapter {
	private static final Logger logger = Logger.getLogger("openjtcc");
	private static final long SECOND_MILLIS = 1000L;
	private static final long MAX_WAIT_MILLIS = SECOND_MILLIS * 30;

	private BootstrapContext bootstrapContext;
	private RecoveryManager recoveryManager;

	private TransactionRecoveryWork recoveryWork;
	private TransactionTimingWork timingWork;
	private TransactionCleanupWork cleanupWork;

	public void endpointActivation(MessageEndpointFactory msgEndpointFactory, ActivationSpec activation)
			throws ResourceException {
	}

	public void endpointDeactivation(MessageEndpointFactory msgEndpointFactory, ActivationSpec activation) {
	}

	public XAResource[] getXAResources(ActivationSpec[] activation) throws ResourceException {
		throw new NotSupportedException();
	}

	public void start(BootstrapContext context) throws ResourceAdapterInternalException {
		this.bootstrapContext = context;
		this.recoveryManager.reconstruct();

		WorkManager workManager = this.bootstrapContext.getWorkManager();
		boolean recoverySuccess = false;
		try {
			WorkListener recoveryListener = (WorkListener) this.recoveryWork;
			workManager.scheduleWork(this.recoveryWork, SECOND_MILLIS, null, recoveryListener);
			recoverySuccess = true;
		} catch (WorkException e) {
			this.recoveryWork.setCompleted(true);
			e.printStackTrace();
		}

		boolean timingSuccess = false;
		try {
			WorkListener timingListener = (WorkListener) this.timingWork;
			workManager.scheduleWork(this.timingWork, SECOND_MILLIS, null, timingListener);
			timingSuccess = true;
		} catch (WorkException e) {
			this.timingWork.setCompleted(true);
			e.printStackTrace();
		}

		boolean cleanupSuccess = false;
		try {
			WorkListener cleanupListener = (WorkListener) this.cleanupWork;
			workManager.scheduleWork(this.cleanupWork, SECOND_MILLIS, null, cleanupListener);
			cleanupSuccess = true;
		} catch (WorkException e) {
			this.cleanupWork.setCompleted(true);
			e.printStackTrace();
		}

		if ((recoverySuccess && timingSuccess && cleanupSuccess) == false) {
			throw new ResourceAdapterInternalException();
		}
		logger.info("[ResourceAdapter] start successful");
	}

	public void stop() {
		logger.info("[ResourceAdapter] stop");
		boolean success = this.processStop();
		if (success) {
			// ignore
		} else {
			throw new RuntimeException();
		}
	}

	private boolean processStop() {
		boolean success = false;
		this.recoveryWork.release();
		this.timingWork.release();
		this.cleanupWork.release();

		long begin = System.currentTimeMillis();
		boolean executing = this.recoveryWork.isCompleted() == false || this.timingWork.isCompleted() == false
				|| this.cleanupWork.isCompleted() == false;
		while (executing) {
			this.sleepMillis(SECOND_MILLIS);
			if (this.recoveryWork.isCompleted() && this.timingWork.isCompleted() && this.cleanupWork.isCompleted()) {
				executing = false;
				success = true;
			} else if ((System.currentTimeMillis() - begin) > MAX_WAIT_MILLIS) {
				executing = false;
			}
		}
		logger.info(String.format("[ResourceAdapter] stop cost: %s", System.currentTimeMillis() - begin));
		return success;
	}

	private void sleepMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public RecoveryManager getRecoveryManager() {
		return recoveryManager;
	}

	public void setRecoveryManager(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}

	public TransactionRecoveryWork getRecoveryWork() {
		return recoveryWork;
	}

	public void setRecoveryWork(TransactionRecoveryWork recoveryWork) {
		this.recoveryWork = recoveryWork;
	}

	public TransactionTimingWork getTimingWork() {
		return timingWork;
	}

	public void setTimingWork(TransactionTimingWork timingWork) {
		this.timingWork = timingWork;
	}

	public TransactionCleanupWork getCleanupWork() {
		return cleanupWork;
	}

	public void setCleanupWork(TransactionCleanupWork cleanupWork) {
		this.cleanupWork = cleanupWork;
	}

}
