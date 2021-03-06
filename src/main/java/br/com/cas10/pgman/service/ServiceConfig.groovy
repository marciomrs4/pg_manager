package br.com.cas10.pgman.service

import java.beans.beancontext.BeanContext;

import br.com.cas10.pgman.worker.WorkerConfig;

import javax.sql.DataSource

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement

import br.com.cas10.pgman.agent.AgentConfig;
import br.com.cas10.pgman.analitics.Snapshots;

@Configuration
@ComponentScan(basePackages=["br.com.cas10.pgman.service"])
@Import([ AgentConfig.class, WorkerConfig.class ])
@EnableTransactionManagement(proxyTargetClass = true)
class ServiceConfig {

	@Bean
	DataSource dataSource() {
		JndiTemplate jndi = new JndiTemplate();
		return jndi.lookup("java:comp/env/jdbc/pgman", DataSource.class)
	}

	@Bean
	DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource())
	}
	
	@Bean
	ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler()
		int threads = Math.ceil(Runtime.runtime.availableProcessors() / 2.0d)
		taskScheduler.setPoolSize(threads)
		return taskScheduler
	}
	
	@Bean(initMethod="start", destroyMethod="shutdown")
	Scheduler quartzScheduler() {
		StdSchedulerFactory.getDefaultScheduler();
	}
	
	@Bean
	Snapshots snapshots() {
		return new Snapshots()
	}
}
