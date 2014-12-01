package spaceteam.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class AbstractWidget implements Widget {

	public static final Class[] WIDGET_CLASSES = {ButtonGroupWidget.class, PushButtonWidget.class, SliderWidget.class, ToggleButtonWidget.class};
	private static final long serialVersionUID = -8797040547028913329L;

	protected static final Random RANDOM = new Random();
	
	protected transient List<InteractionListener> listeners;
	
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
	
	protected void interactionOccurred(int value) {
		for (InteractionListener listener : listeners) {
			listener.interactionOccurred(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static Widget generateWidget(String name, String verb) {
		Class<? extends Widget> clazz = WIDGET_CLASSES[RANDOM.nextInt(WIDGET_CLASSES.length)];
		Constructor<? extends Widget> constructor;
		try {
			constructor = clazz.getDeclaredConstructor(String.class, String.class);
			return constructor.newInstance(name, verb);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		listeners = new LinkedList<InteractionListener>();
	}

}
