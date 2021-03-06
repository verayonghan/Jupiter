package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;


public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if (conn == null) {
		    System.err.println("DB connection failed");     
		    return;
		 }
		try {
			String sql = "INSERT IGNORE INTO history(user_id,item_id) VALUE (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if (conn == null) {
		    System.err.println("DB connection failed");     
		    return;
		 }
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		 if (conn == null) {
		     return new HashSet<>();
		   }
		 Set<String> favoriteItemIds = new HashSet<>();
		 
		 try {
			 String sql = "SELECT item_id FROM history WHERE user_id = ?";
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ps.setString(1, userId);
			 ResultSet rs = ps.executeQuery();
			 while (rs.next()) {
				 String itemId = rs.getString("item_id");
				 favoriteItemIds.add(itemId);
				 }
		 }catch (SQLException e) {
				 e.printStackTrace();
			 }
		return favoriteItemIds;
					
				   
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
		     return new HashSet<>();
		   }
		 Set<Item> favoriteItems = new HashSet<>();
		 Set<String> itemIds = getFavoriteItemIds(userId);
		 
		 try {
			 String sql = "SELECT * FROM items WHERE item_id = ?";
			 PreparedStatement ps = conn.prepareStatement(sql);
			 for (String itemId : itemIds) {
				   ps.setString(1, itemId);
				   ResultSet rs = ps.executeQuery();
				   
				   ItemBuilder builder = new ItemBuilder();
				   while (rs.next()) { //read result set (might > 1)
					   builder.setItemId(rs.getString("item_id"));
					   builder.setName(rs.getString("name")); 
					   builder.setAddress(rs.getString("address"));
					   builder.setImageUrl(rs.getString("image_url"));
					   builder.setUrl(rs.getString("url"));
					   builder.setCategories(getCategories(itemId));
					   builder.setDistance(rs.getDouble("distance"));
					   builder.setRating(rs.getDouble("rating"));
					   
					   favoriteItems.add(builder.build());//add item
					   }
				   }
			 } catch (SQLException e) {
			   e.printStackTrace();
			   }   
			   
		 return favoriteItems;
		 }


	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		 if (conn == null) {
		     return new HashSet<>();
		   }
		 Set<String> categories = new HashSet<>();
		 
		 try {
			 String sql = "SELECT * FROM categories WHERE item_id = ?";
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ps.setString(1, itemId);
			 ResultSet rs = ps.executeQuery();
			 while (rs.next()) {
				 String category = rs.getString("categories");
				 categories.add(category);
				 }
		 }catch (SQLException e) {
				 System.out.println(e.getMessage());
			 }
		return categories;
	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
		if (conn == null) {
		    System.err.println("DB connection failed");
		    return;
		 }
		
		  try {
			  String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)"; 
			  PreparedStatement ps = conn.prepareStatement(sql);
			  ps.setString(1, item.getItemId());
			  ps.setString(2, item.getName());
			  ps.setDouble(3, item.getRating()); 
		      ps.setString(4, item.getAddress()); 
			  ps.setString(5, item.getImageUrl()); 
			  ps.setString(6, item.getUrl()); 
			  ps.setDouble(7, item.getDistance()); 
			  ps.execute();
				
			  sql = "INSERT IGNORE INTO categories VALUES(?, ?)"; 
			  ps = conn.prepareStatement(sql);
			  ps.setString(1, item.getItemId());
			  for(String category : item.getCategories()) {
				  ps.setString(2, category);
				  ps.execute();
				  }
			  } catch (Exception e) {
					// TODO Auto-generated catch block
				  e.printStackTrace();
				  }
		  }

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) { 
			return "";
		}
		String name = ""; 
		 
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";  //？考虑了injection 
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  userId);
			ResultSet rs = ps.executeQuery(); //select读取操作都是去访问result set
			                                 //insert，delete可以execute
			while (rs.next()) {              //rs默认指向-1位置，因此call rs.next()，此时rs有一行或没有，用if也可以
				name = rs.getString("first_name") + " " + rs.getString("last_name");
				 }
		 }catch (SQLException e) {
				 System.out.println(e.getMessage());
			 }
		return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) { 
			return false;
		}
		 
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";   //match via db
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery(); 			                           
			if (rs.next()) {              
				return true;
				 }
		 }catch (SQLException e) {
				 System.out.println(e.getMessage());
			 }
		return false;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}	
		return items;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		// TODO Auto-generated method stub
		if ( conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		try	{
			String sql = "INSERT IGNORE INTO users VALUES(?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
