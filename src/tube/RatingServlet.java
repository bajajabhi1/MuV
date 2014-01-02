package tube;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RatingServlet
 */
@WebServlet("/RatingServlet")
public class RatingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RatingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//process only if its multipart content
	
		Util util = new Util();
		util.loadProperties();
		String name = request.getParameter("videoname");
		String rating = request.getParameter("rating");
	    System.out.println(name);
	    System.out.println(rating);
	    name = name.substring(name.lastIndexOf("/")+1, name.length());
	    System.out.println(name);
	    util.rateVideo(name, Integer.parseInt(rating));
	    response.sendRedirect("/MuV/list");
	}
}
