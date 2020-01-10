package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories; 
	private String imageUrl; 
	private String url;
	private double distance;

// constructor容易顺序出错（多个string）
// parameter可能不全，多个constructor
// 也可以先用无参constructor，然后每个field用各自setter，不常见，不是所有field都有setter
// 常用builder pattern
	private Item(ItemBuilder builder) { 
		this.itemId = builder.itemId; //静态内部类的成员
		this.name = builder.name; 
		this.rating = builder.rating; 
		this.address = builder.address; 
		this.categories = builder.categories; 
		this.imageUrl = builder.imageUrl; 
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	//把item-object转换为json-object，给response
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));//["",""]
			obj.put("image_url", imageUrl);
			obj.put("distance", distance);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

// builder pattern 创建静态内部类item builder
// 如果不是静态内部类，普通内部类需要先有item object才能创建item builder object
// 如果写成并列的class也不行，private的constructor就无法call， 如果把constructor改成public的，与思路违背
// field与item class一样
// 每个field创建setter
// 创建build方法：把builder-object给item的constructor，return
// 在item class中加入private的constructor，参数是builder-object，把builder-object的field给item-object
// ItemBuilder builder = new ItemBuilder();
// builder.setAddress(“”) 随便set哪些field，set的是builder的field，item里面的field依旧是immutable的（初始化时被builder里的field赋值的）
// Item item = builder.build()
// 若想直接在item class中set，需要每个field都要有对应setter，如果有immutable的一旦初始化不希望被更改，就没有setter，没法直接set

	public static class ItemBuilder{
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories; 
		private String imageUrl; 
		private String url;
		private double distance;
		
		
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public ItemBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}
		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}
		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
		
		public Item build() {
			return new Item(this);
			}
	}
}
