package org.bytesoft.openjtcc.archive;

import java.io.Serializable;

import org.bytesoft.openjtcc.Compensable;
import org.bytesoft.openjtcc.xa.XidImpl;
import org.bytesoft.utils.CommonUtils;

public class CompensableArchive {
	public boolean launchSvc;
	public XidImpl branchXid;
	public Compensable<Serializable> service;
	public Serializable variable;
	public boolean tryCommitted;
	public boolean confirmed;
	public boolean cancelled;
	public boolean committed;
	public boolean rolledback;

	public int hashCode() {
		int hash = 23;
		hash += 29 * (this.branchXid == null ? 0 : this.branchXid.hashCode());
		return hash;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		CompensableArchive that = (CompensableArchive) obj;
		return CommonUtils.equals(this.branchXid, that.branchXid);
	}
}
