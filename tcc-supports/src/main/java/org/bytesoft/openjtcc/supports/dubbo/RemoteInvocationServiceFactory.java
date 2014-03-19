package org.bytesoft.openjtcc.supports.dubbo;

import org.bytesoft.openjtcc.common.TerminalKey;

public interface RemoteInvocationServiceFactory {

	public RemoteInvocationService getRemoteInvocationService(TerminalKey terminalKey);

	public RemoteInvocationService getRemoteInvocationService();

}
