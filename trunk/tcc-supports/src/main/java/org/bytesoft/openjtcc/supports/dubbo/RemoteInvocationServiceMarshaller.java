package org.bytesoft.openjtcc.supports.dubbo;

import org.bytesoft.openjtcc.supports.serialize.TerminatorInfo;

public interface RemoteInvocationServiceMarshaller {

	public RemoteInvocationService unmarshallRemoteInvocationService(TerminatorInfo terminatorInfo);

}
