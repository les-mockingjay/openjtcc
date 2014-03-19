package org.bytesoft.openjtcc.supports.spring.bean.beanfactory;

public interface NativeBeanFactory {

	public <T> T getBean(Class<T> interfaceClass, String beanId);

}
