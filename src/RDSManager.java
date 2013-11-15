import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBSecurityGroup;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.ModifyDBParameterGroupRequest;
import com.amazonaws.services.rds.model.Parameter;


public class RDSManager {

	private AmazonRDSClient rds;
	//RDS - MySql instance
	private final String RDS_ENGINE = "MySQL";
	private final String ENGINE_VERSION = "5.5.31";
	private final String LICENSE_MODEL = "general-public-license";
	private final boolean AUTO_MINOR_VERSION_UPGRADE = true;
	private final String DB_INSTANCE_TYPE ="db.t1.micro";
	private final boolean MULTI_AZ =false;
	private final int ALLOCATED_STORAGE = 5;

	private final String DBInstanceIdentifier = "ab3900MySql";
	private final String MASTER_USER_NAME = "ab3900_user";
	private final String MasterUserPassword = "ab3900_pass";
	private final String DB_NAME = "ab3900db";
	private final int DB_PORT = 3306;
	private final int BackupRetentionPeriod =1;
	private final boolean PubliclyAccessible = true;

	// db security group parameters
	private final String SECURITY_GROUP_NAME = "DBSecurityGroup";
	private final String SECURITY_GROUP_DESC = "this is db security group description";
	//private final String OWNER_ID = "785708217328";

	// db parameter group
	private final String DB_PARAM_GROUP_NAME = "DBParamGroup";
	private final String DB_PARAM_GROU_DESC = "this is db parameter group description";
	private final String DB_PARAM_GROUP_FAMILY = "mysql5.5";

	public RDSManager (AmazonRDSClient rdsClient)
	{
		this.rds = rdsClient;
	}
	
	public void createRDSSecurityGroup(){
		try {

			System.out.println("Creating RDS Security Group");

			CreateDBSecurityGroupRequest d = new CreateDBSecurityGroupRequest();
			d.setDBSecurityGroupName(SECURITY_GROUP_NAME);
			d.setDBSecurityGroupDescription(SECURITY_GROUP_DESC);
			rds.createDBSecurityGroup(d);


			AuthorizeDBSecurityGroupIngressRequest auth = new AuthorizeDBSecurityGroupIngressRequest();
			auth.setDBSecurityGroupName(SECURITY_GROUP_NAME);
			auth.setCIDRIP("0.0.0.0/0");
			//auth.setEC2SecurityGroupName(groupName);
			//auth.setEC2SecurityGroupOwnerId(OwnerId);
			DBSecurityGroup dbsecuritygroup= rds.authorizeDBSecurityGroupIngress(auth);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public String createRDS()
	{
		String endPointAddr = null;
		try
		{
			System.out.println("Creating RDS");
			CreateDBParameterGroupRequest cdpgr = new CreateDBParameterGroupRequest();
			cdpgr.setDBParameterGroupName(DB_PARAM_GROUP_NAME);
			cdpgr.setDescription(DB_PARAM_GROU_DESC);
			cdpgr.setDBParameterGroupFamily(DB_PARAM_GROUP_FAMILY);
			rds.createDBParameterGroup(cdpgr);

			Collection<Parameter> parameters = new ArrayList<Parameter>();
			parameters.add( new Parameter()
			.withParameterName("max_connections")
			.withParameterValue("200")
			.withApplyMethod("immediate"));
			parameters.add( new Parameter()
			.withParameterName("max_allowed_packet")
			.withParameterValue("999999")
			.withApplyMethod("immediate"));

			rds.modifyDBParameterGroup( new ModifyDBParameterGroupRequest().withDBParameterGroupName(DB_PARAM_GROUP_NAME).withParameters(parameters));

			/// create configuration of instance
			CreateDBInstanceRequest cdbir = new CreateDBInstanceRequest();
			cdbir.setEngine(RDS_ENGINE);
			cdbir.setEngineVersion(ENGINE_VERSION);
			cdbir.setLicenseModel(LICENSE_MODEL);
			cdbir.setAutoMinorVersionUpgrade(AUTO_MINOR_VERSION_UPGRADE);
			cdbir.setDBInstanceClass(DB_INSTANCE_TYPE);
			cdbir.setMultiAZ(MULTI_AZ);
			cdbir.setAllocatedStorage(ALLOCATED_STORAGE);
			cdbir.setDBInstanceIdentifier(DBInstanceIdentifier);
			cdbir.setMasterUsername(MASTER_USER_NAME);
			cdbir.setMasterUserPassword(MasterUserPassword);
			cdbir.setDBName(DB_NAME);
			cdbir.setPort(DB_PORT);
			cdbir.setBackupRetentionPeriod(BackupRetentionPeriod);
			cdbir.setPubliclyAccessible(PubliclyAccessible);
			cdbir.setDBParameterGroupName(DB_PARAM_GROUP_NAME);
			ArrayList<String> arrDbSecur = new ArrayList<String>();
			arrDbSecur.add(SECURITY_GROUP_NAME);
			cdbir.setDBSecurityGroups(arrDbSecur);

			System.out.println("Creating RDS DB Instance");
			// creating instance
			DBInstance dbi= rds.createDBInstance(cdbir);

			// wait till instance created
			boolean isWaiting = true;
			while(isWaiting){
				Thread.sleep(30000);
				DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
				request.setDBInstanceIdentifier(dbi.getDBInstanceIdentifier());
				DescribeDBInstancesResult result = rds.describeDBInstances(request);
				List<DBInstance> d= result.getDBInstances();
				Iterator<DBInstance> i = d.iterator();

				while(i.hasNext()){
					DBInstance d1 = i.next();
					System.out.println("RDS Status : " + d1.getDBInstanceStatus());
					if(d1.getDBInstanceStatus().equals("available")){
						isWaiting = false;
						System.out.println("RDS Endpoint Address : " +        d1.getEndpoint().getAddress());
						endPointAddr = d1.getEndpoint().getAddress();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endPointAddr;
	}

}
