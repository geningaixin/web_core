package com.huagao.core.db.source;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.huagao.core.db.source.scanner.MapperScanner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * Packgae : com.huagao.tongshan.floodprevention.config
 * Auther : Gening
 * Time : 2018/8/30
 * Description : 数据源配置一
 * Version :
 */
@Configuration
@PropertySource("classpath:db.properties")
@MapperScanner(basePackages = "${db.mybatis.mapperScanner.basePackage}", sqlSessionTemplateRef = "sqlSessionTemplateOne")
public class DataSourceConfig {

	@Value("${spring.datasource.source1.url}")
	private String url;

	@Value("${spring.datasource.source1.username}")
	private String username;

	@Value("${spring.datasource.source1.password}")
	private String password;

	@Value("${spring.datasource.source1.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.source1.initialSize}")
	private Integer initialSize;

	@Value("${spring.datasource.source1.minIdle}")
	private Integer minIdle;

	@Value("${spring.datasource.source1.maxActive}")
	private Integer maxActive;

	@Value("${spring.datasource.source1.maxWait}")
	private Integer maxWait;

	@Value("${spring.datasource.source1.timeBetweenEvictionRunsMillis}")
	private Long timeBetweenEvictionRunsMillis;

	@Value("${spring.datasource.source1.minEvictableIdleTimeMillis}")
	private Long minEvictableIdleTimeMillis;

	@Value("${spring.datasource.source1.validationQuery}")
	private String validationQuery;

	@Value("${spring.datasource.source1.testWhileIdle}")
	private Boolean testWhileIdle;

	@Value("${spring.datasource.source1.testOnBorrow}")
	private Boolean testOnBorrow;

	@Value("${spring.datasource.source1.testOnReturn}")
	private boolean testOnReturn;

	@Value("${spring.datasource.source1.poolPreparedStatements}")
	private boolean poolPreparedStatements;

	@Value("${spring.datasource.source1.maxPoolPreparedStatementPerConnectionSize}")
	private Integer maxPoolPreparedStatementPerConnectionSize;

	@Value("${spring.datasource.source1.filters}")
	private String filters;

	@Value("${spring.datasource.source1.connectionProperties}")
	private String connectionProperties;

	@Value("${druid.servlet.url.mapping}")
	private String urlMappings;

	@Value("${druid.client.allow}")
	private String allow;

	@Value("${druid.client.deny}")
	private String deny;

	@Value("${druid.client.loginUsername}")
	private String loginUsername;

	@Value("${druid.client.loginPassword}")
	private String loginPassword;

	@Value("$druid.client.resetEnable}")
	private String resetEnable;

	@Value("${druid.filter.urlPatterns}")
	private String urlPatterns;

	@Value("${druid.filter.exclusions}")
	private String exclusions;

	@Value("${db.mytatis.mapperLocations}")
	private String mapperLocations;

	@Value("${db.mytatis.typeAliasesPackage}")
	private String typeAliasesPackage;

	// 设置主数据源
	@Bean(name = "dataSource")
	@Primary // 当有同类型的注入时，@Primary备注解的优先注入，避免spring因为不知道注入哪一个而报错
	public DataSource DataSource() throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setFilters(filters);
		dataSource.setConnectionProperties(connectionProperties);
		return new DruidDataSource();
	}

	// druid web.xml Servlet 配置
	@Bean
	public ServletRegistrationBean druidStatViewServlet() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new StatViewServlet(), urlMappings);
		registrationBean.addInitParameter("allow", allow); // IP白名单 (没有配置或者为空，则允许所有访问)
		registrationBean.addInitParameter("deny", deny); // IP黑名单 (存在共同时，deny优先于allow)
		registrationBean.addInitParameter("loginUsername", loginUsername);
		registrationBean.addInitParameter("loginPassword", loginPassword);
		registrationBean.addInitParameter("resetEnable", resetEnable);
		return registrationBean;
	}

	// druid web.xml Filter 配置
	@Bean
	public FilterRegistrationBean druidWebStatViewFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(new WebStatFilter());
		registrationBean.addInitParameter("urlPatterns", urlPatterns);
		registrationBean.addInitParameter("exclusions", exclusions);
		return registrationBean;
	}

	// 设置主数据源的 SessionFactory
	@Bean(name = "sqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactoryOne(@Qualifier("dataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setMapperLocations(resolver.getResources(mapperLocations));
		bean.setTypeAliasesPackage(typeAliasesPackage);
		return bean.getObject();
	}

	// 设置主数据源的事务
	@Bean(name = "dataSourceTransactionManager")
	@Primary
	public PlatformTransactionManager dataSourceTransactionManagerOne(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	// 设置数据源 Session模板
	@Bean(name = "sqlSessionTemplate")
	@Primary
	public SqlSessionTemplate sqlSessionTemplateOne(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
