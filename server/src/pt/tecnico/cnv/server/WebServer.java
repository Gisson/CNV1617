package pt.tecnico.cnv.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.util.logging.Logger;
import java.util.logging.Level;


import pt.tecnico.cnv.server.Test;

public class WebServer {
    private static final Logger LOGGER = Logger.getLogger("WebServer");
    int test1 = Test.test; // just testing dependencies in the Makefile

    public static void main(String[] args) throws Exception {
        LOGGER.setLevel(Level.INFO);
        LOGGER.log(Level.INFO,"Starting webserver...");
	    
	    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This was the query:" + t.getRequestURI().getQuery() 
                               + "##";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
