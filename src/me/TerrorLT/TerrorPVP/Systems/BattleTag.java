package me.TerrorLT.TerrorPVP.Systems;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.TerrorLT.TerrorPVP.ExtFunctions;
import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLBattleTag;

public class BattleTag{
	
	public void Tag(Player player, Player hitby, double damage)
	{
		Tag(player.getUniqueId(), hitby.getUniqueId(), damage);
	}
	
	/**
	 * Combat tags a player
	 * @param uuid player to tag
	 * @param claim player that tagged them
	 * @param damage the damage that was dealt
	 */
	public void Tag(UUID uuid, UUID claim, double damage)
	{
		SQLBattleTag tag = SQLBattleTag.SqlWithdraw(uuid.toString());
		if(tag != null){
			if(tag.getClaim().equals(claim.toString()))
			{
				tag.setDamage(tag.getDamage()+damage);
			}else{
				tag.setDamage(tag.getDamage()-damage);
				if(tag.getDamage() < 0)
				{
					tag.setClaim(claim.toString());
					tag.negateDamage();
				}
			}
			tag.setTimestamp(ExtFunctions.getTimeStamp());
		}else{
			tag = new SQLBattleTag(uuid.toString(), claim.toString(), 
					damage + Globals.firstHitBenefit, ExtFunctions.getTimeStamp());
		}
		SQLBattleTag.sqlDeposit(tag);
	}

	public Player getKiller(Player player)
	{
		return getKiller(player.getUniqueId());
	}

	/**
	 * Used to get the person who killed someone
	 * @param uuid Unique id of the person who died
	 * @return the player who killed them (if anyone)
	 */
	public Player getKiller(UUID uuid)
	{
		Player killer = null;
		SQLBattleTag tag = SQLBattleTag.SqlWithdraw(uuid.toString());
		
		if(tag != null)
			if(tag.getTimestamp() + Globals.claimTimeout > ExtFunctions.getTimeStamp()){
				killer = Bukkit.getPlayer(UUID.fromString(tag.getClaim()));
			}else{
				SQLBattleTag.removeSingle(uuid.toString());
			}
		
		return killer;
	}
	
	public void Kill(Player player)
	{
		Kill(player.getUniqueId());
	}
	
	/**
	 * Removes the tags that have something to do with the uuid provided
	 * @param uuid The uuid of the player to remove from the data base
	 */
	public void Kill(UUID uuid)
	{
		SQLBattleTag.removeReferenced(uuid.toString());
	}
}
