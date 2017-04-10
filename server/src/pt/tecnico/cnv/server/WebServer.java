package pt.tecnico.cnv.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

public class WebServer {
    private static final Logger LOGGER = Logger.getLogger("WebServer");
    int test1 = Test.test; // just testing dependencies in the Makefile

    public static void main(String[] args) throws Exception {
        LOGGER.setLevel(Level.INFO);
        LOGGER.log(Level.INFO,"Starting webserver...");


        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/r.html", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();
            String response = "This was the query:" + uri.getQuery()
                               + "##\n";
            Map<String, String> query_pairs = splitQuery(uri.getQuery());
            response+=query_pairs;
            for (Map.Entry<String, String> entry : query_pairs.entrySet()) {
                response+="key="+entry.getKey()+"\n";
                response+="value="+entry.getValue()+"\n";
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
             
            os.close();
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
