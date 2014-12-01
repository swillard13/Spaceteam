package spaceteam.shared;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderWidget extends AbstractWidget {

	private static final long serialVersionUID = -447416336403303896L;

	private static final int MIN_VALUE_MASK =   0b000001;
	private static final int MAX_VALUE_MASK =   0b011110;
	private static final int ORIENTATION_MASK = 0b100000;

	public SliderWidget(String name, String verb) {
		super(name, verb);
	}
	
	public int getMin() {
		return getRandomBits() & MIN_VALUE_MASK;
	}
	
	public int getMax() {
		return (getRandomBits() & MAX_VALUE_MASK) >> 1 + 1;
	}
	
	public int getOrientation() {
		return (getRandomBits() & ORIENTATION_MASK) == 0 ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL;
	}
	
	@Override
	public JPanel getComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		final JSlider slider = new JSlider();
		JLabel label = new JLabel(getName(), SwingConstants.CENTER);
		slider.setMinimum(getMin());
		slider.setMaximum(getMax());
		slider.setOrientation(getOrientation());
		slider.setMajorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!slider.getValueIsAdjusting()) {
					interactionOccurred(slider.getValue());
				}
			}
		});
		panel.add(label, BorderLayout.NORTH);
		panel.add(slider, BorderLayout.CENTER);
		return panel;
	}

	@Override
	public int getRandomValue() {
		return RANDOM.nextInt(getMax() - getMin()) + getMin();
	}

}
