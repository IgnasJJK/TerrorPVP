package me.TerrorLT.TerrorPVP.SQLObjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import me.TerrorLT.TerrorPVP.Globals;
import me.TerrorLT.TerrorPVP.Main;
import me.TerrorLT.TerrorPVP.SQLite.SQLite;

public class SQLPlayer {

	String uuid;
	int kills;
	int deaths;
	int points;
	int totalKills;
	int totalDeaths;
	int stolen;
	int stolenFrom;
	double kdRatio;
	int kdDifference;
	int totalStolen;
	int totalStolenFrom;
	
	//SQL generated
	int place;
	
	public SQLPlayer(String uuid)
	{
		this(uuid, 0, 0, 0, 0, 0, 0, 0, 0d, 0, 0, 0);
	}
	
	public SQLPlayer(String uuid, int kills, int deaths, int points, int tKills, int tDeaths,
			int stolen, int stolenFrom, double ratio, int difference, int totalStolen, 
			int totalStolenFrom)
	{
		this.uuid = uuid;
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
		totalKills = tKills;
		totalDeaths = tDeaths;
		this.stolen = stolen;
		this.stolenFrom = stolenFrom;
		kdRatio = ratio;
		kdDifference = difference;
	}
	
	public String getUUID(){return uuid;}
	public int getKills(){return kills;}
	public int getDeaths(){return deaths;}
	public int getPoints(){return points;}
	public int getTotalKills(){return totalKills;}
	public int getTotalDeaths(){return totalDeaths;}
	public int getStolen(){return stolen;}
	public int getStolenFrom(){return stolenFrom;}
	public int getKdDifference(){return kdDifference;}
	public double getKdRatio(){return kdRatio;}
	public int getPlace(){return place;}
	public int getTotalStolen() {return totalStolen;}
	public int getTotalStolenFrom() {return totalStolenFrom;}
	
	public void setKills(int x){kills = x; calcKDRatioDiff();}
	public void setDeaths(int x){deaths = x; calcKDRatioDiff();}
	public void setPoints(int x){points = x;}
	public void setTotalKills(int x){totalKills = x;}
	public void setTotalDeaths(int x){totalDeaths = x;}
	public void setStolen(int x){stolen = x;}
	public void setStolenFrom(int x){stolenFrom = x;}
	//private void setPlace(int x){place = x;}
	public void setTotalStolen(int x) {totalStolen = x;}
	public void setTotalStolenFrom(int x) {totalStolenFrom = x;}
	
	public void calcKDRatioDiff()
	{
		if(deaths > 0) kdRatio = ((((double)stolenFrom/2d)+(double)kills)/(double)deaths);
		else kdRatio = kills;
		kdDifference = ((stolenFrom/2)+kills)-deaths;
	}
	
	public static void sqlDeposit(SQLPlayer player)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Main.playerData.getSqlConnection();
			
			ps = conn.prepareStatement("REPLACE INTO " + Globals.PLAYERS + 
					" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, player.getUUID());
			ps.setInt(2, player.getKills());
			ps.setInt(3, player.getDeaths());
			ps.setInt(4, player.getPoints());
			ps.setInt(5, player.getTotalKills());
			ps.setInt(6, player.getTotalDeaths());
			ps.setInt(7, player.getStolen());
			ps.setInt(8, player.getStolenFrom());
			ps.setDouble(9, player.getKdRatio());
			ps.setInt(10, player.getKdDifference());
			ps.setInt(11, player.getTotalStolen());
			ps.setInt(12, player.getTotalStolenFrom());
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
	
	public static SQLPlayer SqlWithdraw(String uuid)
	{
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SQLPlayer player = null;
        
        try {
            conn = Main.playerData.getSqlConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Globals.PLAYERS
            		+ " WHERE uuid = '"+uuid+"';");
         
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equals(uuid)){
                    player =  new SQLPlayer(
                    		rs.getString("uuid"), 
                    		rs.getInt("kills"), 
                    		rs.getInt("deaths"), 
                    		rs.getInt("points"), 
                    		rs.getInt("totalKills"),
                    		rs.getInt("totalDeaths"), 
                    		rs.getInt("stolen"),
                    		rs.getInt("stolenFrom"),
                    		rs.getDouble("kdRatio"),
                    		rs.getInt("kdDifference"),
                    		rs.getInt("totalStolen"),
                    		rs.getInt("totalStolenFrom"));
                    //player.setPlace(rs.getInt("rowNum"));
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

	public static SQLPlayer SqlGetByPlace(int place){
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SQLPlayer player = null;
        
        try {
            conn = Main.playerData.getSqlConnection();
            ps = conn.prepareStatement("SELECT * FROM " + Globals.PLAYERS
            		+ " ORDER BY kdDifference DESC LIMIT "+(place-1)+",1;");
         
            rs = ps.executeQuery();
            while(rs.next()){
                    player =  new SQLPlayer(
                    		rs.getString("uuid"), 
                    		rs.getInt("kills"), 
                    		rs.getInt("deaths"), 
                    		rs.getInt("points"), 
                    		rs.getInt("totalKills"),
                    		rs.getInt("totalDeaths"), 
                    		rs.getInt("stolen"),
                    		rs.getInt("stolenFrom"),
                    		rs.getDouble("kdRatio"),
                    		rs.getInt("kdDifference"),
                    		rs.getInt("totalStolen"),
                    		rs.getInt("totalStolenFrom"));
                    //TODO: Remove?
                    player.calcKDRatioDiff();
                    //player.setPlace(rs.getInt("rowNum"));
                    return player;
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
	
	public static SQLPlayer getPlayerOrDefault(String uuid)
	{
		SQLPlayer player = SqlWithdraw(uuid);
		
		if(player != null) return player;
		else return new SQLPlayer(uuid);
	}
	
}
