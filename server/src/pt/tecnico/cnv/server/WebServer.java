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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.LinkedHashMap;

import pt.tecnico.cnv.server.Test;
import raytracer.RayTracer;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class WebServer {
    private static final Logger LOGGER = Logger.getLogger("WebServer");
    int test1 = Test.test; // just testing dependencies in the Makefile

    public static void main(String[] args) throws Exception {
        LOGGER.setLevel(Level.INFO);
        LOGGER.log(Level.INFO,"Raytracer class: " + RayTracer.class.getName());
        LOGGER.log(Level.INFO,"Starting webserver...");
        LOGGER.log(Level.INFO,"WD: " + System.getProperty("user.dir"));


        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/r.html", new MyHandler());
        server.setExecutor(new WebServerExecutor()); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
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
                File temp = File.createTempFile("render", ".bmp");
                RayTracer rt = new RayTracer(scols, srows, wcols, wrows, coff, roff);
                rt.readScene(new File("../raytracer/"+inFile));
                rt.draw(temp);


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
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
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

}
// vim: expandtab:ts=4:sw=4
