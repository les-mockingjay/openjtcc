package org.bytesoft.openjtcc.common;

import java.io.Serializable;
import java.util.Arrays;

public class PropagationKey implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private byte[] instanceKey;

	public PropagationKey(byte[] bytes) {
		this.instanceKey = bytes;
	}

	public byte[] getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(byte[] instanceKey) {
		this.instanceKey = instanceKey;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.instanceKey);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (PropagationKey.class.isInstance(obj) == false) {
			return false;
		}
		PropagationKey that = (PropagationKey) obj;
		return Arrays.equals(this.instanceKey, that.instanceKey);
	}
}
