package com.myWorstEnemy.service;

import com.myWorstEnemy.service.domain.Champion;
import com.myWorstEnemy.service.domain.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static Logger log = LoggerFactory.getLogger(StaticDataService.class);

	@Inject
	public StaticDataService(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
	{
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	private static final String SQL_DELETE_ALL_FROM_CHAMPIONS = "DELETE FROM champions;";

	private static final String SQL_INSERT_INTO_CHAMPIONS = "INSERT INTO champions " +
			"VALUES (:id, :name, :title, :key, :splashArtUrl);";

	private static final String SQL_SELECT_CHAMPION_BY_KEY = "SELECT * FROM champions " +
			"WHERE key = :key;";

	public class ExampleRowMapper implements RowMapper<Example>
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

	public boolean deleteChampionsTableRows()
	{
		try
		{
			int rowsDeleted = namedParameterJdbcTemplate.update(SQL_DELETE_ALL_FROM_CHAMPIONS, new MapSqlParameterSource());
			log.info("Deleted " + rowsDeleted + " from champions table!");
			return true;
		}
		catch (DataAccessException e)
		{
			log.error("DataAccessException ", e);
			return false;
		}
	}

	public boolean insertIntoChampions(String id, String name, String title, Integer key, String splashArtUrl)
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		namedParameters.addValue("name", name);
		namedParameters.addValue("title", title);
		namedParameters.addValue("key", key.toString());
		namedParameters.addValue("splashArtUrl", splashArtUrl);

		return namedParameterJdbcTemplate.update(SQL_INSERT_INTO_CHAMPIONS, namedParameters) == 1;
	}

	public Champion getChampionByKey(Integer key)
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("key", key.toString());

		return namedParameterJdbcTemplate.queryForObject(SQL_SELECT_CHAMPION_BY_KEY, namedParameters, new ChampionRowMapper());
	}

	public class ChampionRowMapper implements RowMapper<Champion>
	{
		@Override
		public Champion mapRow(ResultSet rs, int rowNum) throws SQLException
		{
			Champion champion = new Champion();
			champion.setId(rs.getInt("key"));
			champion.setName(rs.getString("name"));
			champion.setTitle(rs.getString("title"));
			champion.setSplashArtUrl(rs.getString("splash_art_url"));

			return champion;
		}
	}
}
