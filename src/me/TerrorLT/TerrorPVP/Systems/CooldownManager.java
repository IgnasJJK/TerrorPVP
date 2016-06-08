package me.TerrorLT.TerrorPVP.Systems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Objects.Cooldown;

public class CooldownManager{
	
	private List<Cooldown> cooldowns = new ArrayList<Cooldown>();
	private int timerId = 0;
	
	public CooldownManager()
	{
		startTimer();
	}
	
	public void addCooldown(Cooldown cd)
	{
		cooldowns.add(cd);
	}
	
	public void removeCooldown(Player player, String id)
	{
		if(cooldowns.size() > 0)
		for(int i = 0; i < cooldowns.size(); i++)
		{
			Cooldown cd = cooldowns.get(i);
			if(cd.player == player && cd.cooldown == id)
			{
				cooldowns.remove(cd);
				player.sendMessage(ChatColor.RED + id + ChatColor.GREEN + " is no longer on cooldown.");
				return;
			}
		}
	}
	
	public void clearCooldowns(Player player)
	{
		if(cooldowns.size() > 0)
		for(int i = 0; i < cooldowns.size(); i++)
		{
			Cooldown cd = cooldowns.get(i);
			if(cd.player == player)
			{
				cooldowns.remove(cd);
			}
		}
		player.sendMessage(ChatColor.GREEN+ "Cooldowns cleared.");
	}
	
	public boolean hasCooldown(Player player, String id, boolean message)
	{
		if(cooldowns.size() > 0)
		for(int i = 0; i < cooldowns.size(); i++)
		{
			Cooldown cd = cooldowns.get(i);
			if(cd.player == player && cd.cooldown == id)
			{
				if(message) player.sendMessage(ChatColor.RED + id + ChatColor.GREEN + 
						" is on cooldown for "+cd.time+" seconds.");
				return true;
			}
		}
		
		return false;
	}
	
	public void startTimer()
	{
		timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable(){
			@Override
			public void run(){
				countDown();
			}
		}, 20L, 20L);
	}
	
	public void stopTimer()
	{
		Bukkit.getScheduler().cancelTask(timerId);
	}
	
	public void countDown()
	{
		if(cooldowns.size() > 0)
		for(int i = 0; i < cooldowns.size(); i++)
		{
			cooldowns.get(i).countDown();
			
			Cooldown cd = cooldowns.get(i);
			if(cd.time <= 0) removeCooldown(cd.player, cd.cooldown);
		}
	}	
}