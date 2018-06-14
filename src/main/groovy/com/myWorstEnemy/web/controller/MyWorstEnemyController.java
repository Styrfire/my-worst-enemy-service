package com.myWorstEnemy.web.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myWorstEnemy.service.domain.Champion;
import com.myWorstEnemy.service.domain.SomeList;
import com.myWorstEnemy.service.StaticDataService;
import com.riot.api.RiotApi;
import com.riot.dto.Match.MatchList;
import com.riot.dto.StaticData.ChampionList;
import com.riot.dto.Summoner.Summoner;
import com.riot.exception.RiotApiException;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class MyWorstEnemyController
{
	private RiotApi api = new RiotApi("RGAPI-541ffe90-4364-463b-ba56-ba2782b9e108");
	private static Logger logger = Logger.getLogger(MyWorstEnemyController.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Inject
	MyWorstEnemyController(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
	{
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@RequestMapping("/")
	public String helloWorld()
	{
		return "Hello world!";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/topFiveChampions/{summonerName}", produces=MediaType.APPLICATION_JSON_VALUE)
	public String topFiveChampions(@PathVariable String summonerName)
	{
		Summoner summoner;
		MatchList matchList;
		try
		{
			summoner = api.getSummonerByName(summonerName);

			// get ranked stats by summoner name
			matchList = api.getMatchListByAccountId(summoner.getAccountId());
		} catch (RiotApiException e)
		{
			System.out.println(e.getMessage());
			return e.getMessage();
		}

		Map<Integer, Integer> championGamesMap = new HashMap<>();
		if (matchList != null)
		{
			for (int i = 0; i < matchList.getEndIndex(); i++)
			{
				int queue = 420;
				// 400 = draft pick
				// 420 = ranked solo
				if (matchList.getMatches().get(i).getQueue() == queue)
				{
					if (queue == 400)
						logger.info("index: " + i + " was a draft game!");
					else if (queue == 420)
						logger.info("index: " + i + " was a ranked game!");

					// if champion is in the list
					if (championGamesMap.containsKey(matchList.getMatches().get(i).getChampion()))
					{
						Integer games = championGamesMap.get(matchList.getMatches().get(i).getChampion());
						championGamesMap.replace(matchList.getMatches().get(i).getChampion(), ++games);
					}
					else
					{
						championGamesMap.put(matchList.getMatches().get(i).getChampion(), 1);
					}
				}
			}
		} else
			System.out.println("matchList is null!");

		// convert championGamesMap into two arrays with matching indexes
		Integer[] listOfChampionIds = new Integer[championGamesMap.size()];
		Integer[] listOfChampionNumOfGames = new Integer[championGamesMap.size()];
		int index = 0;
		for (Map.Entry<Integer, Integer> mapEntry : championGamesMap.entrySet())
		{
			listOfChampionIds[index] = mapEntry.getKey();
			listOfChampionNumOfGames[index] = mapEntry.getValue();
			index++;
		}

		// sort champions by number of games (bubble sort cus i'm lazy)
		boolean swapped;
		do
		{
			swapped = false;
			for (int i = 1; i < listOfChampionIds.length; i++)
			{
				if (listOfChampionNumOfGames[i - 1] < listOfChampionNumOfGames[i])
				{
					Integer temp = listOfChampionNumOfGames[i - 1];
					listOfChampionNumOfGames[i - 1] = listOfChampionNumOfGames[i];
					listOfChampionNumOfGames[i] = temp;
					temp = listOfChampionIds[i - 1];
					listOfChampionIds[i - 1] = listOfChampionIds[i];
					listOfChampionIds[i] = temp;
					swapped = true;
				}
			}
		} while (swapped);

		int upperBound = 5;
		if (listOfChampionIds.length < 5)
			upperBound = listOfChampionIds.length;

		StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);

		JsonArray championJsonArr = new JsonArray();
		try
		{
			for (int i = 0; i < upperBound; i++)
			{
				Champion champion = staticDataService.getChampionById(listOfChampionIds[i]);
				JsonObject championJson = new JsonObject();
				championJson.addProperty("name", champion.getName());
				championJson.addProperty("title", champion.getTitle());
				championJson.addProperty("splashArtUrl", champion.getSplashArtUrl().replaceAll("splash", "loading"));
				championJson.addProperty("id", champion.getId());
				championJson.addProperty("numOfGames", listOfChampionNumOfGames[i]);
				championJsonArr.add(championJson);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return e.getMessage();
		}

		// get champions by id's
		JsonObject topFiveChampions = new JsonObject();
		topFiveChampions.add("champions", championJsonArr);
		System.out.println(topFiveChampions.toString());

		return topFiveChampions.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{championId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public String selectedChampion(@PathVariable String championId)
	{
		// last 10 matches
		// grade
		logger.info("{\"championId\":\"" + championId + "\"}");
		return "{\"championId\":\"" + championId + "\"}";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/loadExampleTable")
	public String loadExampleTable()
	{
		StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);
		SomeList someList = new SomeList();
		someList.setExampleList(staticDataService.getExampleInfo());
		return someList.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/loadChampionsTable")
	public boolean loadChampionsTable()
	{
		try
		{
			StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);
			ChampionList championList = api.getStaticChampionInfo();
			for (Map.Entry<String, com.riot.dto.StaticData.Champion> entry : championList.getData().entrySet())
			{
//				String splashArtUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + entry.getValue().getKey() + "_0.jpg";
				String splashArtUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + entry.getValue().getKey() + "_0.jpg";
				if (!staticDataService.insertIntoChampions(entry.getValue().getId(), entry.getValue().getName(), entry.getValue().getTitle(), entry.getValue().getKey(), splashArtUrl))
					logger.info("(" + entry.getValue().getId() +", "+ entry.getValue().getName() + ", " + entry.getValue().getTitle() + ", " + entry.getValue().getKey() + ", " + splashArtUrl + ") was not inserted!");
				else
					logger.info("(" + entry.getValue().getId() +", "+ entry.getValue().getName() + ", " + entry.getValue().getTitle() + ", " + entry.getValue().getKey() + ", " + splashArtUrl + ") was inserted!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
}