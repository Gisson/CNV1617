import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mylib.AbstractHttpHandler;
import mylib.MultithreadedExecutor;
import mylib.VersionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.Map;

public class LoadBalancer {
	private static int DEFAULT_PORT = 8000;
    private static final Logger L = Logger.getLogger("WebServer");
    
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
        server.createContext("/r.html", new RenderHandler());
        server.createContext("/kill-balancer", new ExitHandler());
        server.createContext("/version", new VersionHandler());
        server.setExecutor(new MultithreadedExecutor());
        server.start();
        L.info("main exiting...");
	}

    static class RenderHandler extends AbstractHttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();
            /* FIXME TODO */
                String query = uri.getQuery();
                Map<String, String> query_pairs = splitQuery(query);
                String inFile = query_pairs.get("f");
                Integer scols = Integer.valueOf(query_pairs.get("sc"));
                Integer srows = Integer.valueOf(query_pairs.get("sr"));
                Integer wcols = Integer.valueOf(query_pairs.get("wc"));
                Integer wrows = Integer.valueOf(query_pairs.get("wr"));
                Integer coff = Integer.valueOf(query_pairs.get("coff"));
                Integer roff = -Integer.valueOf(query_pairs.get("roff"));
		try {
			MSS mss = MSS.getInstance();
			mss.query(inFile, scols,  srows,  wcols,  wrows,  coff,  roff);
		} catch (Exception e) {
			e.printStackTrace();
		}
            t.close();
        }
    }

    static class ExitHandler extends AbstractHttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            doTextResponse(t, LoadBalancer.class.getSimpleName() + "goodbyte world", 200);
            System.exit(0);
        }
    }
}
