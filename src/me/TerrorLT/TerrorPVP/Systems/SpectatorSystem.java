package me.TerrorLT.TerrorPVP.Systems;

import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Kits.KitSystem;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatorSystem implements Listener{

	ItemStack spectatorModeItem = null;
	
	public List<Player> players = new ArrayList<Player>();
	
	public SpectatorSystem(){
		loadData();
	}
	
	public ItemStack getSpectatorModeItem()
	{
		return spectatorModeItem;
	}
	
	public void loadData()
	{
		Configuration config = Main.data.configuration;
		
		if(config.contains("hotbaritems.spectatormode"))
		spectatorModeItem = KitSystem.loadItemFromConfig(
				config.getConfigurationSection("hotbaritems.spectatormode"));
		
	}
	
	public void hideSpectators()
	{
		for(Player plr : Bukkit.getServer().getOnlinePlayers())
		{
			for(Player plr2 : Bukkit.getServer().getOnlinePlayers())
			{
				if(plr == plr2) continue;
				if(!HandlerFunctions.onPvpWorld(plr) || !HandlerFunctions.onPvpWorld(plr2)) continue;
				
				if((players.contains(plr) && players.contains(plr2)) || 
						(!players.contains(plr) && !players.contains(plr2)))
				{
					plr.showPlayer(plr2);
					plr2.showPlayer(plr);
					continue;
				}
				
				if(players.contains(plr2) && !players.contains(plr))
				{
					plr.hidePlayer(plr2);
					continue;
				}
				if(players.contains(plr) && !players.contains(plr2))
				{
					plr2.hidePlayer(plr);
					continue;
				}
			}
		}
	}
	
	public void ToggleSpectatorMode(Player player)
	{
		if(TagManager.SPECTATOR_MODE.contains(player))
		{
			HandlerFunctions.CleanPlayer(player);
			HandlerFunctions.GiveGuiItems(player);
		}else{
			setSpectatorMode(player, true);
		}
	}
	
	public void setSpectatorMode(Player player, boolean spectator)
	{
		if(spectator){
			TagManager.SPECTATOR_MODE.apply(player);
			player.setAllowFlight(true);
			player.setFlying(true);
			//player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
			players.add(player);
		}else{
			TagManager.SPECTATOR_MODE.remove(player);
			player.setFlying(false);
			player.setAllowFlight(false);
			if(players.contains(player));players.remove(player);
		}
		
		hideSpectators();
	}
	
	@EventHandler
	public void onSpectatorToggle(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(spectatorModeItem == null) return;
		Player player = e.getPlayer();
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(!player.getItemInHand().equals(spectatorModeItem)) return;
		
		e.setCancelled(true);
		if(player.hasPermission("terrorpvp.spectatormode"))
			ToggleSpectatorMode(player);
		else player.sendMessage(ChatColor.RED + "You do not have permission to use Spectator Mode.");
	}
	
}
