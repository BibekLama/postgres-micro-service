package fr.epita.pgsql.services.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Repository;

import fr.epita.pgsql.connection.DBConnection;
import fr.epita.pgsql.datamodel.Role;

@Repository
public class PgsqlRoleDAO {
	
	public Role getRoleById(long id) {
		Role role = null;
		DBConnection db;
		
		try {
			db = new DBConnection();
			PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT * FROM \"ROLES\" WHERE role_id=?");
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				role = new Role();
				role.setRole_id(rs.getLong("role_id"));
				role.setRole(rs.getString("role"));
			}
			pstmt.close();
			rs.close();
			db.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return role;
	}
	
	public Role getRoleByName(String name) {
		Role role = null;
		DBConnection db;
		
		try {
			db = new DBConnection();
			PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT * FROM \"ROLES\" WHERE role=?");
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				role = new Role();
				role.setRole_id(rs.getLong("role_id"));
				role.setRole(rs.getString("role"));
			}
			pstmt.close();
			rs.close();
			db.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return role;
	}
	
	public Role addRole(Role role) {
		Role addedRole = null;
		try {
			DBConnection db = new DBConnection();
			
			// Check if user exists
			String sqlQuery = "INSERT INTO \"ROLES\" (role) VALUES (?)";
			
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, role.getRole());
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				ResultSet r = pstmt.getGeneratedKeys();
				if(r.next()) {
					// Select recently added user
					addedRole = getRoleById(r.getLong(1));
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return addedRole;
	}
}
