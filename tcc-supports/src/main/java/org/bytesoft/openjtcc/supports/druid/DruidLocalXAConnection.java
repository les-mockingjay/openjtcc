package org.bytesoft.openjtcc.supports.druid;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidLocalXAConnection implements XAConnection {
	private final DruidPooledConnection druidPooledConnection;
	private final DruidLocalXAResource xaResource = new DruidLocalXAResource();
	private boolean initialized = false;
	private boolean logicalConnectionReleased = false;
	private int pooledConnectionSharingCount = 0;

	public DruidLocalXAConnection(DruidPooledConnection connection) {
		this.druidPooledConnection = connection;
	}

	@Override
	public Connection getConnection() throws SQLException {
		DruidLogicalConnection logicalConnection = new DruidLogicalConnection(this, this.druidPooledConnection);
		if (this.initialized) {
			this.pooledConnectionSharingCount++;
		} else {
			this.xaResource.setLocalTransaction(logicalConnection);
			this.initialized = true;
			this.logicalConnectionReleased = false;
		}
		return logicalConnection;
	}

	public void closeLogicalConnection() throws SQLException {
		if (this.pooledConnectionSharingCount > 0) {
			this.pooledConnectionSharingCount--;
		} else if (this.initialized) {
			if (this.logicalConnectionReleased) {
				throw new SQLException();
			} else {
				this.logicalConnectionReleased = true;
			}
		} else {
			throw new SQLException();
		}
	}

	public void commitLocalTransaction() throws SQLException {
		try {
			this.druidPooledConnection.commit();
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw new SQLException(ex);
		} finally {
			try {
				this.close();
			} catch (SQLException ex) {
				// ignore
			}
		}
	}

	public void rollbackLocalTransaction() throws SQLException {
		try {
			this.druidPooledConnection.rollback();
		} catch (SQLException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw new SQLException(ex);
		} finally {
			try {
				this.close();
			} catch (SQLException ex) {
				// ignore
			}
		}
	}

	@Override
	public void close() throws SQLException {
		try {
			this.druidPooledConnection.close();
		} finally {
			this.initialized = false;
		}
	}

	@Override
	public void addConnectionEventListener(ConnectionEventListener paramConnectionEventListener) {
		this.druidPooledConnection.addConnectionEventListener(paramConnectionEventListener);
	}

	@Override
	public void removeConnectionEventListener(ConnectionEventListener paramConnectionEventListener) {
		this.druidPooledConnection.removeConnectionEventListener(paramConnectionEventListener);
	}

	@Override
	public void addStatementEventListener(StatementEventListener paramStatementEventListener) {
		this.druidPooledConnection.addStatementEventListener(paramStatementEventListener);
	}

	@Override
	public void removeStatementEventListener(StatementEventListener paramStatementEventListener) {
		this.druidPooledConnection.removeStatementEventListener(paramStatementEventListener);
	}

	@Override
	public XAResource getXAResource() throws SQLException {
		return this.xaResource;
	}

}
