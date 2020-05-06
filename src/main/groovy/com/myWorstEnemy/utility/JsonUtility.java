package com.myWorstEnemy.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myWorstEnemy.service.StaticDataService;
import com.myWorstEnemy.service.domain.ChampionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtility
{
	private static Logger logger = LoggerFactory.getLogger(JsonUtility.class);

	public static String createSelectedChampionJson(int championId, int numOfGames, LinkedHashMap<Integer, ChampionInfo> enemyChampionInfoMap, StaticDataService staticDataService)
	{
		JsonObject selectedChampionJson = new JsonObject();
		selectedChampionJson.addProperty("name", staticDataService.getChampionByKey(championId).getName());
		selectedChampionJson.addProperty("title", staticDataService.getChampionByKey(championId).getTitle());
		selectedChampionJson.addProperty("loadingImageUrl", staticDataService.getChampionByKey(championId).getLoadingImgUrl());
		selectedChampionJson.addProperty("numOfGames", numOfGames);

		JsonArray enemyChampions = new JsonArray();

		LinkedHashMap<Integer, ChampionInfo> sortedEnemyChampionInfoMap = new LinkedHashMap<>();

		enemyChampionInfoMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedEnemyChampionInfoMap.put(x.getKey(), x.getValue()));

		for (Map.Entry<Integer, ChampionInfo> enemyChampionInfoEntry : sortedEnemyChampionInfoMap.entrySet())
		{
			JsonObject currentChampion = new JsonObject();
			currentChampion.addProperty("name", staticDataService.getChampionByKey(enemyChampionInfoEntry.getKey()).getName());
			currentChampion.addProperty("iconImageUrl", staticDataService.getChampionByKey(enemyChampionInfoEntry.getKey()).getIconImgUrl());
			currentChampion.addProperty("gamesPlayed", enemyChampionInfoEntry.getValue().getGamesPlayed());
			currentChampion.addProperty("gamesLost", enemyChampionInfoEntry.getValue().getGamesLost());
			currentChampion.addProperty("gamesBanned", enemyChampionInfoEntry.getValue().getGamesBanned());

			enemyChampions.add(currentChampion);
		}

		JsonObject championJson = new JsonObject();
		championJson.add("selectedChampion", selectedChampionJson);
		championJson.add("enemyChampions", enemyChampions);

		logger.info("selected mock champion results: " + championJson.toString());

		return  championJson.toString();
	}

	public static String createSelectedChampionJsonMock()
	{
		JsonObject selectedChampionJsonMock = new JsonObject();
		selectedChampionJsonMock.addProperty("name", "Zac");
		selectedChampionJsonMock.addProperty("title", "the Secret Weapon");
		selectedChampionJsonMock.addProperty("loadingImageUrl", "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Zac_0.jpg");
		selectedChampionJsonMock.addProperty("numOfGames", "13");

		JsonObject yasuo = new JsonObject();
		yasuo.addProperty("name", "Yasuo");
		yasuo.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Yasuo.png");
		yasuo.addProperty("gamesPlayed", "1");
		yasuo.addProperty("gamesLost", "1");
		yasuo.addProperty("gamesBanned", "9");

		JsonObject vayne = new JsonObject();
		vayne.addProperty("name", "Vayne");
		vayne.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Vayne.png");
		vayne.addProperty("gamesPlayed", "2");
		vayne.addProperty("gamesLost", "1");
		vayne.addProperty("gamesBanned", "2");

		JsonObject masterYi = new JsonObject();
		masterYi.addProperty("name", "Master Yi");
		masterYi.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/MasterYi.png");
		masterYi.addProperty("gamesPlayed", "2");
		masterYi.addProperty("gamesLost", "2");
		masterYi.addProperty("gamesBanned", "4");

		JsonObject akali = new JsonObject();
		akali.addProperty("name", "Akali");
		akali.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Akali.png");
		akali.addProperty("gamesPlayed", "3");
		akali.addProperty("gamesLost", "2");
		akali.addProperty("gamesBanned", "9");

		JsonObject jhin = new JsonObject();
		jhin.addProperty("name", "Jhin");
		jhin.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Jhin.png");
		jhin.addProperty("gamesPlayed", "5");
		jhin.addProperty("gamesLost", "2");
		jhin.addProperty("gamesBanned", "0");

		JsonObject urgot = new JsonObject();
		urgot.addProperty("name", "Urgot");
		urgot.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Urgot.png");
		urgot.addProperty("gamesPlayed", "0");
		urgot.addProperty("gamesLost", "0");
		urgot.addProperty("gamesBanned", "13");

		JsonObject leblanc = new JsonObject();
		leblanc.addProperty("name", "Leblanc");
		leblanc.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Leblanc.png");
		leblanc.addProperty("gamesPlayed", "2");
		leblanc.addProperty("gamesLost", "1");
		leblanc.addProperty("gamesBanned", "5");

		JsonObject xinZhao = new JsonObject();
		xinZhao.addProperty("name", "Xin Zhao");
		xinZhao.addProperty("iconImageUrl", "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/XinZhao.png");
		xinZhao.addProperty("gamesPlayed", "7");
		xinZhao.addProperty("gamesLost", "3");
		xinZhao.addProperty("gamesBanned", "3");

		JsonArray enemyChampionsMock = new JsonArray();
		enemyChampionsMock.add(yasuo);
		enemyChampionsMock.add(vayne);
		enemyChampionsMock.add(masterYi);
		enemyChampionsMock.add(akali);
		enemyChampionsMock.add(jhin);
		enemyChampionsMock.add(urgot);
		enemyChampionsMock.add(leblanc);
		enemyChampionsMock.add(xinZhao);

		JsonObject championJsonMock = new JsonObject();
		championJsonMock.add("selectedChampion", selectedChampionJsonMock);
		championJsonMock.add("enemyChampions", enemyChampionsMock);

		logger.info("selected mock champion results: " + championJsonMock.toString());

		return  championJsonMock.toString();
	}
}
