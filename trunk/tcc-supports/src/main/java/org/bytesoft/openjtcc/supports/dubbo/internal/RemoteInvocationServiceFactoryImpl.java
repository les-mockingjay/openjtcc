package org.bytesoft.openjtcc.supports.dubbo.internal;

import java.lang.reflect.Proxy;

import org.bytesoft.openjtcc.common.TerminalKey;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RemoteInvocationServiceFactoryImpl implements RemoteInvocationServiceFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public RemoteInvocationService getRemoteInvocationService(TerminalKey terminalKey) {
		RemoteInvocationEndpointSelector handler = new RemoteInvocationEndpointSelector();
		handler.setTerminalKey(terminalKey);
		handler.setApplicationContext(this.applicationContext);
		return (RemoteInvocationService) Proxy.newProxyInstance(RemoteInvocationService.class.getClassLoader(),
				new Class[] { RemoteInvocationService.class }, handler);
	}

	@Override
	public RemoteInvocationService getRemoteInvocationService() {
		RemoteInvocationEndpointSelector handler = new RemoteInvocationEndpointSelector();
		handler.setApplicationContext(this.applicationContext);
		return (RemoteInvocationService) Proxy.newProxyInstance(RemoteInvocationService.class.getClassLoader(),
				new Class[] { RemoteInvocationService.class }, handler);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
