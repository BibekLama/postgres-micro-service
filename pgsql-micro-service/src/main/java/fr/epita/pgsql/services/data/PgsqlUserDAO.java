package fr.epita.pgsql.services.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import fr.epita.pgsql.connection.DBConnection;
import fr.epita.pgsql.datamodel.Profile;
import fr.epita.pgsql.datamodel.Role;
import fr.epita.pgsql.datamodel.User;
import fr.epita.services.business.PgsqlAddressBusinessException;
import fr.epita.services.business.PgsqlProfileBusinessException;
import fr.epita.services.business.PgsqlUserBusinessException;

@Repository
public class PgsqlUserDAO{
	
	@Inject
	PgsqlProfileDAO profileDAO;
	
	@Inject
	PgsqlRoleDAO roleDAO;
	
	/* Get users list
	 * return: List<User>
	 */
	public List<User> listUsers() throws PgsqlUserBusinessException{
		
		List<User> users = new ArrayList<>();
		try {
			DBConnection db = new DBConnection();
			PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT * FROM \"USERS\"");
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setUser_id(rs.getLong("user_id"));
				Role role = roleDAO.getRoleById(rs.getLong("role_id"));
				user.setRole(role);
				user.setPassword("***");
				users.add(user);
			}
			
			if(users.size() == 0) {
				throw new PgsqlUserBusinessException("User list is empty.");
			}
			
			pstmt.close();
			rs.close();
			db.close();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to retrieve users data.", e);
		}
		return users;
	}
	
	/* Get user detail
	 * params: long id
	 * return: User 
	 */
	public User getUserById(long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		User user = new User();
		DBConnection db;
		try {
			db = new DBConnection();
			
			// Select user by id from USERS table
			PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT * FROM \"USERS\" WHERE user_id=?");
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				user.setUser_id(rs.getLong("user_id"));
				user.setUsername(rs.getString("username"));
				user.setPassword("***");
				Role role = roleDAO.getRoleById(rs.getLong("role_id"));
				user.setRole(role);
				Profile profile = profileDAO.getProfileByUserId(rs.getLong("user_id"));
				user.setProfile(profile);
				
				pstmt.close();
				rs.close();
				db.close();
			}else {
				pstmt.close();
				rs.close();
				db.close();
				throw new PgsqlUserBusinessException("User does not exists.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to retrieve user's details.", e);
		}
	
		return user;
	}
	
	/* Add new user
	 * params: User user
	 * return: User
	 */
	public User addUser(User user) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		try {
			DBConnection db = new DBConnection();
			
			// Check if user exists
			String sqlQuery = "SELECT * FROM \"USERS\" WHERE username=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, user.getUsername());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("username").equals(user.getUsername())) {
					throw new PgsqlUserBusinessException("Username already exists.");
				}
			}
			
			// Insert new user in the USERS table
			Role role = null;
			if(user.getRole() != null) {
				role = roleDAO.getRoleByName(user.getRole().getRole());
			}
			User addedUser = null;
			if(role != null) {
				String sqlQuery1 = "INSERT INTO \"USERS\" (username, password, role_id) VALUES (?, ?, ?)";
				PreparedStatement pstmt1 = db.getConnection().prepareStatement(sqlQuery1, Statement.RETURN_GENERATED_KEYS);
				pstmt1.setString(1, user.getUsername());
				pstmt1.setString(2, user.getPassword());
				pstmt1.setLong(3, role.getRole_id());
				int affectedRows = pstmt1.executeUpdate();
				if(affectedRows > 0) {
					ResultSet r = pstmt1.getGeneratedKeys();
					if(r.next()) {
						addedUser = getUserById(r.getLong(1));
					}
				}
				pstmt1.close();
			}else {
				String sqlQuery1 = "INSERT INTO \"USERS\"(username, password) VALUES (?, ?)";
				PreparedStatement pstmt1 = db.getConnection().prepareStatement(sqlQuery1, Statement.RETURN_GENERATED_KEYS);
				pstmt1.setString(1, user.getUsername());
				pstmt1.setString(2, user.getPassword());
				int affectedRows = pstmt1.executeUpdate();
				if(affectedRows > 0) {
					ResultSet r = pstmt1.getGeneratedKeys();
					if(r.next()) {
						addedUser = getUserById(r.getLong(1));
					}
				}
				pstmt1.close();
			}
			
			rs.close();
			pstmt.close();
			db.close();
			
			if(addedUser != null) {
				return addedUser;
			}else {
				throw new PgsqlUserBusinessException("Recently added user not found.");
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to add user.", e);
		}
	}
	
	/*
	 * Update user's details
	 * params: User user, long id
	 * return: User
	 */
	public User updateUser(User user, long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Check if user exists in the USERS table
			getUserById(id);
			
			// Check if username exists in the USERS table
			String sqlQuery = "SELECT * FROM \"USERS\" WHERE username=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, user.getUsername());
			ResultSet result = pstmt.executeQuery();
			if(result.next()) {
				if(result.getString("username").equals(user.getUsername()) && id != result.getLong("user_id")) {
					throw new PgsqlUserBusinessException("Username is not available.");
				}
			}
					
			// Update user's details from USERS table
			Role role = null;
			if(user.getRole() != null) {
				role = roleDAO.getRoleByName(user.getRole().getRole());
			}
			
			int affectedRows = 0;
			if(role != null) {
				String sqlQuery1 = "UPDATE \"USERS\" SET username=?, password=?, role_id=? WHERE user_id=?";
				PreparedStatement pstmt1 = db.getConnection().prepareStatement(sqlQuery1);
				pstmt1.setString(1, user.getUsername());
				pstmt1.setString(2, user.getPassword());
				pstmt1.setLong(3, role.getRole_id());
				pstmt1.setLong(4, id);
				affectedRows = pstmt1.executeUpdate();
				pstmt1.close();
			}else {
				String sqlQuery1 = "UPDATE \"USERS\" SET username=?, password=? WHERE user_id=?";
				PreparedStatement pstmt1 = db.getConnection().prepareStatement(sqlQuery1);
				pstmt1.setString(1, user.getUsername());
				pstmt1.setString(2, user.getPassword());
				pstmt1.setLong(3, id);
				affectedRows = pstmt1.executeUpdate();
				pstmt1.close();
			}
			
			if(affectedRows > 0) {
				pstmt.close();
				db.close();
				return getUserById(id);
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlUserBusinessException("User update unsuccessfull");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to update user's details.", e);
		}
	}
	
	/* Delete user
	 * params: long id
	 * return: User
	 */
	public User deleteUser(long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			User user = getUserById(id);
			String sqlQuery = "DELETE FROM \"USERS\" WHERE user_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				pstmt.close();
				db.close();
				return user;
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlUserBusinessException("User is not deleted.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to delete user.", e);
		}
	}
	
	/* User login
	 * params: String username, String password
	 * return: User
	 */
	public User userLogin(String username, String password) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			String sqlQuery = "SELECT * FROM \"USERS\" WHERE username=? AND password=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				User user = getUserById(rs.getLong("user_id"));
				rs.close();
				pstmt.close();
				db.close();
				return user;
			}else {
				rs.close();
				pstmt.close();
				db.close();
				throw new PgsqlUserBusinessException("Username or password incorrect.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlUserBusinessException("Unable to login user.", e);
		}
	}
	
}
