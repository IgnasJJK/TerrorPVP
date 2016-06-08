package me.TerrorLT.TerrorPVP.Objects;

import java.util.Random;

import me.TerrorLT.TerrorPVP.ExtFunctions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Kill {

	public static final boolean F = false;
	public static final boolean T = true;
	
	public boolean FARAWAY = F;
	public boolean ARTILLERY = F;
	public boolean BAREHANDED = F;
	public boolean WITHABOW = F;
	public boolean RANGED = F;
	public boolean STOLEN = F;
	
	static final int PKILL = 100;
	static final int PFARAWAY = 50;
	static final int PARTILLERY = 100;
	static final int PBAREHANDED = 20;
	static final int PWITHABOW = 20;
	static final int PRANGED = 0;
	static final int PSTOLEN = 0;
	
	static final double FARAWAY_DIST = 20d;
	static final double ARTILLERY_DIST = 40d;
	
	public static final int STEAL_PAYBACK = 30;
	
	private int pointCount = -1;
	
	//TODO: ^ Make all of these customizable
	
	Random r = new Random();
	
	public int getPoints()
	{
		if(pointCount < 0){
		int points = PKILL + (PKILL != 0 ? PKILL + r.nextInt(20)-10 : 0);
		
			if(RANGED) points += PRANGED + (PRANGED != 0 ? PRANGED + r.nextInt(10)-5 : 0);
			if(ARTILLERY) points += PARTILLERY + (PARTILLERY != 0 ? PARTILLERY + r.nextInt(10)-5 : 0);
			else if(FARAWAY) points += PFARAWAY + (PFARAWAY != 0 ? PFARAWAY + r.nextInt(10)-5 : 0);
		
			if(WITHABOW) points += PWITHABOW + (PWITHABOW != 0 ? PWITHABOW + r.nextInt(10)-5 : 0);
			if(BAREHANDED) points += PBAREHANDED + (PBAREHANDED != 0 ? PBAREHANDED + r.nextInt(10)-5 : 0);
			
			pointCount = points;
		}
		
		return pointCount;
		
	}
	
	public static Kill process(Player corpse, Player killer, DamageCause cause)
	{
		Kill kill = new Kill();
		
		if(killer == null) return kill;
		
		if(cause == DamageCause.PROJECTILE){
			
			double distance = ExtFunctions.getDistanceBetween(corpse.getLocation(), killer.getLocation());

			if(distance > ARTILLERY_DIST) kill.setArtillery();
			if(distance > FARAWAY_DIST && !kill.ARTILLERY) kill.setFaraway();
			kill.setRanged();
			
		}else if(cause == DamageCause.ENTITY_ATTACK)
		{	
			if(killer.getItemInHand() != null)
			{
				if(killer.getItemInHand().getType() == Material.BOW) kill.setWithABow();
			}else kill.setBarehanded();
			
		}
		return kill;
		
	}
	
	public String GetKillMessage()
	{
		String message = "KILL";
		
		if(ARTILLERY) message = "ARTILLERY " + message;
		if(RANGED && !ARTILLERY) message = "RANGED " + message;
		if(FARAWAY) message = "FARAWAY " + message; 
		if(BAREHANDED) message = "BAREHANDED " + message;
		if(WITHABOW) message += " WITH A.. BOW?!";
		if(STOLEN) message = "STOLEN " + message;
		
		return message;	
	}
	
	public void setFaraway(){FARAWAY = T; setRanged();}
	public void setArtillery(){ARTILLERY = T; setRanged();}
	public void setBarehanded(){BAREHANDED = T;}
	public void setWithABow(){WITHABOW = T;}
	public void setRanged(){RANGED=T;}
	public void setStolen(){STOLEN=T;}
	
}
