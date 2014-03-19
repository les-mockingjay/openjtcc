package org.bytesoft.openjtcc.supports.adapter.work;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkListener;

import org.bytesoft.openjtcc.task.TransactionRecoveryTask;

public class TransactionRecoveryWork extends TransactionRecoveryTask implements Work, WorkListener {

	private boolean completed = false;

	public void workAccepted(WorkEvent workEvent) {
	}

	public void workCompleted(WorkEvent workEvent) {
		this.completed = true;
	}

	public void workRejected(WorkEvent workEvent) {
	}

	public void workStarted(WorkEvent workEvent) {
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
