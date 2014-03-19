package org.bytesoft.openjtcc.supports.dubbo.internal;

import java.io.Serializable;

import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationType;
import org.bytesoft.openjtcc.supports.rmi.RemoteInvocationRequest;

public class RemoteInvocationRequestImpl implements RemoteInvocationRequest, Serializable {
	private static final long serialVersionUID = 1L;
	private RemoteInvocationType invocationType;
	private String declaringClass;
	private String interfaceClass;
	private String methodName;
	private String[] parameterTypes;
	private Object[] parameterValues;
	private Object transactionContext;
	private String application;
	private String endpoint;
	private String beanId;

	public String getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(String declaringClass) {
		this.declaringClass = declaringClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object getTransactionContext() {
		return transactionContext;
	}

	public void setTransactionContext(Object transactionContext) {
		this.transactionContext = transactionContext;
	}

	public Object[] getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Object[] parameterValues) {
		this.parameterValues = parameterValues;
	}

	public RemoteInvocationType getInvocationType() {
		return invocationType;
	}

	public void setInvocationType(RemoteInvocationType invocationType) {
		this.invocationType = invocationType;
	}

	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String toString() {
		return String.format("Rmi-Request [%s/%s] bean: %s, method: %s", //
				this.application, this.endpoint, this.beanId, this.methodName);
	}
}
