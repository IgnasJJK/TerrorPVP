package me.TerrorLT.TerrorPVP.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.TerrorLT.TerrorPVP.Actions.ActionProfile;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Kit {

	private boolean Enabled = true;  //Is kit available for use
	private boolean Default = false; //Is kit available to all players

	private String internalName = null; //Used for internal identification
	
	ItemStack head = null;
	ItemStack torso = null;
	ItemStack legs = null;
	ItemStack feet = null;
	
	int price = 0;
	int droprate = 0;
	
	private ItemStack displayItem = null;
	
	private ItemStack[] inventoryItems = null;
	
	private PotionEffectType[] potionEffects = null;
	private int[] potionLevels = null;
	
	private ActionProfile actions = null;
	
	public void setEnabled(boolean Enabled)
	{
		this.Enabled = Enabled;
	}
	
	public void setDefault(boolean Default)
	{
		this.Default = Default;
	}
	
	public void setInternalName(String internalName)
	{
		this.internalName = internalName;
	}
	
	public void setArmor(ItemStack head, ItemStack torso, ItemStack legs, ItemStack feet)
	{
		this.head = head;
		this.torso = torso;
		this.legs = legs;
		this.feet = feet;
	}

	public void setDisplayItem(ItemStack item)
	{
		displayItem = item;
	}

	public void setInventory(ItemStack[] inventory)
	{
		if(inventory.length != 36) return;
		
		inventoryItems = inventory;
	}
	
	public void equipPlayer(Player player)
	{	
		TagManager.SELECTED_KIT.apply(player, this);
		TagManager.PREVIOUS_KIT.apply(player, getInternalName());
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(getArmor());
		
		if(inventoryItems != null)
		for(int i = 0; i < 36; i++)
		{
			if(inventoryItems[i] == null) continue;
			
			player.getInventory().setItem(i, inventoryItems[i]);
		}
		
		if(potionEffects != null)
		for( int i = 0; i < potionEffects.length; i++)
		{
			player.addPotionEffect(new PotionEffect(potionEffects[i], Integer.MAX_VALUE, potionLevels[i]), true);
		}
	}
	
	public ItemStack[] getArmor()
	{
		ItemStack[] armor = new ItemStack[4];
		if(feet != null)  armor[0] = feet;
		if(legs != null)  armor[1] = legs;
		if(torso != null) armor[2] = torso;
		if(head != null)  armor[3] = head;
		
		return armor;
	}
	
	public String getDisplayName()
	{
		if(!displayItem.hasItemMeta()) return internalName;
		if(!displayItem.getItemMeta().hasDisplayName()) return internalName;
		return displayItem.getItemMeta().getDisplayName();	
	}
	
	public String getInternalName()
	{
		return internalName;
	}
	
	public ItemStack getDisplayItem()
	{
		return displayItem;
	}
	
	public boolean isEnabled()
	{
		if(internalName == null) return false;
		if(displayItem == null) return false;
		return Enabled;
	}
	
	public boolean isDefault()
	{
		return Default;
	}
	
	public String getPerm()
	{
		return "terrorpvp.kits."+internalName;
	}
	
	public boolean hasKit(Player player)
	{
		if(isDefault()) return true;
		
		return player.hasPermission(getPerm());
	}
	
	public ActionProfile getActionProfile()
	{
		return actions;
	}
	
	public void setActionProfile(ActionProfile actionProfile)
	{
		if(actionProfile == null) return;
		
		actions = actionProfile;
	}
	
	public boolean hasActionProfile()
	{
		return actions != null;
	}
	
	public ItemStack getItem(int index)
	{
		if(index < 0 || index >= 36) return null;
		if(inventoryItems == null) return null;
		return inventoryItems[index];
	}
	
	public static Kit loadFromSection(ConfigurationSection section)
	{
		if(!section.contains("internal")) return null;
		if(!section.contains("displayitem")) return null;
		
		Kit kit = new Kit();
		
		ItemStack displayItem = KitSystem.loadItemFromConfig(section.getConfigurationSection("displayitem"));
		String internalName = section.getString("internal");		
		
		ItemStack[] armor = null;
		
		if(section.contains("enabled"))
			kit.setEnabled(section.getBoolean("enabled"));
		if(section.contains("default"))
			kit.setDefault(section.getBoolean("default"));
		
		//Load armor if set
		if(section.contains("armour"))
		{
			armor = new ItemStack[4];
			
			if(section.contains("armour.head"))
				armor[0] = KitSystem.loadItemFromConfig(section.getConfigurationSection("armour.head"));
			else armor[0] = null;
			if(section.contains("armour.torso"))
				armor[1] = KitSystem.loadItemFromConfig(section.getConfigurationSection("armour.torso"));
			else armor[1] = null;
			if(section.contains("armour.legs"))
				armor[2] = KitSystem.loadItemFromConfig(section.getConfigurationSection("armour.legs"));
			else armor[2] = null;
			if(section.contains("armour.feet"))
				armor[3] = KitSystem.loadItemFromConfig(section.getConfigurationSection("armour.feet"));
			else armor[3] = null;
		}
		
		if(section.contains("potioneffects"))
		{
			List<String> effs = section.getStringList("potioneffects");
			List<String> scanned = new ArrayList<String>();
			for(String s : effs)
			{
				String[] parts = s.split(":");
				if(PotionEffectType.getByName(parts[0]) != null)
					scanned.add(s);
			}
			
			kit.potionEffects = new PotionEffectType[scanned.size()];
			kit.potionLevels = new int[scanned.size()];
			
			for(int i = 0; i < scanned.size(); i++)
			{
				String[] parts = scanned.get(i).split(":");
				kit.potionEffects[i] = PotionEffectType.getByName(parts[0]);
				if(parts.length > 1)
				{
					int x = Integer.parseInt(parts[1]);
					kit.potionLevels[i] = x;
				}else{
					kit.potionLevels[i] = 1;
				}
			}
			
		}
		
		ItemStack[] inventory = null;
		
		//Load inventory if set
		if(section.contains("inventory"))
		{
			inventory = new ItemStack[36];
			
			for(int i = 0; i < 36; i++)
			{
				if(section.contains("inventory."+i)){
					ConfigurationSection itemSection = section.getConfigurationSection("inventory."+i);
					inventory[i] = KitSystem.loadItemFromConfig(itemSection);
				}
			}
		}
		

		
		kit.setDisplayItem(displayItem);
		
		if(armor != null) kit.setArmor(armor[0], armor[1], armor[2], armor[3]);
		if(internalName != null) kit.setInternalName(internalName);
		if(inventory != null) kit.setInventory(inventory);
		
		ActionProfile aProfile = ActionProfile.readProfile(section);
		if(aProfile != null){
			aProfile.setupTriggers(kit);
			kit.setActionProfile(aProfile);
		}
		return kit;
	}

}
