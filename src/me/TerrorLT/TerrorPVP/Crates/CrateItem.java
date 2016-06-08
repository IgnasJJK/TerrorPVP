package me.TerrorLT.TerrorPVP.Crates;

public class CrateItem {

	public enum ItemType{
		NONE("NONE"),
		MONEY("MONEY"),
		KIT("KIT"),
		TITLE("TITLE"),
		OTHER("OTHER");
		
		String identifier = null;
		
		ItemType(String id)
		{
			identifier = id;
		}
		
		public String getIdentifier()
		{
			return identifier;
		}
		
		public static ItemType getById(String id)
		{
			for(ItemType type : ItemType.values())
				if(type.getIdentifier().compareTo(id) == 0)
					return type;
			
			return NONE;
		}
	}
	
	ItemType type = ItemType.NONE;
	
	String kitInternalName = null;
	int moneyAmount = 0;
	
	//TODO: Add internal names for titles and a title system
	
	int rarity = 0;
	
	public CrateItem(ItemType type, int rarity)
	{
		this.type = type;
		this.rarity = rarity;
	}
	
	public void setKit(String internal){
		kitInternalName = internal;
	}
	
	public void setMoney(int amount){
		moneyAmount = amount;
	}
	
	public ItemType getType(){
		return type;
	}
	
	public String getKit(){
		return kitInternalName;
	}
	
	public int getMoney(){
		return moneyAmount;
	}
	
	public int getRarity(){
		return rarity;
	}
	
	
}
