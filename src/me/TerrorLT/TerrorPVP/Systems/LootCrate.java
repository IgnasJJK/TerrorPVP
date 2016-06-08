package me.TerrorLT.TerrorPVP.Systems;

import java.util.UUID;

import me.TerrorLT.TerrorPVP.ExtFunctions;
import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Objects.ConfigLocation;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayerKeys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LootCrate implements Listener{

	public static String name = ChatColor.RED + "Flaming Crate";
	public static Location location;
	
	public static boolean playParticles = false;
	public static final float radius = 0.7f;
	public static int particleTask = -1;
	
	public static ItemStack keyItem = null;
	
	public static void loadItems()
	{
		
	}
	
	/**
	 * Sets the location of the loot crate
	 * @param location 
	 */
	public static void setLocation(Location location)
	{
		if(!HandlerFunctions.onPvpWorld(location.getWorld())) return;
		if(particleTask > 0) Bukkit.getScheduler().cancelTask(particleTask);
		LootCrate.location = location;
		ConfigLocation l = new ConfigLocation(location);
		l.put(Main.main.configuration, Globals.CNode_LootChest);
		Main.main.saveConfiguration();
		doParticles();
	}
	
	/**
	 * Returns an ItemStack to display the loot crate's keys with
	 * @param amount item amount to display
	 * @return key item of the loot crate
	 */
	public static ItemStack getKeyItem(int amount)
	{
		if(keyItem == null)
		{
			keyItem = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta im = keyItem.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Flaming Key");
			keyItem.setItemMeta(im);
		}
		
		ItemStack item = keyItem;
		item.setAmount(amount);
		
		return item;
	}
	
	/**
	 * Loads the loot crate's location from the configuration
	 */
	public static void loadLocation()
	{
		ConfigLocation l = (ConfigLocation)
				(new ConfigLocation().retrieve(Main.main.configuration, Globals.CNode_LootChest));
		if(l != null){
			location = l.getLocation();
			doParticles();
		}
	}
	
	/**
	 * Gives keys to a player
	 * @param player the player to give keys to (either name or UUID)
	 * @param amount the amount of keys to give
	 */
	public static void addKeys(String player, int amount)
	{
		SQLPlayerKeys keys = null;

		Player playerObj = Bukkit.getPlayer(player);
		if(playerObj != null)
		{
			keys = SQLPlayerKeys.WithdrawOrDefault(playerObj.getUniqueId().toString());
		}else{
			UUID uuid = null;
			try{
				uuid= UUID.fromString(player);
			}catch(IllegalArgumentException e){}
			
			if(uuid != null)
			{
				//OfflinePlayer offPlayerObj = Bukkit.getOfflinePlayer(uuid);
				keys = SQLPlayerKeys.WithdrawOrDefault(uuid.toString());
			}else{
				@SuppressWarnings("deprecation")
				OfflinePlayer offPlayerObj = Bukkit.getOfflinePlayer(player);
				if(offPlayerObj != null)
				{
					keys = SQLPlayerKeys.WithdrawOrDefault(offPlayerObj.getUniqueId().toString());
				}else{ //Out of ways to get a player
					ExtFunctions.logToFile("FAILED TO GIVE KEYS. TO:"+player + "  AMT:"+amount);
					return;
				}	
			}
		}
		
		if(keys != null)
		{
			keys.setKeys(keys.getKeys()+amount);
			SQLPlayerKeys.sqlDeposit(keys);
			
			ExtFunctions.logToFile(amount+" keys delivered to "+player+".");
			
			if(playerObj != null)
			{
				playerObj.sendMessage(Messager.formatGivenKeys(amount));
				if(!TagManager.SELECTED_KIT.contains(playerObj))
				{
					HandlerFunctions.CleanPlayerInventory(playerObj);
					HandlerFunctions.GiveGuiItems(playerObj);
				}
			}
		}
	}
	
	/**
	 * Used to show a player what they got from a crate.
	 * @param player player to show it to.
	 */
	public static void openCrate(Player player)
	{
		Inventory inv = Bukkit.getServer()
				.createInventory(player, InventoryType.HOPPER, /*"            "+*/name);
		//TODO: Add reward to chest
		
		player.openInventory(inv);
	}
	
	public static void doParticles()
	{	
		particleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable(){
			@Override
			public void run(){
				if(location == null) return;
				World world = location.getWorld();
				
				for(int i = 0; i < 360; i += 10)
				{
					world.playEffect(
							new Location(world, location.getBlockX()+(radius*Math.sin(i))+0.5d, 
									location.getY()-0.2d, location.getBlockZ()+(radius*Math.cos(i))+0.5d),
									Effect.LAVA_POP, null);
				}
				
			}
		}, 10L, 12L);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Player player = e.getPlayer();
		
		
		if(TagManager.SET_FLAMECHEST.contains(player))
		{
			e.setCancelled(true);
			if(e.getClickedBlock().getType() != Material.CHEST)
			{
				player.sendMessage(Messager.LootChest_SetNotChest);
			}else{
				setLocation(e.getClickedBlock().getLocation());
				TagManager.SET_FLAMECHEST.remove(e.getPlayer());
				player.sendMessage(Messager.LootChest_SetSuccess);
			}
			return;
		}
		
		if(e.getClickedBlock().getLocation().equals(location)) //Flame chest clicked
		{
			e.setCancelled(true);
			SQLPlayerKeys keys = 
					SQLPlayerKeys.WithdrawOrDefault(player.getUniqueId().toString());
			if(keys.getKeys() == 0){
				player.sendMessage(Messager.LootChest_NoKeys);
				player.sendMessage(Messager.LootChest_KeyAds);
			}else{
				//TODO: Make it give a reward
				openCrate(player);
			}
		}
		
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer().getLocation().getWorld())) return;
		
		if(e.getInventory().getName().compareTo(name) == 0)
		{
			//Stop rolling, give item
		}
	}
	
	/*@Override
	public void onEnable() {
		prefix = "Crate";
		loadLocation();
	}

	@Override
	public void onDisable() {
		
	}*/
	
}
