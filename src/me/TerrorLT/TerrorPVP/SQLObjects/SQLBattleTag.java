package me.TerrorLT.TerrorPVP.SQLObjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.SQLite.SQLite;

public class SQLBattleTag {
	
	String uuid;
	String claim;
	double damage;
	int timestamp;
	
	boolean uuidChanged = false;
	boolean claimChanged = false;
	boolean damageChanged = false;
	
	public SQLBattleTag(String uuid, String claim, double damage, int timestamp)
	{
		this.uuid = uuid;
		this.claim = claim;
		this.damage = damage;
		this.timestamp = timestamp;
	}
	
	public String getUUID()
	{
		return uuid;
	}
	
	public String getClaim()
	{
		return claim;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public int getTimestamp()
	{
		return timestamp;
	}
	
	public void negateDamage()
	{
		damage *= -1;
	}
	
	public void setClaim(String claim)
	{
		this.claim = claim;
		claimChanged = true;
	}
	
	public void setDamage(double damage)
	{
		this.damage = damage;
		damageChanged = true;
	}
	public void setTimestamp(int timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public static void sqlDeposit(SQLBattleTag tag)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Main.playData.getSqlConnection();
			
			ps = conn.prepareStatement("REPLACE INTO " + Globals.BATTLE_TAGS + " VALUES (?,?,?,?)");
			ps.setString(1, tag.getUUID());
			ps.setString(2, tag.getClaim());
			ps.setDouble(3, tag.getDamage());
			ps.setInt(4, tag.getTimestamp());
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			}catch(SQLException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public static SQLBattleTag SqlWithdraw(String uuid)
	{
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Main.playData.getSqlConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Globals.BATTLE_TAGS
            		+ " WHERE uuid = '"+uuid+"';");
         
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equals(uuid)){
                    return new SQLBattleTag(rs.getString("uuid"), 
                    		rs.getString("claim"), 
                    		rs.getDouble("damage"),
                    		rs.getInt("timestamp"));
                }
            }
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_EXECUTE, ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_CLOSE, ex);
            }
        }
        return null;
	}
	
	public static void removeSingle(String uuid)
	{
		Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Main.playData.getSqlConnection();
            ps = conn.prepareStatement("DELETE FROM " + Globals.BATTLE_TAGS
            		+ " WHERE uuid = '"+uuid+"'");
            ps.executeUpdate();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_EXECUTE, ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_CLOSE, ex);
            }
        }
	}
	
	public static void removeReferenced(String uuid)
	{
		Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Main.playData.getSqlConnection();
            ps = conn.prepareStatement("DELETE FROM " + Globals.BATTLE_TAGS
            		+ " WHERE uuid = '"+uuid+"' OR claim = '"+uuid+"'");
            ps.executeUpdate();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_EXECUTE, ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_CLOSE, ex);
            }
        }
	}
	
}
