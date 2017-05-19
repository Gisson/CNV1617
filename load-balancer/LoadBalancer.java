import java.util.logging.Logger;
import java.util.logging.Level;

public class LoadBalancer {
    private static final Logger L = Logger.getLogger("WebServer");
    
	public static void main(String argv[]) {
        L.setLevel(Level.INFO);
		L.info(LoadBalancer.class.getSimpleName() + " starting in '"+ System.getProperty("user.dir")+"'...");

	}
}
