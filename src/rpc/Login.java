package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		DBConnection conn = DBConnectionFactory.getConnection(); //实际上如果不debug，不需要connection
		
		//get请求一般不需要用body，在header里面， 只需get parameter， post一般需要访问body
		
		
		try {                                                //timCat自动映射session id和session
			HttpSession session = request.getSession(false); //加boolean， true才返回新的，false则返回null
			JSONObject obj = new JSONObject();
			if (session != null) {
				
				String userId = session.getAttribute("user_id").toString(); //用于debug
				obj.put("status", "OK").put("user_id", userId).put("name", conn.getFullname(userId));
			} else {
				obj.put("status", "Invalid Session"); //help debug
				response.setStatus(403);  //403 error
			}
			RpcHelper.writeJsonObject(response, obj); //help debug
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}
			
			//只存session.getId在cookie的，session object是保存在tomCat server里
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
		DBConnection conn = DBConnectionFactory.getConnection();
		
		
		try {
			JSONObject input = RpcHelper.readJSONObject(request);  //读取request
			String userId = input.getString("user_id");
			String password = input.getString("password");   //前端返回的接口
			
			JSONObject obj = new JSONObject();
			if (conn.verifyLogin(userId, password)) {
				HttpSession session = request.getSession();    //session由timCat提供, session id也由timcat放在相应的response里
				session.setAttribute("user_id", userId);        //基于session之后要实现什么，只登录不需要写session
				session.setMaxInactiveInterval(600); //设置有限时间
				obj.put("status", "OK").put("user_id", userId).put("name", conn.getFullname(userId));
				//检查request header有没有session id， 如果有返回对应的object，如果没有创建一个新的session
			} else {
				obj.put("status", "User Doesn't Exist"); //help debug
				response.setStatus(401);  //用户名不存在或密码不正确，返回401 error
			}
			
			RpcHelper.writeJsonObject(response, obj); //help debug
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
