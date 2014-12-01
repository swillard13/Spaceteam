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
			for(int i = 0; i < limit; i++) {
				Thread.sleep(1000);
				if (on) {
					parent.updateTime(limit-i, limit);
				} else {
					break;
				}
			}
			
			if (on) {
				parent.timeEnd();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
