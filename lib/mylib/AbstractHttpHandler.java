package mylib;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.LinkedHashMap;
import java.util.Map;


public abstract class AbstractHttpHandler  implements HttpHandler {
    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String prettyBytes(long bytes) {
        String response = bytes + " bytes";
        if( bytes > 1024*1024) {
            return (bytes/1024/1024) + " MiB (" + response + ")";
        } else {
            return response;
        }
    }


    public static String getEnv(String env) {
        String result = System.getenv(env);
        return env + ": " + (result != null ? result : "(null)");
    }

    public static String getProperty(String p) {
        String result = System.getProperty(p);
        return p + ": " + (result != null ? result : "(null)");
    }

    public static void doTextResponse(HttpExchange t, String response, int code) throws IOException {
        t.sendResponseHeaders(code, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


}
// vim: expandtab:ts=4:sw=4
