package org.bytesoft.openjtcc.supports;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import org.bytesoft.openjtcc.archive.CompensableArchive;
import org.bytesoft.openjtcc.archive.TerminatorArchive;
import org.bytesoft.openjtcc.archive.TransactionArchive;
import org.bytesoft.openjtcc.common.TransactionContext;

public interface TransactionLogger {
	public static final String NULL = "(null)";

	/* service */
	public void enlistService(TransactionContext transactionContext, CompensableArchive holder);

	public void delistService(TransactionContext transactionContext, CompensableArchive holder);

	public void updateService(TransactionContext transactionContext, CompensableArchive holder);

	public void confirmService(TransactionContext transactionContext, CompensableArchive holder);

	public void cancelService(TransactionContext transactionContext, CompensableArchive holder);

	public void commitService(TransactionContext transactionContext, CompensableArchive holder);

	public void rollbackService(TransactionContext transactionContext, CompensableArchive holder);

	/* remote terminator */
	public void registerTerminator(TransactionContext transactionContext, TerminatorArchive holder);

	public void prepareTerminator(TransactionContext transactionContext, TerminatorArchive holder);

	public void commitTerminator(TransactionContext transactionContext, TerminatorArchive holder);

	public void rollbackTerminator(TransactionContext transactionContext, TerminatorArchive holder);

	public void cleanupTerminator(TransactionContext transactionContext, TerminatorArchive holder);

	/* transaction */
	public void beginTransaction(TransactionArchive transaction);

	public void prepareTransaction(TransactionArchive transaction);

	public void updateTransaction(TransactionArchive transaction);

	public void completeTransaction(TransactionArchive transaction);

	public void cleanupTransaction(TransactionArchive transaction);

	public Set<TransactionArchive> getLoggedTransactionSet();

	/* default transaction logger */
	public static TransactionLogger defaultTransactionLogger = NullTransactionLoggerHanlder.getNullTransactionLogger();

	public static class NullTransactionLoggerHanlder implements InvocationHandler {

		// private static final Logger logger = Logger.getLogger("openjtcc");
		private static final NullTransactionLoggerHanlder instance = new NullTransactionLoggerHanlder();

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// logger.info(String.format("%4s[transaction-log] %s", "", method.getName()));
			// synchronized (Object.class) {
			// System.err.printf("%4s[transaction-log] %s%n", "", method.getName());
			// System.err.flush();
			// }
			Class<?> clazz = method.getReturnType();
			if (Void.TYPE.equals(clazz)) {
				return null;
			} else if (Set.class.equals(clazz)) {
				return this.newInstance(HashSet.class);
			} else {
				return null;
			}
		}

		private Object newInstance(Class<?> clazz) {
			try {
				return clazz.newInstance();
			} catch (Exception ex) {
				return null;
			}
		}

		public static TransactionLogger getNullTransactionLogger() {
			return (TransactionLogger) Proxy.newProxyInstance(TransactionLogger.class.getClassLoader(),
					new Class[] { TransactionLogger.class }, instance);
		}
	}
}
