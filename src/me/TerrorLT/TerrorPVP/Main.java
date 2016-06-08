package me.TerrorLT.TerrorPVP;

import java.util.logging.Logger;

import me.TerrorLT.TerrorPVP.Actions.ActionHandler;
import me.TerrorLT.TerrorPVP.Handling.BlockHandler;
import me.TerrorLT.TerrorPVP.Handling.DeathHandler;
import me.TerrorLT.TerrorPVP.Handling.HandlerFunctions;
import me.TerrorLT.TerrorPVP.Handling.SafezoneHandler;
import me.TerrorLT.TerrorPVP.Kits.KitSystem;
import me.TerrorLT.TerrorPVP.SQLite.Database;
import me.TerrorLT.TerrorPVP.Systems.BattleTag;
import me.TerrorLT.TerrorPVP.Systems.CooldownManager;
import me.TerrorLT.TerrorPVP.Systems.GameplayMetrics;
import me.TerrorLT.TerrorPVP.Systems.LootCrate;
import me.TerrorLT.TerrorPVP.Systems.Highscores;
import me.TerrorLT.TerrorPVP.Systems.PlayerManager;
import me.TerrorLT.TerrorPVP.Systems.ShopSystem;
import me.TerrorLT.TerrorPVP.Systems.SpectatorSystem;
import me.TerrorLT.TerrorPVP.Systems.TagManager;
import me.TerrorLT.TerrorPVP.Systems.TitleSystem;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

public class Main extends JavaPlugin {
	
	//Bukkit objects
	public static JavaPlugin plugin;
	public static Logger logger;
	//public PluginManager manager;
	
	//Databases
	public static Database playerData;
	public static Database playData;
	
	//Systems
	public static KitSystem kitSystem;
	public static CooldownManager cooldownManager;
	public static BattleTag battleTag;
	public static Highscores highscores;
	public static PlayerManager playerManager;
	//public static ShopManager shopManager;
	
	public static SpectatorSystem spectatorSystem;
	public static TitleSystem titleSystem;
	public static ShopSystem shopSystem;
	
	public static GameplayMetrics gameplayMetrics;
	
	public static Configuration main;
	public static Configuration data;
	public static Configuration kits;
	public static Configuration cats;
	public static Configuration shop;
	public static Configuration metrics;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		logger = getLogger();
		//manager = getServer().getPluginManager();
		
		setupDatabases();
		
		ActionBarAPI.enable(plugin);
		
		initializeConfigs();
		
		getServer().getPluginManager().registerEvents(new DeathHandler(), plugin);
		getServer().getPluginManager().registerEvents(new SafezoneHandler(), plugin);
		getServer().getPluginManager().registerEvents(new ActionHandler(), plugin);
		getServer().getPluginManager().registerEvents(new BlockHandler(), plugin);
		
		HandlerFunctions.ResetAllPlayers();
		
	}
	
	@Override
	public void onDisable()
	{
	}
	
	private void initializeConfigs()
	{	

		main = new Configuration(getDataFolder(), "config.yml");
		data = new Configuration(getDataFolder(), "data.yml");
		kits = new Configuration(getDataFolder(), "kits.yml");
		cats = new Configuration(getDataFolder(), "categories.yml");
		shop = new Configuration(getDataFolder(), "shop.yml");
		metrics = new Configuration(getDataFolder(), "metrics.yml");
		
		Globals.ReadConfiguration();
		
		kitSystem = new KitSystem();
		cooldownManager = new CooldownManager();
		battleTag = new BattleTag();
		highscores = new Highscores();
		playerManager = new PlayerManager();
		//shopManager = new ShopManager();
		
		spectatorSystem = new SpectatorSystem();
		titleSystem = new TitleSystem();
		shopSystem = new ShopSystem();
		
		gameplayMetrics = new GameplayMetrics();
		
		highscores.loadSigns();

		getServer().getPluginManager().registerEvents(spectatorSystem, plugin);
		getServer().getPluginManager().registerEvents(shopSystem, plugin);
		getServer().getPluginManager().registerEvents(kitSystem, plugin);
		getServer().getPluginManager().registerEvents(highscores, plugin);
		getServer().getPluginManager().registerEvents(titleSystem, plugin);		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("tkresetdbs"))
		{
			if(!sender.hasPermission("terrorpvp.resetdatabases"))
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			playData.dropTableIfExists(Globals.BATTLE_TAGS);
			playerData.dropTableIfExists(Globals.PLAYERS);
			playerData.dropTableIfExists(Globals.PLAYER_KEYS);
			setupDatabases();
			sender.sendMessage(Messager.DB_RESET_SUCCESS);
			HandlerFunctions.ResetAllPlayers();
			highscores.doUpdateSigns();
			return true;
		}
		
		if(args.length >= 1)
		if(cmd.getName().equalsIgnoreCase("tpvp") && args[0].equalsIgnoreCase("reload"))
		{
			if(!sender.hasPermission("terrorpvp.reload"))
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			HandlerFunctions.ResetAllPlayers();
			initializeConfigs();
			sender.sendMessage(ChatColor.GREEN + "Reloaded config.");
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("tkgivekeys"))
		{
			if(!sender.hasPermission("terrorpvp.keys"))
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			if(args.length < 2){
				sender.sendMessage(ChatColor.RED + "Missing arguments.");
				return true;
			}
			try{
			int x = Integer.parseInt(args[1]);
			String id = args[0];
			
			LootCrate.addKeys(id, x);
			sender.sendMessage(ChatColor.GREEN + "Keys successfully delivered.");
			}catch(NumberFormatException e)
			{
				return true;
			}
			
			return true;
		}
		
		if(!(sender instanceof Player)){
			Messager.Deliver(sender, Messager.Cannot_Command_Console);
		}
		
		Player player = (Player) sender;
		
		if(player == null) return true;
		
		if(args.length >= 2)
		if(cmd.getName().equalsIgnoreCase("tpvp") && args[0].equalsIgnoreCase("setzone")){
			
			if(!player.hasPermission("terrorpvp.setzone"))
			{
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			//TODO: Make it less complicated
			if(args[1].equalsIgnoreCase("safe")){
				if(TagManager.SET_WORLDZONE.contains(player)) return true;
				if(TagManager.SET_SAFEZONE.contains(player))
				{
					SafezoneHandler.StopSetSafezone(player);
				}else{
					SafezoneHandler.TempP1 = null;
					TagManager.SET_SAFEZONE.apply(player);
					Messager.Deliver(player, Messager.Zone_SetP1);
				}
				return true;
			}else if(args[1].equalsIgnoreCase("world"))
			{
				if(TagManager.SET_SAFEZONE.contains(player)) return true;
				
				if(TagManager.SET_WORLDZONE.contains(player))
				{
					SafezoneHandler.StopSetWorldzone(player);
				}else{
					SafezoneHandler.TempP1 = null;
					TagManager.SET_WORLDZONE.apply(player);
					Messager.Deliver(player, Messager.Zone_SetP1);
				}
				return true;
			}//TODO: else if crate
		}
		if(cmd.getName().equalsIgnoreCase("tpvp") && args[0].equalsIgnoreCase("unsetzone")){
			
			if(!player.hasPermission("terrorpvp.setzone"))
			{
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			if(args[1].equalsIgnoreCase("safe")){
				return true;
			}else if(args[1].equalsIgnoreCase("world"))
			{
				return true;
			}//TODO: else if crate
		}
		
		if(cmd.getName().equalsIgnoreCase("tkcc")){
			
			if(!player.hasPermission("terrorpvp.cooldowns.clear"))
			{
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			
			cooldownManager.clearCooldowns(player);
			return true;
		}
		/*if(cmd.getName().equalsIgnoreCase("tksetchest"))
		{
			if(TagManager.SET_FLAMECHEST.contains(player))
			{
				TagManager.SET_FLAMECHEST.remove(player);
				player.sendMessage(Messager.LootChest_SetCancel);
			}else{
				TagManager.SET_FLAMECHEST.apply(player);
				player.sendMessage(Messager.LootChest_Set);
			}		
			return true;
		}*/
		
		if(cmd.getName().equalsIgnoreCase("kitpvp"))
		{
			//player.teleport(Globals.getKitPvpSpawnLocation());
			if(player.getWorld().equals(Bukkit.getWorld(Globals.worldName))) return true;
			
			HandlerFunctions.ResetState(player);
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("hub") || cmd.getName().equalsIgnoreCase("spawn"))
		{
			player.teleport(getServer().getWorld(Globals.hubWorldName).getSpawnLocation());
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("build"))
		{
			if(!player.hasPermission("terrorpvp.build")){
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
				
			
			if(args.length == 0) return true;
			
			if(args[0].equalsIgnoreCase("bypass"))
			{
				if(args.length == 1)
				{
					TogglePlayerBuildBypass(player);
					return true;
				}
				if(args.length > 1)
				{
					Player p = getServer().getPlayer(args[1]);
					if(p != null)
					{
						TogglePlayerBuildBypass(p);
						player.sendMessage(ChatColor.GREEN + "Building bypass enabled for "+ p.getDisplayName());
						return true;
					}else{
						player.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}
				}
			}
			return true;
		}
		
		
		if(cmd.getName().equalsIgnoreCase("tkspawn"))
		{
			if(!player.hasPermission("terrorpvp.tkspawn"))
			{
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
			HandlerFunctions.ResetState(player);
			return true;
		}
		
		
		return false; 
	}
	
	private void TogglePlayerBuildBypass(Player player)
	{
		if(TagManager.BUILD_BYPASS.contains(player))
		{
			TagManager.BUILD_BYPASS.remove(player);
			player.sendMessage(ChatColor.GREEN + "Building bypass disabled.");
		}else{
			TagManager.BUILD_BYPASS.apply(player);
			player.sendMessage(ChatColor.GREEN + "Building bypass enabled.");
		}
	}
	
	private void setupDatabases()
	{
		playerData = new Database(getLogger(), "players", getDataFolder());
		playData = new Database(getLogger(), "dynamics", getDataFolder());
		
		playerData.createTableIfNotExists(Globals.PLAYERS, "`uuid` VARCHAR(128) UNIQUE NOT NULL,"+
		"`kills` INT DEFAULT 0,`deaths` INT DEFAULT 0,`points` INT DEFAULT 0," +
		"`totalKills` INT DEFAULT 0,`totalDeaths` INT DEFAULT 0,"+
		"`stolen` INT DEFAULT 0,`stolenFrom` INT DEFAULT 0,`kdRatio` DOUBLE DEFAULT 0,"+
		"`kdDifference` INT DEFAULT 0,`totalStolen` INT DEFAULT 0,"+
		"`totalStolenFrom` INT DEFAULT 0,PRIMARY KEY (`uuid`)");
		
		playerData.createTableIfNotExists(Globals.PLAYER_KEYS, 
				"`uuid` VARCHAR(128) UNIQUE NOT NULL,`keys` INT DEFAULT 0,`killKeys` INT DEFAULT 0,PRIMARY KEY (`uuid`)");
		
		playData.createTableIfNotExists(Globals.BATTLE_TAGS, "`uuid` VARCHAR(128) UNIQUE NOT NULL,"+
		"`claim` VARCHAR(128) NOT NULL,`damage` DOUBLE DEFAULT 0,`timestamp` INT DEFAULT 0,"+
				"PRIMARY KEY (`uuid`)");
	}
	
}
