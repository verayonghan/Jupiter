package recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		
		List<Item> recommendedItems = new ArrayList<>();
		//1. get all favorited itemIds
		DBConnection conn = DBConnectionFactory.getConnection();
		Set<String> favoritedItemIds = conn.getFavoriteItemIds(userId);
		
		Map<String, Integer> allCategories = new HashMap<>();
		for (String itemId : favoritedItemIds) {
			Set<String> categories = conn.getCategories(itemId);
			for (String category : categories) {
				if (allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category) + 1);
				} else {
					allCategories.put(category, 0);
				}
			}
			
		}
		
		
//		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
//		Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() {
//			public int compare(Entry<String, Integer> e1, Entry<String,Integer> e2) {
//				return e2.getValue() - e1.getValue();
//			}
//		};
//		Collections.sort(categoryList, comparator); 
		
//		Set<String> visitedItemIds = new HashSet<>();
//		for (Entry<String, Integer> category : categoryList) {
//			List<Item> items = conn.searchItems(lat, lon, category.getKey());
//			
//			for (Item item : items) {
//				if (!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
//					recommendedItems.add(item);
//					visitedItemIds.add(item.getItemId());
//				}
//			}
//		}

			List<Item> items = conn.searchItems(lat, lon, "Music");
			
			for (Item item : items) {
					recommendedItems.add(item);
			}

	
		
		conn.close();
		return recommendedItems;
	}

	
	
	
}
