package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import entity.Item;
import recommendation.GeoRecommendation;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		HttpSession session = request.getSession(false); 
		
		if (session == null) {
			response.setStatus(403);
			return; 
		}
		// optional
		String userId = session.getAttribute("user_id").toString();
		
		double lat = Double.parseDouble(request.getParameter("lat")); 
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		GeoRecommendation recommendation = new GeoRecommendation();
		

		try {
			List<Item> items = recommendation.recommendItems(userId, lat, lon);
			
			JSONArray array = new JSONArray(); 
	
			for (Item item : items) {
				array.put(item.toJSONObject()); 
				}	
			RpcHelper.writeJsonArray(response, array);
			} catch (Exception e ) {
				e.printStackTrace();
			} 
		
	}
		
		
		
		
		
		
		
		
		
		
		

//**************   multiple key value pairs *********
		
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
//		
//		JSONArray array = new JSONArray();
//		try {
//			JSONObject obj1 = new JSONObject();
//			obj1.put("name", "abcd");
//			obj1.put("address", "san francisco");
//			obj1.put("time", "01/01/2017");
//			array.put(obj1);
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			JSONObject obj2 = new JSONObject();
//			obj2.put("name", "1234");
//			obj2.put("address", "san jose");
//			obj2.put("time", "01/02/2017");
//			array.put(obj2);
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		writer.print(array);
//		writer.close();
//	}
		
//**************   using RpcHelper *********
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcd").put("address", "San Francisco").put("time", "01/01/2017"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			array.put(new JSONObject().put("username", "1234").put("address", "San Jose").put("time", "01/02/2017"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
//		
//	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
