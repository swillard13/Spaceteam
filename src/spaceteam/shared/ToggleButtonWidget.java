package spaceteam.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

public class ToggleButtonWidget extends AbstractWidget {

	private static final long serialVersionUID = 4697211658521621436L;
	
	public ToggleButtonWidget(String name, String verb) {
		super(name, verb);
	}	

	@Override
	public JComponent getComponent() {
		JToggleButton button = new JToggleButton(getName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interactionOccurred(button.isSelected());
			}	
		});
		return button;
	}

}
