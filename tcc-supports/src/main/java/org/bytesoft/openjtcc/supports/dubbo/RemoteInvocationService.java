package org.bytesoft.openjtcc.supports.dubbo;

import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteInvocationRequestImpl;
import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteInvocationResponseImpl;

public interface RemoteInvocationService {

	public RemoteInvocationResponseImpl invoke(RemoteInvocationRequestImpl request);

}
