package org.bytesoft.openjtcc.supports.dubbo.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import javax.transaction.HeuristicRollbackException;
import javax.transaction.TransactionManager;

import org.bytesoft.openjtcc.NoSuchTransactionException;
import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.TransactionManagerImpl;
import org.bytesoft.openjtcc.common.TerminalKey;
import org.bytesoft.openjtcc.common.TransactionContext;
import org.bytesoft.openjtcc.remote.Cleanupable;
import org.bytesoft.openjtcc.remote.Committable;
import org.bytesoft.openjtcc.remote.Prepareable;
import org.bytesoft.openjtcc.remote.RemoteTerminator;
import org.bytesoft.openjtcc.remote.Rollbackable;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationInterceptor;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService;
import org.bytesoft.openjtcc.supports.dubbo.common.RemoteInvocationType;
import org.bytesoft.openjtcc.supports.spring.bean.beanfactory.NativeBeanFactory;

public class RemoteInvocationServerInvoker implements RemoteInvocationService {
	private NativeBeanFactory beanFactory;
	private TransactionManager transactionManager;
	private RemoteInvocationInterceptor remoteInvocationInterceptor;

	public RemoteInvocationResponseImpl invoke(RemoteInvocationRequestImpl request) {
		RemoteInvocationResponseImpl response = this.validateRequest(request);
		TerminalKey instanceKey = this.getTerminalKey();
		response.setApplication(instanceKey.getApplication());
		response.setEndpoint(instanceKey.getEndpoint());

		try {
			if (RemoteInvocationType.service.equals(request.getInvocationType())) {
				try {
					this.afterReceiveRequest(request);

					return this.handleServiceRequest(request, response);
				} finally {
					try {
						this.beforeSendResponse(response);
					} catch (RuntimeException rex) {
						if (response.isFailure()) {
							// ignore
						} else {
							response.setThrowable(rex);
						}
					}
				}
			} else if (RemoteInvocationType.transaction.equals(request.getInvocationType())) {
				return this.handleTransactionRequest(request, response);
			} else if (RemoteInvocationType.cleanup.equals(request.getInvocationType())) {
				return this.handleCleanupRequest(request, response);
			}
		} catch (Throwable thrown) {
			response.setThrowable(thrown);
			return response;
		}
		response.setThrowable(new IllegalArgumentException());
		return response;
	}

	private RemoteInvocationResponseImpl validateRequest(RemoteInvocationRequestImpl request) {
		RemoteInvocationResponseImpl response = new RemoteInvocationResponseImpl();
		response.setRequest(request);
		if (request == null) {
			response.setInvocationType(RemoteInvocationType.service);
			response.setThrowable(new NullPointerException("request cannot be null!"));
		} else {
			response.setInvocationType(request.getInvocationType());
			if (request.getDeclaringClass() == null) {
				response.setThrowable(new NullPointerException("declaring class cannot be null!"));
			} else if (request.getMethodName() == null) {
				response.setThrowable(new NullPointerException("method name cannot be null!"));
			}
		}
		return response;
	}

	private RemoteInvocationResponseImpl handleCleanupRequest(RemoteInvocationRequestImpl request,
			RemoteInvocationResponseImpl response) {
		try {
			TransactionContext context = (TransactionContext) request.getTransactionContext();
			TransactionManagerImpl txm = (TransactionManagerImpl) this.transactionManager;
			TransactionImpl transaction = txm.getTransaction(context.getGlobalXid());
			if (transaction == null) {
				// ignore
			} else {
				RemoteTerminator terminator = transaction.getTerminatorSkeleton();
				terminator.cleanup();
			}
		} catch (RemoteException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (RuntimeException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		}
		return response;
	}

	private RemoteInvocationResponseImpl handleTransactionRequest(RemoteInvocationRequestImpl request,
			RemoteInvocationResponseImpl response) {
		String className = request.getDeclaringClass();
		String methodName = request.getMethodName();
		String[] types = request.getParameterTypes() == null ? new String[0] : request.getParameterTypes();

		Class<?> clazz = null;
		Class<?>[] parameterTypes = new Class<?>[types.length];
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			for (int i = 0; i < types.length; i++) {
				parameterTypes[i] = loadClass(types[i]);
			}
		} catch (ClassNotFoundException ex) {
			response.setThrowable(ex);
			return response;
		} catch (Throwable ex) {
			response.setThrowable(ex);
			return response;
		}

		TransactionManagerImpl txManager = (TransactionManagerImpl) this.transactionManager;
		try {
			TransactionContext context = (TransactionContext) request.getTransactionContext();
			TransactionImpl transaction = txManager.getTransaction(context.getGlobalXid());

			if (transaction == null) {
				if (Prepareable.class.equals(clazz)) {
					response.setThrowable(new NoSuchTransactionException());
					return response;
				} else if (Committable.class.equals(clazz)) {
					response.setThrowable(new HeuristicRollbackException());
					return response;
				} else if (Rollbackable.class.equals(clazz)) {
					return response;
				} else if (Cleanupable.class.equals(clazz)) {
					return response;
				} else {
					throw new IllegalStateException();
				}
			}

			txManager.associateTransaction(transaction);

			Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
			Object result = method.invoke(transaction.getTerminatorSkeleton(), request.getParameterValues());
			response.setResult(result);

		} catch (NoSuchMethodException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (SecurityException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			response.setThrowable(ex.getTargetException());
			ex.printStackTrace();
		} catch (Throwable ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} finally {
			txManager.unassociateTransaction();
		}
		return response;
	}

	private RemoteInvocationResponseImpl handleServiceRequest(RemoteInvocationRequestImpl request,
			RemoteInvocationResponseImpl response) {
		String className = request.getDeclaringClass();
		String methodName = request.getMethodName();
		String[] types = request.getParameterTypes() == null ? new String[0] : request.getParameterTypes();

		Class<?> clazz = null;
		Class<?>[] parameterTypes = new Class<?>[types.length];
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			for (int i = 0; i < types.length; i++) {
				parameterTypes[i] = loadClass(types[i]);
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			response.setThrowable(ex);
			return response;
		} catch (Throwable ex) {
			ex.printStackTrace();
			response.setThrowable(ex);
			return response;
		}

		try {
			Object service = this.beanFactory.getBean(clazz, request.getBeanId());
			Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
			Object result = method.invoke(service, request.getParameterValues());
			response.setResult(result);
		} catch (NoSuchMethodException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (SecurityException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			response.setThrowable(ex.getTargetException());
			ex.printStackTrace();
		} catch (Throwable ex) {
			response.setThrowable(ex);
			ex.printStackTrace();
		}

		return response;
	}

	private void afterReceiveRequest(RemoteInvocationRequestImpl request) {
		if (this.remoteInvocationInterceptor != null) {
			this.remoteInvocationInterceptor.afterReceiveRequest(request);
		}
	}

	private void beforeSendResponse(RemoteInvocationResponseImpl response) {
		if (this.remoteInvocationInterceptor != null) {
			this.remoteInvocationInterceptor.beforeSendResponse(response);
		}
	}

	private Class<?> loadClass(String clsName) throws ClassNotFoundException {
		if (Byte.TYPE.getName().equals(clsName)) {
			return Byte.TYPE;
		} else if (Boolean.TYPE.getName().equals(clsName)) {
			return Boolean.TYPE;
		} else if (Short.TYPE.getName().equals(clsName)) {
			return Short.TYPE;
		} else if (Character.TYPE.getName().equals(clsName)) {
			return Character.TYPE;
		} else if (Integer.TYPE.getName().equals(clsName)) {
			return Integer.TYPE;
		} else if (Float.TYPE.getName().equals(clsName)) {
			return Float.TYPE;
		} else if (Long.TYPE.getName().equals(clsName)) {
			return Long.TYPE;
		} else if (Double.TYPE.getName().equals(clsName)) {
			return Double.TYPE;
		}
		return Thread.currentThread().getContextClassLoader().loadClass(clsName);
	}

	public void setRemoteInvocationInterceptor(RemoteInvocationInterceptor remoteInvocationInterceptor) {
		this.remoteInvocationInterceptor = remoteInvocationInterceptor;
	}

	public void setBeanFactory(NativeBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public TerminalKey getTerminalKey() {
		TransactionManagerImpl txm = (TransactionManagerImpl) this.transactionManager;
		return txm.getInstanceKey();
	}

	public String getApplication() {
		throw new IllegalStateException();
	}

}
