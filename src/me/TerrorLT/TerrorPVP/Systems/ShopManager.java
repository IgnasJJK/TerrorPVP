package me.TerrorLT.TerrorPVP.Systems;

import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.ExtFunctions;
import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Kits.DataTypes;
import me.TerrorLT.TerrorPVP.Kits.Kits;
import me.TerrorLT.TerrorPVP.Kits.OldKit;
import me.TerrorLT.TerrorPVP.Objects.PlayerTitle;
import me.TerrorLT.TerrorPVP.Objects.TitleSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class ShopManager implements Listener{
	
	private ItemStack guiShopItem = null;
	private ItemStack guiShopItemKits = null;
	private ItemStack guiShopItemTitles = null;
	
	private String REMOVE_TITLE = ChatColor.RED + "Remove Title";
	
	public TitleSet titleSet = new TitleSet();
	
	public ItemStack createShopItem(Material material, String name, 
			int price, List<String> desc)
	{
		if(desc == null) desc = new ArrayList<String>();
		
		ItemStack item = new ItemStack(material, 1);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GREEN+name);
		if(price > 0) desc.add(0, ChatColor.GREEN+"Price: " + ChatColor.GOLD+ Messager.formatPoints(price));
		im.setLore(desc);
		item.setItemMeta(im);
		return item;
	}
	
	public ItemStack getItemShopItem(){
		if(guiShopItem == null)
		{
			guiShopItem = new ItemStack(Material.GOLD_INGOT);
			ItemMeta im = guiShopItem.getItemMeta();
			im.setDisplayName(Globals.GUI_ITEMSHOP_ITEM);
			guiShopItem.setItemMeta(im);
		}
		
		return guiShopItem;
	}

	public void openCategories(Player player) {
		if(guiShopItemKits == null)
		{
			guiShopItemKits = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta im = guiShopItemKits.getItemMeta();
			im.setDisplayName(Globals.GUI_ITEMSHOP_ITEM_KITS);
			guiShopItemKits.setItemMeta(im);
		}
		if(guiShopItemTitles == null)
		{
			guiShopItemTitles = new ItemStack(Material.NAME_TAG, 1);
			ItemMeta im = guiShopItemTitles.getItemMeta();
			im.setDisplayName(Globals.GUI_ITEMSHOP_ITEM_TITLES);
			guiShopItemTitles.setItemMeta(im);
		}
		Inventory categories = Bukkit.createInventory(player, InventoryType.HOPPER, Globals.GUI_ITEMSHOP_MAIN);
		
		categories.addItem(guiShopItemKits);
		categories.addItem(guiShopItemTitles);
		
		player.openInventory(categories);
		
	}
	
	public void openKitShop(Player player)
	{
		Inventory kitshop = Bukkit.createInventory(null, 54, Globals.GUI_ITEMSHOP_KITS);		
		for(Kits kit : Kits.values())
		{
			if(kit.getType() != DataTypes.SHOP || player.hasPermission(kit.getNode())) continue;
			kitshop.addItem(
					createShopItem(kit.getKit().displayMaterial, 
							kit.getKit().KitName, kit.getPrice(), new ArrayList<String>()));
		}
		
		player.openInventory(kitshop);
	}
	
	public  void onKitShopClick(Player player, String clickedItemName)
	{
		if(clickedItemName == null || clickedItemName == "") return;
		
		Kits kit = Kits.getByItemName(clickedItemName);
		if(kit == null) return;
		if(Main.playerManager.tryReduceMoney(player, kit.getPrice()))
		{
			player.closeInventory();
			//givePermission(player, kit.getNode());
			player.sendMessage(Messager.BOUGHT + ChatColor.YELLOW + kit.getKit().KitName + " Kit");
			ExtFunctions.logToFile(player.getUniqueId().toString()+" ("+player.getName()+") bought " + kit.getKit().KitName + " kit.");
		}else{
			player.sendMessage(Messager.NOT_ENOUGH_MONEY);
		}
	}
	
	public void openTitleShop(Player player)
	{
		Inventory titleshop = Bukkit.createInventory(null, 54, Globals.GUI_ITEMSHOP_TITLES);
		
		if(titleSet.getTitles() != null)
		if(titleSet.getTitles().size() > 0)
		for(PlayerTitle title : titleSet.getTitles())
		{	
			if(!player.hasPermission(title.getNode()) && title.getType() == DataTypes.SHOP){
				titleshop.addItem(title.createShopTitle());
			}else if(!player.hasPermission(title.getNode()) && title.getType() == DataTypes.KEYCRATE)
			{
				titleshop.addItem(title.createCrateTitle());
			}else{
				titleshop.addItem(title.createOwnedTitle());
			}
		}
		
		titleshop.setItem(45, createShopItem(Material.BARRIER, REMOVE_TITLE, 0, null));
		
		player.openInventory(titleshop);
	}
	
	public void onTitleShopClick(Player player, String clickedItemName)
	{
		if(clickedItemName == null || clickedItemName == "") return;
		if(clickedItemName.compareTo(REMOVE_TITLE) == -2) //TODO: Find out why this returns -2
		{
			player.closeInventory();
			//setTitle(player, "");
			player.sendMessage(Messager.TITLE_REMOVED);
			return;
		}
		
		PlayerTitle title = titleSet.getByItemName(clickedItemName);
		if(title == null) return;
		
		if(!player.hasPermission(title.getNode()) && title.getType() != DataTypes.DEFAULT){
			if (title.getType() == DataTypes.SHOP) {
				if (Main.playerManager.tryReduceMoney(player, title.getPrice())) {
					player.closeInventory();
					//givePermission(player, title.getNode());
					player.sendMessage(Messager.BOUGHT + ChatColor.YELLOW
							+ title.getTitle() + " Title");
					ExtFunctions.logToFile(player.getUniqueId().toString()
							+ " (" + player.getName() + ") bought "
							+ title.getTitle() + " title.");
					openTitleShop(player);
				} else {
					player.sendMessage(Messager.NOT_ENOUGH_MONEY);
				}
			}
			if(title.getType() == DataTypes.KEYCRATE)
			{
				player.sendMessage(Messager.LootChest_YouCanOnly);
			}
		}else{
			player.closeInventory();
//			//setTitle(player, title.getTitle());
			player.sendMessage(Messager.TITLE_SET_TO + "" + title.getTitle());
			ExtFunctions.logToFile(player.getUniqueId() + 
					" ("+player.getName()+") set their title to " + title.getTitle());
		}
	}

	/*@Override
	public void onEnable() {
		prefix = "Shop";
		loadSettings();
		titleSet = (TitleSet) titleSet.retrieve(data, "Titles");
		readKitData();

		instance = this;
	}

	@Override
	public void onDisable() {
		
	}*/
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(e.getAction() != Action.RIGHT_CLICK_AIR &&
				e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getItem() != null)
			if(e.getItem().hasItemMeta())
				if(e.getItem().getItemMeta().getDisplayName() != null)
					if(e.getItem().getItemMeta().getDisplayName().equals(Globals.GUI_KITSELECT_ITEM)){
						OldKit.openGui(e.getPlayer());
					}else if(e.getItem().getItemMeta().getDisplayName().equals(Globals.GUI_ITEMSHOP_ITEM))
					{
						openCategories(e.getPlayer());
					}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getWhoClicked().getWorld())) return;
		if(e.getCurrentItem() == null) return;
		Player player = (Player)e.getWhoClicked();
		ItemMeta im = e.getCurrentItem().getItemMeta();
		
		if(im != null)
		if(e.getInventory().getName().equals(Globals.GUI_KITSELECT))
		{
			e.setCancelled(true);
			
			OldKit kit = OldKit.getKitByName(im.getDisplayName());
			if(kit == null) return;
			kit.outfit(player);
			player.closeInventory();
		}else if(e.getInventory().getName().equals(Globals.GUI_ITEMSHOP_MAIN))
		{
			e.setCancelled(true);
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Globals.GUI_ITEMSHOP_ITEM_KITS))
			{
				player.closeInventory();
				openKitShop(player);
			}else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Globals.GUI_ITEMSHOP_ITEM_TITLES))
			{
				player.closeInventory();
				openTitleShop(player);
			}
		}else if(e.getInventory().getName().equals(Globals.GUI_ITEMSHOP_KITS))
		{
			e.setCancelled(true);
			onKitShopClick(player, e.getCurrentItem().getItemMeta().getDisplayName());
		}else if(e.getInventory().getName().equals(Globals.GUI_ITEMSHOP_TITLES))
		{
			e.setCancelled(true);
			onTitleShopClick(player, e.getCurrentItem().getItemMeta().getDisplayName());
		}
	}
	
}
