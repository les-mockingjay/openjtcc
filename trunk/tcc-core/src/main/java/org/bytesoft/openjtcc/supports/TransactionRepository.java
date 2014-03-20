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
package org.bytesoft.openjtcc.supports;

import java.util.Set;

import org.bytesoft.openjtcc.TransactionImpl;
import org.bytesoft.openjtcc.xa.XidImpl;

public interface TransactionRepository {

	public void putTransaction(XidImpl globalXid, TransactionImpl transaction);

	public TransactionImpl getTransaction(XidImpl globalXid);

	public TransactionImpl removeTransaction(XidImpl globalXid);

	public void putErrorTransaction(XidImpl globalXid, TransactionImpl transaction);

	public TransactionImpl getErrorTransaction(XidImpl globalXid);

	public TransactionImpl removeErrorTransaction(XidImpl globalXid);

	public TransactionLogger getTransactionLogger();

	public Set<TransactionImpl> getActiveTransactionSet();

	public Set<TransactionImpl> getErrorTransactionSet();

}
