package home.apetrivskyy.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class TaskWithResult implements Callable<String> {

	private final int id;
	
	public TaskWithResult(int id) {
		this.id = id;
	}

	@Override
	public String call() throws Exception {
		return "Result of " + id;
	}
	
}

public class CallableDemo {
	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		
		List<Future<String>> results = new ArrayList<Future<String>>();
		
		for (int i = 0; i < 10; i++) {
			results.add(service.submit(new TaskWithResult(i)));
		}
		
		for (Future<String> result : results) {
			try {
				System.out.println(result.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				service.shutdown();
			}
		}
	}
}
