package fr.epita.pgsql.services.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import fr.epita.pgsql.connection.DBConnection;
import fr.epita.pgsql.datamodel.Address;
import fr.epita.services.business.PgsqlAddressBusinessException;

@Repository
public class PgsqlAddressDAO {
	
	/* Get address by id
	 * params: long id
	 * return: Address
	 */
	public Address getAddressById(long id) throws PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Select address from ADDRESS table by id
			String sqlQuery = "SELECT * FROM \"ADDRESSES\" WHERE address_id="+id;
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Address address = new Address();
				address.setAddress_id(rs.getLong("address_id"));
				address.setCountry(rs.getString("country"));
				address.setCity(rs.getString("city"));
				address.setPostal_code(rs.getString("postal_code"));
				address.setStreet_name(rs.getString("street_name"));
				address.setStreet_number(rs.getString("street_number"));
				rs.close();
				pstmt.close();
				db.close();
				return address;
			}else {
				rs.close();
				pstmt.close();
				db.close();
				throw new PgsqlAddressBusinessException("Address is not found.");
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new PgsqlAddressBusinessException("Unable to retrieve address.", e);
		}
	}
	
	/* Get address by  profile id
	 * params: long profileId
	 * return: List<Address>
	 */
	public List<Address> getAddressByProfileId(long profileId) throws PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Select address from ADDRESS table by id
			String sqlQuery = "SELECT * FROM \"ADDRESSES\" WHERE profile_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, profileId);
			ResultSet rs = pstmt.executeQuery();
			List<Address> addresses = new ArrayList<>();
			while(rs.next()) {
				Address address = new Address();
				address.setAddress_id(rs.getLong("address_id"));
				address.setCountry(rs.getString("country"));
				address.setCity(rs.getString("city"));
				address.setPostal_code(rs.getString("postal_code"));
				address.setStreet_name(rs.getString("street_name"));
				address.setStreet_number(rs.getString("street_number"));
				addresses.add(address);
			}
			rs.close();
			pstmt.close();
			db.close();
			return addresses;
		}catch(SQLException e){
			e.printStackTrace();
			throw new PgsqlAddressBusinessException("Unable to retrieve address.", e);
		}
	}
	
	/* Add address
	 * params: Address address, profileId
	 * return: Address
	 */
	public Address addAddress(Address address, long profileId) throws PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Insert address to ADDRESS table 
			String sqlQuery = "INSERT INTO \"ADDRESSES\" (country, city, postal_code, street_name, street_number, profile_id) VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, address.getCountry());
			pstmt.setString(2, address.getCity());
			pstmt.setString(3, address.getPostal_code());
			pstmt.setString(4, address.getStreet_name());
			pstmt.setString(5, address.getStreet_number());
			pstmt.setLong(6, profileId);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				ResultSet rs = pstmt.getGeneratedKeys();
				if(rs.next()) {
					Address addedAddress = getAddressById(rs.getLong(1));
					rs.close();
					pstmt.close();
					db.close();
					return addedAddress;
				}else {
					rs.close();
					pstmt.close();
					db.close();
					throw new PgsqlAddressBusinessException("Unable to retireve added address.");
				}
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlAddressBusinessException("Address is not added.");
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new PgsqlAddressBusinessException("Unable to add address.", e);
		}
	}
	
	/* Update address
	 * params: Address address, long id
	 * return: Address
	 */
	public Address updateAddress(Address address, long id) throws PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Check if address exists
			getAddressById(id);
			
			// Update address from ADDRESS table 
			String sqlQuery = "UPDATE \"ADDRESSES\" SET country=?, city=?, postal_code=?, street_name=?, street_number=? WHERE address_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setString(1, address.getCountry());
			pstmt.setString(2, address.getCity());
			pstmt.setString(3, address.getPostal_code());
			pstmt.setString(4, address.getStreet_name());
			pstmt.setString(5, address.getStreet_number());
			pstmt.setLong(6, id);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				pstmt.close();
				db.close();
				return getAddressById(id);
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlAddressBusinessException("Address is not updated.");
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new PgsqlAddressBusinessException("Unable to update address.", e);
		}
	}
	
	/* Delete address by id
	 * params: long id
	 * return: Address
	 */
	public Address deleteAddress(long id) throws PgsqlAddressBusinessException{
		try {
			DBConnection db = new DBConnection();
			
			// Check if address exists
			Address address = getAddressById(id);
			
			// Delete address from ADDRESS table 
			String sqlQuery = "DELETE FROM \"ADDRESSES\" WHERE address_id=?";
			PreparedStatement pstmt = db.getConnection().prepareStatement(sqlQuery);
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();
			if(affectedRows > 0) {
				pstmt.close();
				db.close();
				return address;
			}else {
				pstmt.close();
				db.close();
				throw new PgsqlAddressBusinessException("Address is not deleted.");
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new PgsqlAddressBusinessException("Unable to delete address.", e);
		}
	}
}
