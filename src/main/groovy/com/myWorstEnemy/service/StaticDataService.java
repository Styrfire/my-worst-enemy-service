package com.myWorstEnemy.service;

//import org.apache.log4j.Logger;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import javax.inject.Inject;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@Repository
//@Lazy
//public class StaticDataService
//{
//	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//	private static Logger log = Logger.getLogger(StaticDataService.class);
//
//	@Inject
//	public StaticDataService(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
//	{
//		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//	}
//
//	private static final String SQL_LOOKUP_OBJECT = "SELECT * " +
//	"FROM TABLE " +
//	"WHERE COLUMN_NAME = :agentNumber";
//
//	public Object getObjectInfo(String agentNumber)
//	{
//		try
//		{
//			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//			namedParameters.addValue("agentNumber", String.valueOf(agentNumber));
//			return namedParameterJdbcTemplate.queryForObject(SQL_LOOKUP_OBJECT, namedParameters,
//			new ObjectRowMapper());
//		}
//		catch (DataAccessException e)
//		{
//			log.error("DataAccessException ", e);
//			throw e;
//		}
//	}
//	public class ObjectRowMapper implements RowMapper<Object>
//	{
//		@Override
//		public Object mapRow(ResultSet rs, int rowNum) throws SQLException
//		{
//			Object something = new Object();
//			return something;
//		}
//	}
//}
