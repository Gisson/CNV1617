package mylib;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class StringHandler extends AbstractHttpHandler {
	private String response;

	public StringHandler(String response) {
		this.response = response;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		doTextResponse(t, response, 200);
	}
}
