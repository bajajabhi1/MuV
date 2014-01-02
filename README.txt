There are basically two components of the project.

1. MuV Back-end Services Set up
One component installs and sets up all the back-end Amazon services of S3, RDS and CloudFront. All these components are deployed by code and no manual handling is required.

The files used for this part are -
MuVServerSetup.java - It has the main method which has to be run to set up the above mentioned services in one go. Also it contains a method to create the CloudFront Configuration.
RDSManager.java     - This class has the API to create the RDS Security group and setting up MySQL Database.
S3BucketManager.java - This class has the API to manage the S3 related tasks like create and delete bucket, put and delete object (videos in present case).
MySqlAPI.java       - This class has the APIs for querying the RDS MySQL Database. For example, createTable() that creates the Video_INFO table which keeps the video information, readAll() that gets the list of all videos, updateRating() that updates the rating of video. 

2. MuV Web Application project
This component creates the web application project and includes the servlets, properties files and other utility files that are deployed on tomcat server on EC2 instance. It also includes the Front-end html and jsp files.
 
 AwsCredentials.properties - file containing AWS credentials.
 config.properties - properties file containing values of s3, cloudfront and RDS links.
 DeleteVideoServlet.java - Servlet that handles delete video requests.
 FileUploadServlet.java - Servlet that handles video file upload requests.
 ListServlet.java - Servlet that handles video list requests.
 RatingServlet.java - Servlet that handles requests to update the rating of a video.
 S3BucketManager.java - This class has the API to manage the S3 related tasks like put and delete videos.
 Util.java - Utility file that provide APIs for loading properties file and exposes APIs to servlet for handling RDS MySQL DB queries.
 VideoVO.java - Java Object that represents a Video.
 MySqlAPI.java - This class has the APIs for querying the RDS MySQL Database. For example, createTable() that creates the Video_INFO table which keeps the video information, readAll() that gets the list of all videos, updateRating() that updates the rating of video. 

The below are the files that makes the GUI - 
about.html - Page that opens on clicking "About us" link
index.html - default welcome page, that redirects it to the servlet.
upload.jsp - Page that opens as a pop-up on clicking on upload button on Home page. It is used to upload video.
result.jsp - Page which is returned by upload servlet and it closes the upload pop-up.
list.jsp - The home page that displays the video list and plays the videos.
style.css - CSS file for styling

All the images used are put in S3 bucket and accessed through it.
