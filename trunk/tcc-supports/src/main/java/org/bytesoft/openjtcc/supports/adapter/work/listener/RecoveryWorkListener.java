package org.bytesoft.openjtcc.supports.adapter.work.listener;

import javax.resource.spi.work.WorkAdapter;
import javax.resource.spi.work.WorkEvent;

import org.bytesoft.openjtcc.supports.adapter.TransactionResourceAdapter;

public class RecoveryWorkListener extends WorkAdapter {
	private TransactionResourceAdapter resourceAdapter;

	public RecoveryWorkListener(TransactionResourceAdapter adapter) {
		this.resourceAdapter = adapter;
	}

	public void workCompleted(WorkEvent event) {
		this.resourceAdapter.setRecoveryCompleted(true);
	}

}
