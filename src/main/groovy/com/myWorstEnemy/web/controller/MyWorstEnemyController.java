package com.myWorstEnemy.web.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myWorstEnemy.service.domain.Champion;
import com.myWorstEnemy.service.domain.SomeList;
import com.myWorstEnemy.service.StaticDataService;
import com.riot.api.RiotApi;
import com.riot.dto.Match.Match;
import com.riot.dto.Match.MatchList;
import com.riot.dto.StaticData.ChampionList;
import com.riot.dto.Summoner.Summoner;
import com.riot.exception.RiotApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class MyWorstEnemyController
{
	//todo DO NOT COMMIT THIS
	private RiotApi api = new RiotApi("API_KEY");
	private static Logger logger = LoggerFactory.getLogger(MyWorstEnemyController.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Value("${myWorstEnemy.useMock:false}")
	private Boolean useMock;

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
		if (!useMock)
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
				logger.error(e.getMessage());
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
						} else
						{
							championGamesMap.put(matchList.getMatches().get(i).getChampion(), 1);
						}
					}
				}
			} else
				logger.error("matchList is null!");

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
			} catch (Exception e)
			{
				e.printStackTrace();
				return e.getMessage();
			}

			// get champions by id's
			JsonObject topFiveChampions = new JsonObject();
			topFiveChampions.add("champions", championJsonArr);
			logger.error(topFiveChampions.toString());

			return topFiveChampions.toString();
		}
		else
		{
			logger.info("Using mocked version of /topFiveChampions with summonerName = Zann Starfire");
			Integer[] listOfChampionIds = {154, 5, 56, 32, 98};
			Integer[] listOfChampionNumOfGames = {13, 11, 10, 7, 4, 4};

			StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);

			JsonArray championJsonArr = new JsonArray();
			try
			{
				for (int i = 0; i < 5; i++)
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
			} catch (Exception e)
			{
				e.printStackTrace();
				return e.getMessage();
			}

			// get champions by id's
			JsonObject topFiveChampions = new JsonObject();
			topFiveChampions.add("champions", championJsonArr);
			logger.info(topFiveChampions.toString());

			return topFiveChampions.toString();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{summonerName}/{championId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public String selectedChampion(@PathVariable String summonerName, @PathVariable int championId)
	{
		if (!useMock)
		{

			Summoner summoner;
			MatchList matchList;
			try
			{
				summoner = api.getSummonerByName(summonerName.replace('+', ' '));

				// get ranked stats by summoner name
				matchList = api.getMatchListByAccountId(summoner.getAccountId());
			} catch (RiotApiException e)
			{
				logger.error(e.getMessage());
				return e.getMessage();
			}

			Match match = new Match();
			if (matchList != null)
			{
				for (int i = 0; i < matchList.getEndIndex(); i++)
				{
					int queue = 420;
					// 400 = draft pick
					// 420 = ranked solo
					if ((matchList.getMatches().get(i).getQueue() == queue) && (matchList.getMatches().get(i).getChampion() == championId))
					{
						if (queue == 400)
							logger.info("index: " + i + " was a draft game!");
						else if (queue == 420)
							logger.info("index: " + i + " was a ranked game!");

//						try
//						{
//							match = api.getMatchByMatchId(matchList.getMatches().get(i).getGameId());
//						}
//						catch (RiotApiException e)
//						{
//							e.printStackTrace();
//							return e.getMessage();
//						}
					}
				}
			} else
				logger.error("matchList is null!");

			JsonObject selectedChampionJson = new JsonObject();
			selectedChampionJson.addProperty("name", "Zac");
			selectedChampionJson.addProperty("title", "the Secret Weapon");
			selectedChampionJson.addProperty("loadingImageUrl", "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Zac_0.jpg");
			selectedChampionJson.addProperty("numOfGames", "13");

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

			JsonArray enemyChampions = new JsonArray();
			enemyChampions.add(yasuo);
			enemyChampions.add(vayne);
			enemyChampions.add(masterYi);
			enemyChampions.add(akali);
			enemyChampions.add(jhin);
			enemyChampions.add(urgot);
			enemyChampions.add(leblanc);
			enemyChampions.add(xinZhao);

			JsonObject championJson = new JsonObject();
			championJson.add("selectedChampion", selectedChampionJson);
			championJson.add("enemyChampions", enemyChampions);

			logger.info("selected champion results: " + championJson.toString());
			return championJson.toString();
		}
		else
		{
			logger.info("Using mocked version of /selectedChampion with summonerName = Zann Starfire and championId = 154");
			JsonObject selectedChampionJson = new JsonObject();
			selectedChampionJson.addProperty("name", "Zac");
			selectedChampionJson.addProperty("title", "the Secret Weapon");
			selectedChampionJson.addProperty("loadingImageUrl", "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/Zac_0.jpg");
			selectedChampionJson.addProperty("numOfGames", "13");

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

			JsonArray enemyChampions = new JsonArray();
			enemyChampions.add(yasuo);
			enemyChampions.add(vayne);
			enemyChampions.add(masterYi);
			enemyChampions.add(akali);
			enemyChampions.add(jhin);
			enemyChampions.add(urgot);
			enemyChampions.add(leblanc);
			enemyChampions.add(xinZhao);

			JsonObject championJson = new JsonObject();
			championJson.add("selectedChampionName", selectedChampionJson);
			championJson.add("enemyChampions", enemyChampions);

			logger.info("selected champion results: " + championJson.toString());
			return championJson.toString();
		}
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