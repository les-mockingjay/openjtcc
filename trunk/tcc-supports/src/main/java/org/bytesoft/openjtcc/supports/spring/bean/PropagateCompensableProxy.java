package org.bytesoft.openjtcc.supports.spring.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bytesoft.openjtcc.TransactionManagerImpl;

public class PropagateCompensableProxy<T extends Serializable> extends NativeCompensableProxy<T> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object invokeBizMethod(Method method, Object[] args) throws Throwable {
		PropagateCompensableSynchronization sync = new PropagateCompensableSynchronization(this.facade);
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

	public int hashCode() {
		return (int) this.proxyId;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!PropagateCompensableProxy.class.equals(obj.getClass())) {
			return false;
		}
		PropagateCompensableProxy<T> that = (PropagateCompensableProxy<T>) obj;
		return this.proxyId == that.proxyId;
	}
}
