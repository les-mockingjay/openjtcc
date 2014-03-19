package org.bytesoft.openjtcc.supports.serialize;

import java.io.Serializable;

import org.bytesoft.openjtcc.Compensable;

public interface CompensableMarshaller {

	public CompensableInfo marshallCompensable(Compensable<Serializable> compensable);

	public Compensable<Serializable> unmarshallCompensable(CompensableInfo node);

}
