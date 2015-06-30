# OpenJTCC构成 #
OpenJTCC主要由tcc-compensable、tcc-core、tcc-supports三部分构成，其中：
  * tcc-compensable定义了tcc与应用程序相关的接口；
  * tcc-core为openjtcc事务管理器的实现；
  * tcc-supports用于集成spring、rpc框架（如Dubbo）、DB连接池（如dbcp、druid）。

# OpenJTCC配置 #

### Spring配置 ###
```
<!-- openjtcc.xml位于tcc-supports.jar包中，如无修改不需取出，直接import即可 -->
<import resource="classpath:openjtcc.xml" />

<!-- 使用数据库来记录事务日志（目前唯一可选项）时，需要配置数据源（默认为loggerDataSource，名称需与openjtcc.xml中保持一致） -->
<!-- 使用dbcp -->
<bean id="loggerDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
	<property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
	<property name="url" value="jdbc:oracle:thin:@192.168.29.213:1521:hshdb" />
	<property name="username" value="ptz1" />
	<property name="password" value="ptz1" />
</bean>

<!-- 使用druid -->
<!--
<bean id="loggerDataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
	<property name="url" value="jdbc:oracle:thin:@192.168.29.213:1521:hshdb" />
	<property name="username" value="ptz1" />
	<property name="password" value="ptz1" />

	<property name="filters" value="stat" />

	<property name="maxActive" value="4" />
	<property name="initialSize" value="2" />
	<property name="maxWait" value="60000" />
	<property name="minIdle" value="2" />
</bean>
-->
```

### DB数据源配置（dbcp/druid） ###
```
<!-- 使用dbcp -->
<bean id="dataSource" class="org.apache.commons.dbcp.managed.BasicManagedDataSource"
		destroy-method="close">
	<property name="transactionManager" ref="tccTransactionManager" />

	<property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
	<property name="url" value="jdbc:oracle:thin:@192.168.29.213:1521:hshdb" />
	<property name="username" value="ptz2" />
	<property name="password" value="ptz2" />
	<property name="maxActive" value="20" />
	<property name="maxIdle" value="4" />
	<property name="maxWait" value="4" />
</bean>
```

```
<!-- 使用druid -->
<!--
<bean id="dataSource" class="org.bytesoft.openjtcc.supports.druid.DruidLocalXADataSource">
	<property name="druidDataSource" ref="loggerDataSource" />
	<property name="transactionManager" ref="tccTransactionManager" />
</bean>
-->
```
### Dubbo配置 ###

##### 默认属性文件openjtcc.properties #####
openjtcc.xml文件中配置了默认加载openjtcc.properties属性文件，该文件需要提供如下几个配置项：
```
tcc.application=tcc-demo4dubbo-server
tcc.endpoint=default
tcc.timeout=300
```
其中，tcc.application为当前应用名称（如tcc-demo4dubbo-server）；
tcc.endpoint为当前应用实例端点名称（如应用tcc-demo4dubbo-server部署在集群上时，则endpoint为某集群实例名称）；
tcc.timeout为事务超时时间（单位：秒）。

##### Dubbo基础配置 #####
```
<dubbo:application name="${tcc.application}" />
<dubbo:consumer timeout="60000" retries="0" />
<dubbo:registry address="multicast://224.5.6.7:23421" />
<dubbo:protocol name="dubbo" port="${dubbo.port}" />
<dubbo:service ref="remoteInvocationService" interface="org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService" />
```

##### Dubbo客户端应用配置：reference #####
```
<dubbo:reference id="remote-service" check="true" loadbalance="random" interface="org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService" />
```
必须提供一个id为remote-service的配置。

```
<dubbo:reference id="tcc-demo4dubbo-server-remote-service" check="true" url="dubbo://${tcc-server-appname}:${tcc-server-appport}" interface="org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService" />
<dubbo:reference id="tcc-demo4dubbo-server-default-remote-service" check="true" url="dubbo://${tcc-server-appname}:${tcc-server-appport}" interface="org.bytesoft.openjtcc.supports.dubbo.RemoteInvocationService" />
```
假设客户端应用（假设名称为tcc-demo4dubbo-client）调用的远程服务端应用名称为${tcc-server-appname}（如tcc-demo4dubbo-server）且Endpoint名称为${tcc-server-endname}，则1）除remote-service外还必须提供两个配置，其ID分别为${tcc-server-appname}-remote-service、${tcc-server-appname}-${tcc-server-endname}-remote-service；2）上述两个配置必须指定url，url为dubbo://${tcc-server-appname}:${tcc-server-appport}。<br />
注意：上文描述中的${tcc-server-appname}、${tcc-server-endname}、${tcc-server-appport}均为变量标识，需以真实值填入。

##### Dubbo客户端应用配置：remote-bean-factory #####
```
<bean id="sampleBeanFactory" parent="remoteBeanFactory">
	<property name="application" value="${tcc-server-appname}" />
</bean>
```


# 应用程序开发 #
说明：业务接口定义的方法下文中简称Try方法。
### 本地Service调用 ###

##### 约束 #####
  1. 业务接口方法定义中必须抛出CompensableException异常；
  1. 业务实现类必须实现业务接口、Compensable接口；
  1. 业务实现中必须注入CompensableContext（在openjtcc.xml中定义，beanId为compensableContext）；
  1. 推荐使用注解来定义事务传播属性（在业务接口和Compensable接口定义的方法上）；
  1. Try方法中必须通过CompensableContext.setCompensableVariable(Serializable)方法来设置用于Confirm/Cancel的参数。该参数将在调用Compensable.confirm(Serializable)/Compensable.cancel(Serializable)时提供给业务实现类。
  1. Action/Servlet/Main调用Service时，必须通过NativeBeanFactory（在openjtcc.xml中配置，beanId为nativeBeanFactory）来获取本地Service的引用；当前方法以在TCC事务中时，不可以再通过NativeBeanFactory获取CompensableService。

##### 本地Service调用：业务接口 #####
```
public interface NativeAccountService {
	public void transfer(String fromAccountId, String toAccountId, double amount) throws CompensableException, AccountException;
}
```
##### 本地Service调用：业务实现 #####
```
public class NativeAccountServiceImpl<T extends NativeTransferVariable> implements NativeAccountService, Compensable<T> {

	private AccountDao accountDao;
	private CompensableContext<T> compensableContext;

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void transfer(String fromAccountId, String toAccountId, double amount) throws CompensableException, AccountException {
		String fromTransferId = this.accountDao.transferOutAccount(fromAccountId, amount);
		String toTransferId = this.accountDao.transferInAccount(toAccountId, amount);
		NativeTransferVariable variable = new NativeTransferVariable();
		variable.setFromTransferId(fromTransferId);
		variable.setToTransferId(toTransferId);
		this.compensableContext.setCompensableVariable((T) variable);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void cancel(NativeTransferVariable variable) throws CompensableException {
		String fromTransferId = variable.getFromTransferId();
		String toTransferId = variable.getToTransferId();
		this.accountDao.cancelTransferOutAccount(fromTransferId);
		this.accountDao.cancelTransferInAccount(toTransferId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirm(NativeTransferVariable variable) throws CompensableException {
		String fromTransferId = variable.getFromTransferId();
		String toTransferId = variable.getToTransferId();
		this.accountDao.confirmTransferOutAccount(fromTransferId);
		this.accountDao.confirmTransferInAccount(toTransferId);
	}

	static class NativeTransferVariable implements Serializable {
		private static final long serialVersionUID = 1L;

		private String fromTransferId;
		private String toTransferId;

		public String getToTransferId() {
			return toTransferId;
		}

		public void setToTransferId(String toTransferId) {
			this.toTransferId = toTransferId;
		}

		public String getFromTransferId() {
			return fromTransferId;
		}

		public void setFromTransferId(String fromTransferId) {
			this.fromTransferId = fromTransferId;
		}

	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setCompensableContext(CompensableContext<T> compensableContext) {
		this.compensableContext = compensableContext;
	}
}
```

##### 调用本地Service #####
```
public static void main(String... args) throws Throwable {
	String xml = "classpath:spring-context.xml";

	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(xml);

	NativeBeanFactory beanFactory = (NativeBeanFactory) context.getBean("nativeBeanFactory");
	NativeAccountService accountService = beanFactory.getBean(NativeAccountService.class, "nativeAccountService");
	accountService.transfer("1001", "1002", 1.0);

	context.close();
}
```

### 远程Service调用 ###

##### 约束 #####
除上文（本地Service调用中）定义的约束外，
  1. 远程Service业务接口的方法定义中必须抛出CompensableException和RemoteException异常；
  1. 客户端应用中需要注入RemoteBeanFactory；
  1. 客户端应用必须通过RemoteBeanFactory来获取远程Service的引用；
  1. 客户端应用在confirm/cancel时，仅需要confirm/cancel自己的操作即可，不需要confirm/cancel远程Service（远程Service的confirm/cancel操作由远程Service所在端点的事务管理器自行调度）。

##### 远程Service调用：server业务接口 #####
```
public interface RemoteAccountService {
	public void transferIn(String accountId, double amount) throws AccountException, RemoteException;
	public void transferOut(String accountId, double amount) throws AccountException, RemoteException;
}
```

##### 远程Service调用：server业务实现 #####
```
public class RemoteAccountServiceImpl<T extends ServerTransferVariable> implements RemoteAccountService, Compensable<T> {

	private AccountDao accountDao;
	private CompensableContext<T> compensableContext;

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferIn(String accountId, double amount) throws AccountException {
		String transferId = this.accountDao.transferInAccount(accountId, amount);
		ServerTransferVariable variable = new ServerTransferVariable();
		variable.setTransferId(transferId);
		variable.setTransferOut(false);
		this.compensableContext.setCompensableVariable((T) variable);
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferOut(String accountId, double amount) throws AccountException {
		String transferId = this.accountDao.transferOutAccount(accountId, amount);
		ServerTransferVariable variable = new ServerTransferVariable();
		variable.setTransferId(transferId);
		variable.setTransferOut(true);
		this.compensableContext.setCompensableVariable((T) variable);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void cancel(ServerTransferVariable variable) throws CompensableException {
		String transferId = variable.getTransferId();
		if (variable.isTransferOut()) {
			this.accountDao.cancelTransferOutAccount(transferId);
		} else {
			this.accountDao.cancelTransferInAccount(transferId);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirm(ServerTransferVariable variable) throws CompensableException {
		String transferId = variable.getTransferId();
		if (variable.isTransferOut()) {
			this.accountDao.confirmTransferOutAccount(transferId);
		} else {
			this.accountDao.confirmTransferInAccount(transferId);
		}
	}

	static class ServerTransferVariable implements Serializable {
		private static final long serialVersionUID = 1L;

		private boolean transferOut;
		private String transferId;

		public boolean isTransferOut() {
			return transferOut;
		}

		public void setTransferOut(boolean transferOut) {
			this.transferOut = transferOut;
		}

		public String getTransferId() {
			return transferId;
		}

		public void setTransferId(String transferId) {
			this.transferId = transferId;
		}

	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setCompensableContext(CompensableContext<T> compensableContext) {
		this.compensableContext = compensableContext;
	}

}
```

##### 远程Service调用：client业务接口 #####
```
public interface AccountTransferService {
	public void nativeAccountTransferInToRemoteAccount(String fromAccountId, String toAccountId, double amount)
			throws AccountException;
	public void remoteAccountTransferInToNativeAccount(String fromAccountId, String toAccountId, double amount)
			throws AccountException;
}
```


##### 远程Service调用：client业务实现 #####
```
public class AccountTransferServiceImpl<T extends TransferVariable> implements AccountTransferService, Compensable<T> {

	private AccountDao accountDao;
	private CompensableContext<T> compensableContext;
	private RemoteBeanFactory defaultBeanFactory;

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void nativeAccountTransferInToRemoteAccount(String fromAccountId, String toAccountId, double amount)
			throws AccountException {

		String transferId = this.accountDao.transferOutAccount(fromAccountId, amount);

		RemoteAccountService remoteAccountService = this.defaultBeanFactory.getBean(RemoteAccountService.class,
				"remoteAccountService");
		try {
			remoteAccountService.transferIn(toAccountId, amount);
		} catch (RemoteException ex) {
			throw new AccountException(ex);
		}

		TransferVariable variable = new TransferVariable();
		variable.setNativeAccountOut(true);
		variable.setTransferId(transferId);

		this.compensableContext.setCompensableVariable((T) variable);

	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void remoteAccountTransferInToNativeAccount(String fromAccountId, String toAccountId, double amount)
			throws AccountException {

		String transferId = this.accountDao.transferInAccount(fromAccountId, amount);

		RemoteAccountService remoteAccountService = this.defaultBeanFactory.getBean(RemoteAccountService.class,
				"remoteAccountService");
		try {
			remoteAccountService.transferOut(toAccountId, amount);
		} catch (RemoteException ex) {
			throw new AccountException(ex);
		}

		TransferVariable variable = new TransferVariable();
		variable.setNativeAccountOut(false);
		variable.setTransferId(transferId);

		this.compensableContext.setCompensableVariable((T) variable);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void cancel(TransferVariable variable) throws CompensableException {
		String transferId = variable.getTransferId();
		if (variable.isNativeAccountOut()) {
			this.accountDao.cancelTransferOutAccount(transferId);
		} else {
			this.accountDao.cancelTransferInAccount(transferId);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirm(TransferVariable variable) throws CompensableException {
		String transferId = variable.getTransferId();
		if (variable.isNativeAccountOut()) {
			this.accountDao.confirmTransferOutAccount(transferId);
		} else {
			this.accountDao.confirmTransferInAccount(transferId);
		}
	}

	static class TransferVariable implements Serializable {
		private static final long serialVersionUID = 1L;

		private boolean nativeAccountOut;
		private String transferId;

		public boolean isNativeAccountOut() {
			return nativeAccountOut;
		}

		public void setNativeAccountOut(boolean nativeAccountOut) {
			this.nativeAccountOut = nativeAccountOut;
		}

		public String getTransferId() {
			return transferId;
		}

		public void setTransferId(String transferId) {
			this.transferId = transferId;
		}

	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setCompensableContext(CompensableContext<T> compensableContext) {
		this.compensableContext = compensableContext;
	}

	public void setDefaultBeanFactory(RemoteBeanFactory defaultBeanFactory) {
		this.defaultBeanFactory = defaultBeanFactory;
	}

}
```

# 异常 #
### CompensableCommittingException异常 ###
当业务调用捕获到CompensableCommittingException时，表示事务管理器已正在提交TCC事务（尚未提交或已部分提交），但由于某些原因（如网络故障等）当前提交时出错，后续当继续尝试完成提交操作。

### 启发式异常 ###
事务管理器可抛出的启发式异常有：HeuristicRollbackException，HeuristicMixedException，该异常会被容器/Spring封装后抛出，如org.springframework.transaction.HeuristicCompletionException为由Spring抛出的启发式异常，请参考Spring的定义来判断事务是否提交/回滚。

### 应用异常 ###
根据容器/Spring对应用异常的支持，以及应用程序配置的回滚策略，判断事务管理器是否提交/回滚事务。