package me.TerrorLT.TerrorPVP.Handling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Kits.Kit;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.connorlinfoot.titleapi.TitleAPI;

public class SafezoneHandler implements Listener {
	
	public static Location TempP1 = null;
	
	private Random rand = new Random();
	
	private List<Material> removeWhenDropped = new ArrayList<Material>();
	private List<Material> allowDrop = new ArrayList<Material>();
	
	public SafezoneHandler()
	{
		removeWhenDropped.add(Material.BOWL);
		
		removeWhenDropped.add(Material.MUSHROOM_SOUP);
		removeWhenDropped.add(Material.POTION);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{	
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!Globals.isSafezoneSet()) return;
		
		Player player = e.getPlayer();
		
		if(TagManager.SPECTATOR_MODE.contains(player)) return;
		
		boolean outsideTag = TagManager.OUTSIDE_SAFEZONE.contains(player);
		boolean withinBounds = HandlerFunctions.withinSafezoneBounds(e.getTo());
		
		if(!outsideTag && !withinBounds) //Leaving Safe zone
		{
			Kit kit = null;
			if(!TagManager.SELECTED_KIT.contains(player))
			if(TagManager.PREVIOUS_KIT.contains(player)){
				Object o = TagManager.PREVIOUS_KIT.get(player);
				if(o != null)
					if(o instanceof String)
						kit = Main.kitSystem.getKitByInternalName((String)o);
			}
			
			if(!TagManager.SELECTED_KIT.contains(player))
			if(kit != null)
			{
				ActionBarAPI.sendActionBar(player, ChatColor.GREEN + 
						"Selected previously used kit: " + ChatColor.GOLD + kit.getDisplayName());
				Main.kitSystem.setPlayerKit(player, kit, true);
			}else{
				HandlerFunctions.bounceBack(player, e.getFrom(), e.getTo());
				TitleAPI.sendTitle(player, 5, 15, 5, "", ChatColor.RED + "You cannot leave unarmed!");
				ActionBarAPI.sendActionBar(player, ChatColor.RED + "Select a kit before leaving.", 200);
				return;
			}
			
			HandlerFunctions.leaveSafezone(player);
			return;
		}
		
		if(outsideTag && withinBounds) //Entering Safe zone
		{
			HandlerFunctions.bounceBack(player, e.getFrom(), e.getTo());
			
			//TODO: Make them separate somehow
			if(rand.nextInt(1000) < 995) TitleAPI.sendTitle(e.getPlayer(), 5, 10, 5, "", ChatColor.RED + "You cannot return.");
			else TitleAPI.sendTitle(e.getPlayer(), 2, 3, 2, "", ChatColor.RED + "YOU CAN'T");
			return;
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e)
	{
		if(HandlerFunctions.onPvpWorld(e.getFrom().getWorld()) && !HandlerFunctions.onPvpWorld(e.getTo().getWorld()))
		{
			if(TagManager.OUTSIDE_SAFEZONE.contains(e.getPlayer()) && TagManager.SELECTED_KIT.contains(e.getPlayer()))
			{
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot leave this world while you're in a battle.");
			}else{
				HandlerFunctions.CleanPlayer(e.getPlayer());
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveWorld(PlayerMoveEvent e)
	{
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!Globals.isWorldzoneSet()) return;
		
		Player player = e.getPlayer();
		
		boolean withinBounds = Globals.getWorldzone().withinWalls(e.getTo());
		
		if(!withinBounds) //Leaving World zone
		{
			HandlerFunctions.bounceBack(player, e.getFrom(), e.getTo());
			//TODO: Make them separate somehow
			if(rand.nextInt(1000) < 995) TitleAPI.sendTitle(e.getPlayer(), 5, 10, 5, "", ChatColor.RED + "You cannot proceed.");
			else TitleAPI.sendTitle(e.getPlayer(), 2, 3, 2, "", ChatColor.RED + "DEAD END");
			return;
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		
		if(TagManager.SPECTATOR_MODE.contains(e.getPlayer()) ||
				HandlerFunctions.withinSafezoneBounds(e.getPlayer().getLocation())){
			e.setCancelled(true);
			return;
		}
		
		Material dropped = e.getItemDrop().getItemStack().getType();
		
		if(removeWhenDropped.contains(dropped)) e.getItemDrop().remove();
		else if(!allowDrop.contains(dropped)) e.setCancelled(true);
	}
	
	public void onItemPickup(PlayerPickupItemEvent e){
		
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		
		if(TagManager.SPECTATOR_MODE.contains(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void SetSafezone(PlayerInteractEvent e)
	{
		if(TagManager.SET_SAFEZONE.contains(e.getPlayer()))
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK ||
					e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				e.setCancelled(true);
				if(TempP1 == null)
				{
					TempP1 = e.getClickedBlock().getLocation();
					Messager.Deliver(e.getPlayer(), Messager.Zone_SetP2);
				}else{
					Location P2 = e.getClickedBlock().getLocation();
					
					Globals.setSafezone(TempP1, P2);
					TempP1 = null;
					
					Messager.Deliver(e.getPlayer(), Messager.Zone_SetSuccess);
					TagManager.SET_SAFEZONE.remove(e.getPlayer());
				}
			}
		
		if(TagManager.SET_WORLDZONE.contains(e.getPlayer()))
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK ||
					e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				e.setCancelled(true);
				if(TempP1 == null)
				{
					TempP1 = e.getClickedBlock().getLocation();
					Messager.Deliver(e.getPlayer(), Messager.Zone_SetP2);
				}else{
					Location P2 = e.getClickedBlock().getLocation();
					
					Globals.setWorldZone(TempP1, P2);
					TempP1 = null;
					
					Messager.Deliver(e.getPlayer(), Messager.Zone_SetSuccess);
					TagManager.SET_WORLDZONE.remove(e.getPlayer());
				}
			}
	}
	
	public static void StopSetSafezone(Player player)
	{
		Messager.Deliver(player, Messager.Zone_SetCancel);
		TagManager.SET_SAFEZONE.remove(player);
		TempP1 = null;
	}
	
	public static void StopSetWorldzone(Player player)
	{
		Messager.Deliver(player, Messager.Zone_SetCancel);
		TagManager.SET_WORLDZONE.remove(player);
		TempP1 = null;
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getEntity().getWorld())) return;
		
		e.setCancelled(true);
	}
	
}
