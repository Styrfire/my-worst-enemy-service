package com.myWorstEnemy.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myWorstEnemy.service.StaticDataService;
import com.myWorstEnemy.service.domain.ChampionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonUtility
{
	private static Logger logger = LoggerFactory.getLogger(JsonUtility.class);

	public static String createSelectedChampionJson(int championId, int numOfGames, Map<Integer, ChampionInfo> enemyChampionInfoMap, StaticDataService staticDataService)
	{
		JsonObject selectedChampionJson = new JsonObject();
		selectedChampionJson.addProperty("name", staticDataService.getChampionByKey(championId).getName());
		selectedChampionJson.addProperty("title", staticDataService.getChampionByKey(championId).getTitle());
		selectedChampionJson.addProperty("loadingImageUrl", staticDataService.getChampionByKey(championId).getSplashArtUrl());
		selectedChampionJson.addProperty("numOfGames", numOfGames);

		JsonArray enemyChampions = new JsonArray();

		// convert championGamesMap into two arrays with matching indexes
		Integer[] championKeyList = new Integer[enemyChampionInfoMap.size()];
		ChampionInfo[] championInfoList = new ChampionInfo[enemyChampionInfoMap.size()];
		int index = 0;
		for (Map.Entry<Integer, ChampionInfo> mapEntry : enemyChampionInfoMap.entrySet())
		{
			championKeyList[index] = mapEntry.getKey();
			championInfoList[index] = mapEntry.getValue();
			index++;
		}

		// sort champions by number of games (bubble sort cus i'm lazy)
		boolean swapped;
		do
		{
			swapped = false;
			for (int i = 1; i < championKeyList.length; i++)
			{
				if ((championInfoList[i - 1].getGamesPlayed() + championInfoList[i - 1].getGamesBanned()) < (championInfoList[i].getGamesPlayed() + championInfoList[i].getGamesBanned()))
				{
					ChampionInfo temp = championInfoList[i - 1];
					championInfoList[i - 1] = championInfoList[i];
					championInfoList[i] = temp;
					Integer temp1 = championKeyList[i - 1];
					championKeyList[i - 1] = championKeyList[i];
					championKeyList[i] = temp1;
					swapped = true;
				}
			}
		} while (swapped);

		for (int i = 0; i < championKeyList.length; i++)
		{
			JsonObject currentChampion = new JsonObject();
			currentChampion.addProperty("name", staticDataService.getChampionByKey(championKeyList[i]).getName());
			currentChampion.addProperty("iconImageUrl", staticDataService.getChampionByKey(championKeyList[i]).getSplashArtUrl());
			currentChampion.addProperty("gamesPlayed", championInfoList[i].getGamesPlayed());
			currentChampion.addProperty("gamesLost", championInfoList[i].getGamesLost());
			currentChampion.addProperty("gamesBanned", championInfoList[i].getGamesBanned());

			enemyChampions.add(currentChampion);
		}

		JsonObject championJsonMock = new JsonObject();
		championJsonMock.add("selectedChampion", selectedChampionJson);
		championJsonMock.add("enemyChampions", enemyChampions);

		logger.info("selected mock champion results: " + championJsonMock.toString());

		return  championJsonMock.toString();
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
