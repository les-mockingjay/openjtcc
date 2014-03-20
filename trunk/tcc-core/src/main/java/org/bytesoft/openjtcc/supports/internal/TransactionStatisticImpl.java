/**
 * Copyright 2014 yangming.liu<liuyangming@gmail.com>.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 */
package org.bytesoft.openjtcc.supports.internal;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.supports.TransactionStatistic;

public class TransactionStatisticImpl implements TransactionStatistic {

	public void fireBeginTransaction(TransactionImpl transaction) {
	}

	public void firePreparingTransaction(TransactionImpl transaction) {
	}

	public void firePreparedTransaction(TransactionImpl transaction) {
	}

	public void fireCommittingTransaction(TransactionImpl transaction) {
	}

	public void fireCommittedTransaction(TransactionImpl transaction) {
	}

	public void fireRollingBackTransaction(TransactionImpl transaction) {
	}

	public void fireRolledbackTransaction(TransactionImpl transaction) {
	}

	public void fireCompleteFailure(TransactionImpl transaction) {
	}

	public void fireCleanupTransaction(TransactionImpl transaction) {
	}

	public void fireRecoverTransaction(TransactionImpl transaction) {
	}

}
