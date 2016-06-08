package me.TerrorLT.TerrorPVP.Systems;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Objects.HighscoreSign;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class Highscores implements Listener{

	HashMap<String, HighscoreSign> highscoreSigns = new HashMap<String, HighscoreSign>();
	HashMap<Integer, SQLPlayer> semiCache;
	DecimalFormat df = new DecimalFormat("####0.##");
	
	/**
	 * Used to register a sign that gets updated with high score information
	 * @param location Location of the sign
	 * @param place The place in the rankings which the sign should show
	 * @param timed Whether the sign shows overall or monthly high scores.
	 */
	public void addHighscoreSign(Location location, int place, boolean timed)
	{
		HighscoreSign sign = new HighscoreSign(location, place, timed);
		sign.put(Main.main.configuration, Globals.CNode_HighScores);
		Main.main.saveConfiguration();
		
		highscoreSigns.put(HighscoreSign.generateId(location), sign);
		
		doUpdateSigns();
	}
	
	/**
	 * Used to remove a high score sign from the database
	 * @param location location of the sign
	 */
	public void removeHighscoreSign(Location location)
	{
		String id = HighscoreSign.generateId(location);
		if(highscoreSigns.containsKey(id)) highscoreSigns.remove(id);
			Globals.removeNode(Globals.CNode_HighScores+"."+id);
	}
	
	/**
	 * Loads all sign data from a .yml file
	 * @param config the configuration to load the data from
	 */
	public void loadSigns()
	{
		if(!Main.main.configuration.contains(Globals.CNode_HighScores)) return;
		
		ConfigurationSection section = 
				Main.main.configuration.getConfigurationSection(Globals.CNode_HighScores);
		
		for(String s : section.getKeys(false))
		{
			HighscoreSign sign = (HighscoreSign)(new HighscoreSign()
				.retrieve(Main.main.configuration, Globals.CNode_HighScores+"."+s));
			
			if(sign.isValid())
				highscoreSigns.put(s, sign);
			else{
				Globals.removeNode(Globals.CNode_HighScores+"."+s);
			}
		}
		doUpdateSigns();
	}
	
	public void doUpdateSigns()
	{
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
			@Override
			public void run()
			{
				updateSigns();
			}
		});
		
	}
	
	public void updateSigns()
	{
		semiCache = new HashMap<Integer, SQLPlayer>();
		
		for( HighscoreSign sign : highscoreSigns.values())
		{			
			Location loc = sign.getLocation();
			if(loc == null) continue;
			if(loc.getBlock() == null) continue;
			if(loc.getBlock().getState() == null) continue;
			if(!(loc.getBlock().getState() instanceof Sign)) continue;
			
			Sign block = (Sign)loc.getBlock().getState();			
			SQLPlayer player;
			
			if(semiCache.containsKey(sign.getPlace()))
				player = semiCache.get(sign.getPlace());
			else player = SQLPlayer.SqlGetByPlace(sign.getPlace());
			
			block.setLine(0, ChatColor.YELLOW + "["+sign.getPlace()+"]");
			if(player == null){
				block.setLine(1, "");
				block.setLine(2, "");
				block.setLine(3, "");
				block.update();
				continue;
			}
			
			if(!semiCache.containsKey(sign.getPlace())) 
				semiCache.put(sign.getPlace(), player);
			
			Player plr = Bukkit.getPlayer(UUID.fromString(player.getUUID()));
			if(plr != null)
				block.setLine(1, plr.getName());
			else
				block.setLine(1, Bukkit.getOfflinePlayer(
						UUID.fromString(player.getUUID())).getName());
			if(sign.getTimed()){
				block.setLine(2, Messager.formatKD(player.getKills(), 
								player.getDeaths(), player.getStolenFrom()));
			}else{
				block.setLine(2, Messager.formatKD(player.getTotalKills(), 
						player.getTotalDeaths(), player.getTotalStolenFrom()));
			}
			block.setLine(3, Messager.formatDouble(player.getKdRatio()));
			block.update();
		}
		
		semiCache = null;
	}
	
	@EventHandler
	public void onSignPlace(SignChangeEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		
		if(!e.getLine(0).equalsIgnoreCase("[HSO]") &&
				!e.getLine(0).equalsIgnoreCase("[HST]")) return;
		
		int place = Integer.parseInt(e.getLine(1));
		boolean timed = false;
		
		if(e.getLine(0).equalsIgnoreCase("[HST]")) timed = true;
		
		addHighscoreSign(e.getBlock().getLocation(), place, timed);
	}
	
}
