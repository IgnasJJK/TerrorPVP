package me.TerrorLT.TerrorPVP.Kits;

import org.bukkit.ChatColor;

public enum Kits {

	ARCHER(new Archer()),
	MONKEY(new Monkey()),
	THOR(new Thor()),
	WIGHT(new Wight());
	
	private OldKit kit = null;
	private String permNode = null;
	private DataTypes type = DataTypes.DEFAULT;
	private int Price = 0;
	private int DropRate = 0;
	
	Kits(OldKit kit)
	{
		this.kit = kit;
	}
	
	public void setData(DataTypes type, String perm, int price, int droprate)
	{
		permNode = perm.toLowerCase();
		this.type = type;
		Price = price;
		DropRate = droprate;
	}
	
	public static Kits getByName(String EnumName)
	{
		for( Kits kit : Kits.values() )
		{
			if(kit.toString().equals(EnumName))
				return kit;
		}
		return null;
	}
	
	public static Kits getByItemName(String name)
	{
		name = ChatColor.stripColor(name);
		
		for(Kits kit : Kits.values())
		{
			if(kit.getKit().KitName.equalsIgnoreCase(name))
				return kit;
		}
		return null;		
	}
	
	public static Kits getByObject(Object obj)
	{
		for(Kits kit : Kits.values())
		{
			if(kit.getKit().equals(obj))
				return kit;
		}
		return null;
	}
	
	public String getNode()
	{
		return "terrorpvp.kits."+permNode;
	}
	public OldKit getKit()
	{
		return kit;
	}
	public DataTypes getType()
	{
		return type;
	}
	public int getPrice(){
		return Price;
	}
	
}
