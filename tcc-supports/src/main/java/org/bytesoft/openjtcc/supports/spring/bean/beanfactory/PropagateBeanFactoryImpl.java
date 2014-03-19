package org.bytesoft.openjtcc.supports.spring.bean.beanfactory;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.Compensable;
import org.bytesoft.openjtcc.supports.spring.bean.PropagateCompensableProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class PropagateBeanFactoryImpl implements NativeBeanFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private TransactionManager transactionManager;

	private final AtomicLong atomic = new AtomicLong();

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> interfaceClass, String beanId) {
		Object beanInst = this.applicationContext.getBean(beanId);
		if (interfaceClass.isInstance(beanInst) //
				&& Compensable.class.isInstance(beanInst)//
		) {
			PropagateCompensableProxy<Serializable> proxy = new PropagateCompensableProxy<Serializable>();
			proxy.setBeanName(beanId);
			proxy.setTarget((Compensable<Serializable>) beanInst);
			proxy.setProxyId(atomic.incrementAndGet());
			proxy.setTransactionManager(this.transactionManager);

			ClassLoader classLoader = beanInst.getClass().getClassLoader();
			Class<?>[] interfaces = null;
			if (Compensable.class.equals(interfaceClass)) {
				interfaces = new Class[] { interfaceClass };
			} else {
				interfaces = new Class[] { interfaceClass, Compensable.class };
			}
			Object proxyInst = Proxy.newProxyInstance(classLoader, interfaces, proxy);

			proxy.setFacade((Compensable<Serializable>) proxyInst);

			return interfaceClass.cast(proxyInst);
		}
		throw new RuntimeException();
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
