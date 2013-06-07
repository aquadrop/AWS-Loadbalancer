package ece1779.ec2.user;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import ece1779.ec2.models.Image;
import ece1779.ec2.models.User;

public class ExtraFileUpload extends HttpServlet {

	private static final long serialVersionUID = -3107270894262780835L;
	String error = null;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession();
		
		PrintWriter out = response.getWriter();

		try {

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List<FileItem> items = upload.parseRequest(request);

			// Create image object
			Image image = new Image();

			// Set User ID
			FileItem userIdInput = (FileItem) items.get(0);
			int userId = 29;//Integer.parseInt(userIdInput.getString());
			if (userId == -1) {
				User user = (User) session.getAttribute("user");
				userId = user.getId();
			}			
			image.setUserId(userId);
			
			// Set Keys
			HashMap<String, String> keys = new HashMap<String, String>();
			keys.put("key1", "MyObjectKey_" + UUID.randomUUID());
			keys.put("key2", "MyObjectKey_" + UUID.randomUUID());
			keys.put("key3", "MyObjectKey_" + UUID.randomUUID());
			keys.put("key4", "MyObjectKey_" + UUID.randomUUID());
			image.setKeys(keys);

			String path = this.getServletContext().getRealPath("/upload") + "/";
			String name1 = path + keys.get("key1");
			String name2 = path + keys.get("key2");
			String name3 = path + keys.get("key3");
			String name4 = path + keys.get("key4");

			// Uploaded File
			FileItem theFile = (FileItem) items.get(1);

			// Store original file in server
			File file1 = new File(name1);
			theFile.write(file1);

			// Flip image
			IMOperation op = new IMOperation();
			op.addImage();
			op.flip();
			op.addImage();
			ConvertCmd cmd = new ConvertCmd();
			cmd.run(op, name1, name2);
			File file2 = new File(name2);

			// Flop image
			op = new IMOperation();
			op.addImage();
			op.flop();
			op.addImage();
			cmd = new ConvertCmd();
			cmd.run(op, name1, name3);
			File file3 = new File(name3);

			// Resize Image
			op = new IMOperation();
			op.addImage();
			// op.blur(2.0);
			op.resize(200);
			op.addImage();
			cmd = new ConvertCmd();
			cmd.run(op, name1, name4);
			File file4 = new File(name4);

			// Save to S3
			s3SaveFile(file1, keys.get("key1"), out);
			s3SaveFile(file2, keys.get("key2"), out);
			s3SaveFile(file3, keys.get("key3"), out);
			s3SaveFile(file4, keys.get("key4"), out);
			
			// Insert to Database
			updateDatabase(userId, keys.get("key1"), keys.get("key2"), keys.get("key3"), keys.get("key4"));

			request.setAttribute("imageObj", image);
			RequestDispatcher view = request.getRequestDispatcher("viewImage.jsp");
			view.forward(request, response);

		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	public void updateDatabase(int userid, String key1, String key2, String key3, String key4) {
		Connection con = null;
		try {
			// Get DB connection from pool
			DataSource dbcp = (DataSource) this.getServletContext().getAttribute("dbpool");
			con = dbcp.getConnection();

			// Execute SQL query
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO images (userId, key1, key2, key3, key4) VALUES (" + userid + ",'" + key1 + "','" + key2 + "','" + key3 + "','" + key4
					+ "')";

			// stmt.execute("GRANT INSERT ON database.* TO 'ec2-user@'servername.ap-southeast-1.compte.internal' IDENTIFIED BY 'password'");
			stmt.execute(sql);

		} catch (Exception ex) {
			getServletContext().log(ex.getMessage());
			error = ex.getMessage();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				getServletContext().log(e.getMessage());
			}
		}
	}

	public void s3SaveFile(File file, String key, PrintWriter out) throws IOException {

		BasicAWSCredentials awsCredentials = (BasicAWSCredentials) this.getServletContext().getAttribute("AWSCredentials");

		AmazonS3 s3 = new AmazonS3Client(awsCredentials);

		String bucketName = "uoftece1779winter2013";

		try {
			s3.putObject(new PutObjectRequest(bucketName, key, file));

			s3.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);

		} catch (AmazonServiceException ase) {
			out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			out.println("Error Message:    " + ase.getMessage());
			out.println("HTTP Status Code: " + ase.getStatusCode());
			out.println("AWS Error Code:   " + ase.getErrorCode());
			out.println("Error Type:       " + ase.getErrorType());
			out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, " + "such as not being able to access the network.");
			out.println("Error Message: " + ace.getMessage());
		}
	}

}
