package com.myWorstEnemy.service.domain

class ChampionInfo implements Comparable<ChampionInfo> {
	String iconImageUrl
	int gamesPlayed
	int gamesLost
	int gamesBanned

	@Override
	int compareTo(ChampionInfo anotherChampionInfo)
	{
		// if this games played + games banned < another games played + games banned return -1
		// else if this games played + games banned > another games played + games banned return 1
		// else return 0
		return ((this.gamesPlayed + this.gamesBanned) < (anotherChampionInfo.gamesPlayed + anotherChampionInfo.gamesBanned)) ? -1 : (((this.gamesPlayed + this.gamesBanned) > (anotherChampionInfo.gamesPlayed + anotherChampionInfo.gamesBanned)) ? 1 : 0)
	}
}
