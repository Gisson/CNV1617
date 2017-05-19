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


		// Add another .waitUntilActive(dynamoDB, tableName);tem
            Map<String, AttributeValue> item = newItem (id, filename, request.get("sc"),  request.get("sr"),  request.get("wc"), request.get("wr"), request.get("coff"),  request.get("roff"), dyn_count, alloc_count);
             PutItemRequest putItemRequest = new PutItemRequest("cnv-metrics", item);
             PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
            System.out.println("Result: " + putItemResult);

    }

    private static Map<String, AttributeValue> newItem(UUID id, String filename, Integer sc, Integer sr, Integer wc, Integer wr, Integer coff, Integer roff, int dyn, int alloc) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("uuid", new AttributeValue(id.toString()));
	item.put("filename", new AttributeValue(filename));
        item.put("sc", new AttributeValue().withN(sc.toString()));
	item.put("sr", new AttributeValue().withN(sr.toString()));
	item.put("wc", new AttributeValue().withN(wc.toString()));
	item.put("wr", new AttributeValue().withN(wr.toString()));
	item.put("coff", new AttributeValue().withN(coff.toString()));
	item.put("roff", new AttributeValue().withN(roff.toString()));
        item.put("dyn_count", new AttributeValue().withN(Integer.toString(dyn)));
	item.put("alloc_count", new AttributeValue().withN(Integer.toString(alloc)));

        return item;
    }


}
/*Escrever o resto aqui*/



