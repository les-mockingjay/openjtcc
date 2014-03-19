package org.bytesoft.openjtcc.supports.adapter.work.listener;

import javax.resource.spi.work.WorkAdapter;
import javax.resource.spi.work.WorkEvent;

import org.bytesoft.openjtcc.supports.adapter.TransactionResourceAdapter;

public class TimingWorkListener extends WorkAdapter {
	private TransactionResourceAdapter resourceAdapter;

	public TimingWorkListener(TransactionResourceAdapter adapter) {
		this.resourceAdapter = adapter;
	}

	public void workCompleted(WorkEvent event) {
		this.resourceAdapter.setTimingCompleted(true);
	}

}
