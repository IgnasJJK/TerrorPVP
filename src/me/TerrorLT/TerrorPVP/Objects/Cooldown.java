package me.TerrorLT.TerrorPVP.Objects;

import org.bukkit.entity.Player;

public class Cooldown {

	public Player player;
	public int time;
	public String cooldown;
	
	public Cooldown(Player player, int time, String cooldown)
	{
		this.player = player;
		this.time = time;
		this.cooldown = cooldown;
	}
	
	public void countDown()
	{
		time -= 1;
	}
}
