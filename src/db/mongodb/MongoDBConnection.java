package db.mongodb;

import static com.mongodb.client.model.Filters.eq; //import ...Filters -> Filters.eq() 

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public MongoDBConnection() {
		mongoClient =MongoClients.create();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			return;
		}
		db.getCollection("users").updateOne(eq("user_id", userId), 
				new Document("$push", 
						new Document("favorite", new Document("$each", itemIds))));

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (db == null) {
			return;
		}
		db.getCollection("users").updateOne(eq("user_id",userId), 
				new Document("$pullAll",new Document("favorite",itemIds)));

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> favoriteItemIds = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id",userId));
		if (iterable.first() != null && iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>)iterable.first().get("favorite");
			favoriteItemIds.addAll(list);
		}
		return favoriteItemIds;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemIds(userId);
		for (String itemId : favoriteItemIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
			if (iterable.first() != null) {
				ItemBuilder builder = new ItemBuilder();
				Document doc = iterable.first();
				builder.setAddress(doc.getString("address"));
				builder.setDistance(doc.getDouble("distance"));
				builder.setImageUrl(doc.getString("image_url"));
				builder.setUrl(doc.getString("url"));
				builder.setName(doc.getString("name"));
				builder.setItemId(doc.getString("item_Id"));
				builder.setRating(doc.getDouble("rating"));
				builder.setCategories(getCategories(itemId));
				favoriteItems.add(builder.build());
			}
		}
		return favoriteItems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getCategories(String itemId) {
		if (db == null) {
			return new HashSet<>();
		}
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", itemId));
		if (iterable.first() != null && iterable.first().containsKey("categories")) {
			List<String> list = (List<String>) iterable.first().get("categories");
			categories.addAll(list);
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}	
		return items;
		
	}

	@Override
	public void saveItem(Item item) {
		if (db == null) {
			return;
		}
		//FindIterable<Document> iterable = db.getCollection("items").find(new Document("item_id",item.getItemId()));
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id",item.getItemId()));
		if(iterable.first() == null) {
			db.getCollection("items").insertOne(new Document()
					.append("item_id", item.getItemId())
					.append("distance", item.getDistance())
					.append("name", item.getName())
					.append("address", item.getAddress())
					.append("categories", item.getCategories())
					.append("url", item.getImageUrl())
					.append("rating", item.getRating())
					.append("image_url", item.getImageUrl())
					);
		}
		System.out.println("save");
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		// TODO Auto-generated method stub
		return false;
	}

}
