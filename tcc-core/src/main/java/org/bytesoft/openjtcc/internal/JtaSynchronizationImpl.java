package org.bytesoft.openjtcc.internal;

import javax.transaction.Synchronization;

import org.bytesoft.openjtcc.supports.AbstractSynchronization;
import org.bytesoft.openjtcc.xa.XidImpl;

public class JtaSynchronizationImpl extends AbstractSynchronization {
	public Synchronization synchronization;

	public JtaSynchronizationImpl(XidImpl globalXid, Synchronization sync) {
		super(globalXid);
		this.synchronization = sync;
	}

	public void afterCreation(XidImpl xid) {
		// ignore
	}

	public void beforeCompletion(XidImpl xid) {
		if (this.synchronization != null) {
			this.synchronization.beforeCompletion();
		}
	}

	public void afterCompletion(XidImpl xid, int status) {
		if (this.synchronization != null) {
			this.synchronization.afterCompletion(status);
		}
	}
}
