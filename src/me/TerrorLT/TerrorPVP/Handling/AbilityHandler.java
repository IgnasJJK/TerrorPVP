package me.TerrorLT.TerrorPVP.Handling;

import me.TerrorLT.TerrorPVP.Kits.Wight;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AbilityHandler implements Listener{


	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!TagManager.SELECTED_KIT.contains(e.getPlayer())) return;
		Player player = e.getPlayer();

		Object kit = TagManager.SELECTED_KIT.get(player);
		
		if(kit instanceof Wight)
		{
			if(player.isSneaking())
				if(e.getFrom().getY() < e.getTo().getY()) //Rising
					Wight.Vanish(player);
		}
		
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getEntity().getWorld())) return;
		if(!(e.getEntity() instanceof Player)) return;
		if(!(e.getDamager() instanceof Player)) return;
		if(!TagManager.SELECTED_KIT.contains(e.getDamager())) return;
		
		Player player = (Player)e.getEntity();
		Player damager = (Player)e.getDamager();
		Object damagerKit = TagManager.SELECTED_KIT.get(damager);
		
		if(damagerKit instanceof Wight)
		{
			Wight.Reappear(damager);
		}
		
		
	}
}
