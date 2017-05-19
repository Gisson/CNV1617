package mylib;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class VersionHandler extends AbstractHttpHandler {
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
					+ "availableProcessors: " + run.availableProcessors()+ "\n"
				+ "freeMemory: " + prettyBytes(run.freeMemory()) + "\n"
				+ "totalMemory: "+ prettyBytes(run.totalMemory()) + "\n";
		doTextResponse(t, response, 200);
	}
}
