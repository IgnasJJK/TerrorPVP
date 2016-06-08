package me.TerrorLT.TerrorPVP.Handling;

import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockHandler implements Listener{

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!TagManager.BUILD_BYPASS.contains(e.getPlayer())){
			e.setCancelled(true);
			//TODO: Send message, perhaps?
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!TagManager.BUILD_BYPASS.contains(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
	
}
