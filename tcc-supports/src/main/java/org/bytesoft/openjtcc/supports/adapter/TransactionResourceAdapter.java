package org.bytesoft.openjtcc.supports.adapter;

import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;

import org.bytesoft.openjtcc.recovery.RecoveryManager;
import org.bytesoft.openjtcc.supports.adapter.listener.TransactionCompletionWorkListener;
import org.bytesoft.openjtcc.supports.adapter.listener.TransactionRecoveryWorkListener;
import org.bytesoft.openjtcc.supports.adapter.listener.TransactionTimingWorkListener;

public class TransactionResourceAdapter implements ResourceAdapter {
	private static final Logger logger = Logger.getLogger("lemon-jta");
	private static final long SECOND_MILLIS = 1000L;
	private static final long MAX_WAIT_MILLIS = SECOND_MILLIS * 30;

	private BootstrapContext bootstrapContext;
	private RecoveryManager recoveryManager;

	private boolean timingCompleted;
	private boolean recoveryCompleted;
	private boolean cleanupCompleted;
	private Work recoveryWork;
	private Work timingWork;
	private Work completionWork;

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
			WorkListener recoveryWorkListener = new TransactionRecoveryWorkListener(this);
			workManager.scheduleWork(this.recoveryWork, SECOND_MILLIS, null, recoveryWorkListener);
			recoverySuccess = true;
		} catch (WorkException e) {
			this.recoveryCompleted = true;
			e.printStackTrace();
		}

		boolean timingSuccess = false;
		try {
			WorkListener timingWorkListener = new TransactionTimingWorkListener(this);
			workManager.scheduleWork(this.timingWork, SECOND_MILLIS, null, timingWorkListener);
			timingSuccess = true;
		} catch (WorkException e) {
			this.timingCompleted = true;
			e.printStackTrace();
		}

		boolean cleanupSuccess = false;
		try {
			WorkListener completionWorkListener = new TransactionCompletionWorkListener(this);
			workManager.scheduleWork(this.completionWork, SECOND_MILLIS, null, completionWorkListener);
			cleanupSuccess = true;
		} catch (WorkException e) {
			this.cleanupCompleted = true;
			e.printStackTrace();
		}

		if (!(recoverySuccess && timingSuccess && cleanupSuccess)) {
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
		this.completionWork.release();

		long begin = System.currentTimeMillis();
		boolean executing = !this.recoveryCompleted || !this.timingCompleted || !this.cleanupCompleted;
		while (executing) {
			this.sleepMillis(SECOND_MILLIS);
			if (this.recoveryCompleted && this.timingCompleted && this.cleanupCompleted) {
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

	public boolean isTimingCompleted() {
		return timingCompleted;
	}

	public void setTimingCompleted(boolean timingCompleted) {
		this.timingCompleted = timingCompleted;
	}

	public boolean isRecoveryCompleted() {
		return recoveryCompleted;
	}

	public void setRecoveryCompleted(boolean recoveryCompleted) {
		this.recoveryCompleted = recoveryCompleted;
	}

	public boolean isCleanupCompleted() {
		return cleanupCompleted;
	}

	public void setCleanupCompleted(boolean cleanupCompleted) {
		this.cleanupCompleted = cleanupCompleted;
	}

	public Work getRecoveryWork() {
		return recoveryWork;
	}

	public void setRecoveryWork(Work recoveryWork) {
		this.recoveryWork = recoveryWork;
	}

	public Work getTimingWork() {
		return timingWork;
	}

	public void setTimingWork(Work timingWork) {
		this.timingWork = timingWork;
	}

	public Work getCompletionWork() {
		return completionWork;
	}

	public void setCompletionWork(Work completionWork) {
		this.completionWork = completionWork;
	}

	public void setRecoveryManager(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}

}
