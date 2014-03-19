package org.bytesoft.openjtcc.supports.spring.bean.beanfactory;

/**
 * 
 * @author yangming.liu
 */
public interface RemoteBeanFactory {
	public <T> T getBean(Class<T> interfaceClass, String beanId);
}
