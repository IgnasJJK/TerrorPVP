package me.TerrorLT.TerrorPVP.Actions;

import org.bukkit.event.player.PlayerInteractEvent;

public class CancelInteractAction extends InteractAction {

	public void execute(PlayerInteractEvent e)
	{
		e.setCancelled(true);
	}
	
}
