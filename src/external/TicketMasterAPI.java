package external;

import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection; 
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

// 1. ticket master(json) 
// 2. back-end(java): json转换为item-object（数据清理），再转换为json-object返回给前端) -> 这之间还有其他操作，直接操作json object会不简洁
// 3. front-end(java-script, json) ->

// 401 authentication 身份验证错误 //login没成功
// 403 authorization 身份验证通过，但没有权限 //login成功，无权访问

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json"; 
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "cZj2wEvaA0Qzsf3t92z4DkuycCsCLb07";
	
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");  //"Rick Sun" => "Rick%20Sun"
			} catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block 
				e.printStackTrace();
			}
  		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		String url = URL + "?" + query;
		
		System.out.println(url);
		
		try {
		HttpURLConnection connection = (HttpURLConnection) new
		URL(url).openConnection(); connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode(); 
		System.out.println("Sending request to url: " + url); 
		System.out.println("Response code: " + responseCode);
		if (responseCode != 200) {
		return new ArrayList<Item>();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = reader.readLine()) != null) { response.append(line);
		}
		reader.close();
		
		JSONObject obj = new JSONObject(response.toString()); //string是json format的string
//		
		if (!obj.isNull("_embedded")) {
		JSONObject embedded = obj.getJSONObject("_embedded"); 
		return getItemList(embedded.getJSONArray("events"));
//		JSONArray e1 = embedded.getJSONArray("events");
//		JSONObject e2 = e1.getJSONObject(0);
//		JSONArray e3 = e2.getJSONArray("images"); 
//		return e3;
		}
		} catch (Exception e) {
		// TODO Auto-generated catch block e.printStackTrace();
		}
		return new ArrayList<Item>();
		
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>(); 
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i); 
			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) { //check有没有id这个key
				builder.setItemId(event.getString("id"));
				}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
				}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
				}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
				}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
				}
			builder.setAddress(getAddress(event)); 
			builder.setCategories(getCategories(event)); 
			builder.setImageUrl(getImageUrl(event));
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	// address藏的比较深，需要helper function
	// root是一个event
	private String getAddress(JSONObject event) throws JSONException { 
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded"); //key "_embedded" 对应的value type是json object
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues"); //多个address，assume选第一个
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder addressBuilder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							addressBuilder.append(address.getString("line1")); }
						if (!address.isNull("line2")) { 
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line2")); }
						if (!address.isNull("line3")) { 
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line3")); }
						}
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city"); 
						if (!city.isNull("name")) {
							addressBuilder.append(","); 
							addressBuilder.append(city.getString("name"));
							} 
						}
					String addressStr = addressBuilder.toString(); 
					if (!addressStr.equals("")) {
						return addressStr;
						}
					} 
				}
			}
		return ""; 
		}
	
	private String getImageUrl(JSONObject event) throws JSONException { 
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images"); 
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i); 
				if (!image.isNull("url")) {
					return image.getString("url");
					}
				} 
			}
		return "";
		}
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>(); 
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications"); 
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i); 
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) { 
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	//**************  test method *********
	private void queryAPI(double lat, double lon) { 
		List<Item> events = search(lat, lon, null);
	try {
		for(Item event : events) {
			System.out.println(event.toJSONObject());
		}
	}
//	for (int i = 0; i < events.length(); ++i) {
//	JSONObject event = events.getJSONObject(i); 
//	System.out.println(event.toString(2));
//	}
	catch (Exception e) {
	 e.printStackTrace();
	 } 
	}
	
	//************* call queryAPI ***********
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicketMasterAPI tmApi = new TicketMasterAPI(); 
		// Mountain View, CA
		tmApi.queryAPI(37.38, -122.08);
		// London, UK
		//tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		//tmApi.queryAPI(29.682684, -95.295410);
	}

}
