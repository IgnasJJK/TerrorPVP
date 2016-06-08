package me.TerrorLT.TerrorPVP.SQLObjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.SQLite.SQLite;

public class SQLPlayerKeys {
	
	String uuid;
	int keys;
	int killKeys;
	
	public SQLPlayerKeys(String uuid)
	{
		this(uuid, 0, 0);
	}
	
	public SQLPlayerKeys(String uuid, int keys, int killKeys)
	{
		this.uuid = uuid;
		this.keys = keys;
		this.killKeys = killKeys;
	}
	
	public String getUUID(){return uuid;}
	public int getKeys(){return keys;}
	public int getKillKeys(){return killKeys;}
	
	public void setKeys(int x) {keys = x;}
	public void setKillKeys(int x){killKeys = x;}
	
	public static void sqlDeposit(SQLPlayerKeys player)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Main.playerData.getSqlConnection();
			
			ps = conn.prepareStatement("REPLACE INTO " + Globals.PLAYER_KEYS + 
					" VALUES (?,?,?)");
			ps.setString(1, player.getUUID());
			ps.setInt(2, player.getKeys());
			ps.setInt(3, player.getKillKeys());
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
	
	public static SQLPlayerKeys SqlWithdraw(String uuid)
	{
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SQLPlayerKeys player = null;
        
        try {
            conn = Main.playerData.getSqlConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Globals.PLAYER_KEYS
            		+ " WHERE uuid = '"+uuid+"';");
         
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equals(uuid)){
                    player =  new SQLPlayerKeys(
                    		rs.getString("uuid"), 
                    		rs.getInt("keys"),
                    		rs.getInt("killKeys"));
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
        return player;
	}
	
	public static SQLPlayerKeys WithdrawOrDefault(String uuid)
	{
		SQLPlayerKeys pkeys = SqlWithdraw(uuid);
		if(pkeys == null){
			pkeys = new SQLPlayerKeys(uuid);
			sqlDeposit(pkeys);
		}
		return pkeys;
	}
}
