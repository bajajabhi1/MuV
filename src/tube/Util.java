package tube;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class Util {
	Properties prop = new Properties();
	AWSCredentials credentials = null;
	AmazonS3Client s3 = null;

	public Util()
	{
		try {
			credentials = new PropertiesCredentials(FileUploadServlet.class.getResourceAsStream("AwsCredentials.properties"));
			s3 = new AmazonS3Client(credentials);				
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		}
	}

	public List<VideoVO> getVideoList()
	{		
		MySqlAPI mysql = new MySqlAPI(getRdsEndPoint());
		System.out.println("got the video list");
		return mysql.readAll();		
	}
	
	public void addVideo(String fileName, File uploadFile)
	{		
		S3BucketManager s3Manager = new S3BucketManager(s3, getS3BucketName());
		s3Manager.putOject(fileName, uploadFile);
		MySqlAPI mysql = new MySqlAPI(getRdsEndPoint());
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Date date = new Date(ts.getTime());
		String dateString = DateFormat.getDateTimeInstance(
	            DateFormat.MEDIUM, DateFormat.SHORT).format(date);
		VideoVO vo = new VideoVO(fileName,ts,fileName, getCloudFrontEP()+"/"+fileName, 0, 0, dateString);
		mysql.insertEntry(vo);
		mysql.addVote(fileName);
		mysql.updateRating(fileName, 1); // set rating to 1 by default
		System.out.println("Entry made in RDS");
	}

	public void deleteVideo(String fileName)
	{		
		S3BucketManager s3Manager = new S3BucketManager(s3, getS3BucketName());
		s3Manager.deleteObject(fileName);
		MySqlAPI mysql = new MySqlAPI(getRdsEndPoint());
		mysql.deleteEntry(fileName);
		System.out.println("Deleted from RDS");
	}

	public void rateVideo(String fileName, int addRating)
	{		
		// Update rating  code
		MySqlAPI mysql = new MySqlAPI(getRdsEndPoint());
		VideoVO vo = mysql.getEntry(fileName);
		int currRating = vo.getRating();
		int currVotes = vo.getTotalvotes();
		int newRating = Math.round(((currRating * currVotes) +  addRating)/ (float)(++currVotes));
		if(currVotes>0 && newRating <0)
		{
			newRating = 1;
		}
		if(currVotes>0 && newRating >5)
		{
			newRating = 5;
		}
		mysql.updateRating(fileName, newRating);
		mysql.addVote(fileName);
		System.out.println("Ratings updated in RDS");
	}

	public void loadProperties()
	{
		try {
			//load a properties file
			InputStream in = Util.class.getResourceAsStream("config.properties");
			prop.load(in);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public String getCloudFrontEP()
	{
		return prop.getProperty("cloud_front_end_point");
	}
	public String getS3BucketName()
	{
		return prop.getProperty("s3_bucket");
	}
	public String getDbUserName()
	{
		return prop.getProperty("dbuser");
	}
	public String getUploadDirectory()
	{
		return prop.getProperty("UPLOAD_DIRECTORY");
	}
	public String getDbPwd()
	{
		return prop.getProperty("dbpwd");
	}
	public String getRdsEndPoint()
	{
		return prop.getProperty("RDS_END_POINT");
	}
}
