package spaceteam.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class PushButtonWidget extends AbstractWidget {

	private static final long serialVersionUID = -1442941113352797813L;

	public PushButtonWidget(String name, String verb) {
		super(name, verb);
	}
	
	@Override
	public JButton getComponent() {
		JButton button = new JButton(getName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interactionOccurred(true);
			}		
		});
		return button;
	}

}
