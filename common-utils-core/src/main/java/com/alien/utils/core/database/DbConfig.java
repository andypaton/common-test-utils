package com.alien.utils.core.database;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Configuration
@PropertySource("classpath:environment.properties")
public class DbConfig {

	private static final Logger LOGGER = Logger.getLogger(DbConfig.class.getName());

	@Value("${db.driver}")
	private String driver;

	@Value("${db.url}")
	private String url;

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;

	/**
	 * static PropertySourcesPlaceholderConfigurer is required for the @Value
	 * annotations to work. Must be static
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "jdbcTemplate")
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

	@Bean(name = "namedParameterJdbcTemplate")
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(getDataSource());
	}
	
	private DataSource getDataSource() {
		
		LOGGER.debug("creating local datasource. driver [" + driver + "], url [" + url + "], username [" + username + "], password [" + password + "]");
		
		// logger not working ???
//		System.out.println("creating local datasource. driver [" + driver + "], url [" + url + "], username [" + username + "], password [" + password + "]");
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setInitialSize(4);
		dataSource.setMaxActive(8);
		dataSource.setRemoveAbandoned(true);
		return dataSource;
	}

}
