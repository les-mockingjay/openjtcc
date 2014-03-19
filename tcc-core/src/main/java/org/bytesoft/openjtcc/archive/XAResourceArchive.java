package org.bytesoft.openjtcc.archive;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class XAResourceArchive {
	public XAResource xaRes;
	public int vote;
	public Xid xid;
	public boolean completed;
	public boolean committed;
	public boolean rolledback;

	public int hashCode() {
		return this.xaRes == null ? 31 : this.xaRes.hashCode();
	}

	public boolean equals(Object obj) {
		if (XAResourceArchive.class.isInstance(obj) == false) {
			return false;
		}
		XAResourceArchive that = (XAResourceArchive) obj;
		if (this.xaRes == that.xaRes) {
			return true;
		} else if (this.xaRes == null || that.xaRes == null) {
			return false;
		} else {
			return this.xaRes.equals(that.xaRes);
		}
	}
}
