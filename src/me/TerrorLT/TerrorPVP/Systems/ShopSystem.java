package me.TerrorLT.TerrorPVP.Systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Kits.Kit;
import me.TerrorLT.TerrorPVP.Kits.KitSystem;

public class ShopSystem implements Listener{

	HashMap<String, Integer> kitPrices = new HashMap<String, Integer>();
	HashMap<String, Integer> titlePrices = new HashMap<String, Integer>();
	
	private String addPermission = null;
	
	ItemStack shopItem = null;
	ItemStack kitCategoryItem = null;
	
	private String categoryShop = ChatColor.BLUE + "What may you be looking for?";
	private String kitCategoryShop = ChatColor.BLUE + "Kit Shop";
	private String titleCategoryShop = ChatColor.DARK_GREEN + "Title Shop";
	
	public ShopSystem()
	{
		loadItems();
		loadSettings();
	}
	
	public void loadItems()
	{
		Configuration config = Main.shop.configuration;
		
		if(!config.contains("shop.hotbaritem")) return;
		
		shopItem = KitSystem.loadItemFromConfig(config.getConfigurationSection("shop.hotbaritem"));
		
		ConfigurationSection section = null;
		if(config.contains("shop.kits")) section = config.getConfigurationSection("shop.kits");
		if(section != null)
		for(String node : section.getKeys(false))
		{
			kitPrices.put(node, section.getInt(node));
		}
		
		section = null;
		if(config.contains("shop.titles")) config.getConfigurationSection("shop.titles");
		
		if(section != null)
		for(String node : section.getKeys(false))
		{
			titlePrices.put(node, section.getInt(node));
		}
		
	}
	
	public void loadSettings()
	{
		Configuration data = Main.data.configuration;

		if(data.contains("Settings.giveperm"))
			addPermission = data.getString("Settings.giveperm");
		
		if(data.contains("kitsetup.shopkititem"))
			kitCategoryItem = KitSystem.loadItemFromConfig(data.getConfigurationSection("kitsetup.shopkititem"));
			
	}
	
	public void givePermission(Player player, String perm)
	{
		String cmd = addPermission.replaceAll("\\{name\\}", player.getName())
				.replaceAll("\\{perm\\}", perm)
				.replaceAll("\\{uuid\\}", player.getUniqueId().toString());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}
	
	public ItemStack getShopItem()
	{
		return shopItem;
	}
	
	public Inventory getShopCategories(Player player)
	{
		Inventory inv = Bukkit.createInventory(player, InventoryType.HOPPER, categoryShop);
		
		inv.setItem(1, kitCategoryItem);
		inv.setItem(3, Main.titleSystem.getTitleSelectItem());
		
		return inv;
	}
	
	public Inventory getKitCategory(Player player)
	{
		Inventory inv = Bukkit.createInventory(null, 54, kitCategoryShop);
		
		for(Entry<String, Integer> entry : kitPrices.entrySet())
		{
			Kit kit = Main.kitSystem.getKitByInternalName(entry.getKey());
			if(!kit.hasKit(player))
			{
				ItemStack dItem = kit.getDisplayItem();
				ItemStack display = new ItemStack(dItem.getType(), dItem.getAmount());
				
				ItemMeta im = dItem.getItemMeta();
				List<String> lore = im.getLore();
				lore.add(0,"");
				lore.add(0,ChatColor.DARK_PURPLE + "Price: "+ ChatColor.GOLD + "" + entry.getValue() + "C");
				im.setLore(lore);
				display.setItemMeta(im);
				
				inv.addItem(display);
			}
		}
		
		return inv;
	}
	
	@EventHandler
	public void OpenCategoryInventory(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(shopItem == null) return;
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!e.getPlayer().getItemInHand().equals(shopItem)) return;
		
		Inventory inv = getShopCategories(e.getPlayer());
		e.getPlayer().openInventory(inv);
	}

	
	@EventHandler
	public void ShopClick(InventoryClickEvent e)
	{
		Player player = (Player)e.getWhoClicked();
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(e.getCurrentItem() == null) return;

		ItemMeta im = e.getCurrentItem().getItemMeta();
		
		if(im == null) return;
		
		if(e.getInventory().getName().compareTo(kitCategoryShop) == 0)
		{			
			e.setCancelled(true);
			if(!im.hasDisplayName()) return;
			
			Kit kit = Main.kitSystem.getKitByDisplayName(im.getDisplayName());
			
			if(kit != null)
			{
				//Sell kit
				player.closeInventory();
				if(Main.playerManager.tryReduceMoney(player, kitPrices.get(kit.getInternalName())))
				{
					givePermission(player, kit.getPerm());
					sendMessage(player, "You have bought: " + kit.getDisplayName() + " Kit");
				}else{
					sendMessage(player, ChatColor.RED + "You don't have enough money to buy that");
				}
				
			}
		}else if(e.getInventory().getName().compareTo(categoryShop) == 0)
		{
			e.setCancelled(true);
			if(e.getCurrentItem().equals(kitCategoryItem)){
				player.closeInventory();
				Inventory inv = getKitCategory(player);
				player.openInventory(inv);
			}else if(e.getCurrentItem().equals(Main.titleSystem.getTitleSelectItem()))
			{
				player.closeInventory();
				Inventory inv = getShopTitles(player, 1);
				player.openInventory(inv);
			}
		}else if(e.getInventory().getName().compareTo(titleCategoryShop) == 0)
		{
			e.setCancelled(true);
			if(e.getCurrentItem().getType().equals(Material.NAME_TAG))
			{
				String display = e.getCurrentItem().getItemMeta().getDisplayName();

				Title t = Main.titleSystem.getByDisplayName(display);
				
				if(t != null)
				{
					if(!titlePrices.containsKey(t.getInternalName())) return;
					Integer price = titlePrices.get(t.getInternalName());
					
					player.closeInventory();
					if(Main.playerManager.tryReduceMoney(player, price))
					{
						givePermission(player, t.getPerm());
						sendMessage(player, "You have bought: " + Messager.formatColors(t.getTitle()) + " Title");
					}else{
						sendMessage(player, ChatColor.RED + "You don't have enough money to buy that");
					}
					
					
				}
			}
		}
		
	}
	
	public Inventory getShopTitles(Player player, int page)
	{
		List<Title> titles = new ArrayList<Title>();
		
		for(Entry<String, Integer> entry : titlePrices.entrySet())
		{
			Title t = Main.titleSystem.getTitle(entry.getKey());
			if(t == null) continue;
			
			if(!t.hasTitle(player))
				titles.add(t);
		}
		
		boolean multipage = titles.size() > 45;
		
		Inventory inv = Bukkit.createInventory(null, multipage ? 54 : 45, titleCategoryShop);
		
		if(multipage)
		{
			if((page-1)*45 > titles.size()) page = 1;
		}else page = 1;
		
		int to = page*45;
		if(to > titles.size()) to = titles.size();
		
		for(int i = (page-1)*45; i < to; i++)
		{
			Title t = titles.get(i);
			
			Integer price = titlePrices.get(t.getInternalName());
			
			ItemStack iStack = new ItemStack(Material.NAME_TAG);
			ItemMeta im = iStack.getItemMeta();
			im.setDisplayName(Messager.formatColors(t.getTitle()));
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.DARK_PURPLE + "Price: "+ ChatColor.GOLD + "" + price  + "C");
			im.setLore(lore);
			iStack.setItemMeta(im);
			
			inv.setItem((i%45), iStack);
		}
		
		return inv;
	}
	
	public void sendMessage(Player player, String message)
	{
		player.sendMessage(ChatColor.AQUA + "[Shop]" + ChatColor.GREEN + message);
	}
}
