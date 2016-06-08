package me.TerrorLT.TerrorPVP.Systems;

import java.util.List;

import me.TerrorLT.TerrorPVP.Main;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public enum TagManager {
	
	PLAYERS("tpvpPlayer"),
	OUTSIDE_SAFEZONE("tpvpOutSafezone"),
	SPECTATOR_MODE("tpvpSpectating"),
	
	BUILD_BYPASS("tpvpBuildBypass"),
	
	SELECTED_KIT("tpvpSelectedKit"),
	PREVIOUS_KIT("tpvpPreviousKit"),
	
	USES_POTIONS("tpvpUsePotions"),
	
	//Misc
	SET_SAFEZONE("tpvpSetSafezone"),
	SET_FLAMECHEST("tpvpSetFlameChest"),
	SET_WORLDZONE("tpvpSetWorldzone");
	
	private String tag;
	
	TagManager(String tag)
	{
		this.tag = tag;
	}
	
	public boolean contains(Metadatable md)
	{
		return md.hasMetadata(this.tag);
	}
	
	public void apply(Metadatable md)
	{
		apply(md, true);
	}
	
	public Object get(Metadatable md)
	{
		if(md == null || !contains(md))
			return null;
		
		List<MetadataValue> meta = md.getMetadata(this.tag);
		
		if(meta.size() == 0){
			remove(md);
			return null;
		}
		
		return meta.get(0).value();
	}
	
	public void apply(Metadatable md, Object obj)
	{
		if(md == null) return;
		
		md.setMetadata(this.tag, new FixedMetadataValue(Main.plugin, obj));
	}
	
	public void remove(Metadatable md)
	{
		if(md == null) return;
		
		md.removeMetadata(this.tag, Main.plugin);
	}
	
}
