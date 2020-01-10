package rpc;

import java.util.List;
import java.util.Set;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
              //endpoint
@WebServlet("/search")          //timcat lib
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());

//*****************    HTML   *************
//		response.setContentType("text/html");
//		PrintWriter writer = response.getWriter();
//		if (request.getParameter("username") != null) {
//			String username = request.getParameter("username"); 
//			
//			writer.println("<html><body>");
//			writer.println("<h1>Hello " + username + "</h1>");
//			writer.println("</body></html>");
//			writer.close();
//		}
		
//***************  JSON   *************
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
//		if (request.getParameter("username") != null) {
//			String username = request.getParameter("username");
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put("username", username);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			writer.print(obj);
//		}
//		writer.close();
		
//		***************  JSON ARRAY   *************
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
		
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcde"));
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		try {
//			array.put(new JSONObject().put("username", "1234"));
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

//		writer.print(array);
//		writer.close();
//	}
		
//************   using RpcHelper  ***************
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "abcd").put("address", "San Francisco").put("time", "01/01/2017"));
//			array.put(new JSONObject().put("username", "1234").put("address", "San Jose").put("time", "01/02/2017"));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
//		
//	}
		
//************** connect TicketMasterAPI ****************
		// allow access only if session exists
		HttpSession session = request.getSession(false); 
		if (session == null) {
		response.setStatus(403);
		return; 
		}
		// optional
		String userId = session.getAttribute("user_id").toString();
		
		double lat = Double.parseDouble(request.getParameter("lat")); //get parameter -> request header
		double lon = Double.parseDouble(request.getParameter("lon"));
//		TicketMasterAPI tmAPI = new TicketMasterAPI();
//		List<Item> items = tmAPI.search(lat, lon, null);
//		JSONArray array = new JSONArray(); 
//		for (Item item : items) {
//			array.put(item.toJSONObject()); 
//		}	
//		 
//		RpcHelper.writeJsonArray(response, array);
		String term = request.getParameter("term");
		DBConnection connection = DBConnectionFactory.getConnection();//default mysql
		try {
			List<Item> items = connection.searchItems(lat, lon, term);
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
			JSONArray array = new JSONArray(); 
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", favoritedItemIds.contains(item.getItemId()));
				array.put(obj); 
				}	
			RpcHelper.writeJsonArray(response, array);
			} catch (Exception e ) {
				e.printStackTrace();
			} finally {
				connection.close();
			}
		
	}
		
		

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub 
		doGet(request, response);
	}

}
