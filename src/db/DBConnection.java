package db;

import java.util.List;
import java.util.Set;

import entity.Item;

public interface DBConnection {
//close the connection 
	public void close();
	
//	*******  insert the favorite items for a user *********
//	
//	@param userId
//	@param itemIds
	public void setFavoriteItems(String userId, List<String> itemIds);
	
//	*******  delete the favorite items for a user *********
//	
//	@param userId
//	@param itemIds
	public void unsetFavoriteItems(String userId, List<String> itemIds);
	
//	*******  get the favorite item id for a user *********
//	
//	@param userId
//	@return itemIds
	public Set<String> getFavoriteItemIds(String userId);
	
//	*******  get the favorite items for a user *********
//	
//	@param userId
//	@return itemIds
	public Set<Item> getFavoriteItems(String userId);
	
//	*******  get categories based on the item id  *********
//	
//	@param userId
//	@return set of categories 
	public Set<String> getCategories(String itemId);
	
//	*******  search items near a geolocation and a term *********
//	
//	@param userId
//	@param lat
//	@param lon	
//	@param term (Nullable)	
//	@return list of items
	public List<Item> searchItems(double lat, double lon, String term);

//  save item into db

//	@param item
	public void saveItem(Item item);
	
//  get full name of a user
//	@param item
	public String getFullname(String userId);
	
//	return whether the credential is correct
//	@param userId
//	@param password
//  @return boolean
	public boolean verifyLogin(String userId, String password);
	
	public boolean registerUser(String userId, String password, String firstname, String lastname);
}
