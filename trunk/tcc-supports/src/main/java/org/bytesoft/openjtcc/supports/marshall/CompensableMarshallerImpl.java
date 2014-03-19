package org.bytesoft.openjtcc.supports.marshall;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.bytesoft.openjtcc.Compensable;
import org.bytesoft.openjtcc.supports.serialize.CompensableInfo;
import org.bytesoft.openjtcc.supports.serialize.CompensableMarshaller;
import org.bytesoft.openjtcc.supports.spring.bean.NativeCompensableProxy;
import org.bytesoft.openjtcc.supports.spring.bean.beanfactory.NativeBeanFactory;

public class CompensableMarshallerImpl implements CompensableMarshaller/* , ApplicationContextAware */{
	private NativeBeanFactory beanFactory;

	@SuppressWarnings("unchecked")
	public CompensableInfo marshallCompensable(Compensable<Serializable> compensable) {
		if (Proxy.isProxyClass(compensable.getClass())) {
			NativeCompensableProxy<Serializable> handler = (NativeCompensableProxy<Serializable>) Proxy
					.getInvocationHandler(compensable);
			try {
				CompensableInfo info = new CompensableInfo();
				info.setIdentifier(handler.getBeanName());
				return info;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Compensable<Serializable> unmarshallCompensable(CompensableInfo info) {
		Serializable identifier = info.getIdentifier();
		if (info != null && String.class.isInstance(identifier)) {
			String beanName = (String) identifier;
			Compensable<Serializable> service = this.beanFactory.getBean(Compensable.class, beanName);
			return service;
		}
		return null;
	}

	public void setBeanFactory(NativeBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
