package me.TerrorLT.TerrorPVP.Systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Kits.KitSystem;

public class TitleSystem implements Listener{

	//List<Title> titles = new ArrayList<Title>();
	
	HashMap<String, Title> titles = new HashMap<String, Title>();
	HashMap<String, String> titlePointers = new HashMap<String, String>();
	
	private String titleSelectScreen = ChatColor.BLUE + "Your titles";
	
	private ItemStack titleSelectItem = null;
	private ItemStack removeTitleItem = null;
	
	//Command
	private String setTitle = null;
	private String removeTitle = null;
	
	public TitleSystem()
	{
		loadItems();
	}
	
	public HashMap<String, Title> getTitles()
	{
		return titles;
	}
	
	public void setTitle(Player player, String title)
	{
		if(setTitle == null) return;
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
				setTitle.replaceAll("\\{name\\}", player.getName())
						.replaceAll("\\{title\\}", title)
						.replaceAll("\\{world\\}", Globals.worldName)
						.replaceAll("\\{uuid\\}", player.getUniqueId().toString()));
	}
	
	public void removeTitle(Player player)
	{
		if(removeTitle == null) return;
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
		removeTitle.replaceAll("\\{name\\}", player.getName())
		.replaceAll("\\{world\\}", Globals.worldName)
		.replaceAll("\\{uuid\\}", player.getUniqueId().toString()));
	}
	
	public void loadItems()
	{
		Configuration config = Main.data.configuration;
		
		if(config.contains("Settings.settitle"))
			setTitle = config.getString("Settings.settitle");
		if(config.contains("Settings.deltitle"))
			setTitle = config.getString("Settings.deltitle");
		
		if(config.contains("hotbaritems.titleselectitem"))
			titleSelectItem = KitSystem.loadItemFromConfig(config.getConfigurationSection("hotbaritems.titleselectitem"));
		if(config.contains("miscitems.removetitleitem"))
			removeTitleItem = KitSystem.loadItemFromConfig(config.getConfigurationSection("miscitems.removetitleitem"));
		
		
		ConfigurationSection section = config.getConfigurationSection("titles");
		
		for(String node : section.getKeys(false))
		{
			Title title = new Title(node);
			
			String t = section.getString(node);
			
			title.setTitle(t);
			title.setDefault(isDefault(node));
			titles.put(node, title);
			titlePointers.put(Messager.formatColors(t), node);
		}
	}
	
	public ItemStack getTitleSelectItem()
	{
		return titleSelectItem;
	}
	
	public boolean isDefault(String s)
	{
		return s.startsWith("def");
	}

	public Title getTitle(String id)
	{
		if(titles.containsKey(id))
			return titles.get(id);
		else return null;
	}
	
	public Title getByDisplayName(String name)
	{
		if(titlePointers.containsKey(name))
			return titles.get(titlePointers.get(name));
		return null;
	}
	
	public Inventory getTitleInventory(Player player, int page)
	{
		List<Title> owned = new ArrayList<Title>();
		
		for(Entry<String, Title> entry : titles.entrySet())
		{
			if(entry.getValue().hasTitle(player))
				owned.add(entry.getValue());
		}
		
		
		if(owned.size() <= 0) return null;
		
		boolean needPages = (owned.size() > 45);
		Inventory inv = Bukkit.createInventory(null, needPages ? 54 : 45, titleSelectScreen);
		
		if(needPages)
		{
			if((page-1)*44 > owned.size()) page = 1;
		}else page = 1;
		
		int to = page*44;
		if(to > owned.size()) to = owned.size();
		
		inv.setItem(0, removeTitleItem);
		
		for(int i = (page-1)*44; i < to; i++)
		{
			inv.setItem((i%44)+1, owned.get(i).getItem());
		}
		
		return inv;
	}
	
	@EventHandler
	public void onItemInteract(PlayerInteractEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!e.getPlayer().getItemInHand().equals(titleSelectItem)) return;
		
		Inventory inv = getTitleInventory(e.getPlayer(), 1);
		
		if(inv == null)
			e.getPlayer().sendMessage(ChatColor.RED + "You don't have any titles.");
		else e.getPlayer().openInventory(inv);
	}
	
	@EventHandler
	public void onTitleSelected(InventoryClickEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getWhoClicked().getWorld())) return;
		if(e.getCurrentItem() == null) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(!e.getInventory().getName().equals(titleSelectScreen)) return;
		
		Player player = (Player)e.getWhoClicked();
		
		e.setCancelled(true);
		
		ItemStack item = e.getCurrentItem();
		if(item.getType() == Material.PAPER){
			//TODO: Handle pages
			return;
		}
		
		if(item.getType() == Material.NAME_TAG)
		{
			String title = item.getItemMeta().getDisplayName();
			setTitle(player, title);
			player.closeInventory();
			player.sendMessage(ChatColor.GREEN + "Your title has been set to " + title);
		}
		
		if(item.equals(removeTitleItem))
		{
			removeTitle(player);
			player.closeInventory();
			player.sendMessage(ChatColor.GREEN + "Your title has been removed.");
		}
		
	}
	
	
}
