package mylib;

import java.util.concurrent.Executor;

public class MultithreadedExecutor implements Executor{

	@Override
	public void execute(Runnable r){
		new Thread(r).start();
	}
}