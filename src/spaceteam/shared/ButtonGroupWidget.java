package spaceteam.shared;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public class ButtonGroupWidget extends AbstractWidget {
	
	private static final long serialVersionUID = 8018745708854762833L;

	private static final int MIN_MASK = 0b00011;
	private static final int RANGE_MASK = 0b11100;
	
	public ButtonGroupWidget(String name, String verb) {
		super(name, verb);
	}
	
	public int getMin() {
		return getRandomBits() & MIN_MASK;
	}
	
	public int getMax() {
		return getMin() + ((getRandomBits() & RANGE_MASK) >> 2) + 1;
	}
	
	@Override
	public JPanel getComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
		ButtonGroup group = new ButtonGroup();
		for (int i = getMin(); i <= getMax(); ++i) {
			final JToggleButton button = new JToggleButton(String.valueOf(i));
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					interactionOccurred(Integer.parseInt(button.getText()));
				}		
			});
			group.add(button);
			buttonPanel.add(button);
		};
		panel.add(new JLabel(super.getName(), SwingConstants.CENTER), BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.CENTER);
		return panel;
	}

	@Override
	public int getRandomValue() {
		return RANDOM.nextInt(getMax() - getMin()) + getMin();
	}

}
