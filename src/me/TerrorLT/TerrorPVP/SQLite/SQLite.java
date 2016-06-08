package me.TerrorLT.TerrorPVP.SQLite;

import java.util.logging.Logger;

public class SQLite {

	public final static String SQL_CONNECTION_EXECUTE = "Failed to execute MySQL statement: ";
	public final static String SQL_CONNECTION_CLOSE = "Failed to close MySQL connection: ";
	public final static String SQL_NO_CONNECTION = "Unable to retrieve MySQL connection: ";
	public final static String SQL_TABLE_NOT_FOUND = "Database error: Table not found.";
	public final static String MISSING_SQLITE_JBDC = "SQLite JBDC not found. Download it and put it into the lib folder.";
	public final static String SQL_GETCONNECTION_FAILED = "Failed to connect to MySQL.";
	public final static String IO_ERROR = "File I/O Error: ";
	
	Logger logger;
	
	public SQLite(Logger logger)
	{
		this.logger = logger;
	}
	
	
}
