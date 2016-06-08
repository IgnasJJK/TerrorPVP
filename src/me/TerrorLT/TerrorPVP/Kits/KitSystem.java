package me.TerrorLT.TerrorPVP.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Systems.TagManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class KitSystem implements Listener{

	public enum KitCategory{
		CRATE("CRATE"),
		DONATOR("DONATOR");
		
		public String identifier = "";
		List<String> categoryKits = new ArrayList<String>();
		
		KitCategory(String id)
		{
			identifier = id;
		}
		
		public void addToCategory(String internalName){
			if(!contains(internalName))
				categoryKits.add(internalName);
		}
		
		public boolean contains(String internalName){
			return categoryKits.contains(internalName);
		}
		
		public static KitCategory getCategory(String id)
		{
			for(KitCategory cat : KitCategory.values())
				if(cat.identifier.equalsIgnoreCase(id))
					return cat;
			return null;
		}
		
	}
	
	JavaPlugin instance;
	
	HashMap<String, Kit> internalKits = new HashMap<String, Kit>();
	HashMap<String, String> displayPointers = new HashMap<String, String>();
	
	ItemStack kitSelectionItem = null;
	ItemStack normalCategoryItem = null;
	ItemStack crateCategoryItem = null;
	ItemStack donatorCategoryItem = null;
	
	ItemStack soupItemFull = null;
	ItemStack soupItemEmpty = null;
	ItemStack potionItem = null;
	
	String healthOptions = ChatColor.RED + "Healing Item";
	
	String kitSelectionInventoryName = ChatColor.BLUE + "Select a kit";
	String categorySelectionInventoryName = ChatColor.BLUE + "Select a kit category";
	
	public KitSystem()
	{
		loadMiscItems();
		loadKits();
		loadCategories();
	}
	
	public void loadMiscItems()
	{
		Configuration config = Main.data.configuration;
		if(config.contains("kitsetup.kitselectitem"))
			kitSelectionItem = loadItemFromConfig(
				config.getConfigurationSection("kitsetup.kitselectitem"));
		
		if(config.contains("kitsetup.normalcategory"))
			normalCategoryItem = loadItemFromConfig(
					config.getConfigurationSection("kitsetup.normalcategory"));
		
		if(config.contains("kitsetup.cratecategory"))
			crateCategoryItem = loadItemFromConfig(
					config.getConfigurationSection("kitsetup.cratecategory"));
		
		if(config.contains("kitsetup.donatorcategory"))
			donatorCategoryItem = loadItemFromConfig(
					config.getConfigurationSection("kitsetup.donatorcategory"));
		
		if(config.contains("kitsetup.soupitemfull"))
			soupItemFull = loadItemFromConfig(
					config.getConfigurationSection("kitsetup.soupitemfull"));
		if(config.contains("kitsetup.soupitemempty"))
			soupItemEmpty = loadItemFromConfig(
					config.getConfigurationSection("kitsetup.soupitemempty"));
		
		if(potionItem == null){
			Potion p = new Potion(PotionType.INSTANT_HEAL, 2);
			p.setSplash(true);
			potionItem = p.toItemStack(1);
		}
	}
	
	public void loadKits()
	{
		Configuration config = Main.kits.configuration;
		
		if(!config.contains("kits")) return;
		
		ConfigurationSection kitSection = config.getConfigurationSection("kits");
		
		for(String node : kitSection.getKeys(false))
		{
			Kit kit = Kit.loadFromSection(kitSection.getConfigurationSection(node));
			internalKits.put(kit.getInternalName(), kit);
			displayPointers.put(kit.getDisplayName(), kit.getInternalName());
		}
	}
	
	public void loadCategories()
	{
		Configuration config = Main.cats.configuration;
		
		if(config.contains("kits")){
			ConfigurationSection kits = config.getConfigurationSection("kits");
			
			for(String key : kits.getKeys(false))
			{
				KitCategory cat = KitCategory.getCategory(key);
				if(cat == null) continue;
				
				List<String> kitList = kits.getStringList(key);
				
				for(String s : kitList)
				{
					cat.addToCategory(s);
				}
			}
		}
		
		
	}
	
	public Kit getKitByInternalName(String name)
	{
		if(internalKits.containsKey(name))
			return internalKits.get(name);
		return null;
	}
	
	public Kit getKitByDisplayName(String name)
	{
		if(displayPointers.containsKey(name))
			if(internalKits.containsKey(displayPointers.get(name)))
				return internalKits.get(displayPointers.get(name));
		
		return null;
	}
	
	public Inventory getCategoryInventory(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, InventoryType.HOPPER, categorySelectionInventoryName);
		inventory.setItem(1, normalCategoryItem);
		inventory.setItem(2, crateCategoryItem);
		inventory.setItem(3, donatorCategoryItem);
		
		return inventory;
	}
	
	public ItemStack getHealthOptionsItem(Player player)
	{
		ItemStack item = null;
		
		if(TagManager.USES_POTIONS.contains(player))
		{
			item = new ItemStack(potionItem.getType());
			item.setDurability(potionItem.getDurability());
		}else{
			item = new ItemStack(soupItemFull.getType());
		}
		
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(healthOptions);
		item.setItemMeta(im);
		
		return item;
	}
	
	//IMPORTANT: Store category id in page item
	public Inventory getKitInventory(Player player, KitCategory category, int page)
	{
		Inventory inventory = Bukkit.createInventory(null, 45, kitSelectionInventoryName);
		
		for(Entry<String, Kit> entry : internalKits.entrySet())
		{
			
			if(!entry.getValue().isEnabled()) continue;
			if(!entry.getValue().hasKit(player)) continue;
			
			if(category == null){
				if(KitCategory.CRATE.contains(entry.getKey()) || 
						KitCategory.DONATOR.contains(entry.getKey())) continue;
			}else{
				if(!category.contains(entry.getKey())) continue;
			}
			
			inventory.addItem(entry.getValue().getDisplayItem());
		}
		
		for(Entry<String, Kit> entry : internalKits.entrySet())
		{
			
			if(!entry.getValue().isEnabled()) continue;
			if(entry.getValue().hasKit(player)) continue;
			
			if(category == null){
				if(KitCategory.CRATE.contains(entry.getKey()) || 
						KitCategory.DONATOR.contains(entry.getKey())) continue;
			}else{
				if(!category.contains(entry.getKey())) continue;
			}
			
			boolean crate = category == KitCategory.CRATE;
			boolean donation = category == KitCategory.DONATOR;
			
			inventory.addItem(getLockedItem(entry.getValue(), (!crate && !donation), crate, donation));
		}
		
		return inventory;
	}
	
	public ItemStack getLockedItem(Kit kit, boolean shop, boolean crate, boolean donation)
	{
		ItemStack dItem = kit.getDisplayItem();
		ItemStack iStack = new ItemStack(Material.STAINED_GLASS_PANE);
		iStack.setDurability((short)15);
		
		ItemMeta im = iStack.getItemMeta();
		
		List<String> lore = new ArrayList<String>();
		if(shop) lore.add(ChatColor.BLUE + "You can get this kit from the shop.");
		if(crate) lore.add(ChatColor.BLUE + "You can get this kit from a crate.");
		if(donation) lore.add(ChatColor.BLUE + "You can get this kit by donating for the server.");
		
		im.setLore(lore);
		im.setDisplayName(ChatColor.RED + ChatColor.stripColor(dItem.getItemMeta().getDisplayName()));
		
		iStack.setItemMeta(im);
		
		return iStack;
	}
	
	public void giveKitSelectionItem(Player player)
	{
		if(kitSelectionItem != null)
			player.getInventory().addItem(kitSelectionItem);
	}
	
	public void setPlayerKit(Player player, Kit kit, boolean onLeave)
	{
		Main.gameplayMetrics.incrementKitUsage(kit.getInternalName());
		
		if(player.getGameMode() != GameMode.SURVIVAL)
			player.setGameMode(GameMode.SURVIVAL);
		
		if(!onLeave)HandlerFunctions.CleanPlayer(player);
		
		kit.equipPlayer(player);
		giveHealthItems(player);
		
	}
	
	public void giveHealthItems(Player player)
	{
		ItemStack item = null;
		
		if(TagManager.USES_POTIONS.contains(player))
		{
			item = potionItem;
		}else{
			item = soupItemFull;
		}
		
		for(int i = 0; i < 36; i++)
		{
			PlayerInventory inv = player.getInventory();
			if(inv.getItem(i) == null)
				inv.setItem(i, item);
		}
	}
	
	@EventHandler
	public void onChangeHealthOption(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.RIGHT_CLICK_AIR) return;

		Player player = e.getPlayer();
		
		if(!HandlerFunctions.onPvpWorld(player)) return;
		
		ItemStack item = player.getItemInHand();
		
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		
		if(item.getItemMeta().getDisplayName().compareTo(healthOptions) == 0)
		{
			e.setCancelled(true);
			
			if(TagManager.USES_POTIONS.contains(player)){
				TagManager.USES_POTIONS.remove(player);
				player.sendMessage(ChatColor.GREEN + "Health item set to " + ChatColor.RED + "Soup");
			}else{
				TagManager.USES_POTIONS.apply(player);
				player.sendMessage(ChatColor.GREEN + "Health item set to " + ChatColor.RED + "Potion of Health");
			}
			
			player.setItemInHand(getHealthOptionsItem(player));
			
		}
	}
	
	@EventHandler
	public void onUseHealthItem(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.RIGHT_CLICK_AIR) return;
		final Player player = e.getPlayer();
		
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(HandlerFunctions.withinSafezoneBounds(player.getLocation())){ 
			if(player.getItemInHand() == potionItem || 
					player.getItemInHand() == soupItemFull) 
				e.setCancelled(true);
			return;		
		}
		if(TagManager.USES_POTIONS.contains(player)) return;
		if(!player.getItemInHand().equals(soupItemFull)) return;
		/*if(!player.getItemInHand().hasItemMeta()) return;
		if(!player.getItemInHand().getItemMeta().hasDisplayName()) return;
		if(player.getItemInHand().getItemMeta().getDisplayName().
				compareTo(soupItemFull.getItemMeta().getDisplayName()) != 0) return;*/
		
		if(player.getHealth() == player.getHealthScale()) return;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable(){
			@Override
			public void run()
			{
				player.getItemInHand().setType(soupItemEmpty.getType());
				player.getItemInHand().setItemMeta(soupItemEmpty.getItemMeta());
				player.updateInventory();
			}
		}, 1L);
		

		
		double health = player.getHealth() + 7.5d;
		if(health > player.getHealthScale()) health = player.getHealthScale();
		player.setHealth(health);
	}
	
	public void dropHealthItems(Player at)
	{
		if(at.getGameMode() != GameMode.SURVIVAL || TagManager.SPECTATOR_MODE.contains(at))
			return;
		
		Location l = at.getLocation().clone();
		
		Random r = new Random();
		
		int count = r.nextInt(4)+3;
		int mid = r.nextInt(3)+2;
		
		for(int i = 0; i < count; i++){
			if(i < mid)
				l.getWorld().dropItemNaturally(l, soupItemFull.clone());
			else
				l.getWorld().dropItemNaturally(l, potionItem.clone());
		}
	}
	
	@EventHandler
	public void OpenCategoryInventory(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(kitSelectionItem == null) return;
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		if(!e.getPlayer().getItemInHand().equals(kitSelectionItem)) return;
		
		Inventory inv = getCategoryInventory(e.getPlayer());
		e.getPlayer().openInventory(inv);
	}
	
	@EventHandler
	public void SelectKit(InventoryClickEvent e)
	{
		Player player = (Player)e.getWhoClicked();
		if(!HandlerFunctions.onPvpWorld(player)) return;
		if(e.getCurrentItem() == null) return;

		ItemMeta im = e.getCurrentItem().getItemMeta();
		
		if(im == null) return;
		
		if(e.getInventory().getName().compareTo(kitSelectionInventoryName) == 0)
		{			
			e.setCancelled(true);
			if(!im.hasDisplayName()) return;
			
			if(displayPointers.containsKey(im.getDisplayName()))
			{
				Kit kit = internalKits.get(displayPointers.get(im.getDisplayName()));
				
				//if(!kit.getDisplayItem().getItemMeta().getLore().equals(im.getLore())) return;
				
				player.closeInventory();
				setPlayerKit(player, kit, false);
			}
			
		}else if(e.getInventory().getName().compareTo(categorySelectionInventoryName) == 0)
		{
			e.setCancelled(true);
			if(im.getDisplayName() == normalCategoryItem.getItemMeta().getDisplayName())
			{
				player.closeInventory();
				Inventory inv = getKitInventory(player, null, 1);
				player.openInventory(inv);
			}else if(im.getDisplayName() == crateCategoryItem.getItemMeta().getDisplayName()){
				player.closeInventory();
				Inventory inv = getKitInventory(player, KitCategory.CRATE, 1);
				player.openInventory(inv);
			}else if(im.getDisplayName() == donatorCategoryItem.getItemMeta().getDisplayName()){
				player.closeInventory();
				Inventory inv = getKitInventory(player, KitCategory.DONATOR, 1);
				player.openInventory(inv);
			}
			
		}
		
	}
	
	public static ItemStack loadItemFromConfig(ConfigurationSection section)
	{
		if(!section.contains("material")) return null;
		
		//Get Material
		Material material = Material.getMaterial(section.getString("material"));
		
		int amount = 1;
		
		//Change amount if defined.
		if(section.contains("amount"))
			amount = section.getInt("amount");
		
		if(material == null) return null;
		
		//Create ItemStack
		ItemStack iStack = new ItemStack(material, amount);
		
		if(section.contains("durability"))
			iStack.setDurability((short)section.getInt("durability"));
		
		//Add enchantments if defined
		if(section.contains("enchantments"))
		{
			List<String> enchantments = section.getStringList("enchantments");
			
			for(String ench : enchantments)
			{
				String[] parts = ench.split(":");
				if(parts.length == 0) continue;
				
				Enchantment enchantment = Enchantment.getByName(parts[0]);
				if(enchantment == null) continue;
				int level = 1;
				if(parts.length >= 2)
					level = Integer.parseInt(parts[1]);
				
				iStack.addUnsafeEnchantment(enchantment, level);
			}	
		}
		
		ItemMeta meta = iStack.getItemMeta();
		
		if(section.contains("colour") && meta instanceof LeatherArmorMeta)
		{
			LeatherArmorMeta lam = (LeatherArmorMeta)meta;
			byte r = 0;
			byte g = 0;
			byte b = 0;
			
			if(section.contains("colour.red"))
				r = (byte)section.getInt("colour.red");
			if(section.contains("colour.green"))
				g = (byte)section.getInt("colour.green");
			if(section.contains("colour.blue"))
				b = (byte)section.getInt("colour.blue");
			
			if(r < 0) r = 0; if(r > 255) r = (byte) 255;
			if(g < 0) g = 0; if(g > 255) g = (byte) 255;
			if(b < 0) b = 0; if(b > 255) b = (byte) 255;
			
			lam.setColor(Color.fromRGB(r, g, b));
			iStack.setItemMeta(lam);
			meta = iStack.getItemMeta();
		}
		
		//Change item meta if defined
		if(section.contains("meta"))
		{
			//Display Name
			if(section.contains("meta.displayname"))
				meta.setDisplayName(
						Messager.formatColors(section.getString("meta.displayname")));
			
			if(section.contains("meta.colour") && meta instanceof LeatherArmorMeta)
			{
				LeatherArmorMeta lam = (LeatherArmorMeta)meta;
				byte r = 0;
				byte g = 0;
				byte b = 0;
				
				if(section.contains("meta.colour.red"))
					r = (byte)section.getInt("meta.colour.red");
				if(section.contains("meta.colour.green"))
					g = (byte)section.getInt("meta.colour.green");
				if(section.contains("meta.colour.blue"))
					b = (byte)section.getInt("meta.colour.blue");
				
				if(r < 0) r = 0; if(r > 255) r = (byte) 255;
				if(g < 0) g = 0; if(g > 255) g = (byte) 255;
				if(b < 0) b = 0; if(b > 255) b = (byte) 255;
				
				lam.setColor(Color.fromRGB(r, g, b));
				meta = (ItemMeta)lam;
			}
			
			//Description
			if(section.contains("meta.lore")){
				List<String> lore = section.getStringList("meta.lore");
				for(int i = 0; i < lore.size(); i++)
				{
					lore.set(i, Messager.formatColors(lore.get(i)));
				}
				meta.setLore(lore);
			}
			iStack.setItemMeta(meta);
		}
		
		return iStack;
	}
	
}
