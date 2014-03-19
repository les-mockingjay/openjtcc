package org.bytesoft.openjtcc.supports;

import javax.transaction.Synchronization;

import org.bytesoft.openjtcc.xa.XidImpl;

public abstract class AbstractSynchronization implements Synchronization {

	private XidImpl xid;
	private boolean beforeCompletionRequired;
	private boolean afterCompletionRequired;

	public AbstractSynchronization() {
		this(null);
	}

	public AbstractSynchronization(XidImpl xid) {
		this.xid = xid;
		this.beforeCompletionRequired = true;
		this.afterCompletionRequired = true;
	}

	public final void beforeCompletion() {
		if (this.beforeCompletionRequired) {
			this.beforeCompletionRequired = false;
			this.beforeCompletion(this.xid);
		}
	}

	public final void afterCompletion(int status) {
		if (this.afterCompletionRequired) {
			this.afterCompletionRequired = false;
			this.afterCompletion(this.xid, status);
		}
	}

	public abstract void afterCreation(XidImpl xid);

	public abstract void beforeCompletion(XidImpl xid);

	public abstract void afterCompletion(XidImpl xid, int status);

	public void setXid(XidImpl xid) {
		this.xid = xid;
	}

}
