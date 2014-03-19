package org.bytesoft.openjtcc.supports.serialize;

import java.io.IOException;

public interface ObjectSerializer {
	public byte[] serialize(Object var) throws IOException;

	public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
