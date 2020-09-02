package fr.epita.pgsql.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	final String pgsqlURL = "jdbc:postgresql://192.168.0.15:10532/movies-db";
	final String pgsqlUser = "postgres";
	final String pgsqlPass = "postgres";
	
	Connection connection;
	
	public DBConnection() throws SQLException {
		connection = DriverManager.getConnection(pgsqlURL, pgsqlUser, pgsqlPass);
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public void close() throws SQLException {
		this.connection.close();
	}
}