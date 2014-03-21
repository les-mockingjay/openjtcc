/**
 * Copyright 2014 yangming.liu<liuyangming@gmail.com>.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 */
package org.bytesoft.openjtcc.common;

import java.io.Serializable;

import org.bytesoft.openjtcc.xa.XidImpl;

public class TransactionContext implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private transient boolean coordinator;
	private transient boolean recovery;

	private XidImpl globalXid;
	private XidImpl branchXid;
	private transient TerminalKey terminalKey;
	private PropagationKey instanceKey;
	private long createdTime;
	private long expiredTime;
	private boolean compensable;

	public TransactionContext() {
	}

	public void propagateTransactionContext(TransactionContext that) {
		this.branchXid = that.branchXid;
	}

	public void revertTransactionContext(XidImpl branchXid) {
		this.branchXid = branchXid;
	}

	public TransactionContext clone() {
		TransactionContext that = new TransactionContext();
		that.globalXid = this.globalXid;
		that.branchXid = this.branchXid;
		that.instanceKey = this.instanceKey;
		that.createdTime = System.currentTimeMillis();
		that.expiredTime = this.getExpiredTime();
		that.compensable = this.compensable;
		return that;
	}

	public XidImpl getBranchXid() {
		return branchXid;
	}

	public void setBranchXid(XidImpl branchXid) {
		this.branchXid = branchXid;
	}

	public void setGlobalXid(XidImpl globalXid) {
		this.globalXid = globalXid;
	}

	public XidImpl getGlobalXid() {
		return this.globalXid;
	}

	public boolean isCoordinator() {
		return coordinator;
	}

	public void setCoordinator(boolean coordinator) {
		this.coordinator = coordinator;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isCompensable() {
		return compensable;
	}

	public void setCompensable(boolean compensable) {
		this.compensable = compensable;
	}

	public boolean isFresh() {
		return this.recovery == false;
	}

	public boolean isRecovery() {
		return recovery;
	}

	public void setRecovery(boolean recovery) {
		this.recovery = recovery;
	}

	public PropagationKey getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(PropagationKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public TerminalKey getTerminalKey() {
		return terminalKey;
	}

	public void setTerminalKey(TerminalKey terminalKey) {
		this.terminalKey = terminalKey;
	}

}
