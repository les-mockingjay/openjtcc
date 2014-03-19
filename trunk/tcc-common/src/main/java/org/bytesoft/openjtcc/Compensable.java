package org.bytesoft.openjtcc;

import java.io.Serializable;

public interface Compensable<T extends Serializable> {

	public void confirm(T variable) throws CompensableException;

	public void cancel(T variable) throws CompensableException;
}
