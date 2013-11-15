import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.Aliases;
import com.amazonaws.services.cloudfront.model.CacheBehaviors;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateDistributionResult;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionResult;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.GetDistributionRequest;
import com.amazonaws.services.cloudfront.model.GetDistributionResult;
import com.amazonaws.services.cloudfront.model.GetStreamingDistributionRequest;
import com.amazonaws.services.cloudfront.model.GetStreamingDistributionResult;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.PriceClass;
import com.amazonaws.services.cloudfront.model.S3Origin;
import com.amazonaws.services.cloudfront.model.S3OriginConfig;
import com.amazonaws.services.cloudfront.model.StreamingDistributionConfig;
import com.amazonaws.services.cloudfront.model.StreamingLoggingConfig;
import com.amazonaws.services.cloudfront.model.TrustedSigners;
import com.amazonaws.services.cloudfront.model.ViewerProtocolPolicy;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.s3.AmazonS3Client;


public class Assignment2Setup {
	static AmazonEC2   ec2;
	static AmazonS3Client s3;
	static AmazonRDSClient rdsClient;

	static final String 	S3_BUCKET 			= "edu.columbia.ms.cse.2013.ab3900";
	static final long 		CLOUD_FRONT_MIN_TTL  = 36000;

	public static void main(String args[])
	{
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
					Assignment2Setup.class.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		s3 = new AmazonS3Client(credentials);
		S3BucketManager s3Manager = new S3BucketManager(s3, S3_BUCKET);
		s3Manager.createBucket();
		File file = new File("C:/Users/Abhinav/Downloads/video_test.mp4");		
		// S3 task
		s3Manager.putOject("videokey", file);

		//s3Manager.deleteObject("key");
		//s3Manager.deleteBucket();		
		createCloudFront(credentials);
		rdsClient = new AmazonRDSClient(credentials);
		RDSManager rdsMgr = new RDSManager(rdsClient);
		rdsMgr.createRDSSecurityGroup();
		String rdsEP  = rdsMgr.createRDS();
		// Use the rds end point and connect to DB and create table
		MySqlAPI mysql = new MySqlAPI(rdsEP);
		mysql.createTable();
	}

	public static void createCloudFront(AWSCredentials credentials)
	{
		try
		{
			AmazonCloudFrontClient cloudfront = new AmazonCloudFrontClient(credentials);
			// Downloading distribution -  web
			DistributionConfig webeDist = new DistributionConfig();
			webeDist.withCallerReference(System.currentTimeMillis() + "");
			webeDist.withAliases(new Aliases().withQuantity(0));
			webeDist.withDefaultRootObject("");
			webeDist.withOrigins(new Origins().withItems(
					new Origin().withId(S3_BUCKET).withDomainName(S3_BUCKET + ".s3.amazonaws.com")
					.withS3OriginConfig(new S3OriginConfig().withOriginAccessIdentity("")))
					.withQuantity(1));
			webeDist.withDefaultCacheBehavior(new DefaultCacheBehavior()
			.withTargetOriginId(S3_BUCKET)
			.withForwardedValues(new ForwardedValues().withQueryString(false).withCookies(new CookiePreference().withForward("none")))
			.withTrustedSigners(new TrustedSigners().withQuantity(0).withEnabled(false))
			.withViewerProtocolPolicy(ViewerProtocolPolicy.AllowAll)
			.withMinTTL(CLOUD_FRONT_MIN_TTL));
			webeDist.withCacheBehaviors(new CacheBehaviors().withQuantity(0));
			webeDist.withComment("Testing cloud front");
			webeDist.withLogging(new LoggingConfig().withEnabled(false).withBucket("").withPrefix("").withIncludeCookies(false));
			webeDist.withEnabled(true);
			webeDist.withPriceClass(PriceClass.PriceClass_All);

			CreateDistributionRequest cdr = new CreateDistributionRequest().withDistributionConfig(webeDist);

			CreateDistributionResult webDistResult = cloudfront.createDistribution(cdr);

			// Streaming distribution - RTMP
			StreamingDistributionConfig streamDis = new StreamingDistributionConfig();
			streamDis.withCallerReference(System.currentTimeMillis() + "");
			streamDis.withAliases(new Aliases().withQuantity(0));
			streamDis.withS3Origin(new S3Origin(S3_BUCKET + ".s3.amazonaws.com").withOriginAccessIdentity(""));
			streamDis.withComment("Testing cloud front");
			streamDis.withLogging(new StreamingLoggingConfig().withEnabled(false).withBucket("").withPrefix(""));
			streamDis.withEnabled(true);
			streamDis.withPriceClass(PriceClass.PriceClass_All);
			streamDis.withTrustedSigners(new TrustedSigners().withQuantity(0).withEnabled(false));
			
			CreateStreamingDistributionRequest streamingDistribution = new CreateStreamingDistributionRequest()
			.withStreamingDistributionConfig(streamDis);          
			CreateStreamingDistributionResult streamDistResult =  cloudfront.createStreamingDistribution(streamingDistribution);
			
			boolean isWaitWeb = true;
			boolean isWaitRtmp = true;
			while (isWaitWeb && isWaitRtmp) {
				Thread.sleep(30000);
				GetDistributionResult webGdr = cloudfront.getDistribution(new GetDistributionRequest(webDistResult.getDistribution().getId()));
				String status = webGdr.getDistribution().getStatus();
				System.out.println("Web Distribution Status :" + status);
				if (status.equals("Deployed")) {
					isWaitWeb = false;
					System.out.println("Web Domain Name :" + webGdr.getDistribution().getDomainName());
				}
				GetStreamingDistributionResult rtmpGdr = cloudfront.getStreamingDistribution(
						new GetStreamingDistributionRequest(streamDistResult.getStreamingDistribution().getId()));
				status = rtmpGdr.getStreamingDistribution().getStatus();
				System.out.println("RTMP Distribution Status :" + status);
				if (status.equals("Deployed")) {
					isWaitRtmp = false;
					System.out.println("RTMP Domain Name :" + rtmpGdr.getStreamingDistribution().getDomainName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}