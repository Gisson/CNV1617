import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;


    public class MSS {
        private static MSS mss = null;
        public static MSS getInstance() throws Exception {
            if(mss == null) {
                mss = new MSS();
            }
            return mss;
        }

	private MSS() throws Exception {
		init();
	}



            /*
            * Before running the code:
            *      Fill in your AWS access credentials in the provided credentials
            *      file template, and be sure to move the file to the default location
            *      (~/.aws/credentials) where the sample code will load the
            *      credentials from.
            *      https://console.aws.amazon.com/iam/home?#security_credential
            *
            * WARNING:
            *      To avoid accidental leakage of your credentials, DO NOT keep
            *      the credentials file in your source directory.
            */

		static AmazonDynamoDBClient dynamoDB;

            /**
            * The only information needed to create a client are security credentials
            * consisting of the AWS Access Key ID and Secret Access Key. All other
            * configuration, such as the service endpoints, are performed
            * automatically. Client parameters, such as proxies, can be specified in an
            * optional ClientConfiguration object when constructing a client.
            *
            * @see com.amazonaws.auth.BasicAWSCredentials
            * @see com.amazonaws.auth.ProfilesConfigFile
            * @see com.amazonaws.ClientConfiguration
            */
            private void init() throws Exception {
                /*
                * The ProfileCredentialsProvider will return your [default]
                * credential profile by reading from the credentials file located at
                * (~/.aws/credentials).
                */
                AWSCredentials credentials = null;
                try {
                    credentials = new ProfileCredentialsProvider().getCredentials();
                } catch (Exception e) {
                    throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                        "Please make sure that your credentials file is at the correct " +
                        "location (~/.aws/credentials), and is in valid format.",
                        e);
                }
                dynamoDB = new AmazonDynamoDBClient(credentials);
                Region euWest1 = Region.getRegion(Regions.EU_WEST_1);
                dynamoDB.setRegion(euWest1);
		
            }


        public synchronized void sendToMSS(String filename, Map<String, Integer> request, int dyn_count, int alloc_count) throws Exception {

            
		TableUtils.waitUntilActive(dynamoDB, "cnv-metrics");


		UUID id = UUID.randomUUID();
		float factorx = 100f/request.get("sr");
		float factory = 100f/request.get("sc");

		/*We'll rescale every image to have a sc and sr of maximum 100
		To do so we'll use
		f(x)=(100-0)(x-0)/(sc-0) to every xc
		

		*/



		// Add another .waitUntilActive(dynamoDB, tableName);tem
            Map<String, AttributeValue> item = newItem (id, filename, request.get("sc"),  request.get("sr"),  request.get("wc"), request.get("wr"), request.get("coff"),  request.get("roff"),
 factory*request.get("sc"),
factorx*request.get("sr"),
factory*request.get("wc"),
factorx*request.get("wr"), 
factory*request.get("coff"), 
factorx*request.get("roff"), 
dyn_count, alloc_count, 
factorx*factory*dyn_count, 
factorx*factory*alloc_count);
             PutItemRequest putItemRequest = new PutItemRequest("cnv-metrics", item);
             PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
            System.out.println("Result: " + putItemResult);

    }

    private static Map<String, AttributeValue> newItem(UUID id, String filename, Integer sc, Integer sr, Integer wc, Integer wr, Integer coff, Integer roff, Integer sc_n, Integer sr_n, Integer wc_n, Integer wr_n, Integer coff_n, Integer roff_n, int dyn, int alloc, int dyn_n, int alloc_n) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("uuid", new AttributeValue(id.toString()));
	item.put("filename", new AttributeValue(filename));
        item.put("sc", new AttributeValue().withN(sc.toString()));
	item.put("sr", new AttributeValue().withN(sr.toString()));
	item.put("wc", new AttributeValue().withN(wc.toString()));
	item.put("wr", new AttributeValue().withN(wr.toString()));
	item.put("coff", new AttributeValue().withN(coff.toString()));
	item.put("roff", new AttributeValue().withN(roff.toString()));
	item.put("sc_n", new AttributeValue().withN(sc_n.toString()));
         item.put("sr_n", new AttributeValue().withN(sr_n.toString()));
         item.put("wc_n", new AttributeValue().withN(wc_n.toString()));
         item.put("wr_n", new AttributeValue().withN(wr_n.toString()));
         item.put("coff_n", new AttributeValue().withN(coff_n.toString()));
         item.put("roff_n", new AttributeValue().withN(roff_n.toString()));
        item.put("dyn_count", new AttributeValue().withN(Integer.toString(dyn)));
	item.put("alloc_count", new AttributeValue().withN(Integer.toString(alloc)));
	 item.put("dyn_count_n", new AttributeValue().withN(Integer.toString(dyn_n)));
         item.put("alloc_count_n", new AttributeValue().withN(Integer.toString(alloc_n)));

        return item;
    }
	
	public synchronized void query(String filename, Integer sc, Integer sr, Integer wc, Integer wr, Integer coff, Integer roff){
		
		Integer factorx = 100/sr;
                Integer factory = 100/sc;
             	Integer coff_n = factory*coff; 
		Integer roff_n = factorx*roff;
		Integer wc_n = factory*wc;
		Integer wr_n = factorx*wr;


		// Scan items for movies with a year attribute greater than 1985
             HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
             Condition condition1 = new Condition()
                 .withComparisonOperator(ComparisonOperator.GT.toString())
                 .withAttributeValueList(new AttributeValue().withN(String.valueOf(coff_n-0.2*wc_n)));

             Condition condition2 = new Condition()
                  .withComparisonOperator(ComparisonOperator.LT.toString())
                  .withAttributeValueList(new AttributeValue().withN(String.valueOf(coff_n+wc_n)));

             Condition condition3 = new Condition()
                  .withComparisonOperator(ComparisonOperator.GT.toString())
                  .withAttributeValueList(new AttributeValue().withN(String.valueOf(roff_n-0.2*wr_n)));

              Condition condition4 = new Condition()
                  .withComparisonOperator(ComparisonOperator.LT.toString())
                  .withAttributeValueList(new AttributeValue().withN(String.valueOf(roff_n+wr_n)));
		
             scanFilter.put("coff_n", condition1);
//             scanFilter.put("coff_n", condition2);
//             scanFilter.put("roff_n", condition3);
 //            scanFilter.put("roff_n", condition4);

             ScanRequest scanRequest = new ScanRequest("cnv-metrics").withScanFilter(scanFilter    );
             ScanResult scanResult = dynamoDB.scan(scanRequest);
             System.out.println("Result: " + scanResult);


	}
}
/*Escrever o resto aqui*/



