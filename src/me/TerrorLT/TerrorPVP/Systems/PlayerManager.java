package me.TerrorLT.TerrorPVP.Systems;

import org.bukkit.entity.Player;

import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayer;

public class PlayerManager {

	public void addKill(Player player, int points, boolean stolen)
	{
		SQLPlayer data = SQLPlayer.getPlayerOrDefault(player.getUniqueId().toString());
		data.setPoints(data.getPoints() + points);
		data.setKills(data.getKills()+1);
		data.setTotalKills(data.getTotalKills()+1);
		if(stolen) data.setStolen(data.getStolen()+1);
		SQLPlayer.sqlDeposit(data);
		
		Messager.setTabKDP(player, data);
		
		//return data;
	}
	
	public void addDeath(Player player)
	{
		SQLPlayer data = SQLPlayer.getPlayerOrDefault(player.getUniqueId().toString());
		data.setDeaths(data.getDeaths()+1);
		data.setTotalDeaths(data.getTotalDeaths()+1);
		SQLPlayer.sqlDeposit(data);
		
		Messager.setTabKDP(player, data);
		
		//return data;
	}
	
	public void addStolenFrom(Player player)
	{
		SQLPlayer data = SQLPlayer.getPlayerOrDefault(player.getUniqueId().toString());
		data.setStolenFrom(data.getStolenFrom()+1);
		SQLPlayer.sqlDeposit(data);
		
		Messager.setTabKDP(player, data);
	}
	
	public boolean tryReduceMoney(Player player, int amount)
	{
		SQLPlayer data = SQLPlayer.getPlayerOrDefault(player.getUniqueId().toString());
		if(amount > data.getPoints()) return false;
		data.setPoints(data.getPoints()-amount);
		SQLPlayer.sqlDeposit(data);
		Messager.setTabKDP(player, data);
		return true;
	}
	
}
