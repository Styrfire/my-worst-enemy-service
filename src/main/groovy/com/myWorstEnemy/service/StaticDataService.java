package com.myWorstEnemy.service;

import com.myWorstEnemy.service.domain.Example;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Lazy
public class StaticDataService
{
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static Logger log = Logger.getLogger(StaticDataService.class);

	@Inject
	public StaticDataService(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
	{
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	private static final String SQL_SELECT_ALL_FROM_CHAMPIONS = "SELECT * " +
			"FROM example";

	private static final String SQL_INSERT_INTO_CHAMPIONS = "INSERT INTO champions " +
			"VALUES (:id, :name, :title, :key, :splashArtUrl);";

	public List<Example> getExampleInfo()
	{
		try
		{
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			return namedParameterJdbcTemplate.query(SQL_SELECT_ALL_FROM_CHAMPIONS, namedParameters, new ObjectRowMapper());
		}
		catch (DataAccessException e)
		{
			log.error("DataAccessException ", e);
			throw e;
		}
	}

	public class ObjectRowMapper implements RowMapper<Example>
	{
		@Override
		public Example mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			Example something = new Example();
			something.setExample1(rs.getInt("example1"));
			something.setExample2(rs.getInt("example2"));
			return something;
		}
	}

	public boolean insertIntoChampions(Integer id, String name, String title, String key, String splashArtUrl)
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id.toString());
		namedParameters.addValue("name", name);
		namedParameters.addValue("title", title);
		namedParameters.addValue("key", key);
		namedParameters.addValue("splashArtUrl", splashArtUrl);

		return namedParameterJdbcTemplate.update(SQL_INSERT_INTO_CHAMPIONS, namedParameters) == 1;
	}
}
