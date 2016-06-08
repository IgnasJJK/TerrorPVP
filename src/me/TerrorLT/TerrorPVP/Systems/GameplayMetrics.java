package me.TerrorLT.TerrorPVP.Systems;

import me.TerrorLT.TerrorPVP.Main;

public class GameplayMetrics {
	
	public void incrementKitUsage(String kit)
	{
		incrementNode("Usage."+kit);
	}
	
	public void incrementKitKills(String kit, String deadkit)
	{
		incrementNode("Kills." + deadkit + ".KilledBy." + kit);
	}
	
	public void incrementNode(String node)
	{
		if(!Main.metrics.configuration.contains(node))
		{
			Main.metrics.configuration.set(node, 1);
		}else{
			int c = Main.metrics.configuration.getInt(node);
			Main.metrics.configuration.set(node, c+1);
		}
		
		Main.metrics.saveConfiguration();
	}
	
}
