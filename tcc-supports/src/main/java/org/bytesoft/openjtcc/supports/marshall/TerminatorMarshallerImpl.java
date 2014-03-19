package org.bytesoft.openjtcc.supports.marshall;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.bytesoft.openjtcc.remote.RemoteTerminator;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService;
import org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationServiceMarshaller;
import org.bytesoft.openjtcc.supports.dubbo.internal.RemoteTerminatorHandler;
import org.bytesoft.openjtcc.supports.serialize.TerminatorInfo;
import org.bytesoft.openjtcc.supports.serialize.TerminatorMarshaller;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TerminatorMarshallerImpl implements TerminatorMarshaller, RemoteInvocationServiceMarshaller,
		ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public TerminatorInfo marshallTerminator(RemoteTerminator terminator) throws IOException {
		if (Proxy.isProxyClass(terminator.getClass())) {
			InvocationHandler obj = Proxy.getInvocationHandler(terminator);
			if (RemoteTerminatorHandler.class.isInstance(obj)) {
				RemoteTerminatorHandler handler = (RemoteTerminatorHandler) obj;
				return handler.getRemoteTerminatorInfo();
			}
		}
		return null;
	}

	// @Override
	// public RemoteTerminator unmarshallTerminator(RemoteInvocationResponse res) throws IOException {
	// if (RemoteInvocationResponseImpl.class.isInstance(res)) {
	// RemoteInvocationResponseImpl response = (RemoteInvocationResponseImpl) res;
	//
	// TransactionContext transactionContext = (TransactionContext) response.getTransactionContext();
	// XidImpl branchXid = transactionContext.getBranchXid();
	//
	// TerminatorInfo info = new TerminatorInfo();
	// info.setApplication(response.getApplication());
	// info.setEndpoint(response.getEndpoint());
	// info.setBranchXid(branchXid);
	//
	// return this.unmarshallTerminator(info);
	// }
	// return null;
	// }

	@Override
	public RemoteTerminator unmarshallTerminator(TerminatorInfo info) throws IOException {
		RemoteTerminatorHandler handler = new RemoteTerminatorHandler();
		handler.setRemoteTerminatorInfo(info);
		String beanName = this.getBeanName(info);
		RemoteInvocationService remoteService = null;
		try {
			remoteService = (RemoteInvocationService) this.applicationContext.getBean(beanName);
			handler.setRemoteInvocationService(remoteService);
		} catch (BeansException ex) {
			handler.setRemoteServiceFactory(this);
		}
		return (RemoteTerminator) Proxy.newProxyInstance(RemoteTerminator.class.getClassLoader(),
				new Class[] { RemoteTerminator.class }, handler);
	}

	public RemoteInvocationService unmarshallRemoteInvocationService(TerminatorInfo terminatorInfo) {
		String beanName = this.getBeanName(terminatorInfo);
		RemoteInvocationService remoteService = (RemoteInvocationService) this.applicationContext.getBean(beanName);
		// logger.info(String.format("\tcontext: %s, bname: %s, remote-service: %s", this.applicationContext, beanName,
		// remoteService));
		return remoteService;
	}

	private String getBeanName(TerminatorInfo terminatorInfo) {
		String beanName = null;
		if (terminatorInfo.getApplication() == null) {
			beanName = "remote-service";
		} else if (terminatorInfo.getEndpoint() == null) {
			beanName = String.format("%s-remote-service", terminatorInfo.getApplication());
		} else {
			beanName = String.format("%s-%s-remote-service", terminatorInfo.getApplication(),
					terminatorInfo.getEndpoint());
		}
		return beanName;
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
	}

}
