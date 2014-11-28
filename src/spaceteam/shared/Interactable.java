package spaceteam.shared;

public interface Interactable {

	void addInteractionListener(InteractionListener listener);
	
	void removeInteractionListener(InteractionListener listener);
	
}
