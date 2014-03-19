package org.bytesoft.openjtcc.supports.dubbo;

import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteInvocationRequestImpl;
import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteInvocationResponseImpl;

public interface RemoteInvocationService {

	public String getApplication();

	public RemoteInvocationResponseImpl invoke(RemoteInvocationRequestImpl request);

}
