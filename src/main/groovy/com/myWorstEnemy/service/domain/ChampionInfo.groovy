package com.myWorstEnemy.service.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ChampionInfo implements Comparable<ChampionInfo> {
	String iconImageUrl
	int gamesPlayed
	int gamesLost
	int gamesBanned
	int totalPossibleGames

	private static Logger logger = LoggerFactory.getLogger(ChampionInfo)

	@Override
	int compareTo(ChampionInfo anotherChampionInfo)
	{
		// avoid dividing by 0
		if ((this.totalPossibleGames == 0 || this.gamesPlayed == 0) && (anotherChampionInfo.totalPossibleGames == 0 || anotherChampionInfo.gamesPlayed == 0))
		{
			logger.debug("this and anotherChampionInfo had a divide by 0")

			return 0
		}
		else if (this.totalPossibleGames == 0 || this.gamesPlayed == 0)
		{
			logger.debug("this had a divide by 0")

			if (0 < ((anotherChampionInfo.gamesPlayed/(anotherChampionInfo.totalPossibleGames - anotherChampionInfo.gamesBanned))*(anotherChampionInfo.gamesLost / anotherChampionInfo.gamesPlayed)))
				return -1
			else if (0 > ((anotherChampionInfo.gamesPlayed/(anotherChampionInfo.totalPossibleGames - anotherChampionInfo.gamesBanned))*(anotherChampionInfo.gamesLost / anotherChampionInfo.gamesPlayed)))
				return 1
			else
				return 0
		}
		else if (anotherChampionInfo.totalPossibleGames == 0 || anotherChampionInfo.gamesPlayed == 0)
		{
			logger.debug("anotherChampionInfo had a divide by 0")

			if (((this.gamesPlayed / (this.totalPossibleGames - this.gamesBanned))*(this.gamesLost / this.gamesPlayed)) < 0)
				return -1
			else if (((this.gamesPlayed / (this.totalPossibleGames - this.gamesBanned))*(this.gamesLost / this.gamesPlayed)) > 0)
				return 1
			else
				return 0
		}
		else {
			logger.debug("base case for compareTo")

			// if this chance of being picked * chance of beating you < another chance of being picked * chance of beating you -1
			// if this chance of being picked * chance of beating you > another chance of being picked * chance of beating you 1
			// else return 0
			if (((this.gamesPlayed / (this.totalPossibleGames - this.gamesBanned)) * (this.gamesLost / this.gamesPlayed)) < ((anotherChampionInfo.gamesPlayed / (anotherChampionInfo.totalPossibleGames - anotherChampionInfo.gamesBanned)) * (anotherChampionInfo.gamesLost / anotherChampionInfo.gamesPlayed)))
				return -1
			else if (((this.gamesPlayed / (this.totalPossibleGames - this.gamesBanned)) * (this.gamesLost / this.gamesPlayed)) > ((anotherChampionInfo.gamesPlayed / (anotherChampionInfo.totalPossibleGames - anotherChampionInfo.gamesBanned)) * (anotherChampionInfo.gamesLost / anotherChampionInfo.gamesPlayed)))
				return 1
			else
				return 0
		}
	}
}
