package pt.tecnico.cnv.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URI;
import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.LinkedHashMap;

import pt.tecnico.cnv.server.Test;
import raytracer.RayTracer;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    private static final Logger LOGGER = Logger.getLogger("WebServer");
    int test1 = Test.test; // just testing dependencies in the Makefile
    public static final String RAYTRACER_PATH = System.getProperty("user.dir")+"/../raytracer";
	private static HashMap<Long,String> requests=new HashMap<Long,String>();

    public static void main(String[] args) throws Exception {
        LOGGER.setLevel(Level.INFO);
        LOGGER.log(Level.INFO,"Raytracer class: " + RayTracer.class.getName());
        LOGGER.log(Level.INFO,"Starting webserver...");
        LOGGER.log(Level.INFO,"WD: " + System.getProperty("user.dir"));


        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/r.html", new RenderHandler());
        server.createContext("/kill-yourself", new ExitHandler());
        server.createContext("/version", new VersionHandler());
        server.setExecutor(new WebServerExecutor()); // creates a default executor
        server.start();
        System.out.println("main exiting...");
    }
	public static synchronized String getRequest(long threadId){
		return requests.get(threadId);
	}
    static class RenderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();

            try{
                Map<String, String> query_pairs = splitQuery(uri.getQuery());
                String inFile = query_pairs.get("f");
                int scols = Integer.parseInt(query_pairs.get("sc"));
                int srows = Integer.parseInt(query_pairs.get("sr"));
                int wcols = Integer.parseInt(query_pairs.get("wc"));
                int wrows = Integer.parseInt(query_pairs.get("wr"));
                int coff = Integer.parseInt(query_pairs.get("coff"));
                int roff = -Integer.parseInt(query_pairs.get("roff"));

                String params="f="+inFile+"&sc="+scols+"&sr="+srows+"&wc="+wcols+"&wr="+wrows+"&coff="+coff+"&roff="+roff;

                long threadId = Thread.currentThread().getId();
                requests.put(threadId, params);
                System.out.println("thread id = " + threadId + "; request : " + params);

                File temp = File.createTempFile("render", ".bmp");
                RayTracer rt = new RayTracer(scols, srows, wcols, wrows, coff, roff);
                rt.readScene(new File(RAYTRACER_PATH + "/" + inFile));
                rt.draw(temp);


                Headers headers = t.getResponseHeaders();
                headers.add("Content-Type", "image/bmp");
                String bmpFilename = inFile + "_" + scols + "_" + srows + "_" + wcols + "_" + wrows + "_" + coff + "_" + roff + ".bmp";
                headers.add("Content-Disposition", "inline; filename=\"" + bmpFilename +"\"");
                t.sendResponseHeaders(200, temp.length());

                InputStream is=new FileInputStream(temp);
                OutputStream os = t.getResponseBody();
                int c;
                byte[] buf = new byte[8192];
                while ((c = is.read(buf, 0, buf.length)) > 0) {
                    os.write(buf, 0, c);
                    os.flush();
                }
                os.close();
            } catch (Exception e) {
                String response = "bad arguments?" + "\n\n\n\n";
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                response += sw.toString() + "\n";
                doTextResponse(t, response, 400);
            }
        }
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    static class ExitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            doTextResponse(t, "goodbyte world", 200);
            System.exit(0);
        }
    }

    private static String prettyBytes(long bytes) {
        String response = bytes + " bytes";
        if( bytes > 1024*1024) {
            return (bytes/1024/1024) + " MiB (" + response + ")";
        } else {
            return response;
        }
    }

    static class VersionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = getProperty("java.version") + "\n"
                            + getProperty("java.vendor") + "\n"
                            + getProperty("java.home") + "\n"
                            + getProperty("os.name") + "\n"
                            + getProperty("user.dir") + "\n"
                            + getEnv("JAVA_HOME") + "\n"
                            + getEnv("JAVA_ROOT") + "\n"
                            + getProperty("java.class.path") + "\n";
            // runtime information
            Runtime run = Runtime.getRuntime();
            response += "\n-- JVM Runtime --\n"
                     + "availableProcessors: " + run.availableProcessors() + "\n"
                     + "freeMemory: " + prettyBytes(run.freeMemory()) + "\n"
                     + "totalMemory: " + prettyBytes(run.totalMemory()) + "\n";

            doTextResponse(t, response, 200);
        }
    }

    private static String getEnv(String env) {
        String result = System.getenv(env);
        return env + ": " + (result != null ? result : "(null)");
    }

    private static String getProperty(String p) {
        String result = System.getProperty(p);
        return p + ": " + (result != null ? result : "(null)");
    }

    private static void doTextResponse(HttpExchange t, String response, int code) throws IOException {
        t.sendResponseHeaders(code, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }




}
// vim: expandtab:ts=4:sw=4
