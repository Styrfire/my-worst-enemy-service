package com.myWorstEnemy.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class DataSourceConfiguration
{
	private static Logger logger = Logger.getLogger(DataSourceConfiguration.class);

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
				// do fancy stuff for mysql
				File file = new File("sqlite");
				String jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath() + "\\exampledb.db";
				jdbcUrl = jdbcUrl.replace("\\", "/");

				dataSource = new HikariDataSource();
				dataSource.setDriverClassName(riotDriverClassName);
				dataSource.setJdbcUrl(jdbcUrl);
//				dataSource.setJdbcUrl(riotJdbcUrl); // jdbc Url for actual data source
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
