package fr.epita.pgsql.services.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.inject.Inject;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.stereotype.Repository;

import fr.epita.pgsql.connection.DBConnection;
import fr.epita.pgsql.datamodel.Address;
import fr.epita.pgsql.datamodel.Profile;
import fr.epita.services.business.PgsqlAddressBusinessException;
import fr.epita.services.business.PgsqlProfileBusinessException;

@Repository
public class PgsqlProfileDAO {
	
	@Inject PgsqlAddressDAO addressDAO;

	/* Get profile by id
	 * params: long id
	 * return: Profile
	 */
	public Profile getProfileById(long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			String sqlQuery = "SELECT * FROM \"PROFILES\" WHERE profile_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Profile profile = new Profile();
				profile.setName(rs.getString("name"));
				profile.setProfile_id(rs.getLong("profile_id"));
				profile.setBirth_year(rs.getLong("birth_year"));
				profile.setEmail(rs.getString("email"));
				profile.setGender(rs.getString("gender"));
				List<Address> address = addressDAO.getAddressByProfileId(rs.getLong("profile_id"));
				profile.setAddress(address);
				
				rs.close();
				pstmt.close();
				db.close();
				return profile;
			}else {
				rs.close();
				pstmt.close();
				db.close();
				throw new PgsqlProfileBusinessException("Profile not found.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlProfileBusinessException("Unable to retrieve profile.", e);
		}
	}
	
	/* Get profile by user id
	 * params: long id
	 * return: Profile
	 */
	public Profile getProfileByUserId(long userId) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			String sqlQuery = "SELECT * FROM \"PROFILES\" WHERE user_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, userId);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Profile profile = new Profile();
				profile.setName(rs.getString("name"));
				profile.setProfile_id(rs.getLong("profile_id"));
				profile.setGender(rs.getString("gender"));
				profile.setEmail(rs.getString("email"));
				profile.setBirth_year(rs.getLong("birth_year"));
				List<Address> address = addressDAO.getAddressByProfileId(rs.getLong("profile_id"));
				profile.setAddress(address);
				return profile;
			}
			
			rs.close();
			pstmt.close();
			db.close();	
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlProfileBusinessException("Unable to retrieve profile.", e);
		}
		return null;
	}
	
	/* Add profile
	 * params: Profile profile
	 * return: Profile
	 */
	public Profile addProfile(Profile profile, long userId) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Check if user's email exists
			String sqlQuery = "SELECT * FROM \"PROFILES\" WHERE email=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, profile.getEmail());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("email").equals(profile.getEmail())) {
					throw new PgsqlProfileBusinessException("Email already exists.");
				}
			}
			
			sqlQuery = " INSERT INTO \"PROFILES\"(name, email, birth_year, gender, user_id) VALUES (?, ?, ?, ?, ?)";
			pstmt = db.getConnection().prepareStatement(sqlQuery,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, profile.getName());
			pstmt.setString(2, profile.getEmail());
			pstmt.setLong(3, profile.getBirth_year());
			pstmt.setString(4, profile.getGender());
			pstmt.setLong(5, userId);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				ResultSet r = pstmt.getGeneratedKeys();
				if(r.next()) {
					
					Profile addedProfile = getProfileById(r.getLong(1));
					
					if(profile.getAddress().size() > 0) {
						for(Address address: profile.getAddress()) {
							addressDAO.addAddress(address, addedProfile.getProfile_id());
						}
					}
					r.close();
					rs.close();
					pstmt.close();
					db.close();
					
					return addedProfile;
					
				}else {
					rs.close();
					pstmt.close();
					db.close();
					throw new PgsqlProfileBusinessException("Unable to retrieve added profile.");
				}
			}else {
				rs.close();
				pstmt.close();
				db.close();
				throw new PgsqlProfileBusinessException("Profile not added.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlProfileBusinessException("Unable to insert profile.", e);
		}
	}	
	
	/* Update profile
	 * params: Profile profile
	 * return: Profile
	 */
	public Profile updateProfile(Profile profile, long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Check if user exists
			getProfileById(id);
			
			// Check if user's email exists
			String sqlQuery = "SELECT * FROM \"PROFILES\" WHERE email=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, profile.getEmail());
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getString("email").equals(profile.getEmail()) && rs.getLong("profile_id") != id) {
					throw new PgsqlProfileBusinessException("Email is not available.");
				}
			}
			
			if(profile.getAddress().size() > 0) {
				for(Address address: profile.getAddress()) {
					addressDAO.updateAddress(address, address.getAddress_id());
				}
			}
			
			// Update profile detail in PROFILES table
			sqlQuery = " UPDATE \"PROFILES\" SET name=?, email=?, birth_year=?, gender=? WHERE profile_id=?";
			pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, profile.getName());
			pstmt.setString(2, profile.getEmail());
			pstmt.setLong(3, profile.getBirth_year());
			pstmt.setString(4, profile.getGender());
			pstmt.setLong(5, id);
			
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				rs.close();
				pstmt.close();
				db.close();
				return getProfileById(id);
			}else {
				rs.close();
				pstmt.close();
				db.close();
				throw new PgsqlProfileBusinessException("Profile not updated.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlProfileBusinessException("Unable to update profile.", e);
		}
	}
	
	/* Delete profile
	 * params: long id
	 * return: Profile
	 */
	public Profile deleteProfile(long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			Profile profile = getProfileById(id);
			String sqlQuery = "DELETE FROM \"PROFILES\" WHERE profile_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				pstmt.close();
				db.close();
				return profile;
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlProfileBusinessException("Profile is not deleted.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			throw new PgsqlProfileBusinessException("Unable to delete profile.", e);
		}
	}
}
