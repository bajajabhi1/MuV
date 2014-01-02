package tube;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//process only if its multipart content
	
		Util util = new Util();
		util.loadProperties();		
		final String UPLOAD_DIRECTORY = util.getUploadDirectory();
		if(ServletFileUpload.isMultipartContent(request)){
			try {
				
				List<FileItem> multiparts = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);
				
				String fileName = multiparts.get(0).getName();
				File uploadFile = new File(UPLOAD_DIRECTORY + fileName);
				System.out.println(UPLOAD_DIRECTORY + fileName);
				for(FileItem item : multiparts){
					if(!item.isFormField()){
						item.write( uploadFile);
					}
				}
				
				System.out.println("here0");
				
				util.addVideo(fileName, uploadFile);
				uploadFile.delete();

				//File uploaded successfully
				request.setAttribute("message", "File Uploaded Successfully");
			} catch (Exception ex) {
				request.setAttribute("message", "File Upload Failed due to " + ex);
			}          

		}else{
			request.setAttribute("message", "Error! Please upload video content only.");
		}

		request.getRequestDispatcher("/result.jsp").forward(request, response);
	}

}
