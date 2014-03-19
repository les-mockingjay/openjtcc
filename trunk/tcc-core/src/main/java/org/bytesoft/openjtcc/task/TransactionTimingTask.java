package org.bytesoft.openjtcc.task;

import org.bytesoft.openjtcc.supports.schedule.TimingProcesser;

public class TransactionTimingTask extends AbstractScheduleTask {
	private static final int MIN_INTERVAL_SECONDS = 1;
	private static final int MAX_INTERVAL_SECONDS = 1800;
	private static final int DEFAULT_INTERVAL_SECONDS = 1;

	private int timingIntervalSeconds = DEFAULT_INTERVAL_SECONDS;
	private TimingProcesser timingProcesser;

	@Override
	public void execute() {
		this.timingProcesser.processTimingTransaction();
		this.timingProcesser.processExpireTransaction();
	}

	@Override
	public int getIntervalSeconds() {
		int seconds = DEFAULT_INTERVAL_SECONDS;
		if (this.timingIntervalSeconds < MIN_INTERVAL_SECONDS || this.timingIntervalSeconds > MAX_INTERVAL_SECONDS) {
			seconds = DEFAULT_INTERVAL_SECONDS;
		} else {
			seconds = this.timingIntervalSeconds;
		}
		return seconds;
	}

	public TimingProcesser getTimingProcesser() {
		return timingProcesser;
	}

	public void setTimingProcesser(TimingProcesser timingProcesser) {
		this.timingProcesser = timingProcesser;
	}

	public int getTimingIntervalSeconds() {
		return timingIntervalSeconds;
	}

	public void setTimingIntervalSeconds(int timingIntervalSeconds) {
		this.timingIntervalSeconds = timingIntervalSeconds;
	}

}
