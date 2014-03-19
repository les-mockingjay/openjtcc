package org.bytesoft.openjtcc.internal;

import java.io.Serializable;

import org.bytesoft.utils.CommonUtils;

public class ManagedKey implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private String application = "unspecified";
	private String endpoint = "unspecified";

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

	public ManagedKey clone() throws CloneNotSupportedException {
		ManagedKey that = new ManagedKey();
		that.setApplication(this.application);
		that.setEndpoint(this.endpoint);
		return that;
	}

	public int hashCode() {
		int hash = 23;
		hash += 29 * (this.application == null ? 0 : this.application.hashCode());
		hash += 31 * (this.endpoint == null ? 0 : this.endpoint.hashCode());
		return hash;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (ManagedKey.class.equals(obj.getClass()) == false) {
			return false;
		}

		ManagedKey that = (ManagedKey) obj;
		boolean appEquals = CommonUtils.equals(this.application, that.application);
		boolean endEquals = CommonUtils.equals(this.endpoint, that.endpoint);
		return appEquals && endEquals;
	}

}
