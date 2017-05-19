import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import mylib.AbstractHttpHandler;
import mylib.MultithreadedExecutor;
import mylib.VersionHandler;
import mylib.StringHandler;
import java.util.Random;

public class LoadBalancer {
	private static int DEFAULT_PORT = 8000;
    private static final Logger L = Logger.getLogger("WebServer");
    private static AmazonEC2 ec2;
    private static final int INSTANCE_REFRESH_INTERVAL_MS = 10000;

	public static void main(String argv[]) throws IOException {
        L.setLevel(Level.INFO);
		L.info(LoadBalancer.class.getSimpleName() + " starting in '"+ System.getProperty("user.dir")+"'...");

		int port = DEFAULT_PORT;
		if(argv.length > 1) {
	        L.info("argv[1] =  " + argv[1]);
			port = Integer.parseInt(argv[1]);
		}
        L.info("port =  " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        try {

            java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
        } catch (Exception e) {
            L.info("SunJCE failed, but continuing anyway...");
        }
        try {
            initEC2();
            server.createContext("/r.html", new RenderHandler());
            server.createContext("/kill-balancer", new ExitHandler());
            server.createContext("/version", new VersionHandler());
            server.createContext("/instances", new InstancesListHandler());
        } catch(Exception e) {
            ec2 = null;
            String response = "initEC2 failed? Can not reach render nodes.\n\n\n";
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            e.printStackTrace();
            L.severe("initEC2 failed?");
            response += sw.toString() + "\n";
            server.createContext("/", new StringHandler(response));
        }
        server.setExecutor(new MultithreadedExecutor());

        // TODO keep updating
        if(ec2 != null) {
            updateRunningInstances();
        }

        server.start();
        if(ec2 != null) {
            while(Thread.currentThread().isAlive()) {
                try {
                    Thread.sleep(INSTANCE_REFRESH_INTERVAL_MS);
                } catch(InterruptedException e) {
                    L.info("sleel interrupted");
                }
                updateRunningInstances();
            }
        }
        L.info("main exiting...");
	}

    private static List<String> availableRenderNodes = new ArrayList<String>();
    private static synchronized List<String> getAvailableRenderNodes() {
        return availableRenderNodes;
    }

    private static synchronized void setAvailableRenderNodes(List<String> nodes) {
        availableRenderNodes = nodes;
    }

    private static void updateRunningInstances() {
        DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();

        System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
                " Availability Zones.");
                DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();

        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();

        for (Reservation reservation : reservations) {
            instances.addAll(reservation.getInstances());
        }
        List<String> nodes = new ArrayList<String>();
        for(Instance i : instances) {
            String dns = i.getPublicDnsName();
            String state = i.getState().getName();
            System.out.println(state + " -- " + dns);
            if(state != null && state.equals("running")) {
                nodes.add(dns);
            }
        }
        setAvailableRenderNodes(nodes);
        System.out.println("You have " + instances.size() + " Amazon EC2 instance(s) running.");
    }

    static class RenderHandler extends AbstractHttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            List<String> nodes = getAvailableRenderNodes();
            if(nodes.size() > 0) {
                URI uri = t.getRequestURI();
                String query = uri.getQuery();

                /* select random node */
                int rand = new Random().nextInt(nodes.size());
                String node = nodes.get(rand);

                /* redirect :/ */
                Headers headers = t.getResponseHeaders();
                headers.add("Location", "http://"+node+":8000/r.html?"+query);
                t.sendResponseHeaders(301, 0);

                t.close();
            } else {
                doTextResponse(t, "No available render instances!", 503);
            }
        }
    }

    static class ExitHandler extends AbstractHttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            doTextResponse(t, LoadBalancer.class.getSimpleName() + "goodbyte world", 200);
            System.exit(0);
        }
    }

    static class InstancesListHandler extends AbstractHttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String inst = "Available instances:\n" + presentList(getAvailableRenderNodes());
            doTextResponse(t, inst, 200);
        }
    }
    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.PropertiesCredentials
     * @see com.amazonaws.ClientConfiguration
     */
    private static void initEC2() throws Exception {

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
      ec2 = AmazonEC2ClientBuilder.standard().withRegion("eu-west-1").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }
}
