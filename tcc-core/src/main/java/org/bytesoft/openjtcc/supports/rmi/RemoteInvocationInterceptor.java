package org.bytesoft.openjtcc.supports.rmi;

public interface RemoteInvocationInterceptor {

	public void beforeSendRequest(RemoteInvocationRequest request) throws IllegalStateException;

	public void beforeSendResponse(RemoteInvocationResponse response) throws IllegalStateException;

	public void afterReceiveRequest(RemoteInvocationRequest request) throws IllegalStateException;

	public void afterReceiveResponse(RemoteInvocationResponse response) throws IllegalStateException;

}
