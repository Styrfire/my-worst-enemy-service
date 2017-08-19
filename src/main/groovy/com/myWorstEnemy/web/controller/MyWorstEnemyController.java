package com.myWorstEnemy.web.controller;

import com.riot.api.RiotApi;
import com.riot.dto.Summoner.Summoner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyWorstEnemyController
{
	private RiotApi api = new RiotApi();

	@RequestMapping("/")
	public String helloWorld()
	{
		return "Hello world!";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/topFiveChampions/{summonerName}")
	public String topFiveChampions(@PathVariable String summonerName)
	{
		Summoner summoner = api.getSummonerByName(summonerName);

		return "The summoner id = " + summoner.getId();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/topFiveChampionsTest/{summonerName}")
	public String topFiveChamnpionsTest(@PathVariable String summonerName)
	{
		switch (summonerName)
		{
			case "Zann Starfire":
				return summonerName;
			case "Trekin":
				return summonerName;
			default:
				return summonerName;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{championId}")
	public String selectedChampion(@PathVariable String championId)
	{
		// last 10 matches
		// grade
		return "Champion id = " + championId;
	}
}
