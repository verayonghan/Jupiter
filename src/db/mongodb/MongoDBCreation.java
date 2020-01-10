package db.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBCreation {
	
	//Run as java application to create MongoDB collections with index
	public static void main(String[] args) {
		MongoClient mongoClient =MongoClients.create();//localhost,27017
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		//1.create connection 
		
		//2.delete existing collections
		db.getCollection("users").drop();
		db.getCollection("items").drop();
		
		//3.set index（can ignore schema setting）
		IndexOptions indexOptions = new IndexOptions().unique(true);
		                                      //new document -> {}
		db.getCollection("users").createIndex(new Document("user_id",1),indexOptions);
		db.getCollection("items").createIndex(new Document("item_id",1),indexOptions);
		//get -> if collection does not exist, create it
		
		db.getCollection("users").insertOne(new Document().append("user_id", "1111")
				.append("password", "2222")
				.append("first_name", "John")
				.append("last_name", "Smith")); 
		
		mongoClient.close();
		System.out.println("Import is done successfully!");
	}
}
	