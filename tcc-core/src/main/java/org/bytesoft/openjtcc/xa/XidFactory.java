package org.bytesoft.openjtcc.xa;

public interface XidFactory {
	public XidImpl createGlobalXid();

	public XidImpl createGlobalXid(byte[] globalTransactionId);

	public XidImpl createBranchXid(XidImpl globalXid);

	public XidImpl createBranchXid(XidImpl globalXid, byte[] branchQualifier);
}
