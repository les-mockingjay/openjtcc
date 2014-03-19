package org.bytesoft.openjtcc.supports.spring.bean.beanfactory;

import java.lang.reflect.Proxy;

import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationInterceptor;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationServiceFactory;
import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteInvocationClientInvoker;

public class RemoteBeanFactoryImpl implements RemoteBeanFactory {

	private String application;
	private TransactionManager transactionManager;
	private RemoteInvocationInterceptor remoteInvocationInterceptor;
	private RemoteInvocationServiceFactory remoteInvocationServiceFactory;

	@Override
	public <T> T getBean(Class<T> interfaceClass, String beanId) {
		RemoteInvocationClientInvoker client = new RemoteInvocationClientInvoker();
		client.setRemoteInvocationServiceFactory(this.remoteInvocationServiceFactory);
		client.setTransactionManager(this.transactionManager);
		client.setRemoteInvocationInterceptor(this.remoteInvocationInterceptor);
		client.setRemoteInterfaceClass(interfaceClass);
		client.setBeanId(beanId);
		client.setApplication(this.application);
		Object proxyInst = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass },
				client);
		return interfaceClass.cast(proxyInst);
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setRemoteInvocationInterceptor(RemoteInvocationInterceptor remoteInvocationInterceptor) {
		this.remoteInvocationInterceptor = remoteInvocationInterceptor;
	}

	public void setRemoteInvocationServiceFactory(RemoteInvocationServiceFactory remoteInvocationServiceFactory) {
		this.remoteInvocationServiceFactory = remoteInvocationServiceFactory;
	}

	public void setApplication(String application) {
		this.application = application;
	}

}
