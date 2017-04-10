package pt.tecnico.cnv.server;

import java.util.concurrent.Executor;

public class WebServerExecutor implements Executor{

	@Override
	public void execute(Runnable r){
		new Thread(r).start();
	}
}