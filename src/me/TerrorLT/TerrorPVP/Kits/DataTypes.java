package me.TerrorLT.TerrorPVP.Kits;

public enum DataTypes {

	SHOP("SHOP"),
	DEFAULT("DEFAULT"),
	KEYCRATE("KEYCRATE");
	
	private String typeName;
	
	DataTypes(String typeName)
	{
		this.typeName = typeName;
	}
	
	public static DataTypes getTypeByName(String name)
	{
		for(DataTypes type : DataTypes.values())
		{
			if(type.getTypeName().equalsIgnoreCase(name))
				return type;
		}
		return null;
	}
	
	public String getTypeName()
	{
		return typeName;
	}
	
}
