package com.myWorstEnemy.web.controller;

import com.google.gson.Gson;
import com.riot.api.RiotApi;
import com.riot.dto.Match.MatchList;
import com.riot.dto.Summoner.Summoner;
import com.riot.exception.RiotApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

class MyWorstEnemyControllerJUnitTest //extends Specification
{
	MyWorstEnemyController controller;

	NamedParameterJdbcTemplate namedParameterJdbcTemplateMock;
	RiotApi riotApiMock;

	@BeforeEach
	void setup()
	{
		controller = new MyWorstEnemyController(namedParameterJdbcTemplateMock);

		namedParameterJdbcTemplateMock = Mockito.mock(NamedParameterJdbcTemplate.class);
		riotApiMock = Mockito.mock(RiotApi.class);
	}

	@Test
	@DisplayName("Test Hello World function!")
	void test_hello_world()
	{
		assertThat("Hello World!", is(controller.helloWorld()));
	}

	@Disabled
	@Test
	@DisplayName("Test Happy Path topFiveChampions Endpoint")
	void test_happy_path_topFiveChampions_endpoint()
	{
		Summoner summoner = new Summoner();
		summoner.setProfileIconId(554);
		summoner.setName("Zann Starfire");
		summoner.setPuuid("some encrypted value");
		summoner.setSummonerLevel(121);
		summoner.setRevisionDate(1579648985000L);
		summoner.setId("some encrypted value");
		summoner.setAccountId("some encrypted value");

//		Path path = new ("src/test/resources/json/matchlistJson.json")
//		String matchlistJson = Files.readString(null, StandardCharsets.US_ASCII);
//		MatchList matchlist = (new Gson()).fromJson(matchlistJson, MatchList.class);
		try
		{
			Mockito.when(riotApiMock.getSummonerByName("Zann Starfire")).thenReturn(summoner);

			assertThat("", is(controller.topFiveChampions("Zann Starfire")));
		}
		catch (RiotApiException e)
		{
			fail("Exception shouldn't get thrown!");
		}
	}
}
