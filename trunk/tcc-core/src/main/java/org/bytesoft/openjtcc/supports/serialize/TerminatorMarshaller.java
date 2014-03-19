package org.bytesoft.openjtcc.supports.serialize;

import java.io.IOException;

import org.bytesoft.openjtcc.remote.RemoteTerminator;

public interface TerminatorMarshaller {

	public TerminatorInfo marshallTerminator(RemoteTerminator terminator) throws IOException;

	public RemoteTerminator unmarshallTerminator(TerminatorInfo info) throws IOException;

}
