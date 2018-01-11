package com.myWorstEnemy.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.riot.api.RiotApi;
import com.riot.dto.Match.MatchList;
import com.riot.dto.StaticData.Champion;
import com.riot.dto.StaticData.ChampionList;
import com.riot.dto.Summoner.Summoner;
import com.riot.exception.RiotApiException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.util.*;

@RestController
public class MyWorstEnemyController
{
	private RiotApi api = new RiotApi("RGAPI-f23321ad-32f6-418a-9af3-8a0ff83ecfcf");

	@RequestMapping("/")
	public String helloWorld()
	{
		return "Hello world!";
	}

	@CrossOrigin(origins = "*")
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
				if (matchList.getMatches().get(i).getQueue() == 400)
				{
					System.out.println("index: " + i + " was a ranked game!");
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
		JsonArray championJsonArr = new JsonArray();
		try
		{
			for (int i = 0; i < upperBound; i++)
			{
				Champion champion = api.getStaticChampionInfoById(listOfChampionIds[i]);
				JsonObject championJson = new JsonObject();
				championJson.addProperty("name", champion.getName());
				championJson.addProperty("title", champion.getTitle());
				championJson.addProperty("key", champion.getKey());
				championJson.addProperty("id", champion.getId());
				championJson.addProperty("numOfGames", listOfChampionNumOfGames[i]);
				championJsonArr.add(championJson);
			}
		} catch (RiotApiException e)
		{
			System.out.println(e.getMessage());
			return e.getMessage();
		}

		// get champions by id's
		JsonObject topFiveChampions = new JsonObject();
		topFiveChampions.add("champions", championJsonArr);
		System.out.println(topFiveChampions.toString());

		return topFiveChampions.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{championId}")
	public String selectedChampion(@PathVariable String championId)
	{
		// last 10 matches
		// grade
		return "Champion id = " + championId;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/loadChampionTable")
	public boolean loadChampionTable()
	{
		try
		{
			ClassLoader classLoader = getClass().getClassLoader();
			ChampionList championList = (new Gson()).fromJson(new InputStreamReader(classLoader.getResourceAsStream("json/getStaticChampionData.json")), ChampionList.class);
			championList.getData().get("154");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return true;
	}
}