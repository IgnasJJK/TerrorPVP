package me.TerrorLT.TerrorPVP.Handling;

import me.TerrorLT.TerrorPVP.ExtFunctions;
import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Kits.Kit;
import me.TerrorLT.TerrorPVP.Objects.Kill;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayer;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.connorlinfoot.titleapi.TitleAPI;

public class HandlerFunctions {

	//TODO: Reorganize function order
	
	//Checks if player is on the specified world
	public static boolean onPvpWorld(Player player){ 
		return onPvpWorld(player.getWorld()); 
	}
	public static boolean onPvpWorld(World world){ 
		return world.getName().compareTo(Globals.worldName) == 0; 
	}
	
	//checks if player is in the safezone
	public static boolean withinSafezoneBounds(Location location)
	{
		if(!Globals.isSafezoneSet()) return false;
		if(!onPvpWorld(location.getWorld())) return false;
		
		return Globals.safeZone.within(location);
	}
	
	//Sets a player as one who left the safezone
	public static void leaveSafezone(Player player)
	{
		TagManager.OUTSIDE_SAFEZONE.apply(player);
		TitleAPI.sendTitle(player, 5, 40, 5, "", Messager.Safezone_OnLeave);
	}

	//Default state for the world
	public static void ResetState(Player player)
	{
		
		CleanPlayer(player);
		
		//Give special items
		GiveGuiItems(player);
		
		//Particles
		playParticleEffect(player, player.getLocation(), Effect.SMALL_SMOKE, null, 20);
	}
	
	public static void GiveGuiItems(Player player)
	{
		Main.kitSystem.giveKitSelectionItem(player);
		
		player.getInventory().addItem(Main.spectatorSystem.getSpectatorModeItem());
		player.getInventory().addItem(Main.titleSystem.getTitleSelectItem());
		player.getInventory().addItem(Main.shopSystem.getShopItem());
		player.getInventory().addItem(Main.kitSystem.getHealthOptionsItem(player));
		
		//player.getInventory().addItem(ShopManager.getItemShopItem());
		//SQLPlayerKeys keys = SQLPlayerKeys.WithdrawOrDefault(player.getUniqueId().toString());
		//if(keys.getKeys() > 0) 
			//player.getInventory().addItem(LootCrate.getKeyItem(keys.getKeys()));
					
	}
	
	//Clears of all damage, clears inventory, and teleports to spawn point.
	public static void CleanPlayer(final Player player)	{
		Main.spectatorSystem.setSpectatorMode(player, false);
		player.setHealthScale(20d);
		player.setHealth(20d);
		if(Globals.getKitPvpSpawnLocation() != null){
			player.teleport(Globals.getKitPvpSpawnLocation());
		}
		player.setFallDistance(0);
		
		TagManager.SELECTED_KIT.remove(player);
		TagManager.PLAYERS.remove(player);
		
		for(PotionEffect e: player.getActivePotionEffects())
		{
			player.removePotionEffect(e.getType());
		}
		
		//player.setFoodLevel(20);
		CleanPlayerInventory(player);
		Bukkit.getScheduler().runTask(Main.plugin, new Runnable() {			 
		    @Override
		    public void run() {
		        player.setFireTicks(0);
		    }
		});
		
		if(withinSafezoneBounds(player.getLocation()))
			TagManager.OUTSIDE_SAFEZONE.remove(player);
	}
	
	public static void CleanPlayerInventory(Player player)
	{
		player.getInventory().clear();
		RemoveArmor(player);
	}
	
	//Removes a player's armor
	public static void RemoveArmor(Player player)
	{
		player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
	}
	
	//Plays an amount of a particle for a player TODO: Make it work nicely
	public static <T> void playParticleEffect(Player player, Location location, Effect effect, T data, int amount)
	{
		if(!Globals.particlesActive) return;
		for(int i = 0; i < amount; i++)
			player.playEffect(location, effect, data);
	}
	
	//Get damager entity by cause (in case the original entity is not a player, e.g. arrow)
	public static Entity getDamagerEntity(DamageCause cause, Entity eDamager)
	{
		if(eDamager == null) return null;
		if(cause == null) return null;
		
		if(cause == DamageCause.PROJECTILE){
			if(eDamager instanceof Arrow)
			{
				Arrow arrow = (Arrow) eDamager;
				if(arrow.getShooter() instanceof Entity)
					return (Entity)arrow.getShooter();
			}
			if(eDamager.getType().equals(EntityType.FISHING_HOOK))
			{
				FishHook hook = (FishHook) eDamager;
				if(hook.getShooter() instanceof Entity)
					return (Entity)hook.getShooter();
			}
		}else{
			return eDamager;
		}
		return null;
	}
	
	//Calculates if the damage dealt kills a player.
	public static boolean isDamageLethal(Player target, double damage)
	{
		return target.getHealth() - damage * (1d - ExtFunctions.getDamageReduced(target)) <= 0;
	}
	
	//Repairs a player's armor
	public static void RepairArmor(Player player)
	{
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++)
			if(armor[i] != null)
				armor[i].setDurability((short)0);
		player.getInventory().setArmorContents(armor);
	}
	
	public static ItemStack RepairItem(ItemStack item)
	{
		item.setDurability((short)0);
		return item;
	}
	
	//Reset all players
	public static void ResetAllPlayers()
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers())
		{
			if(!onPvpWorld(p)) continue;
			ResetState(p);
			Messager.setTabKDP(p, SQLPlayer.getPlayerOrDefault(p.getUniqueId().toString()));
		}
	}
	
	//Universal kill processing method.
	public static void processKill(Player cadaver, Player slayer, DamageCause cause)
	{
		if(withinSafezoneBounds(cadaver.getLocation())) return;
		
		if(slayer != null)
		Main.gameplayMetrics.incrementKitKills(
				((Kit)TagManager.SELECTED_KIT.get(slayer)).getInternalName(), 
				((Kit)TagManager.SELECTED_KIT.get(cadaver)).getInternalName());
		
		Kill kill = Kill.process(cadaver, slayer, cause);
		if(!withinSafezoneBounds(cadaver.getLocation()))
			Main.kitSystem.dropHealthItems(cadaver);
		ResetState(cadaver);
		Player claim = Main.battleTag.getKiller(cadaver);
		
		if(cause == DamageCause.SUICIDE)
		{
			if(claim == null)
			{
				if(Globals.suicideIsDeath) 
					Main.playerManager.addDeath(cadaver);
			}else Main.playerManager.addDeath(cadaver);
		}else if(claim != null){
			Main.playerManager.addDeath(cadaver);
		}else if(claim == null && Globals.noPlayerDeathIsDeath) Main.playerManager.addDeath(cadaver);
		
		//Removes battletag data in the database
		Main.battleTag.Kill(cadaver);
		
		String deathMsg = "";
		String deathSubtitle = "";
		
		if(cause == DamageCause.SUICIDE)
			deathMsg = Messager.DeathT_Suicide;
		else
			deathMsg = Messager.DeathT_Simple;
		
		if(claim != null)
		{
			if(slayer != null) //Kill
			{
				deathSubtitle = Messager.formatPlayerString(slayer, Messager.DeathS_KilledBy);
			}else{ //Claim kill (death by misc.)
				deathSubtitle = Messager.formatPlayerString(claim, Messager.DeathS_ClaimedBy);
			}
		}
		TitleAPI.sendTitle(cadaver, 2, 24, 12, deathMsg, deathSubtitle);
		
		
		//Kill processing
		if(claim != null)
		{
			if(slayer == null || slayer == claim) //Kill to claimer
			{
				Main.playerManager.addKill(claim, kill.getPoints(), false);
				Messager.DeliverKillMessage(claim, kill);
			}else{ //Kill stolen
				kill.setStolen();
				Main.playerManager.addKill(slayer, kill.getPoints(), true);
				Messager.DeliverKillMessage(slayer, kill);
				Main.playerManager.addStolenFrom(claim);
				Messager.DeliverStolenKillMessage(claim, slayer, Kill.STEAL_PAYBACK);
			}
		}
		
		Main.highscores.doUpdateSigns();
	}	
	
	public static void bounceBack(Player player, Location from, Location to)
	{
		if(!from.getWorld().equals(to.getWorld())) return;
		
		player.teleport(from);
		
		Vector velocity = new Vector();
		velocity.setX(from.getX() - to.getX());
		velocity.setZ(from.getZ() - to.getZ());
		velocity.setY((from.getY() - to.getY())*0.5d);
		
		/*if(Math.abs(velocity.getX()) < Globals.minimumVelocity)
			velocity.setX((velocity.getX() < 0 ? -1 : 1) * Globals.minimumVelocity);
		if(Math.abs(velocity.getZ()) < Globals.minimumVelocity)
			velocity.setZ((velocity.getZ() < 0 ? -1 : 1) * Globals.minimumVelocity);*/
		
		/*if(Globals.bounceForward){
		if(Globals.safeZone.zBorder(e.getTo()) && !Globals.safeZone.xBorder(e.getTo()))
			velocity.setX(velocity.getX()*-1);
		if(Globals.safeZone.xBorder(e.getTo()) && !Globals.safeZone.zBorder(e.getTo()))
			velocity.setZ(velocity.getZ()*-1);
		}*/
		
		
		velocity.multiply(2.3d);
		
		player.setVelocity(velocity);
	}
	
}
