package org.bytesoft.openjtcc.supports.druid;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidLocalXADataSource implements XADataSource, DataSource {
	private PrintWriter logWriter;
	private int loginTimeout;
	private DruidDataSource druidDataSource;
	private TransactionManager transactionManager;

	@Override
	public Connection getConnection() throws SQLException {
		try {
			Transaction transaction = this.transactionManager.getTransaction();
			if (transaction == null) {
				return this.druidDataSource.getConnection();
			} else {
				DruidLocalXAConnection xacon = this.getXAConnection();
				Connection connection = xacon.getConnection();
				XAResource xares = xacon.getXAResource();
				transaction.enlistResource(xares);
				return connection;
			}
		} catch (SystemException ex) {
			throw new SQLException(ex);
		} catch (RollbackException ex) {
			throw new SQLException(ex);
		} catch (RuntimeException ex) {
			throw new SQLException(ex);
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		try {
			Transaction transaction = this.transactionManager.getTransaction();
			if (transaction == null) {
				return this.druidDataSource.getConnection(username, password);
			} else {
				DruidLocalXAConnection xacon = this.getXAConnection(username, password);
				Connection connection = xacon.getConnection();
				XAResource xares = xacon.getXAResource();
				transaction.enlistResource(xares);
				return connection;
			}
		} catch (SystemException ex) {
			throw new SQLException(ex);
		} catch (RollbackException ex) {
			throw new SQLException(ex);
		} catch (RuntimeException ex) {
			throw new SQLException(ex);
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
		if (iface == null) {
			return false;
		} else if (iface.isInstance(this)) {
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) {
		if (iface == null) {
			return null;
		} else if (iface.isInstance(this)) {
			return (T) this;
		}
		return null;
	}

	@Override
	public DruidLocalXAConnection getXAConnection() throws SQLException {
		DruidPooledConnection pooledConnection = (DruidPooledConnection) this.druidDataSource.getPooledConnection();
		return new DruidLocalXAConnection(pooledConnection);
	}

	@Override
	public DruidLocalXAConnection getXAConnection(String user, String passwd) throws SQLException {
		DruidPooledConnection pooledConnection = (DruidPooledConnection) this.druidDataSource.getPooledConnection(user,
				passwd);
		return new DruidLocalXAConnection(pooledConnection);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}

	public void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	public int getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(int loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public void setDruidDataSource(DruidDataSource druidDataSource) {
		this.druidDataSource = druidDataSource;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
