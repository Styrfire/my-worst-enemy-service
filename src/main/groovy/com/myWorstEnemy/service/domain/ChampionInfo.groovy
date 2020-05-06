package com.myWorstEnemy.service.domain

class ChampionInfo implements Comparable<ChampionInfo> {
	String iconImageUrl
	int gamesPlayed
	int gamesLost
	int gamesBanned

	@Override
	int compareTo(ChampionInfo anotherChampionInfo)
	{
		return compare(this.gamesPlayed, this.gamesBanned, anotherChampionInfo.gamesPlayed, anotherChampionInfo.gamesBanned)
	}

	static int compare(int gamesPlayed1, int gamesBanned1, int gamesPlayed2, int gamesBanned2)
	{
		return ((gamesPlayed1 + gamesBanned1) < (gamesPlayed2 + gamesBanned2)) ? -1 : (((gamesPlayed1 + gamesBanned1) < (gamesPlayed2 + gamesBanned2)) ? 0 : 1)
	}

}
