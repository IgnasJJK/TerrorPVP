package me.TerrorLT.TerrorPVP.Handling;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.Messager;
import me.TerrorLT.TerrorPVP.Objects.Kill;
import me.TerrorLT.TerrorPVP.SQLObjects.SQLPlayer;
import me.TerrorLT.TerrorPVP.Systems.TagManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class DeathHandler implements Listener{
	
	public static List<Material> autoRepair;
	
	public DeathHandler()
	{
		autoRepair = new ArrayList<Material>();
		autoRepair.add(Material.WOOD_SWORD);
		autoRepair.add(Material.STONE_SWORD);
		autoRepair.add(Material.IRON_SWORD);
		autoRepair.add(Material.GOLD_SWORD);
		autoRepair.add(Material.DIAMOND_SWORD);
		autoRepair.add(Material.WOOD_AXE);
		autoRepair.add(Material.STONE_AXE);
		autoRepair.add(Material.IRON_AXE);
		autoRepair.add(Material.GOLD_AXE);
		autoRepair.add(Material.DIAMOND_AXE);
		autoRepair.add(Material.WOOD_HOE);
		autoRepair.add(Material.STONE_HOE);
		autoRepair.add(Material.IRON_HOE);
		autoRepair.add(Material.GOLD_HOE);
		autoRepair.add(Material.DIAMOND_HOE);
		autoRepair.add(Material.WOOD_SPADE);
		autoRepair.add(Material.STONE_SPADE);
		autoRepair.add(Material.IRON_SPADE);
		autoRepair.add(Material.GOLD_SPADE);
		autoRepair.add(Material.DIAMOND_SPADE);
		autoRepair.add(Material.WOOD_PICKAXE);
		autoRepair.add(Material.STONE_PICKAXE);
		autoRepair.add(Material.IRON_PICKAXE);
		autoRepair.add(Material.GOLD_PICKAXE);
		autoRepair.add(Material.DIAMOND_PICKAXE);
		//autoRepair.add(Material.BOW);
		autoRepair.add(Material.FISHING_ROD);
	}
	
	@EventHandler
	public void repairOnInteraction(PlayerInteractEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer()))return;
		if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK &&
				e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
		
		if(autoRepair.contains(e.getPlayer().getItemInHand().getType()))
		{
			e.getPlayer().getItemInHand().setDurability((short)0);
		}
		
	}
	
	@EventHandler
	public void onArrowShoow(EntityShootBowEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getEntity().getWorld())) return;
		if(e.getEntityType() != EntityType.PLAYER) return;
		
		Player player = (Player)e.getEntity();
		
		e.getBow().setDurability((short)0);
		player.setItemInHand(e.getBow());
		
		//Player player = (Player)e.getEntity();	
		//player.getItemInHand().setDurability((short)0);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getEntity())) return;
		HandlerFunctions.processKill(e.getEntity(), null, DamageCause.MAGIC);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamagedByEntity(EntityDamageByEntityEvent e)
	{
		if(!(e.getEntity() instanceof Player)) return;
		if(!HandlerFunctions.onPvpWorld(e.getEntity().getWorld())) return;
		
		Player target = (Player)e.getEntity();
		Entity damager = HandlerFunctions.getDamagerEntity(e.getCause(), e.getDamager());
		
		if(!(damager instanceof Player)){
			e.getDamager().remove();//Despawns non-player attackers
			damager.remove();		//and their projectiles. 
			e.setCancelled(true);
			return;
		}
		
		/*if(e.getDamager() instanceof Arrow)
		{
			if(TagManager.SPECTATOR_MODE.contains(target))
			{
				Arrow arrow = (Arrow) e.getDamager();
				Vector velocity = arrow.getVelocity();
	 
	            Player shooter = (Player) arrow.getShooter();
	            
	            //target.teleport(target.getLocation().add(velocity.multiply(-2)));
	            target.teleport(target.getLocation().add(0,5,0));
	            
	            target.setFlying(true);
	               
	            Arrow newArrow = shooter.launchProjectile(Arrow.class);
	            newArrow.setShooter(shooter);
	            newArrow.setVelocity(velocity);
	            newArrow.setBounce(false);
	            
	            try {
	            	Method getHandleMethod = arrow.getClass().getMethod("getHandle");
	            	Object handle = getHandleMethod.invoke(arrow);
	            	Field fromPlayerField = handle.getClass().getField("fromPlayer");
	            	fromPlayerField.setInt(handle, 2);
	            } catch (Throwable ex) {
	            	ex.printStackTrace(); // Up to you to ignore this, might break (gracefully-ish) on new MC versions.
	           	}
	            
	            e.setCancelled(true);
	            arrow.remove();
	            return;
			}
		}*/
		
		if(TagManager.SPECTATOR_MODE.contains(target)){ 
			e.setCancelled(true);
			return;
		}
		
		
		Player assailant = (Player) damager;
		if(TagManager.SPECTATOR_MODE.contains(assailant)) //Anti Spectator PVP
		{
			e.setCancelled(true);
			return;
		}
		if(assailant.equals(target)) //Stops arrow damage on self
		{
			e.setCancelled(true);
			return;
		}
		
		if(HandlerFunctions.withinSafezoneBounds(target.getLocation()) ||
				HandlerFunctions.withinSafezoneBounds(assailant.getLocation()))
		{	//If either the attacker or the attacked are in the safe zone
			e.setCancelled(true);
			return;
		}
		
		Main.battleTag.Tag(target, assailant, e.getDamage());

		//Kills player if the damage is lethal
		if(HandlerFunctions.isDamageLethal(target, e.getDamage())){ 
			e.setCancelled(true);
			
			HandlerFunctions.processKill(target, assailant, e.getCause());
			
			return;
		}
		HandlerFunctions.RepairArmor(target);
	}
	
	//Damaged by environment
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamagedByOther(EntityDamageEvent e){
		if(!(e.getEntity() instanceof Player)) return;
		
		Player player = (Player)e.getEntity();
		if(!HandlerFunctions.onPvpWorld(player)) return;
		
		if(TagManager.SPECTATOR_MODE.contains(player)){ 
			e.setCancelled(true);
			return;
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK ||
				e.getCause() == DamageCause.PROJECTILE) return;
		
		if(e.getCause() == DamageCause.FALL){
		
			Location loc = player.getLocation();
			loc.setY(loc.getY() -2);
			Block b = loc.getBlock();
			
			if(b != null)
				if(b.getType() == Material.SPONGE)
				{
					e.setCancelled(true);
					return;
				}
		
		}
		
		if(HandlerFunctions.isDamageLethal(player, e.getDamage())){
			e.setCancelled(true);
			HandlerFunctions.processKill(player, null, e.getCause());
			return;
		}
		HandlerFunctions.RepairArmor(player);
	}
	
	//For commands that kill one
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {	
		Player sender = event.getPlayer();
	    if(!HandlerFunctions.onPvpWorld(sender)) return;
		
		String[] parts = event.getMessage().split(" ");
		if (parts[0].equalsIgnoreCase("/suicide")) {
	    	event.setCancelled(true);
	    	if(HandlerFunctions.withinSafezoneBounds(sender.getLocation())){
	    		HandlerFunctions.ResetState(sender);
	    		return;
	    	}
	    	HandlerFunctions.processKill(sender, null, DamageCause.SUICIDE);
	    }
	}
	
	//Default state event handlers
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		if(!HandlerFunctions.onPvpWorld(player)) return;
		Messager.setTabKDP(player, 
				SQLPlayer.getPlayerOrDefault(player.getUniqueId().toString()));
		HandlerFunctions.ResetState(player);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKicked(PlayerKickEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		
		if (TagManager.OUTSIDE_SAFEZONE.contains(e.getPlayer())) {
			Player killer = Main.battleTag.getKiller(e.getPlayer());
			Main.battleTag.Kill(e.getPlayer());

			if (killer != null) {
				Main.playerManager.addKill(killer, Kill.process(e.getPlayer(), killer, DamageCause.CUSTOM).getPoints(), false);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogout(PlayerQuitEvent e)
	{
		if(!HandlerFunctions.onPvpWorld(e.getPlayer())) return;
		
		if(TagManager.OUTSIDE_SAFEZONE.contains(e.getPlayer())){
			Player killer = Main.battleTag.getKiller(e.getPlayer());
			Main.battleTag.Kill(e.getPlayer());
			
			if(killer != null){
				Main.playerManager.addKill(killer, Kill.process(e.getPlayer(), killer, DamageCause.CUSTOM).getPoints(), false);
				Main.playerManager.addDeath(e.getPlayer());
				//TODO: Perhaps penalize the player somehow?
			}
		}
	}
	
}
