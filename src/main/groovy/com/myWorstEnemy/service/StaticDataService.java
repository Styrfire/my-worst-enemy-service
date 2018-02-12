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

	private static final String SQL_LOOKUP_OBJECT = "SELECT * " +
	"FROM example";// +
//	"WHERE COLUMN_NAME = :agentNumber";

	public List<Example> getExampleInfo()
	{
		try
		{
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			return namedParameterJdbcTemplate.query(SQL_LOOKUP_OBJECT, namedParameters, new ObjectRowMapper());
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
}
