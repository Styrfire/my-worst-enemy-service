package com.myWorstEnemy.web.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.riot.api.RiotApi;
import com.riot.dto.Match.MatchList;
import com.riot.dto.StaticData.Champion;
import com.riot.dto.StaticData.ChampionList;
import com.riot.dto.Summoner.Summoner;
import com.riot.exception.RiotApiException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MyWorstEnemyController
{
	private RiotApi api = new RiotApi("RGAPI-40af5000-4ad4-4798-82c5-ec377ca3d5dc");

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
			System.out.println(e.getErrorMsg());
			return e.getErrorMsg();
		}

		Map<Integer, Integer> championGamesMap = new HashMap<>();
		List<Integer> listOfChampionIds = new ArrayList<>();
		if (matchList != null)
		{
			for (int i = 0; i < matchList.getEndIndex(); i++)
			{
				if (matchList.getMatches().get(i).getQueue() == 420)
				{
					System.out.println("index: " + i + " was a ranked game!");
					// if champion is in the list
					if (championGamesMap.containsKey(matchList.getMatches().get(i).getChampion()))
					{
						Integer games = championGamesMap.get(matchList.getMatches().get(i).getChampion());
						championGamesMap.replace(matchList.getMatches().get(i).getChampion(), ++games);
					} else
					{
						championGamesMap.put(matchList.getMatches().get(i).getChampion(), 1);
						listOfChampionIds.add(matchList.getMatches().get(i).getChampion());
					}
				}
			}
		} else
			System.out.println("matchList is null!");

		List<Integer> listOfChampionNumOfGames = new ArrayList<>();

		championGamesMap.entrySet();

		// sort champions by number of games (bubble sort cus i'm lazy)
		for (Integer listOfChampionId : listOfChampionIds)
			listOfChampionNumOfGames.add(championGamesMap.get(listOfChampionId));

		// sort champions by number of games (bubble sort cus i'm lazy)
		boolean swapped;
		do
		{
			swapped = false;
			for (int i = 1; i < listOfChampionIds.size(); i++)
			{
				if (listOfChampionNumOfGames.get(i - 1) < listOfChampionNumOfGames.get(i))
				{
					Integer temp = listOfChampionNumOfGames.get(i - 1);
					listOfChampionNumOfGames.set(i - 1, listOfChampionNumOfGames.get(i));
					listOfChampionNumOfGames.set(i, temp);
					temp = listOfChampionIds.get(i - 1);
					listOfChampionIds.set(i - 1, listOfChampionIds.get(i));
					listOfChampionIds.set(i, temp);
					swapped = true;
				}
			}
		} while (swapped);

		List<Champion> championList = new ArrayList<>();
		Champion zac = new Champion();
		zac.setTitle("the Secret Weapon");
		zac.setName("Zac");
		zac.setKey("Zac");
		zac.setId(154);
		Champion nocturne = new Champion();
		nocturne.setTitle("the Eternal Nightmare");
		nocturne.setName("Nocturne");
		nocturne.setKey("Nocturne");
		nocturne.setId(56);
		Champion jhin = new Champion();
		jhin.setTitle("the Virtuoso");
		jhin.setName("Jhin");
		jhin.setKey("Jhin");
		jhin.setId(202);
		Champion sejuani = new Champion();
		sejuani.setTitle("Fury of the North");
		sejuani.setName("Sejuani");
		sejuani.setKey("Sejuani");
		sejuani.setId(113);
		Champion amumu = new Champion();
		amumu.setTitle("the Sad Mummy");
		amumu.setName("Amumu");
		amumu.setKey("Amumu");
		amumu.setId(32);

		championList.add(zac);
		championList.add(nocturne);
		championList.add(jhin);
		championList.add(sejuani);
		championList.add(amumu);

		JsonArray champions = new JsonArray();
//		try
//		{
			for (int i = 0; i < 5; i++)
			{
//				ChampionList championList = new ChampionList();
//				championList = api.getStaticChampionInfoById(listOfChampionIds.get(i));
				JsonObject champion = new JsonObject();
				champion.addProperty("title", championList.get(i).getTitle());
				champion.addProperty("name", championList.get(i).getName());
				champion.addProperty("key", championList.get(i).getKey());
				champion.addProperty("id", championList.get(i).getId());
				champion.addProperty("numOfGames", championGamesMap.get(championList.get(i).getId()));
				champions.add(champion);
			}
//		} catch (RiotApiException e)
//		{
//			System.out.println(e.getErrorMsg());
//			return e.getErrorMsg();
//		}

		// get champions by id's
		JsonObject topFiveChampions = new JsonObject();
		topFiveChampions.add("champions", champions);
		System.out.println(topFiveChampions.toString());

//		JsonObject obj = new JsonObject();
//		obj.addProperty("summonerId", summoner.getId());
//		System.out.println(obj.toString());

		return topFiveChampions.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{championId}")
	public String selectedChampion(@PathVariable String championId)
	{
		// last 10 matches
		// grade
		return "Champion id = " + championId;
	}
}
