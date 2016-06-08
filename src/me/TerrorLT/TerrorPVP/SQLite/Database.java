package me.TerrorLT.TerrorPVP.SQLite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
	
	Connection connection;
	
	String databaseFile;
	File folder;
	
	Logger logger;
	
	public Database(Logger logger, String dbFile, File dbFolder)
	{
		databaseFile = dbFile + ".db";
		folder = dbFolder;
		this.logger = logger;
	}
	
	//Returns the connection object to the database
	public Connection getSqlConnection()
	{
		File database = new File(folder, databaseFile);
		
		if(!database.exists())
		{
			try{
				database.createNewFile();
			}catch(IOException ex)
			{
				logger.log(Level.SEVERE, SQLite.IO_ERROR + databaseFile);
			}
		}
		
		try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            return connection;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, SQLite.SQL_GETCONNECTION_FAILED, ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, SQLite.MISSING_SQLITE_JBDC);
        }
        return null;
		
	}

	public void createTableIfNotExists(String table, String data)
	{
		String query = "CREATE TABLE IF NOT EXISTS " + table + " (" + data + ");";
		
		connection = getSqlConnection();
		try{
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
		}catch(SQLException e){
			logger.log(Level.SEVERE, e.getMessage());
		}
		
		//TODO: Figure out what this is
		//Initialize();
		
	}
	
	public void dropTableIfExists(String table)
	{
		String query = "DROP TABLE IF EXISTS " + table;
		
		connection = getSqlConnection();
		try{
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
		}catch(SQLException e){
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public ResultSet queryResult(String queryString)
	{
		Connection sql = null;
		PreparedStatement statement = null;
		ResultSet result = null; 
		try{
			sql = getSqlConnection();
			statement = sql.prepareStatement(queryString);
			result = statement.executeQuery();
			
		}catch(SQLException ex)
		{
			logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_EXECUTE, ex);
		}finally{
			try{
				if(statement != null) statement.close();
				if(sql != null) sql.close();
			}catch(SQLException ex){
				logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_CLOSE, ex);
			}
		}
		
		return result;
	}
	
	public void query(String queryString)
	{
		Connection sql = null;
		PreparedStatement statement = null;
		//ResultSet result = null; 
		try{
			sql = getSqlConnection();
			statement = sql.prepareStatement(queryString);
			statement.execute();
		}catch(SQLException ex)
		{
			logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_EXECUTE, ex);
		}finally{
			try{
				if(statement != null) statement.close();
				if(sql != null) sql.close();
			}catch(SQLException ex){
				logger.log(Level.SEVERE, SQLite.SQL_CONNECTION_CLOSE, ex);
			}
		}
	}
	
	public void delete()
	{
		if(connection != null)
			try {
				if(!connection.isClosed())
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		File f = new File(folder, databaseFile);
		if(f.exists())
			f.delete();
	}
}
