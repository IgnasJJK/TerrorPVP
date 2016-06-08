package me.TerrorLT.TerrorPVP.Actions;

import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SwitchAction extends EntityDamageAction {

	public void execute(EntityDamageByEntityEvent e)
	{
		Entity damager = HandlerFunctions.getDamagerEntity(e.getCause(), e.getDamager());
		
		if(!(damager instanceof Player)) return;
		
		Location temp = e.getEntity().getLocation();
		e.getEntity().teleport(damager);
		damager.teleport(temp);
	}
}
