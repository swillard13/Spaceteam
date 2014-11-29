package spaceteam.client;

public class Timer extends Thread {
	private ClientThread parent;
	private int limit;
	private boolean on;
	
	public Timer(ClientThread p, int l) {
		parent = p;
		limit = l;
		on = true;
	}
	
	public void turnOff() {
		on = false;
	}
	
	public void run() {
		try {
			Thread.sleep(limit*1000);
			if (on) {
				parent.timeEnd();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
