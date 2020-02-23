package com.myWorstEnemy.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration
{
	@Configuration
	protected static class LocalDataSourceConfiguration
	{
		@Value("${riot.datasource.driverClassName}")
		private String riotDriverClassName;
		@Value("${riot.datasource.url}")
		private String riotJdbcUrl;
		@Value("${riot.datasource.username}")
		private String riotUsername;
		@Value("${riot.datasource.password}")
		private String riotPassword;

		@Bean
		public DataSource dataSource () {
			HikariDataSource dataSource;
			try
			{
				dataSource = new HikariDataSource();
				dataSource.setDriverClassName(riotDriverClassName);
				dataSource.setJdbcUrl("jdbc:sqlite::resource:" + riotJdbcUrl);
				dataSource.setUsername(riotUsername);
				dataSource.setPassword(riotPassword);

				return dataSource;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new BeanCreationException("Can not initialize test datasource.", e);
			}
		}
	}
}
