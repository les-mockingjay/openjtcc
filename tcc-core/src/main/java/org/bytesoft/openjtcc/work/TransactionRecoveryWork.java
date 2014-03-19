package org.bytesoft.openjtcc.work;

import org.bytesoft.openjtcc.recovery.RecoveryManager;

public class TransactionRecoveryWork extends AbstractScheduleWork {
	private static final int MIN_INTERVAL_SECONDS = 5;
	private static final int MAX_INTERVAL_SECONDS = 1800;
	private static final int DEFAULT_INTERVAL_SECONDS = 30;

	private int expireIntervalSeconds = DEFAULT_INTERVAL_SECONDS;
	private RecoveryManager recoveryManager;

	@Override
	public void execute() {
		this.recoveryManager.recover();
	}

	@Override
	public int getIntervalSeconds() {
		int seconds = DEFAULT_INTERVAL_SECONDS;
		if (this.expireIntervalSeconds < MIN_INTERVAL_SECONDS || this.expireIntervalSeconds > MAX_INTERVAL_SECONDS) {
			seconds = DEFAULT_INTERVAL_SECONDS;
		} else {
			seconds = this.expireIntervalSeconds;
		}
		return seconds;
	}

	public RecoveryManager getRecoveryManager() {
		return recoveryManager;
	}

	public void setRecoveryManager(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}

	public int getExpireIntervalSeconds() {
		return expireIntervalSeconds;
	}

	public void setExpireIntervalSeconds(int expireIntervalSeconds) {
		this.expireIntervalSeconds = expireIntervalSeconds;
	}
}
