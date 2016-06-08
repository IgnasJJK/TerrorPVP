package me.TerrorLT.TerrorPVP.Actions;

import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Kits.Kit;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ActionHandler implements Listener {

	LightningAction LIGHTNING_STRIKE = new LightningAction();
	TheForceAction THE_FORCE = new TheForceAction();
	PullTowardsAction PULL_TOWARDS = new PullTowardsAction();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(!TagManager.SELECTED_KIT.contains(player)) return;
		
		Kit kit = (Kit)TagManager.SELECTED_KIT.get(player);
		
		if(!kit.hasActionProfile()) return;
		
		ActionProfile aProfile = kit.getActionProfile();
		
		if(aProfile == null) return;
		
		ItemStack iih = player.getItemInHand();
		
		boolean RIGHT_CLICK = e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK;
		
		if(aProfile.contains("LIGHTNING_STRIKE") && RIGHT_CLICK){
			ActionLink link = aProfile.getLink("LIGHTNING_STRIKE");
			if(matchTrigger(iih, link))
				LIGHTNING_STRIKE.execute(e);
		}
		if(aProfile.contains("THE_FORCE") && RIGHT_CLICK){
			ActionLink link = aProfile.getLink("THE_FORCE");
			if(matchTrigger(iih, link))
				THE_FORCE.execute(e);
		}
	}
	
	boolean matchTrigger(ItemStack itemInHand, ActionLink link)
	{
		if(link != null)
			return link.hasTriggerItemStack() ? 
					itemInHand.getItemMeta().equals(link.getTriggerItemStack().getItemMeta()) : itemInHand == null;
		return false;
	}
	
	@EventHandler
	public void onFishPlayer(PlayerFishEvent e)
	{
		Player player = e.getPlayer();
		
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(!TagManager.SELECTED_KIT.contains(player)) return;
		player.setItemInHand(HandlerFunctions.RepairItem(player.getItemInHand()));
		
		Kit kit = (Kit)TagManager.SELECTED_KIT.get(player);
		
		if(!kit.hasActionProfile()) return;
		ActionProfile aProfile = kit.getActionProfile();
				
		if(aProfile == null) return;
		ItemStack iih = player.getItemInHand();
		
		

		if(HandlerFunctions.withinSafezoneBounds(player.getLocation())) return;
		if(e.getCaught() == null) return;
		if(HandlerFunctions.withinSafezoneBounds(e.getCaught().getLocation())) return;
		
		if(aProfile.contains("PULL_TOWARDS"))
		{
			ActionLink link = aProfile.getLink("THE_FORCE");
			if(matchTrigger(iih, link))
				PULL_TOWARDS.execute(e);
		}
	}
	
	/*@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(!TagManager.SELECTED_KIT.contains(player)) return;
		
		Kit kit = (Kit)TagManager.SELECTED_KIT.get(player);
		
		ActionProfile profile = null;
		
		if(player.getItemInHand() == null)
		{
			profile = kit.getEmptyHandActionProfile();
		}else if(player.getItemInHand().hasItemMeta())
			if(player.getItemInHand().getItemMeta().hasDisplayName())
			{
				profile = kit.getActionProfile(player.getItemInHand().getItemMeta().getDisplayName());
			}
		
		if(profile == null) return;
		
		if(profile.onRightClick != null && profile.onRightClick instanceof InteractAction &&
				(e.getAction() == Action.RIGHT_CLICK_AIR ||
				 e.getAction() == Action.RIGHT_CLICK_BLOCK))
		{
			((InteractAction)profile.onRightClick).execute(e);
		}
	}
	*/
}
