package org.bytesoft.openjtcc.supports.spring.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.Compensable;
import org.bytesoft.openjtcc.TransactionManagerImpl;
import org.springframework.beans.factory.BeanNameAware;

public class NativeCompensableProxy<T extends Serializable> implements InvocationHandler, BeanNameAware {

	protected TransactionManager transactionManager;
	protected long proxyId;
	protected Compensable<T> facade;
	protected Compensable<T> target;
	protected String beanName;

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) {
			return method.invoke(this.target, args);
		}

		try {
			if (Compensable.class.equals(method.getDeclaringClass())) {
				return method.invoke(this.target, args);
			} else if (BeanNameAware.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			}
		} catch (IllegalAccessException ex) {
			RuntimeException rex = new RuntimeException();
			rex.initCause(ex);
			throw rex;
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}

		return this.invokeBizMethod(method, args);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object invokeBizMethod(Method method, Object[] args) throws Throwable {
		NativeCompensableSynchronization sync = new NativeCompensableSynchronization(this.facade);
		sync.setTransactionManager(this.transactionManager);
		TransactionManagerImpl txManager = (TransactionManagerImpl) this.transactionManager;
		try {
			txManager.registerSynchronization(sync);

			return method.invoke(this.target, args);
		} catch (IllegalAccessException ex) {
			RuntimeException rex = new RuntimeException();
			rex.initCause(ex);
			throw rex;
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		} finally {
			txManager.unRegisterSynchronization(sync);
		}
	}

	public void setFacade(Compensable<T> facade) {
		this.facade = facade;
	}

	public Compensable<T> getTarget() {
		return target;
	}

	public void setTarget(Compensable<T> target) {
		this.target = target;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public long getProxyId() {
		return proxyId;
	}

	public void setProxyId(long proxyId) {
		this.proxyId = proxyId;
	}

	public int hashCode() {
		return (int) this.proxyId;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!NativeCompensableProxy.class.equals(obj.getClass())) {
			return false;
		}
		NativeCompensableProxy<T> that = (NativeCompensableProxy<T>) obj;
		return this.proxyId == that.proxyId;
	}

}
