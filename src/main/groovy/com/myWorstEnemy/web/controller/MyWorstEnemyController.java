package com.myWorstEnemy.web.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.myWorstEnemy.service.domain.Champion;
import com.myWorstEnemy.service.domain.ChampionInfo;
import com.myWorstEnemy.service.StaticDataService;
import com.myWorstEnemy.utility.JsonUtility;
import com.riot.api.RiotApi;
import com.riot.dto.Match.Match;
import com.riot.dto.Match.MatchList;
import com.riot.dto.Match.Participant;
import com.riot.dto.Match.TeamBans;
import com.riot.dto.Match.TeamStats;
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
	@Value("${queue}")
	int queue;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private RiotApi api;

	private static Logger logger = LoggerFactory.getLogger(MyWorstEnemyController.class);

	@Inject
	MyWorstEnemyController(NamedParameterJdbcTemplate namedParameterJdbcTemplate)
	{
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.api = new RiotApi(System.getProperty("api.key"));
	}

	@RequestMapping("/")
	String helloWorld()
	{
		return "Hello World!";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/topFiveChampions/{summonerName}", produces=MediaType.APPLICATION_JSON_VALUE)
	String topFiveChampions(@PathVariable String summonerName)
	{
		Summoner summoner;
		MatchList matchList;
		Map<Integer, Integer> championGamesMap = new LinkedHashMap<>();
		try
		{
			summoner = api.getSummonerByName(summonerName);

			int i = 0;
			boolean loop = true;

			while(loop)
			{
				// get matchlist by summoner name per 100
				matchList = api.getMatchListByAccountId(summoner.getAccountId(), null, null,
						null, null, null, i*100, null);

				if (matchList != null && matchList.getMatches().size() != 0)
				{
					for (int j = 0; j < matchList.getMatches().size(); j++)
					{
						// if timestamp is older than the start of patch 10.1 + NA1 offset, break
						// (noted in https://github.com/CommunityDragon/Data/blob/master/patches.json) times in json are in seconds, need miliseconds
						if (matchList.getMatches().get(j).getTimestamp() < 1578488400000L)
						{
							logger.info("matchList.getMatches().get(j).getTimestamp() = " + matchList.getMatches().get(j).getTimestamp());
							loop = false;
							break;
						}

						if (matchList.getMatches().get(j).getQueue() == queue)
						{
							if (queue == 400)
								logger.debug("index: " + j + " was a draft game!");
							else if (queue == 420)
								logger.debug("index: " + j + " was a ranked game!");

							// if champion is in the list
							if (championGamesMap.containsKey(matchList.getMatches().get(j).getChampion()))
							{
								Integer games = championGamesMap.get(matchList.getMatches().get(j).getChampion());
								championGamesMap.replace(matchList.getMatches().get(j).getChampion(), ++games);
							} else
							{
								championGamesMap.put(matchList.getMatches().get(j).getChampion(), 1);
							}
						}
					}

					i++;
				}
				else
				{
					logger.info("matchList is null!");
					break;
				}
			}
		} catch (RiotApiException e)
		{
			logger.error(e.getMessage());
			return e.getMessage();
		}

		// sort into new LinkedHashMap
		LinkedHashMap<Integer, Integer> sortedChampionsGameMap = new LinkedHashMap<>();

		championGamesMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedChampionsGameMap.put(x.getKey(), x.getValue()));

		int upperBound = 5;
		if (sortedChampionsGameMap.size() < upperBound)
			upperBound = sortedChampionsGameMap.size();

		StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);

		JsonArray championJsonArr = new JsonArray();
		try
		{
			int i = 0;
			for (Map.Entry<Integer, Integer> championEntry : sortedChampionsGameMap.entrySet())
			{
				// enforce upperBound
				if (i >= upperBound)
					break;
				else
					i++;

				Champion champion = staticDataService.getChampionByKey(championEntry.getKey());
				JsonObject championJson = new JsonObject();
				championJson.addProperty("name", champion.getName());
				championJson.addProperty("title", champion.getTitle());
				championJson.addProperty("splashArtUrl", champion.getLoadingImgUrl());
				championJson.addProperty("id", champion.getId());
				championJson.addProperty("numOfGames", championEntry.getValue());
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

	// todo: get icon image urls instead of splash arts
	@RequestMapping(method = RequestMethod.GET, value = "/selectedChampion/{summonerName}/{championId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public String selectedChampion(@PathVariable String summonerName, @PathVariable int championId)
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

		StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);

		Match match;
		Map<Integer, ChampionInfo> enemyChampionInfoMap = new HashMap<>();
		int numOfGames = 0;
		if (matchList != null)
		{
			for (int i = 0; i < matchList.getEndIndex(); i++)
			{
				if ((matchList.getMatches().get(i).getQueue() == queue) && (matchList.getMatches().get(i).getChampion() == championId))
				{
					numOfGames++;
					if (queue == 400)
						logger.info("index: " + i + " was a draft game!");
					else if (queue == 420)
						logger.info("index: " + i + " was a ranked game!");

					try
					{
						int teamId = 0;
						match = api.getMatchByMatchId(matchList.getMatches().get(i).getGameId());

						List<Participant> participants = match.getParticipants();
						// get team id for selected champion
						for (Participant participant : participants)
							if (participant.getChampionId() == championId)
							{
								teamId = participant.getTeamId();
								logger.info("Team Id for selected champion is " + teamId);
							}

						if (teamId == 0)
							throw new RiotApiException("Could not find team for selected champion!");

						// did team with id 100 win?
						boolean blueTeamWin = (match.getTeams().get(0).getTeamId() == 100 && match.getTeams().get(0).getWin().equals("Win")) ||
								(!(match.getTeams().get(0).getTeamId() == 100) && !(match.getTeams().get(0).getWin().equals("Win")));

						// if participant is an enemy of the selected champion, add them to the map if they don't
						// already exist, otherwise, bump numbers
						for (Participant participant : participants)
						{
							if (participant.getTeamId() != teamId)
							{
								// if champion id doesn't exist in enemyChampionInfoMap, add it
								if (!enemyChampionInfoMap.containsKey(participant.getChampionId()))
								{
									logger.debug("Added champion id " + participant.getChampionId() + " to enemyChampionInfoMap (unbanned champion)");
									ChampionInfo enemyChampionInfo = new ChampionInfo();
									enemyChampionInfo.setIconImageUrl(staticDataService.getChampionByKey(participant.getChampionId()).getIconImgUrl());
									enemyChampionInfo.setGamesPlayed(1);
									if ((participant.getTeamId() == 100 && blueTeamWin) || (participant.getTeamId() == 200 && !blueTeamWin))
										enemyChampionInfo.setGamesLost(1);
									else
										enemyChampionInfo.setGamesLost(0);
									enemyChampionInfo.setGamesBanned(0);
									enemyChampionInfoMap.put(participant.getChampionId(), enemyChampionInfo);
								}
								else
								{
									logger.debug("Updated champion id " + participant.getChampionId() + " in enemyChampionInfoMap (unbanned champion)");
									enemyChampionInfoMap.get(participant.getChampionId()).setGamesPlayed(enemyChampionInfoMap.get(participant.getChampionId()).getGamesPlayed() + 1);
									if ((participant.getTeamId() == 100 && blueTeamWin) || (participant.getTeamId() == 200 && !blueTeamWin))
										enemyChampionInfoMap.get(participant.getChampionId()).setGamesLost(enemyChampionInfoMap.get(participant.getChampionId()).getGamesLost() + 1);
								}
							}
						}

						// if
						for (TeamStats team : match.getTeams())
							for (TeamBans ban : team.getBans())
							{
								// if champion id doesn't exist in enemyChampionInfoMap, add it, unless it's -1 (which is no ban) to which you ignore it
								if (ban.getChampionId() != -1)
								{
									if (!enemyChampionInfoMap.containsKey(ban.getChampionId()))
									{
										logger.debug("Added champion id " + ban.getChampionId() + " to enemyChampionInfoMap (banned champion)");
										ChampionInfo enemyChampionInfo = new ChampionInfo();
										enemyChampionInfo.setIconImageUrl(staticDataService.getChampionByKey(ban.getChampionId()).getIconImgUrl());
										enemyChampionInfo.setGamesPlayed(0);
										enemyChampionInfo.setGamesLost(0);
										enemyChampionInfo.setGamesBanned(1);
										enemyChampionInfoMap.put(ban.getChampionId(), enemyChampionInfo);
									} else // else bump up the games banned by one
									{
										logger.debug("Updated champion id " + ban.getChampionId() + " in enemyChampionInfoMap (banned champion)");
										enemyChampionInfoMap.get(ban.getChampionId()).setGamesBanned(enemyChampionInfoMap.get(ban.getChampionId()).getGamesBanned() + 1);
									}
								}
								else
									logger.debug("No champion was banned for this person!");
							}
					}
					catch (RiotApiException e)
					{
						e.printStackTrace();
						return e.getMessage();
					}
				}
			}
		} else
			logger.error("matchList is null!");

		for (Map.Entry<Integer, ChampionInfo> entry : enemyChampionInfoMap.entrySet())
		{
			logger.info("Key = " + entry.getKey());
			logger.info("Value.iconImageURL = " + entry.getValue().getIconImageUrl());
			logger.info("Value.gamesPlayed = " + entry.getValue().getGamesPlayed());
			logger.info("Value.gamesLost = " + entry.getValue().getGamesLost());
			logger.info("Value.gamesBanned = " + entry.getValue().getGamesBanned());
		}

		return JsonUtility.createSelectedChampionJson(championId, numOfGames, enemyChampionInfoMap, staticDataService);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getTeamData")
	public String getTeamData()
	{
		return null;
	}

	// todo: add image urls (and splash art if we're feeling fancy)
	@RequestMapping(method = RequestMethod.GET, value = "/loadChampionsTable")
	public boolean loadChampionsTable()
	{
		try
		{
			StaticDataService staticDataService = new StaticDataService(namedParameterJdbcTemplate);

			String patchVersion = "10.5.1";

			// Delete all rows from champions table
			if (!staticDataService.deleteChampionsTableRows())
				throw new Exception("Something went wrong deleting all the rows from the champions table");

			// Create and fill champions table with data
			ChampionList championList = api.getStaticChampionInfo(patchVersion);
			for (Map.Entry<String, com.riot.dto.StaticData.Champion> entry : championList.getData().entrySet())
			{
				String splashImgUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + entry.getValue().getId() + "_0.jpg";
				String loadingImgUrl = "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + entry.getValue().getId() + "_0.jpg";
				String iconImgUrl = "http://ddragon.leagueoflegends.com/cdn/" + patchVersion + "/img/champion/" + entry.getValue().getId() + ".png";

				if (!staticDataService.insertIntoChampions(entry.getValue().getId(), entry.getValue().getName(), entry.getValue().getTitle(), entry.getValue().getKey(), splashImgUrl, loadingImgUrl, iconImgUrl))
					logger.info("(" + entry.getValue().getId() +", "+ entry.getValue().getName() + ", " + entry.getValue().getTitle() + ", " + entry.getValue().getKey() + ", " + splashImgUrl + ", " + loadingImgUrl + ", " + iconImgUrl + ") was not inserted!");
				else
					logger.info("(" + entry.getValue().getId() +", "+ entry.getValue().getName() + ", " + entry.getValue().getTitle() + ", " + entry.getValue().getKey() + ", " + splashImgUrl + ", " + loadingImgUrl + ", " + iconImgUrl + ") was inserted!");
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