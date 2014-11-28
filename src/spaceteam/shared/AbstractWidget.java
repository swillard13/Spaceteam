package spaceteam.shared;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class AbstractWidget implements Widget {

	private static final long serialVersionUID = -8797040547028913329L;

	protected static final Random RANDOM = new Random();
	
	protected final transient List<InteractionListener> listeners;
	
	private String name;
	private String verb;
	private int randBits;
	
	public AbstractWidget(String name, String verb) {
		this.listeners = new LinkedList<InteractionListener>();
		this.name = name;
		this.verb = verb;
		this.randBits = RANDOM.nextInt();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVerb() {
		return verb;
	}
	
	@Override
	public int getRandomBits() {
		return randBits;
	}
	
	@Override
	public void addInteractionListener(InteractionListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeInteractionListener(InteractionListener listener) {
		listeners.remove(listener);
	}
	
	protected void interactionOccurred(Object value) {
		
	}

}
